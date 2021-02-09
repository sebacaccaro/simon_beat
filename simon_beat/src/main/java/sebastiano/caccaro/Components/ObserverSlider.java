package sebastiano.caccaro.Components;

import javax.swing.JSlider;

public class ObserverSlider extends JSlider implements GameListener {

  /**
   *
   */
  private static final long serialVersionUID = 2546020128830677806L;

  @Override
  public void notify(int level) {
    if (level == 0) {
      setEnabled(true);
    }
  }

  public ObserverSlider(int minValue, int maxValue, int stdValue) {
    super(minValue, maxValue, stdValue);
  }
}
