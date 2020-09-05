import javax.swing.*;
import java.awt.*;

public class MyPanel extends JPanel
{
  private Rectangle[] sortRects;
  private int posXColor;

  public MyPanel(Rectangle[] rects)
  {
    sortRects = rects;
    posXColor = -1;
  }
  public MyPanel(Rectangle[] rects, int pos)
  {
    sortRects = rects;
    posXColor = pos;
  }
  public void drawing()
  {
    repaint();
  }
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);

    for(int i = 0; i < sortRects.length; i++)
    {
      int x      = (int) sortRects[i].getX();
      int y      = (int) sortRects[i].getY();
      int height = (int) sortRects[i].getHeight();
      int width  = (int) sortRects[i].getWidth();
      if(posXColor == x)
      {
        g.setColor(Color.BLUE);
      }
      g.fillRect(x, y, height, width);
    }
  }
}
