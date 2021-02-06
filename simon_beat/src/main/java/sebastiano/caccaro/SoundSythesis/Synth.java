package sebastiano.caccaro.SoundSythesis;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
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
  private Map<Integer, RichSample> samples = new HashMap<Integer, RichSample>();
  private final Synthesizer synth = JSyn.createSynthesizer();
  private Map<Integer, VariableRateStereoReader> samplePlayers = new HashMap<Integer, VariableRateStereoReader>();
  private RichSample success;
  private RichSample failure;
  private VariableRateStereoReader gameEffectPlayer;

  enum Result {
    SUCCES,
    FAILURE,
  }

  private Synth() {
    synth.start();
    LineOut lineOut = new LineOut();
    synth.add(lineOut);

    String[] samples = new File(SAMPLES_DIR).list();
    for (String sampleName : samples) {
      RichSample sample = new RichSample(SAMPLES_DIR + sampleName);
      this.samples.put(sample.getCode(), sample);
    }

    for (RichSample sample : this.samples.values()) {
      VariableRateStereoReader samplePlayer = new VariableRateStereoReader();
      samplePlayer.output.connect(0, lineOut.input, 0);
      samplePlayer.output.connect(1, lineOut.input, 1);
      samplePlayer.rate.set(sample.getSample().getFrameRate());

      samplePlayers.put(sample.getCode(), samplePlayer);
      synth.add(samplePlayer);
    }

    gameEffectPlayer = new VariableRateStereoReader();
    gameEffectPlayer.amplitude.set(0.1);
    gameEffectPlayer.output.connect(0, lineOut.input, 0);
    gameEffectPlayer.output.connect(1, lineOut.input, 1);
    synth.add(gameEffectPlayer);
    success =
      new RichSample("src/main/resources/standard_sounds/9998_success.wav");
    failure =
      new RichSample("src/main/resources/standard_sounds/9999_failure.wav");

    lineOut.start();
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
    VariableRateStereoReader player = samplePlayers.get(sample.getCode());
    player.dataQueue.clear();
    player.dataQueue.queue(sample.getSample());
  }

  public List<RichSample> getSamples() {
    return new LinkedList<RichSample>(samples.values());
  }

  public RichSample getSampleFromCode(int code) {
    return samples.get(code);
  }

  @Override
  public void notify(int level) {
    if (level == 0) {
      playGameSound(Result.FAILURE);
    } else {
      playGameSound(Result.SUCCES);
    }
  }
}
