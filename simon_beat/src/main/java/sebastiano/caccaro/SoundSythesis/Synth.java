package sebastiano.caccaro.SoundSythesis;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateDataReader;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.VariableRateStereoReader;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import sebastiano.caccaro.Components.GameListener;

public class Synth implements GameListener {

  private static final Synth INSTANCE = new Synth();
  private static final String SAMPLES_DIR = "src/main/resources/samples/";
  //private Map<Integer, RichSample> samples = new HashMap<Integer, RichSample>();
  private final Synthesizer synth = JSyn.createSynthesizer();
  private Map<Integer, VariableRateDataReader> samplePlayers = new HashMap<Integer, VariableRateDataReader>();
  private RichSample success;
  private RichSample failure;
  private VariableRateDataReader gameEffectPlayer;
  private Map<Integer, SoundBank> soundBanks = new HashMap<Integer, SoundBank>();
  private int selectedSoundBank = -1;
  private LineOut lineOut = new LineOut();

  enum Result {
    SUCCES,
    FAILURE,
  }

  private Synth() {
    synth.start();
    synth.add(lineOut);
    gameEffectPlayer = new VariableRateStereoReader();
    gameEffectPlayer.amplitude.set(0.1);
    gameEffectPlayer.output.connect(0, lineOut.input, 0);
    gameEffectPlayer.output.connect(1, lineOut.input, 1);
    synth.add(gameEffectPlayer);
    try {
      success =
        new RichSample("src/main/resources/standard_sounds/success.wav");
      failure =
        new RichSample("src/main/resources/standard_sounds/failure.wav");
    } catch (Exception e) {
      System.out.println("Standard sounds missing or wrong names");
    }

    String[] folders = new File(SAMPLES_DIR).list();
    for (int i = 0; i < folders.length; i++) {
      folders[i] = SAMPLES_DIR + folders[i] + "/";
    }
    for (String folder : folders) {
      readSoundBank(folder);
    }

    loadSoundBank(0);

    lineOut.start();
  }

  public int readSoundBank(String directory) {
    if (directory.substring(directory.length() - 1) != "/") {
      directory += "/";
    }
    String[] samples = new File(directory).list();
    if (samples.length < 1) {
      return -1;
    }
    String[] dirSplitted = directory.split("/");
    String name = dirSplitted[dirSplitted.length - 1];
    SoundBank soundBank = new SoundBank(name);
    for (String sampleName : samples) {
      RichSample sample;
      try {
        sample = new RichSample(directory + sampleName);
      } catch (Exception e) {
        return -1;
      }
      soundBank.addSample(sample);
    }
    soundBanks.put(soundBank.getCode(), soundBank);
    return soundBank.getCode();
  }

  public int getSelectedCode() {
    return selectedSoundBank;
  }

  public SoundBank getSoundBankFromCode(int code) {
    return soundBanks.get(code);
  }

  public void loadSoundBank(int code) {
    if (code == selectedSoundBank) {
      return;
    }
    for (VariableRateDataReader samplePlayer : samplePlayers.values()) {
      samplePlayer.stop();
      synth.remove(samplePlayer);
    }
    samplePlayers = new HashMap<Integer, VariableRateDataReader>();

    SoundBank currentSoundBank = soundBanks.get(code);
    for (RichSample sample : currentSoundBank.getSampleList()) {
      VariableRateDataReader samplePlayer;
      if (sample.getSample().getChannelsPerFrame() == 1) {
        samplePlayer = new VariableRateMonoReader();
        samplePlayer.output.connect(0, lineOut.input, 0);
        samplePlayer.output.connect(0, lineOut.input, 1);
      } else {
        samplePlayer = new VariableRateStereoReader();
        samplePlayer.output.connect(0, lineOut.input, 0);
        samplePlayer.output.connect(1, lineOut.input, 1);
      }
      samplePlayer.rate.set(sample.getSample().getFrameRate());

      samplePlayers.put(sample.getCode(), samplePlayer);
      synth.add(samplePlayer);
    }
    selectedSoundBank = code;
  }

  public List<SoundBank> getSoundBanks() {
    return new LinkedList<SoundBank>(soundBanks.values());
  }

  public void playGameSound(Result result) {
    RichSample sample = result == Result.SUCCES ? success : failure;
    gameEffectPlayer.rate.set(sample.getSample().getFrameRate());
    gameEffectPlayer.dataQueue.queue(sample.getSample());
  }

  public static Synth getInstance() {
    return INSTANCE;
  }

  public void queueSample(RichSample sample) {
    VariableRateDataReader player = samplePlayers.get(sample.getCode());
    player.dataQueue.clear();
    player.dataQueue.queue(sample.getSample());
  }

  public List<RichSample> getSamples() {
    return new LinkedList<RichSample>(
      soundBanks.get(selectedSoundBank).getSampleList()
    );
  }

  public RichSample getSampleFromCode(int code) {
    return soundBanks.get(selectedSoundBank).getSample(code);
  }

  @Override
  public void notify(int level) {
    if (level == 0) {
      for (VariableRateDataReader samplePlayer : samplePlayers.values()) {
        samplePlayer.dataQueue.clear();
      }
      playGameSound(Result.FAILURE);
    } else {
      playGameSound(Result.SUCCES);
    }
  }
}
