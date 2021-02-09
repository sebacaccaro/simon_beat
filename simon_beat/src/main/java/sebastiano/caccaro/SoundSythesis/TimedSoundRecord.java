package sebastiano.caccaro.SoundSythesis;

public class TimedSoundRecord {

  private RichSample sample;
  private long millisecondsFromStart;
  private int milliSecondPerBeat;

  public TimedSoundRecord() {
    milliSecondPerBeat = 0;
    millisecondsFromStart = 0;
    sample = null;
  }

  public TimedSoundRecord(
    RichSample sample,
    long millisecondsFromStart,
    int milliSecondPerBeat
  ) {
    this.sample = sample;
    this.millisecondsFromStart = millisecondsFromStart;
    this.milliSecondPerBeat = milliSecondPerBeat;
  }

  public int getMillisecondsPerBeat() {
    return this.milliSecondPerBeat;
  }

  public void setMillisecondsPerBeat(int milliSecondPerBeat) {
    this.milliSecondPerBeat = milliSecondPerBeat;
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
