package sebastiano.caccaro;

import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sebastiano.caccaro.Components.GameSubscriber;
import sebastiano.caccaro.Components.InstrumentButton;
import sebastiano.caccaro.Components.LevelLabel;
import sebastiano.caccaro.Components.ObserverCheckBox;
import sebastiano.caccaro.Components.ObserverComboBox;
import sebastiano.caccaro.Components.ObserverSlider;
import sebastiano.caccaro.Components.PlayButton;
import sebastiano.caccaro.Components.ScoreLabel;
import sebastiano.caccaro.Components.WrapLayout;
import sebastiano.caccaro.SoundSythesis.BeatManager;
import sebastiano.caccaro.SoundSythesis.RichSample;
import sebastiano.caccaro.SoundSythesis.SoundBank;
import sebastiano.caccaro.SoundSythesis.Synth;

public class App extends JFrame {

  /**
   *
   */
  private static final long serialVersionUID = 5097313321957638415L;
  static final int MAINTITLE_FONT_SIZE = 120;
  static final int LEVEL_FONT_SIZE = 40;
  static final String FONT_NAME = "Sans-Serif";
  static final List<Color> BUTTON_COLORS = new ArrayList<Color>(
    Arrays.asList(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW)
  );

  private Game game = new Game();
  private Map<Integer, InstrumentButton> buttons = new HashMap<Integer, InstrumentButton>();
  public List<ObserverCheckBox> checkBoxes = new LinkedList<ObserverCheckBox>();
  public ObserverSlider bpmSlider;
  private BeatManager beatManager = new BeatManager();
  private JPanel playPanel;
  private JPanel leftSettingsPanel;
  private ObserverComboBox soundBankSelector;

  public App(String windowTitle) {
    super(windowTitle);
    beatManager.subscribeToResults(game);
    setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    JPanel titlePanel = new JPanel(new GridLayout(0, 1));
    JPanel infoPanel = new JPanel(new GridLayout(0, 3));
    playPanel = new JPanel();
    JPanel settingsPanel = new JPanel(new GridLayout(0, 2));
    playPanel.setLayout(new WrapLayout());
    playPanel.setBackground(Color.DARK_GRAY);

    leftSettingsPanel = new JPanel(new GridLayout(0, 1));
    JPanel rightSettingsPanel = new JPanel(new GridLayout(0, 1));

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
    mainTitle.setHorizontalAlignment(JLabel.CENTER);
    mainTitle.setFont(new Font(FONT_NAME, Font.PLAIN, MAINTITLE_FONT_SIZE));
    mainTitle.setForeground(Color.GRAY);
    titlePanel.setBackground(Color.darkGray);
    titlePanel.add(mainTitle);

    LevelLabel level = new LevelLabel("Level: ");
    level.setFont(new Font(FONT_NAME, Font.PLAIN, LEVEL_FONT_SIZE));
    infoPanel.add(level);
    game.subscribeToLevel(level);

    PlayButton playButton = new PlayButton();
    playButton.setPreferredSize(
      new Dimension(LEVEL_FONT_SIZE * 3, LEVEL_FONT_SIZE)
    );

    game.subscribeToLevel(playButton);
    infoPanel.add(playButton);

    ScoreLabel score = new ScoreLabel();
    score.setFont(new Font(FONT_NAME, Font.PLAIN, LEVEL_FONT_SIZE));
    score.setHorizontalAlignment(JLabel.RIGHT);
    infoPanel.add(score);
    game.subscribeToScore(score);

    Synth synth = Synth.getInstance();
    putButtons();

    soundBankSelector = new ObserverComboBox();
    game.subscribeToLevel(soundBankSelector);
    for (SoundBank soundBank : synth.getSoundBanks()) {
      soundBankSelector.addItem(soundBank);
    }
    soundBankSelector.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          ObserverComboBox selector = (ObserverComboBox) e.getSource();
          SoundBank selected = (SoundBank) selector.getSelectedItem();
          if (selected.getCode() != Synth.getInstance().getSelectedCode()) {
            Synth.getInstance().loadSoundBank(selected.getCode());
            putButtons();
            putCheckBoxes();
            pack();
          }
        }
      }
    );

    JLabel selectorTitle = new JLabel("Available Sound Banks");
    selectorTitle.setFont(new Font(FONT_NAME, Font.BOLD, LEVEL_FONT_SIZE / 2));
    leftSettingsPanel.add(selectorTitle);
    leftSettingsPanel.add(soundBankSelector);
    JButton addSoundBank = new JButton("Add SoundBank");
    addSoundBank.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          JFileChooser fc = new JFileChooser();
          fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          int returnVal = fc.showOpenDialog(App.this);

          if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            int newCode = synth.readSoundBank(file.toString());
            if (newCode != -1) {
              soundBankSelector.addItem(
                Synth.getInstance().getSoundBankFromCode(newCode)
              );
            } else {
              showMessageDialog(
                null,
                "There was an error adding the selected soundbank"
              );
            }
          } else {
            System.out.println("Open command cancelled by user.");
          }
        }
      }
    );
    leftSettingsPanel.add(addSoundBank);
    JLabel soundsTitle = new JLabel("Available Samples");
    soundsTitle.setFont(new Font(FONT_NAME, Font.BOLD, LEVEL_FONT_SIZE / 2));
    leftSettingsPanel.add(soundsTitle);

    putCheckBoxes();

    JLabel bpmTitle = new JLabel("Tempo settings");
    bpmTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    bpmTitle.setFont(new Font(FONT_NAME, Font.BOLD, LEVEL_FONT_SIZE / 2));
    JLabel bpmIndication = new JLabel(
      String.valueOf(beatManager.getBpm() + " BPM"),
      SwingConstants.CENTER
    );
    bpmIndication.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    bpmIndication.setFont(new Font(FONT_NAME, Font.PLAIN, LEVEL_FONT_SIZE / 2));
    ObserverSlider bpmSelector = new ObserverSlider(
      20,
      200,
      beatManager.getBpm()
    );
    game.subscribeToLevel(bpmSelector);
    bpmSelector.addChangeListener(
      new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          JSlider source = (JSlider) (e.getSource());
          bpmIndication.setText(source.getValue() + " BPM");
          if (!source.getValueIsAdjusting()) {
            beatManager.setBpm(source.getValue());
          }
        }
      }
    );
    rightSettingsPanel.add(bpmTitle);
    rightSettingsPanel.add(bpmSelector);
    rightSettingsPanel.add(bpmIndication);

    playButton.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (playButton.isButtonShowingPlay()) {
            // START GAME
            for (InstrumentButton ib : buttons.values()) {
              ib.setEnabled(true);
            }
            bpmSelector.setEnabled(false);
            for (ObserverCheckBox jCheckBox : checkBoxes) {
              jCheckBox.setEnabled(false);
            }
            beatManager.playSequence(game.getLevel() + 1, enabledSamples());
            playButton.setPlayMode(false);
            soundBankSelector.setEnabled(false);
          } else {
            game.gameOver();
            bpmSelector.setEnabled(true);
          }
        }
      }
    );

    game.subscribeToLevel(synth);
    game.subscribeToLevel(beatManager);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pack();
    setVisible(true);
  }

  public void putButtons() {
    //Remove old buttons, then put new ones
    for (Integer key : buttons.keySet()) {
      InstrumentButton button = buttons.get(key);
      playPanel.remove(button);
      beatManager.unSubscribe(key);
      beatManager.unSubscribeToLevelStart(button);
      game.unSubscribeToLevel(button);
    }
    buttons = new HashMap<Integer, InstrumentButton>();

    Synth synth = Synth.getInstance();
    List<RichSample> samples = synth.getSamples();
    for (int i = 0; i < samples.size(); i++) {
      final int sampleNum = i;
      InstrumentButton button = new InstrumentButton(
        BUTTON_COLORS.get(i % BUTTON_COLORS.size()),
        () -> {
          RichSample sample = samples.get(sampleNum);
          synth.queueSample(sample);
          beatManager.addToUserSequence(sample);
        },
        samples.get(i).getCode()
      );
      playPanel.add(button);
      buttons.put(samples.get(i).getCode(), button);
      beatManager.subscribe(button, samples.get(i).getCode());
      beatManager.subscribeToLevelStart(button);
      game.subscribeToLevel(button);
      playPanel.revalidate();
    }
  }

  public void putCheckBoxes() {
    for (ObserverCheckBox checkBox : checkBoxes) {
      leftSettingsPanel.remove(checkBox);
      game.unSubscribeToLevel(checkBox);
    }
    Synth synth = Synth.getInstance();
    for (RichSample sample : synth.getSamples()) {
      ObserverCheckBox checkBox = new ObserverCheckBox(sample.getName(), true);
      checkBoxes.add(checkBox);
      game.subscribeToLevel(checkBox);
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
    leftSettingsPanel.revalidate();
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
    App app = new App("Simon Beat");
  }
}
