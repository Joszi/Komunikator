import java.io.*;
import java.net.*;


public class KlientConsola
{
	//gniazdko, na ktorym nasluchuje SerwerConsola
	//musi byc znane dla wszystkich Klientow
	static final String HOST_NAME = "5.121.150.74";
	static final int PORT_NUMBER = 20000;

	//gniazdko, na ktorym nasluchuje Klient
	//host jest odczytywany pozniej
	private String host = "";
	static final String hostPort = "15000";

	private Serwer serwer;
	private Socket socket;

	private PrintWriter out;
	private BufferedReader in;

	private String nazwa;


	public KlientConsola(String n, Serwer s)
	{
		nazwa = n;
		serwer = s;
	}


	public void zaloguj()
	{
		try
		{
			host = InetAddress.getLocalHost().getHostAddress();
			//jakby nie chcialo dzialac to wpisac recznie swoje IP z Hamachi
			//host = "5.213.245.245";

			socket = new Socket(HOST_NAME, PORT_NUMBER);

			System.out.println(nazwa + ": Nawi¹zano po³¹czenie z " + HOST_NAME + ":" + PORT_NUMBER);

			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			out.println(nazwa);
			out.flush();

			makeRequest("LOGIN " + nazwa + " " + host + ":" + hostPort);
			makeRequest("GETLIST");
		}
		catch(UnknownHostException e)
		{
			//e.printStackTrace();
		}
		catch(IOException e)
		{
			//e.printStackTrace();
		}
	}


	public void wyloguj()
	{
		try
		{
			makeRequest("LOGOUT " + nazwa);

			out.close();
			in.close();
			socket.close();

			System.out.println(nazwa + ": Zakonczono po³¹czenie z " + HOST_NAME + ":" + PORT_NUMBER);
		}
		catch(IOException e)
		{
			//e.printStackTrace();
		}
	}


	public void pobierz()
	{
		try
		{
			makeRequest("GETLIST");
		}
		catch(IOException e)
		{
			//e.printStackTrace();
		}
	}


	private void makeRequest(String request) throws IOException
	{
		try
		{
			System.out.println(nazwa + ": ==>> " + request);

			out.println(request);
			out.flush();

			serwer.wyczyscTablice();

			String response = in.readLine();

			if(request.equals("GETLIST"))
			{
				serwer.zapiszDoMapy(response);
			}

			System.out.println(nazwa + ": <<== " + response);
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
	}
}
