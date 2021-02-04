package sebastiano.caccaro;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import sebastiano.caccaro.Components.InstrumentButton;
import sebastiano.caccaro.SoundSythesis.BeatManager;
import sebastiano.caccaro.SoundSythesis.RichSample;
import sebastiano.caccaro.SoundSythesis.Synth;

public class MainApp extends JFrame {

  static final int MAINTITLE_FONT_SIZE = 120;
  static final int LEVEL_FONT_SIZE = 40;
  static final String FONT_NAME = "Sans-Serif";
  static final List<Color> BUTTON_COLORS = new ArrayList<Color>(
    Arrays.asList(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW)
  );
  static final List<String> SOUNDS_NAMES = new ArrayList<String>(
    Arrays.asList("Lorem", "Ipsum", "Grancassa", "Piatti", "Rullante")
  );

  private Map<Integer, InstrumentButton> buttons = new HashMap<Integer, InstrumentButton>();

  public MainApp(String windowTitle) {
    super(windowTitle);
    final BeatManager bm = new BeatManager();
    setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    JPanel titlePanel = new JPanel();
    JPanel infoPanel = new JPanel();
    JPanel playPanel = new JPanel();
    JPanel settingsPanel = new JPanel();

    settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.X_AXIS));
    JPanel leftSettingsPanel = new JPanel();
    JPanel rightSettingsPanel = new JPanel();
    settingsPanel.add(leftSettingsPanel);
    settingsPanel.add(rightSettingsPanel);
    leftSettingsPanel.setLayout(
      new BoxLayout(leftSettingsPanel, BoxLayout.Y_AXIS)
    );

    add(titlePanel);
    add(infoPanel);
    add(playPanel);
    add(settingsPanel);

    JLabel mainTitle = new JLabel("SIMON BEAT");
    mainTitle.setFont(new Font(FONT_NAME, Font.PLAIN, MAINTITLE_FONT_SIZE));
    titlePanel.add(mainTitle);

    JLabel level = new JLabel("LEVEL 222");
    level.setFont(new Font(FONT_NAME, Font.PLAIN, LEVEL_FONT_SIZE));
    infoPanel.add(level);

    JButton playButton = new JButton("PLAY!");
    playButton.setPreferredSize(
      new Dimension(LEVEL_FONT_SIZE * 3, LEVEL_FONT_SIZE)
    );
    infoPanel.add(playButton);

    Synth synth = Synth.getInstance();
    List<RichSample> samples = synth.getSamples();
    for (int i = 0; i < samples.size(); i++) {
      final int sampleNum = i;
      InstrumentButton button = new InstrumentButton(
        BUTTON_COLORS.get(i % BUTTON_COLORS.size()),
        () -> {
          RichSample sample = samples.get(sampleNum);
          synth.queueSample(sample);
          bm.addToUserSequence(sample);
        },
        samples.get(i).getCode()
      );
      playPanel.add(button);
      buttons.put(samples.get(i).getCode(), button);
      bm.subscribe(button);
    }

    for (String sound : SOUNDS_NAMES) {
      leftSettingsPanel.add(new JCheckBox(sound));
    }

    JSlider bpmSelector = new JSlider();
    JLabel bpmIndication = new JLabel("30 BPM");
    rightSettingsPanel.add(bpmSelector);
    rightSettingsPanel.add(bpmIndication);
    rightSettingsPanel.add(
      new InstrumentButton(
        Color.GREEN,
        () -> {
          bm.playSequence(5, samples);
        },
        20
      )
    );

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pack();
    setVisible(true);
  }

  public static void main(String[] args) {
    MainApp app = new MainApp("Simon Beat");
  }
}
