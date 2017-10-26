import java.io.*;
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
		int i = 0;
		while(true)
		{
			
			socket = chatr.accept();
			(threads[i] = new ServerThread(socket, threads)).start();
			i++;
		}
		
	}
	
	public static void main(String args[])throws IOException
	{
		chatroom_main cm = new chatroom_main();
		cm.runServer();
		
		String msg;
		
		
	}
	
	

}
