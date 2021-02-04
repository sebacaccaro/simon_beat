package sebastiano.caccaro.SoundSythesis;

public interface SampleSubscriber {
  public void notifySample(RichSample sample, int durationMilliseconds);
}
