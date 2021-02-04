package sebastiano.caccaro.SoundSythesis;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateStereoReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Synth {

  private static final Synth INSTANCE = new Synth();
  private static final String SAMPLES_DIR = "src/main/resources/samples/";
  private Map<Integer, RichSample> samples = new HashMap<Integer, RichSample>();
  private final Synthesizer synth = JSyn.createSynthesizer();
  private Map<Integer, VariableRateStereoReader> samplePlayers = new HashMap<Integer, VariableRateStereoReader>();

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

    lineOut.start();
  }

  public static Synth getInstance() {
    return INSTANCE;
  }

  public void queueSample(RichSample sample) {
    VariableRateStereoReader player = samplePlayers.get(sample.getCode());
    player.dataQueue.clear();
    player.dataQueue.queue(sample.getSample());
  }

  public void sleepFor(double seconds) {
    try {
      synth.sleepFor(seconds);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public List<RichSample> getSamples() {
    return new LinkedList<RichSample>(samples.values());
  }

  public RichSample getSampleFromCode(int code) {
    return samples.get(code);
  }
}
