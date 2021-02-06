package sebastiano.caccaro.Components;

import javax.swing.JButton;

public class PlayButton extends JButton implements GameListener {

  private boolean isButtonShowingPlay_attr = true;

  @Override
  public void notify(int level) {
    if (level == 0) {
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
