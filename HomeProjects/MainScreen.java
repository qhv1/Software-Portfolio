import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class MainScreen
{
  static Rectangle[] list;

  static JFrame mainFrame;
  static JFrame selectionFrame;
  static JButton selectionSortButton;
  static JButton insertionSortButton;
  static JButton stepForwardSelection;
  static JButton stepForwardInsertion;
  static MyPanel rc1;
  static JPanel buttons;
  static int counter;

  public static void main(String[] args) throws InterruptedException
  {
    counter = 0;
    mainFrame = new JFrame();
    mainFrame.setSize(600, 400);
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    buttons = new JPanel();
    buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
    mainFrame.setContentPane(buttons);

    selectionSortButton = new JButton("Selection Sort");
    selectionSortButton.addActionListener(new ActionHandler());
    selectionSortButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    buttons.add(selectionSortButton);

    insertionSortButton = new JButton("Insertion Sort");
    insertionSortButton.addActionListener(new ActionHandler());
    insertionSortButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    buttons.add(insertionSortButton);

    mainFrame.pack();
    mainFrame.setVisible(true);

    //Y pos for rectangles other than max height rectangle is
    //currRectY = maxRectHeight - currRectHeight + maxRectY
    list = new Rectangle[10];
    randomizeRectangles(list);

  }
  public static void randomizeRectangles(Rectangle[] list)
  {
    int[] widths = new int[list.length];

    Random rand = new Random();
    int minValue = 10;
    int maxValue = 101;
    int maxWidth = -1;
    for(int i = 0; i < widths.length; i++)
    {
      widths[i] = (int)(Math.random() * (maxValue - minValue + 1) + minValue);
      //System.out.println(widths[i]);
      if(widths[i] > maxWidth)
      {
        maxWidth = widths[i];
      }
    }

    for(int i = 0; i < list.length; i++)
    {
      int x = 10 + (i * 30);
      int height = 20;
      int width = widths[i];
      int y = maxWidth - width + 10;

      list[i] = new Rectangle(x, y, width, height);
    }
  }
  public static void initalizeSelectionSort(Rectangle[] list) throws InterruptedException
  {
    counter = 0;
    selectionFrame = new JFrame();
    selectionFrame.setVisible(true);
    selectionFrame.setSize(600, 400);
    selectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    stepForwardSelection = new JButton("Step Forward");
    stepForwardSelection.setBounds(250, 200, 130, 50);
    stepForwardSelection.addActionListener(new ActionHandler());
    selectionFrame.add(stepForwardSelection);

    rc1 = new MyPanel(list);
    selectionFrame.getContentPane().add(rc1);
    rc1.drawing();
  }
  public static void initalizeInsertionSort(Rectangle[] list) throws InterruptedException
  {
    counter = 1;
    selectionFrame = new JFrame();
    selectionFrame.setVisible(true);
    selectionFrame.setSize(600, 400);
    selectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    stepForwardInsertion = new JButton("Step Forward");
    stepForwardInsertion.setBounds(250, 200, 130, 50);
    stepForwardInsertion.addActionListener(new ActionHandler());
    selectionFrame.add(stepForwardInsertion);

    rc1 = new MyPanel(list);
    selectionFrame.getContentPane().add(rc1);
    rc1.drawing();
  }
  public static void selectionSort(Rectangle[] list) throws InterruptedException
  {
    if(counter < list.length)
    {
      int minIndex = counter;
      for(int j = counter + 1; j < list.length; j++)
      {
          if(list[j].getWidth() < list[minIndex].getWidth())
          {
            minIndex = j;
          }
      }
      int xCopy = (int) list[counter].getX();
      Rectangle rectCopy = list[counter];

      rectCopy.setLocation((int) list[minIndex].getX(), (int) list[counter].getY());
      list[minIndex].setLocation(xCopy, (int) list[minIndex].getY());

      list[counter] = list[minIndex];
      list[minIndex] = rectCopy;

      rc1.drawing();
      counter++;
    }
  }
  public static void insertionSort(Rectangle[] list)
  {
    if(counter < list.length)
    {
      double val = list[counter].getWidth();

      int i = counter - 1;
      int j = counter;

      while(i >= 0 && val < list[i].getWidth())
      {
        Rectangle rectCopy = list[j];
        int xCopy = (int) rectCopy.getX();

        rectCopy.setLocation((int) list[i].getX(), (int)rectCopy.getY());
        list[i].setLocation(xCopy, (int) list[i].getY());

        list[j] = list[i];
        list[i] = rectCopy;

        i--;
        j--;
      }
      counter++;

      rc1.drawing();
    }
  }


  private static class ActionHandler implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if(event.getSource() == selectionSortButton)
      {
        try
        {
          initalizeSelectionSort(list);
        }
        catch(InterruptedException e)
        {
          System.out.println("Failed to sleep");
        }
      }
      else if(event.getSource() == insertionSortButton)
      {
        try
        {
          initalizeInsertionSort(list);
        }
        catch(InterruptedException e)
        {
          System.out.println("Failed to sleep");
        }
      }
      else if(event.getSource() == stepForwardSelection)
      {
        try
        {
          selectionSort(list);
        }
        catch(InterruptedException e)
        {
          System.out.println("Failed to sleep");
        }
      }
      else if(event.getSource() == stepForwardInsertion)
      {
        insertionSort(list);
      }
    }
  }
}
