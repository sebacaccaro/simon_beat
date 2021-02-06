package sebastiano.caccaro.SoundSythesis;

public class TimedSoundRecord {

  private RichSample sample;
  private long millisecondsFromStart;

  public TimedSoundRecord() {
    millisecondsFromStart = 0;
    sample = null;
  }

  public TimedSoundRecord(RichSample sample, long millisecondsFromStart) {
    this.sample = sample;
    this.millisecondsFromStart = millisecondsFromStart;
  }

  public RichSample getSample() {
    return this.sample;
  }

  public void setSample(RichSample sample) {
    this.sample = sample;
  }

  public long getMillisecondsFromStart() {
    return this.millisecondsFromStart;
  }

  public void setMillisecondsFromStart(long millisecondsFromStart) {
    this.millisecondsFromStart = millisecondsFromStart;
  }

  public int differenceFrom(TimedSoundRecord other) {
    return (int) Math.abs(
      this.getMillisecondsFromStart() - other.getMillisecondsFromStart()
    );
  }
}
