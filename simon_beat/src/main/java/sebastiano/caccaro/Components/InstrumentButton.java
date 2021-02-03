package sebastiano.caccaro.Components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

public class InstrumentButton extends JButton {

  final Color baseColor;

  public InstrumentButton(Color btnColor) {
    super();
    this.baseColor = btnColor;
    setBackground(btnColor);
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
          setBackground(getBaseColor().darker());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
          setBackground(getBaseColor());
        }
      }
    );
  }

  public Color getBaseColor() {
    return this.baseColor;
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
