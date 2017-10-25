import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class chatroom_main {
	
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
			new ServerThread(socket).start();
		}
		
	}
	
	public static void main(String args[])throws IOException
	{
		chatroom_main cm = new chatroom_main();
		cm.runServer();
		
		String msg;
		
		
	}
	
	

}
