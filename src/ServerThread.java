
import java.net.*;
import java.io.*;

public class ServerThread extends Thread {

	Socket socket;
	
	ServerThread(Socket socket)
	{
		this.socket = socket;
	}
	
	public void run()
	{
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter pr = new PrintWriter(socket.getOutputStream(), true );
			String message = null;
			while((message = br.readLine())!=null)
			{
				System.out.println(message);
				pr.println("message recieved");
			}
			socket.close();
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
}
