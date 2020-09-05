import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.*;

public class SecureChatClient extends JFrame implements Runnable, ActionListener
{
	public static final int PORT = 8765;

    ObjectInputStream myReader;
    ObjectOutputStream myWriter;
    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String myName, serverName;
	Socket connection;
	SymCipher cipher;
    public SecureChatClient ()
    {
        try {

        myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
        serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
        InetAddress addr =
                InetAddress.getByName(serverName);
        connection = new Socket(addr, PORT);   // Connect to server with new
                                               // Socket
        myWriter = new ObjectOutputStream(connection.getOutputStream());
        myWriter.flush();

        myReader = new ObjectInputStream(connection.getInputStream());

        BigInteger E = (BigInteger)myReader.readObject();
        BigInteger N = (BigInteger)myReader.readObject();
        String type = (String)myReader.readObject();
        if(type.equals("Sub"))
        {
        	cipher = new Substitute();
        }
        else if(type.equals("Add"))
        {
        	cipher = new Add128();
        }
        else
        {
        	cipher = new Substitute();
        }
        BigInteger key = new BigInteger(1, cipher.getKey());
        BigInteger cipherText = key.modPow(E, N);

        byte[] theName = cipher.encode(myName);

        myWriter.writeObject(cipherText);
        myWriter.flush();

        myWriter.writeObject(theName);   // Send name to Server.  Server will need
        myWriter.flush();        // this to announce sign-on and sign-off
                                    // of clients

        this.setTitle(myName);      // Set title to identify chatter

        Box b = Box.createHorizontalBox();  // Set up graphical environment for
        outputArea = new JTextArea(8, 30);  // user
        outputArea.setEditable(false);
        b.add(new JScrollPane(outputArea));

        outputArea.append("Welcome to the Chat Group, " + myName + "\n");

        inputField = new JTextField("");  // This is where user will type input
        inputField.addActionListener(this);

        prompt = new JLabel("Type your messages below:");
        Container c = getContentPane();

        c.add(b, BorderLayout.NORTH);
        c.add(prompt, BorderLayout.CENTER);
        c.add(inputField, BorderLayout.SOUTH);

        Thread outputThread = new Thread(this);  // Thread is to receive strings
        outputThread.start();                    // from Server

	      addWindowListener(
              new WindowAdapter()
              {
                  public void windowClosing(WindowEvent e)
                  { 
                  	try
                  	{
                  	byte[] closingMsg = cipher.encode("CLIENT CLOSING");
                  	myWriter.writeObject(closingMsg);
                    System.exit(0);
                	}
                	catch(Exception t)
                	{
                		System.err.println(t);
                	}
                  }
              }
          );

        setSize(500, 200);
        setVisible(true);

        }
        catch (Exception e)
        {
            System.out.println("Problem starting client!");
        }
    }

    public void run()
    {
        while (true)
        {
             try 
             {
                byte[] encodedMsg = (byte[])myReader.readObject();
                String currMsg = cipher.decode(encodedMsg);
                System.out.println(currMsg);
			    outputArea.append(currMsg+"\n");
             }
             catch (Exception e)
             {
                System.out.println(e +  ", closing client!");
                break;
             }
        }
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e)
    {
        String currMsg = e.getActionCommand();      // Get input value
        inputField.setText("");
        try
        {
        	byte[] encodedMsg = cipher.encode(myName + ":" + currMsg);
        	myWriter.writeObject(encodedMsg);
        	myWriter.flush();
        }
        catch(Exception t)
        {
        	System.err.println(t);
        } // Add name and send it
    }                                               // to Server


    public static void main(String [] args)
    {
         SecureChatClient JR = new SecureChatClient();
         JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}