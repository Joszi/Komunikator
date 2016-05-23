import java.io.*;
import java.net.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


class Serwer extends JFrame implements ActionListener, Runnable
{
	private JComboBox ktoZalogowany = new JComboBox();

	private JButton zaloguj = new JButton("Zaloguj");
	private JButton wyloguj = new JButton("Wyloguj");
	private JButton polacz = new JButton("Polacz");
	private JButton pobierz = new JButton("Pobierz");

	//gniazdko, na ktorym nasluchuje Klient
	//host jest odczytywany pozniej
	private String host = "";
	static final int SERVER_PORT = 15000;

	private ServerSocket serwer;
	private String nazwa;

	//tablice, w ktorych sa zapisywane gniazdka klientow
	static final int ROZMIAR = 20;
	private int ileJestImion = 0;
	private int ileJestAdresow = 0;

	private String[] imionaUzyt = new String[ROZMIAR];
 	private String[] numeryIP = new String[ROZMIAR];
	private String[] numeryPortow = new String[ROZMIAR];

	private KlientConsola klientConsola;
	private Klient kl;


	Serwer(String n)
	{
		super(n);

		nazwa = n;

	  	setSize(310,80);
	  	//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	  	JPanel panel = new JPanel();

	  	ktoZalogowany.setPrototypeDisplayValue("#########################");
	  	panel.add(ktoZalogowany);

	  	polacz.addActionListener(this);
	  	panel.add(polacz);
	  	zaloguj.addActionListener(this);
	  	panel.add(zaloguj);
	  	pobierz.addActionListener(this);
	  	panel.add(pobierz);
	  	wyloguj.addActionListener(this);
	  	panel.add(wyloguj);

	  	polacz.setVisible(false);
	  	zaloguj.setVisible(true);
	  	pobierz.setVisible(false);
	  	wyloguj.setVisible(false);

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
			klientConsola.wyloguj();

			System.exit(0);
		}
	}


	public void actionPerformed(ActionEvent evt)
	{
		String m;
		Object src = evt.getSource();

		if(src==polacz)
		{
			String pom;

			for(int i=0; i<ileJestImion; i++)
	  		{
	  			pom = (String)ktoZalogowany.getSelectedItem();
	  			//System.out.println(pom);

	  			if(pom.equals(imionaUzyt[i]))
	  			{
	  				//System.out.println(numeryIP[i]);
	  				//System.out.println(numeryPortow[i]);

	  				kl = new Klient(nazwa, numeryIP[i], numeryPortow[i]);
				}
	  		}
		}

		if(src==zaloguj)
	  	{
	  		klientConsola = new KlientConsola(nazwa, this);

	  		klientConsola.zaloguj();

	  		polacz.setVisible(true);
	  		zaloguj.setVisible(false);
	  		pobierz.setVisible(true);
	  		wyloguj.setVisible(true);

	  		setSize(310,110);
	  	}

		if(src==pobierz)
	  	{
	  		klientConsola.pobierz();
	  	}

	  	if(src==wyloguj)
	  	{
	  		klientConsola.wyloguj();

	  		polacz.setVisible(false);
	  		zaloguj.setVisible(true);
	  		pobierz.setVisible(false);
	  		wyloguj.setVisible(false);

	  		setSize(310,80);
	  	}

	  	repaint();
	}


	public void wyczyscTablice()
	{
		ktoZalogowany.removeAllItems();

		ileJestImion = 0;
		ileJestAdresow = 0;

		for(int i=0; i<ROZMIAR; i++)
		{
			imionaUzyt[i] = "";
 			numeryIP[i] = "";
			numeryPortow[i] = "";
		}
	}


	public void zapiszDoMapy(String res)
	{
		String[] command = null;
		String[] command2 = null;
		String[] command3 = null;

		String wynik = "";
		String wynik2 = "";

		command = res.split(" +");

		for(int i=0; i<command.length; i++)
		{
			if(i==0)
			{
				command[i] = command[i].substring(1,(command[i].length()-1));
			}
			else
			{
				command[i] = command[i].substring(0,(command[i].length()-1));
			}

			wynik += command[i];

			if(i==command.length-1)
			{
				break;
			}

			wynik += "=";
		}

		command2 = wynik.split("=+");

		for(int i=0; i<command2.length; i+=2)
		{
			for(int j=i+1; j<=i+1; j++)
			{
				//System.out.println(command2[i]);
				//System.out.println(command2[j]);

				imionaUzyt[ileJestImion] = command2[i];
				ileJestImion++;

				ktoZalogowany.addItem(command2[i]);

				wynik2 += command2[j];
			}

			if(i==command2.length-1)
			{
				break;
			}

			wynik2 += ":";
		}

		command3 = wynik2.split(":+");

		for(int i=0; i<command3.length; i+=2)
		{
			for(int j=i+1; j<=i+1; j++)
			{
				//System.out.println(command3[i]);
				//System.out.println(command3[j]);

				numeryIP[ileJestAdresow] = command3[i];
				numeryPortow[ileJestAdresow] = command3[j];
				ileJestAdresow++;
			}
		}
	}


	public void run()
	{
		Socket s;
	  	WatekKlienta klient;

	  	try
	  	{
	  		host = InetAddress.getLocalHost().getHostName();

	  	   	serwer = new ServerSocket(SERVER_PORT);
		}
		catch(IOException e)
		{
		 	//System.out.println(e);

		   	JOptionPane.showMessageDialog(null, "Gniazdko dla serwera nie mo¿e byæ utworzone !!!");

		   	System.exit(0);
		}

		System.out.println("Serwer zosta³ uruchomiony na hoscie " + host);

	  	while(true)
	  	{
			try
			{
				s = serwer.accept();

				if (s!=null)
				{
		  			klient = new WatekKlienta(s, nazwa);
		  		}
			}
			catch(IOException e)
			{
				//System.out.println("BLAD SERWERA: Nie mozna polaczyc sie z klientem ");
			}
		}
	}


	public static void main(String[] args)
	{
		String nazwa;

		nazwa = JOptionPane.showInputDialog("Podaj swoja nazwe uzytkownika: ");

		if(nazwa !=null && !nazwa.equals(""))
		{
			new Serwer(nazwa);
		}
	}
}



class WatekKlienta extends JFrame implements ActionListener, Runnable
{
	private JTextField message = new JTextField(20);
	private JTextArea textArea = new JTextArea(15,18);

	private Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;

	private String nazwa;
	private String nazwa2;


	WatekKlienta(Socket s, String n) throws IOException
	{
		super(n);

		nazwa = n;
		socket = s;

		setSize(250,320);

		JPanel panel = new JPanel();

		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);

		JScrollPane scroll_bars = new JScrollPane(
					textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
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
				//System.out.println("Wyjatek WatkuKlienta " + e);
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
				//System.out.println("Wyjatek Watku Klienta " + e);
			}
		}
	}


	public void run()
	{
		String m;

	   	try
	   	{
	   		output = new ObjectOutputStream(socket.getOutputStream());
	  		input = new ObjectInputStream(socket.getInputStream());

	   		nazwa2 = (String)input.readObject();
	   		setTitle(nazwa2);

	   		output.writeObject(nazwa);

			while(true)
			{
				m = (String)input.readObject();

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

					socket = null;

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



