package sebastiano.caccaro.SoundSythesis;

import java.sql.Time;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BeatManager {

  private static final int STANDARD_BPM = 60;
  private static final int SECONDS_IN_A_MINUTE = 60;

  private int bpm;
  private List<TimedSoundRecord> lastSequence = new LinkedList<TimedSoundRecord>();
  private List<TimedSoundRecord> userSequence = new LinkedList<TimedSoundRecord>();
  private long userSequenceStartTimestamp = 0;
  private List<SampleSubscriber> subscribers = new LinkedList<SampleSubscriber>();

  public BeatManager(int bpm) {
    this.bpm = bpm;
  }

  public BeatManager() {
    this(STANDARD_BPM);
  }

  private int singeBeatInterval() {
    return (int) ((double) SECONDS_IN_A_MINUTE / bpm) * 1000;
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
    /*TODO Rimuovere codice test*/
    if (lastSequence.size() >= userSequence.size()) {
      int el = userSequence.size() - 1;
      System.out.println(
        userSequence.get(el).getMillisecondsFromStart() -
        lastSequence.get(el).getMillisecondsFromStart()
      );
    }
  }

  public void clearUserSequence() {
    userSequence = new LinkedList<TimedSoundRecord>();
  }

  public void subscribe(SampleSubscriber sb) {
    subscribers.add(sb);
  }

  public void playSequence(int beatNumber, List<RichSample> availableSamples) {
    Random randomizer = new Random();
    Synth synth = Synth.getInstance();
    lastSequence = new LinkedList<TimedSoundRecord>();
    clearUserSequence();

    for (int i = 0; i < beatNumber; i++) {
      int maxInt = availableSamples.size();
      /* TODO Better handling of pauses*/
      int randNumber = randomizer.nextInt(maxInt + 1);
      if (randNumber != maxInt) {
        int delayInMilliseconds = singeBeatInterval() * i;
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(
          () -> {
            RichSample sample = availableSamples.get(randNumber);
            lastSequence.add(new TimedSoundRecord(sample, delayInMilliseconds));
            for (SampleSubscriber subscriber : subscribers) {
              subscriber.notifySample(sample, singeBeatInterval() / 2);
            }
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
