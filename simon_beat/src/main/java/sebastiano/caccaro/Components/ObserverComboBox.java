package sebastiano.caccaro.Components;

import javax.swing.JComboBox;
import sebastiano.caccaro.SoundSythesis.SoundBank;

public class ObserverComboBox
  extends JComboBox<SoundBank>
  implements GameListener {

  /**
   *
   */
  private static final long serialVersionUID = -3109197846025858265L;

  @Override
  public void notify(int level) {
    if (level == 0) {
      setEnabled(true);
    }
  }
}
