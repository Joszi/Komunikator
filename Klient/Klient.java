import java.net.*;
import java.io.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


class Klient extends JFrame implements ActionListener, Runnable
{
	private JTextField message = new JTextField(20);
	private JTextArea textArea = new JTextArea(15,18);

	//gniazdko Klienta, z ktorym chcemy sie polaczyc
	private int serwerPort;
	private String serwerIP;

	private String nazwa;
	private String nazwa2;

	private Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;


	Klient(String n, String ip, String port)
	{
		super(n);

	  	nazwa = n;
	  	serwerIP = ip;
	  	serwerPort = Integer.parseInt(port);

	  	setSize(250,320);

	  	JPanel panel = new JPanel();

	  	textArea.setLineWrap(true);
	  	textArea.setWrapStyleWord(true);
	  	textArea.setEditable(false);

	  	JScrollPane scroll_bars = new JScrollPane(
	  				textArea,
	  				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		message.addActionListener(this);

	  	panel.add(scroll_bars);
	  	panel.add(message);

	  	setContentPane(panel);

	  	Thread t = new Thread(this);
	  	t.start();

	  	setVisible(true);
	}


	protected void processWindowEvent(WindowEvent evt)
	{
		super.processWindowEvent(evt);

		if(evt.getID() == WindowEvent.WINDOW_CLOSING)
		{
			try
			{
				input.close();
				output.close();

				socket.close();
				socket = null;
			}
			catch(IOException e)
			{
				//System.out.println("Wyjatek Klienta " + e);
			}
		}
	}


	public void actionPerformed(ActionEvent evt)
	{
		String m;
	  	Object src = evt.getSource();

	  	if(src==message)
	  	{
	  		try
	  		{
	  			m = message.getText();
	  		 	output.writeObject(m);

	  		 	String pom = textArea.getText();

	  		 	if(pom.equals(""))
	  		 	{
	  		 		textArea.setText(pom + nazwa + " <<< " + m);
	  		 	}
	  		 	else
	  		 	{
	  		 		textArea.setText(pom + "\n" + nazwa + " <<< " + m);
	  		 	}

		 	 	message.setText("");

	  		 	if(m.equals("exit"))
	  		 	{
	  		 		input.close();
	  		 		output.close();
	  		 		socket.close();

	  		 		setVisible(false);
	  		 		dispose();

	  		 		return;
	  		 	}
	  		}
	  		catch(IOException e)
	  		{
	  			//System.out.println("Wyjatek klienta " + e);
	  		}
	  }

	  repaint();
	}


	public void run()
	{
		try
		{
	  		socket = new Socket(serwerIP, serwerPort);

	  		input = new ObjectInputStream(socket.getInputStream());
	  		output = new ObjectOutputStream(socket.getOutputStream());

	  		output.writeObject(nazwa);

	  		nazwa2 = (String)input.readObject();

	   		setTitle(nazwa2);
	  	}
	  	catch(Exception e)
	  	{
		 	//System.out.println(e);

		   	JOptionPane.showMessageDialog(null, "Polaczenie sieciowe dla klienta nie moze byc utworzone !!!");

		   	setVisible(false);
		   	dispose();

		    return;
		}

		try
		{
			while(true)
			{
		 		String m = (String)input.readObject();

		 		String pom = textArea.getText();

		 		if(pom.equals(""))
		 		{
		 			textArea.setText(pom + nazwa2 + " >>> " + m);
		 		}
		 		else
		 		{
		 			textArea.setText(pom + "\n" + nazwa2 + " >>> " + m);
		 		}

		 		if(m.equals("exit"))
		 		{
		 			input.close();
	  		 		output.close();
	  		 		socket.close();

	  		 		setVisible(false);
	  		 		dispose();

	  		 		break;
		 		}
		 	}
		}
		catch(Exception e)
		{
		 	//System.out.println(e);

		   	//JOptionPane.showMessageDialog(null, "Polaczenie sieciowe dla klienta zostalo przerwane ");

		   	setVisible(false);
		   	dispose();
		 }
	}
}
