package sebastiano.caccaro.SoundSythesis;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateStereoReader;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Synth {

  private static final Synth INSTANCE = new Synth();
  private static final String SAMPLES_DIR = "src/main/resources/samples/";
  private List<RichSample> samples = new LinkedList<RichSample>();

  private VariableRateStereoReader samplePlayer;

  private Synth() {
    Synthesizer synth = JSyn.createSynthesizer();
    synth.start();
    LineOut lineOut = new LineOut();
    synth.add(lineOut);

    String[] samples = new File(SAMPLES_DIR).list();
    for (String sampleName : samples) {
      try {
        this.samples.add(new RichSample(SAMPLES_DIR + sampleName));
      } catch (IOException e) {
        System.out.println(
          "Could not load sample at path: " + SAMPLES_DIR + sampleName
        );
      }
    }
    VariableRateStereoReader samplePlayer = new VariableRateStereoReader();
    synth.add(samplePlayer);
    samplePlayer.output.connect(0, lineOut.input, 0);
    samplePlayer.output.connect(1, lineOut.input, 1);
    samplePlayer.rate.set(this.samples.get(0).getSample().getFrameRate());
    lineOut.start();
    this.samplePlayer = samplePlayer;
  }

  public Synth getInstance() {
    return this.INSTANCE;
  }

  public void playSample(RichSample sample) {
    samplePlayer.dataQueue.queue(sample.getSample());
  }

  public List<RichSample> getSamples() {
    return new LinkedList<RichSample>(samples);
  }
}
