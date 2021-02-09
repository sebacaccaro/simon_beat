package sebastiano.caccaro.Components;

import javax.swing.JButton;

public class PlayButton extends JButton implements GameListener {

  /**
   *
   */
  private static final long serialVersionUID = 2479831854923385050L;
  private boolean isButtonShowingPlay_attr = true;

  @Override
  public void notify(int level) {
    setPlayMode(level < 1);
  }

  public void setPlayMode(boolean shouldShowPlay) {
    if (shouldShowPlay) {
      setText("Play");
      isButtonShowingPlay_attr = true;
    } else {
      setText("Reset");
      isButtonShowingPlay_attr = false;
    }
  }

  public boolean isButtonShowingPlay() {
    return this.isButtonShowingPlay_attr;
  }
}
