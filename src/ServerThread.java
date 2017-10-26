
import java.net.*;
import java.io.*;

 class ServerThread extends Thread {

	Socket socket;
	private final ServerThread[] threads;
	private DataInputStream is = null;
	private PrintStream ps = null;
	
	ServerThread(Socket socket, ServerThread[] threads)
	{
		this.socket = socket;
		this.threads = threads;
	}
	
	public void run()
	{
		try
		{
			//is = new DataInputStream(socket.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//PrintWriter pr = new PrintWriter(socket.getOutputStream(), true );
		    ps = new PrintStream(socket.getOutputStream());
			ps.println("Enter your name:");
			String name = br.readLine();
			String message = null;
			for(int i = 0;i<100; i ++)
			{
				if(threads[i] != null)
				{
					threads[i].ps.println(name+"joined the chat");
				}
			}
			while((message = br.readLine())!=null)
			{
				//System.out.println(message);
				for(int i = 0;i<100; i ++)
				{
					if(threads[i] != null)
					{
						threads[i].ps.println(name+" : "+message);
					}
				}
			}
			socket.close();
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
}
