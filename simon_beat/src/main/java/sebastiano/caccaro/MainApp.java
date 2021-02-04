package sebastiano.caccaro;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

    for (RichSample sample : samples) {
      JCheckBox checkBox = new JCheckBox(sample.getName(), true);
      leftSettingsPanel.add(checkBox);
      checkBox.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) { // 1 = unsel -> selc // 2 = contrario
            boolean isButtonVisible = ((JCheckBox) e.getSource()).isSelected();
            buttons.get(sample.getCode()).setVisible(isButtonVisible);
          }
        }
      );
    }

    JLabel bpmIndication = new JLabel(String.valueOf(bm.getBpm()));
    JSlider bpmSelector = new JSlider(20, 500, bm.getBpm());
    bpmSelector.addChangeListener(
      new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          JSlider source = (JSlider) (e.getSource());
          bpmIndication.setText(source.getValue() + " BPM");
          if (!source.getValueIsAdjusting()) {
            bm.setBpm(source.getValue());
          }
        }
      }
    );
    rightSettingsPanel.add(bpmSelector);
    rightSettingsPanel.add(bpmIndication);
    rightSettingsPanel.add(
      new InstrumentButton(
        Color.GREEN,
        () -> {
          bm.playSequence(5, enabledSamples());
        },
        20
      )
    );

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pack();
    setVisible(true);
  }

  public List<RichSample> enabledSamples() {
    List<RichSample> enabled = new LinkedList<RichSample>();
    for (int code : buttons.keySet()) {
      if (buttons.get(code).isVisible()) {
        enabled.add(Synth.getInstance().getSampleFromCode(code));
      }
    }
    return enabled;
  }

  public static void main(String[] args) {
    MainApp app = new MainApp("Simon Beat");
  }
}
