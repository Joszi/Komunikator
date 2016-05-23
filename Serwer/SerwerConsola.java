import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;


public class SerwerConsola implements Runnable
{
	static final int SERVER_PORT = 20000;

	private String host = "";
	private ServerSocket serwer = null;
	private ProtokolKomunikacji protokol = null;


	SerwerConsola()
	{
	  	Thread t = new Thread(this);
	  	t.start();
	}


	public void run()
	{
		Socket s;
	  	WatekKlienta klient;
	  	protokol = new ProtokolKomunikacji();

	  	try
	  	{
	  		host = InetAddress.getLocalHost().getHostAddress() + " (" + InetAddress.getLocalHost().getHostName() +") ";

	  	   	serwer = new ServerSocket(SERVER_PORT);
		}
		catch(IOException e)
		{
		 	//System.out.println(e);
		   	//System.out.println("Gniazdko dla serwera nie moze byc utworzone !!!");

		   	System.exit(0);
		}

		System.out.println("Serwer zostal uruchomiony na hoscie " + host);

	  	while(true)
	  	{
			try
			{
				s = serwer.accept();

				if(s!=null)
				{
		  			klient = new WatekKlienta(this, s, protokol);

   	                System.out.println("Nowy klient polaczyl sie z serwerem ");
		  		}
			}
			catch(IOException e)
			{
				//System.out.println("BLAD SERWERA: Nie mozna polaczyc sie z klientem !!!");
			}
		}
	}


	public static void main(String [] args)
	{
		new SerwerConsola();
	}
}



class WatekKlienta implements Runnable
{
    String line;
    String response;
    String [] command;

	ConcurrentMap <String, String> answer;

	BufferedReader input;
    PrintWriter output;

	private ProtokolKomunikacji protokol = null;
	private SerwerConsola sc = null;
	private Socket socket = null;
    private String nazwa = "";


	WatekKlienta(SerwerConsola scon, Socket s, ProtokolKomunikacji pr) throws IOException
	{
       	sc = scon;
	  	socket = s;
	  	protokol = pr;

	  	Thread t = new Thread(this);
	  	t.start();
	}


	public String toString()
	{
		return nazwa;
	}


	public void run()
	{
	   	try
	   	{
	   		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream());

			while((line = input.readLine()) != null)
			{
             	command = line.split(" +");

				if(command[0].equals("LOGIN"))
				{
					response = protokol.LOGIN(command[1], command[2]);

					String pom = command[0];
					pom += " ";
					pom += command[1];
					pom += " ";
					pom += command[2];

					System.out.println(pom);

					output.println(response);
					output.flush();
				}

				else if(command[0].equals("LOGOUT"))
				{
					response = protokol.LOGOUT(command[1]);

					String pom = command[0];
					pom += " ";
					pom += command[1];

					System.out.println(pom);

					output.println(response);
					output.flush();

					input.close();
					output.close();

					socket.close();
					socket = null;

					break;
				}

				else if(command[0].equals("GETLIST"))
				{
					answer = protokol.GETLIST();

					System.out.println(command[0]);

					output.println(answer);
					output.flush();
				}
			}
	   	}
	    catch(IOException e)
	    {
	    	//e.printStackTrace();
	    }
	}
}