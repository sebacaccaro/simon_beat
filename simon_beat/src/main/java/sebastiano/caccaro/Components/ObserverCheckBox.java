package sebastiano.caccaro.Components;

import javax.swing.JCheckBox;

public class ObserverCheckBox extends JCheckBox implements GameSubscriber {

  /**
   *
   */
  private static final long serialVersionUID = 3915692316865769192L;

  public ObserverCheckBox(String text, boolean checked) {
    super(text, checked);
  }

  @Override
  public void notify(int level) {
    if (level == 0) {
      setEnabled(true);
    }
  }
}
