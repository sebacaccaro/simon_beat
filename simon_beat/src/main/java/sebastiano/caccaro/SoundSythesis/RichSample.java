package sebastiano.caccaro.SoundSythesis;

import com.jsyn.data.FloatSample;
import com.jsyn.util.SampleLoader;
import java.io.File;
import java.io.IOException;

public class RichSample {

  private static int runningCode = 0;

  private FloatSample sample;
  private String name;
  private int code;
  private String path;

  public RichSample(String path) throws Exception {
    this.path = path;
    try {
      this.sample = SampleLoader.loadFloatSample(new File(path));
    } catch (IOException e) {
      System.out.println("Could not load sample at path: " + path);
      e.printStackTrace();
    }
    // I'm assung filenames are in this format
    // Code_Sound Name.wav
    String[] pathSplitted = path.split("/");
    String[] fileAndExtension =
      pathSplitted[pathSplitted.length - 1].split("\\.");
    String filename = fileAndExtension[0];
    String extension = fileAndExtension[1];
    this.code = runningCode++;
    this.name = filename;
    if (!extension.equals("wav")) {
      throw (
        new Exception(
          "Could not load " + path + ": wrong extension (" + extension + ")"
        )
      );
    }
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
