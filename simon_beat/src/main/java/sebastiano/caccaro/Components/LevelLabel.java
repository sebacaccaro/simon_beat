package sebastiano.caccaro.Components;

import javax.swing.JLabel;

public class LevelLabel extends JLabel implements GameListener {

  String levelText = "";

  public LevelLabel(String levelText, int level) {
    this.levelText = levelText;
    this.setText(levelText + level);
  }

  public LevelLabel(String levelText) {
    this(levelText, 0);
  }

  public LevelLabel() {
    this("");
  }

  @Override
  public void notify(int level) {
    this.setText(levelText + level);
  }
}
