package sebastiano.caccaro;

import java.util.LinkedList;
import java.util.List;
import sebastiano.caccaro.Components.GameSubscriber;
import sebastiano.caccaro.SoundSythesis.TimedSoundRecord;

public class Game implements ResultsSubscriber {

  private int level;
  private int score;

  private List<GameSubscriber> levelListeners = new LinkedList<GameSubscriber>();
  private List<GameSubscriber> scoreListeners = new LinkedList<GameSubscriber>();
  private int sequenceCounter;

  public Game() {
    reset();
  }

  private void notifyAll(List<GameSubscriber> gameListeners) {
    int value = gameListeners == levelListeners ? level : score;
    for (GameSubscriber gameListener : gameListeners) {
      gameListener.notify(value);
    }
  }

  public void nextLevel() {
    level += 1;
    sequenceCounter = 0;
    notifyAll(levelListeners);
  }

  public void addToScore(TimedSoundRecord user, TimedSoundRecord expected) {
    int timeDifference = user.differenceFrom(expected);
    int timePerBeat = user.getMillisecondsPerBeat();
    int points = timeDifference > timePerBeat
      ? 0
      : (int) (100 - (timeDifference * 1.0 / timePerBeat * 100));
    score += points;
    notifyAll(scoreListeners);
  }

  public void reset() {
    level = 0;
    score = 0;
    sequenceCounter = 0;
  }

  public void subscribeToLevel(GameSubscriber gl) {
    levelListeners.add(gl);
    gl.notify(level);
  }

  public void unSubscribeToLevel(GameSubscriber gl) {
    levelListeners.remove(gl);
  }

  public void unSubscribeToScore(GameSubscriber gl) {
    scoreListeners.remove(gl);
  }

  public void subscribeToScore(GameSubscriber gl) {
    scoreListeners.add(gl);
    gl.notify(score);
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
    notifyAll(levelListeners);
    notifyAll(scoreListeners);
  }

  @Override
  public void notify(GameResult gr) {
    TimedSoundRecord user = gr.getUserRecord();
    TimedSoundRecord expected = gr.getExpectedRecord();
    addToScore(user, expected);
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
