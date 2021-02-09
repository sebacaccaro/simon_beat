package sebastiano.caccaro.SoundSythesis;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SoundBank {

  private static int counter = 0;
  private String name;
  private int code;
  private Map<Integer, RichSample> samples = new HashMap<Integer, RichSample>();

  public SoundBank(String name) {
    this.name = name;
    this.code = counter++;
  }

  public void addSample(RichSample sample) {
    samples.put(sample.getCode(), sample);
  }

  public RichSample getSample(int sampleCode) {
    return samples.get(sampleCode);
  }

  public List<RichSample> getSampleList() {
    return new LinkedList<>(samples.values());
  }

  public String getName() {
    return name;
  }

  public int getCode() {
    return code;
  }

  public String toString() {
    return this.name;
  }
}
