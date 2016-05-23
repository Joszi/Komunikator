import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;


public class ProtokolKomunikacji
{
	private ConcurrentMap<String, String> contacts = new ConcurrentHashMap<String, String>();


  	public String LOGIN(String name, String socket)
  	{
		if(contacts.containsKey(name))
		{
			return "ERROR " + name + " juz istnieje !!!";
		}

		contacts.put(name, socket);

		return "OK " + name + " jestes zalogowany";
	}


	public String LOGOUT(String name)
	{
		contacts.remove(name);

		return "OK " + "wylogowano " + name;
	}


	public ConcurrentMap<String, String> GETLIST()
	{
		return contacts;
	}
}
