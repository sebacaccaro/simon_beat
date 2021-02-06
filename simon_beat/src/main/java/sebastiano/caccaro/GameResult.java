package sebastiano.caccaro;

import sebastiano.caccaro.SoundSythesis.TimedSoundRecord;

public class GameResult {

  private TimedSoundRecord userRecord;
  private TimedSoundRecord expectedRecord;

  public GameResult(
    TimedSoundRecord userRecord,
    TimedSoundRecord expectedRecord
  ) {
    this.userRecord = userRecord;
    this.expectedRecord = expectedRecord;
  }

  public TimedSoundRecord getUserRecord() {
    return this.userRecord;
  }

  public void setUserRecord(TimedSoundRecord userRecord) {
    this.userRecord = userRecord;
  }

  public TimedSoundRecord getExpectedRecord() {
    return this.expectedRecord;
  }

  public void setExpectedRecord(TimedSoundRecord expectedRecord) {
    this.expectedRecord = expectedRecord;
  }
}
