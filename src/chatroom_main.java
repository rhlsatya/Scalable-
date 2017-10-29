import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class chatroom_main {
	private static final ServerThread[] threads = new ServerThread[100];
	public static final int port = 7899;
	private static Socket socket;
	private static ServerSocket chatr;
	
	
	
	
	
	
	public void runServer() throws IOException
	{
		chatr = new ServerSocket(port);
		System.out.println("Server ready for connections..");
		
		while(true)
		{
			
			socket = chatr.accept();
			for(int i = 0; i<100; i++)
			{
				if(threads[i] == null)
				{
					(threads[i] = new ServerThread(socket, threads)).start();
					break;
				}
			}
		}
		
	}
	
	public static void main(String args[])throws IOException
	{
		chatroom_main cm = new chatroom_main();
		cm.runServer();
		
		//String msg;
		
		
	}
	
	

}
