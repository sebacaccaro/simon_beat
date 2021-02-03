package sebastiano.caccaro.SoundSythesis;

import java.util.List;
import java.util.Random;

public class BeatMaker {

  private static final int STANDARD_BPM = 60;
  private static final int SECONDS_IN_A_MINUTE = 60;
  private int bpm;

  public BeatMaker(int bpm) {
    this.bpm = bpm;
  }

  public BeatMaker() {
    this(STANDARD_BPM);
  }

  private double singeBeatInterval() {
    return (double) SECONDS_IN_A_MINUTE / bpm;
  }

  public int getBpm() {
    return bpm;
  }

  public void setBpm(int bpm) {
    this.bpm = bpm;
  }

  public void playSequence(int beatNumber, List<RichSample> availableSamples) {
    Random randomizer = new Random();
    Synth synth = Synth.getInstance();

    for (int i = 0; i < beatNumber; i++) {
      int maxInt = availableSamples.size();
      /* TODO Better handling of pauses*/
      int randNumber = randomizer.nextInt(maxInt + 1);
      if (randNumber != maxInt) {
        RichSample sample = availableSamples.get(randNumber);
        synth.queueSample(sample, singeBeatInterval() * i);
      }
      //synth.sleepFor(singeBeatInterval());
    }
  }

  public static void main(String[] args) {
    BeatMaker bm = new BeatMaker(60);
    bm.playSequence(20, Synth.getInstance().getSamples());
  }
}
