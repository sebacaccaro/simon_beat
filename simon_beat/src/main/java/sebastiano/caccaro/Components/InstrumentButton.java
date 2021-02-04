package sebastiano.caccaro.Components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import sebastiano.caccaro.SoundSythesis.RichSample;
import sebastiano.caccaro.SoundSythesis.SampleSubscriber;

public class InstrumentButton extends JButton implements SampleSubscriber {

  final Color baseColor;
  private int code;
  private Runnable clickFunction;

  public InstrumentButton(Color btnColor, Runnable clickFunction, int code) {
    super();
    this.code = (code);
    this.clickFunction = clickFunction;
    this.baseColor = btnColor.darker().darker();
    setBackground(baseColor);
    setPreferredSize(new Dimension(80, 80));
    setBorder(new LineBorder(Color.BLACK, 0));
    addMouseListener(
      new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
          setBackground(getBaseColor().brighter());
        }

        @Override
        public void mouseExited(MouseEvent e) {
          setBackground(getBaseColor());
        }

        @Override
        public void mousePressed(MouseEvent e) {
          setBackground(getBaseColor().brighter().brighter());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
          setBackground(getBaseColor().brighter());
        }

        @Override
        public void mouseClicked(MouseEvent e) {
          getClickFunction().run();
        }
      }
    );
  }

  @Override
  public void notifySample(RichSample sample, int durationMilliseconds) {
    if (sample.getCode() == this.code) {
      flashFor(durationMilliseconds);
    }
  }

  public void flashFor(int milliSeconds) {
    setBackground(getBaseColor().brighter().brighter());
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    executorService.schedule(
      () -> {
        setBackground(getBaseColor());
      },
      milliSeconds,
      TimeUnit.MILLISECONDS
    );
  }

  public Color getBaseColor() {
    return this.baseColor;
  }

  private Runnable getClickFunction() {
    return clickFunction;
  }

  @Override
  public void paintComponent(Graphics g) {
    //super.paintComponent(g);
    int x = 0;
    int y = 0;
    int w = getWidth();
    int h = getHeight();
    g.setColor(getBackground());
    g.fillRoundRect(x, y, w, h, w / 2, w / 2);
    setFocusPainted(false);
    //setContentAreaFilled(false)
    setMargin(new Insets(0, 0, 0, 0));
  }
}
