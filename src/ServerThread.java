
import java.net.*;
import java.io.*;

 class ServerThread extends Thread {

	Socket socket;
	private final ServerThread[] threads;
	private DataInputStream is = null;
	private PrintStream ps = null;
	private BufferedReader br;
	
	ServerThread(Socket socket, ServerThread[] threads)
	{
		this.socket = socket;
		this.threads = threads;
	}
	
	public void start_conversation()
	{
		
	}
	
	public void join_chat(String msg)throws IOException
	{
		String chat_name = msg.substring(15, msg.indexOf("/n"));
		String client_name = msg.substring(msg.lastIndexOf(32));
		String reply = null;
		int flag = 1;
		if(chat_name.equals(null))//check if chat room name is given 
		{
			flag = 0;
		}
		//check for the format of the message
		else if(!msg.substring((msg.indexOf("/n")+2), (msg.indexOf("/n")+37)).equals("CLIENT_IP: 0\nPORT: 0\nCLIENT_NAME: "))
		{
			flag = 0;
		}
		//confirm a client name
		else if(client_name.equals(null))
		{
			flag = 0;
		}
		if(flag == 1)
		{
			for(int i = 0;i<100; i ++)
			{
				if(threads[i] != null)
				{
					threads[i].ps.println(client_name+" joined the chat");
				}
			}
			
			start_conversation();
			//add_data(chat_name,client_name);
			//send_msg(client_name + "has joined the chat");
		}
		//return reply;
	}
	
	public void checkmsg(String msg) throws IOException
	{
		ps = new PrintStream(socket.getOutputStream());
		//String reply;
		//String chat_name = msg.substring(15, msg.indexOf("/n"));
		//String client_name = msg.substring(msg.lastIndexOf(32));
		if(msg.equals("KILL_SERVICE"))//close down the program
		{
			System.out.println("Killing service");
			ps.println("Killing Service");
			socket.close();
			Thread.currentThread().interrupt();
			return;
		}
		else if(msg.equals("HELO text"))
		{
			ps.println("HELO text\nIP:"+InetAddress.getLocalHost().getHostAddress()+"\nPort:7899\nStudentID:16300602\n");
		}
		
		
		else if(msg.substring(0, 14).equals("JOIN_CHATROOM: "))//call the join function and let it handle everything
		{
			join_chat(msg);
		}
		
		else
		{
			
		}	
	}
	
	
	
	public void run()
	{
		try
		{
			//is = new DataInputStream(socket.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//PrintWriter pr = new PrintWriter(socket.getOutputStream(), true );
		    ps = new PrintStream(socket.getOutputStream());
			//ps.println("Enter your name:");
			//String name = br.readLine();
			String message = br.readLine();
			checkmsg(message);
			/*for(int i = 0;i<100; i ++)
			{
				if(threads[i] != null)
				{
					threads[i].ps.println(name+"joined the chat");
				}
			}*/
			/*while((message = br.readLine())!=null)
			{
				//System.out.println(message);
				for(int i = 0;i<100; i ++)
				{
					if(threads[i] != null && threads[i] != this)
					{
						threads[i].ps.println(name+" : "+message);
					}
				}
				
				
			}*/
			socket.close();
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
}

