package sebastiano.caccaro;

import java.util.LinkedList;
import java.util.List;
import sebastiano.caccaro.Components.GameListener;
import sebastiano.caccaro.SoundSythesis.TimedSoundRecord;

public class Game implements ResultsSubscriber {

  private int level;
  private int score;

  private List<GameListener> levelListener = new LinkedList<GameListener>();
  private int sequenceCounter;

  public Game() {
    reset();
  }

  private void notifyLevelListeners() {
    for (GameListener gameListener : levelListener) {
      gameListener.notify(level);
    }
  }

  public void nextLevel() {
    level += 1;
    sequenceCounter = 0;
    notifyLevelListeners();
  }

  public void addToScore(int points) {
    score += points;
  }

  public void reset() {
    level = 0;
    score = 0;
    sequenceCounter = 0;
  }

  public void subscribeToLevel(GameListener gl) {
    levelListener.add(gl);
    gl.notify(level);
  }

  public int getLevel() {
    return level;
  }

  public int getScore() {
    return score;
  }

  public int sequenceLength() {
    return level + 1;
  }

  public void gameOver() {
    reset();
    notifyLevelListeners();
  }

  @Override
  public void notify(GameResult gr) {
    TimedSoundRecord user = gr.getUserRecord();
    TimedSoundRecord expected = gr.getExpectedRecord();
    int timedDifference = user.differenceFrom(expected);
    boolean isSameButton =
      user.getSample().getCode() == expected.getSample().getCode();
    if (!isSameButton) {
      gameOver();
    } else {
      sequenceCounter += 1;
      if (sequenceCounter == sequenceLength()) {
        nextLevel();
      }
    }
  }
}
