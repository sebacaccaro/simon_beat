package sebastiano.caccaro.SoundSythesis;

import java.security.spec.ECField;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import sebastiano.caccaro.GameResult;
import sebastiano.caccaro.ResultsSubscriber;

public class BeatManager {

  private static final int STANDARD_BPM = 60;
  private static final int SECONDS_IN_A_MINUTE = 60;

  private int bpm;
  private List<TimedSoundRecord> lastSequence = new LinkedList<TimedSoundRecord>();
  private List<TimedSoundRecord> userSequence = new LinkedList<TimedSoundRecord>();
  private long userSequenceStartTimestamp = 0;
  private Map<Integer, SampleSubscriber> subscribers = new HashMap<Integer, SampleSubscriber>();
  private List<ResultsSubscriber> resultsSubscribers = new LinkedList<ResultsSubscriber>();

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
      new TimedSoundRecord(sample, timestamp - userSequenceStartTimestamp)
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

  public void playSequence(int beatNumber, List<RichSample> availableSamples) {
    if (availableSamples.size() == 0) {
      return;
    }
    Random randomizer = new Random();
    Synth synth = Synth.getInstance();
    lastSequence = new LinkedList<TimedSoundRecord>();
    clearUserSequence();

    for (int i = 0; i < beatNumber; i++) {
      int maxInt = availableSamples.size();
      /* TODO Better handling of pauses*/
      int randNumber = randomizer.nextInt(maxInt/* + 1 */);
      if (randNumber != maxInt) {
        RichSample sample = availableSamples.get(randNumber);
        int delayInMilliseconds = singeBeatInterval() * i;
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        SampleSubscriber sb = subscribers.get(sample.getCode());
        lastSequence.add(new TimedSoundRecord(sample, delayInMilliseconds));
        executorService.schedule(
          () -> {
            sb.notifySample(sample, singeBeatInterval() / 2);
            synth.queueSample(sample);
          },
          delayInMilliseconds,
          TimeUnit.MILLISECONDS
        );
      }
      //synth.sleepFor(singeBeatInterval());
    }
  }

  public static void main(String[] args) {
    BeatManager bm = new BeatManager(60);
    bm.playSequence(20, Synth.getInstance().getSamples());
  }
}
