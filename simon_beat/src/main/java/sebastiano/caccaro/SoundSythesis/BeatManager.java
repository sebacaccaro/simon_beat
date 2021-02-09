package sebastiano.caccaro.SoundSythesis;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import sebastiano.caccaro.Components.GameSubscriber;
import sebastiano.caccaro.Components.LevelStartSubscriber;
import sebastiano.caccaro.GameResult;
import sebastiano.caccaro.ResultsSubscriber;

public class BeatManager implements GameSubscriber {

  private static final int STANDARD_BPM = 60;
  private static final int SECONDS_IN_A_MINUTE = 60;
  private static final int MILLISECONDS_BETWEEN_ROUNDS = 2000;

  private int bpm;
  private List<TimedSoundRecord> lastSequence = new LinkedList<TimedSoundRecord>();
  private List<TimedSoundRecord> userSequence = new LinkedList<TimedSoundRecord>();
  private long userSequenceStartTimestamp = 0;
  private Map<Integer, SampleSubscriber> subscribers = new HashMap<Integer, SampleSubscriber>();
  private List<ResultsSubscriber> resultsSubscribers = new LinkedList<ResultsSubscriber>();
  private List<RichSample> cached_samples;
  private List<LevelStartSubscriber> levelStartListeners = new LinkedList<LevelStartSubscriber>();
  private ScheduledExecutorService nextLevelSequence = null;
  private List<ScheduledExecutorService> nextSamples = new LinkedList<ScheduledExecutorService>();

  public BeatManager(int bpm) {
    this.bpm = bpm;
  }

  public BeatManager() {
    this(STANDARD_BPM);
  }

  private int singeBeatInterval() {
    return SECONDS_IN_A_MINUTE * 1000 / bpm;
  }

  public int getBpm() {
    return bpm;
  }

  public void setBpm(int bpm) {
    this.bpm = bpm;
  }

  public void addToUserSequence(RichSample sample) {
    long timestamp = new Date().getTime();
    if (userSequence.isEmpty()) {
      userSequenceStartTimestamp = timestamp;
    }
    userSequence.add(
      new TimedSoundRecord(
        sample,
        timestamp - userSequenceStartTimestamp,
        singeBeatInterval()
      )
    );
    if (lastSequence.size() >= userSequence.size()) {
      int lastPress = userSequence.size() - 1;
      TimedSoundRecord user = userSequence.get(lastPress);
      TimedSoundRecord expected = lastSequence.get(lastPress);
      GameResult gm = new GameResult(user, expected);
      for (ResultsSubscriber rs : resultsSubscribers) {
        rs.notify(gm);
      }
    }
  }

  public void subscribeToResults(ResultsSubscriber rs) {
    resultsSubscribers.add(rs);
  }

  public void clearUserSequence() {
    userSequence = new LinkedList<TimedSoundRecord>();
  }

  public void subscribe(SampleSubscriber sb, int code) {
    subscribers.put(code, sb);
  }

  public void unSubscribe(int code) {
    subscribers.remove(code);
  }

  public void subscribeToLevelStart(LevelStartSubscriber lsl) {
    levelStartListeners.add(lsl);
  }

  public void unSubscribeToLevelStart(LevelStartSubscriber lsl) {
    levelStartListeners.remove(lsl);
  }

  public void playSequence(int beatNumber, List<RichSample> availableSamples) {
    cached_samples = availableSamples;
    if (availableSamples.size() == 0) {
      return;
    }
    Random randomizer = new Random();
    Synth synth = Synth.getInstance();
    lastSequence = new LinkedList<TimedSoundRecord>();
    clearUserSequence();

    for (LevelStartSubscriber levelStartListener : levelStartListeners) {
      levelStartListener.notifyLevelStart(beatNumber * singeBeatInterval());
    }
    for (int i = 0; i < beatNumber; i++) {
      int maxInt = availableSamples.size();
      int randNumber = randomizer.nextInt(maxInt);
      RichSample sample = availableSamples.get(randNumber);
      int delayInMilliseconds = singeBeatInterval() * i;
      SampleSubscriber sb = subscribers.get(sample.getCode());
      lastSequence.add(
        new TimedSoundRecord(sample, delayInMilliseconds, singeBeatInterval())
      );
      ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
      nextSamples.add(executorService);
      executorService.schedule(
        () -> {
          synth.queueSample(sample);
          sb.notifySample(sample, singeBeatInterval() / 2);
          nextSamples.remove(executorService);
        },
        delayInMilliseconds,
        TimeUnit.MILLISECONDS
      );
    }
  }

  public static void main(String[] args) {
    BeatManager bm = new BeatManager(60);
    bm.playSequence(20, Synth.getInstance().getSamples());
  }

  @Override
  public void notify(int level) {
    if (level >= 1) {
      nextLevelSequence = Executors.newSingleThreadScheduledExecutor();
      nextLevelSequence.schedule(
        () -> {
          playSequence(level + 1, cached_samples);
        },
        MILLISECONDS_BETWEEN_ROUNDS,
        TimeUnit.MILLISECONDS
      );
    } else {
      if (nextLevelSequence != null) {
        nextLevelSequence.shutdownNow();
        nextLevelSequence = null;
        for (ScheduledExecutorService nextSample : nextSamples) {
          nextSample.shutdownNow();
        }
        nextSamples = new LinkedList<ScheduledExecutorService>();
      }
    }
  }
}
