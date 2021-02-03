package sebastiano.caccaro.SoundSythesis;

import com.jsyn.data.FloatSample;
import com.jsyn.util.SampleLoader;
import java.io.File;
import java.io.IOException;

public class RichSample {

  private FloatSample sample;
  private String name;
  private int code;
  private String path;

  public RichSample(String path) throws IOException {
    this.path = path;
    this.sample = SampleLoader.loadFloatSample(new File(path));
    // I'm assung filenames are in this format
    // Code_Sound Name.wav
    String[] pathSplitted = path.split("/");
    String filename = pathSplitted[pathSplitted.length - 1].split("\\.")[0];
    String[] filenameSplitted = filename.split("_");
    this.code = Integer.parseInt(filenameSplitted[0]);
    this.name = filenameSplitted[1];
  }

  public int getCode() {
    return this.code;
  }

  public String getName() {
    return this.name;
  }

  public FloatSample getSample() {
    return sample;
  }

  public String toString() {
    return getName();
  }
}
