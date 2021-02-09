package sebastiano.caccaro.Components;

import javax.swing.JLabel;

public class ScoreLabel extends JLabel implements GameListener {

  /**
   *
   */
  private static final long serialVersionUID = -4494782652148589952L;

  @Override
  public void notify(int value) {
    setText("Score: " + value);
  }
}
