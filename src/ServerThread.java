
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
	
	public void start_conversation(String client_name) throws IOException
	{
		String message;
		while((message = br.readLine())!=null)
		{
			//System.out.println(message);
			for(int i = 0;i<100; i ++)
			{
				if(threads[i] != null && threads[i] != this)
				{
					threads[i].ps.println(client_name+" : "+message);
				}
			}
		}
	}
	
	public void join_chat(String msg)throws IOException
	{
		String chat_name = msg.substring(15,(msg.indexOf("CLIENT_IP")-2));
		String client_name = msg.substring(msg.lastIndexOf(32));
		//System.out.println("chat - "+chat_name+"\n"+"client - "+ client_name);
		//System.out.println(msg.substring((msg.indexOf(chat_name)+chat_name.length()+2), (msg.indexOf(chat_name)+chat_name.length()+38)));
		//String reply = null;
		int flag = 1;
		if(chat_name.equals(null))//check if chat room name is given 
		{
			//System.out.println("Condition 1");
			flag = 0;
		}
		//check for the format of the message - KEEP AN EYE TO TEST - USING TWO \\???
		else if(!((msg.substring((msg.indexOf(chat_name)+chat_name.length()+2), (msg.indexOf(chat_name)+chat_name.length()+37))).equals("CLIENT_IP: 0\\nPORT: 0\\nCLIENT_NAME:")))
		{
			//System.out.println("Condition 2");
			flag = 0;
		}
		//confirm a client name
		else if(client_name.equals(null))
		{
			//System.out.println("Condition 3");
			flag = 0;
		}
		if(flag == 1)
		{
			ps.println("JOINED_CHATROOM:"+ chat_name + "\n" + 
					"SERVER_IP: \n" + 
					"PORT: [port number of chat room]\n" + 
					"ROOM_REF: \n" + 
					"JOIN_ID: [integer that uniquely identifies client joining]");
			//System.out.println("All satisfied");
			for(int i = 0;i<100; i ++)
			{
				if(threads[i] != null)
				{
					System.out.println("chat joined");
					threads[i].ps.println(client_name+" joined the chat");
				}
			}
			
			start_conversation(client_name);
			//add_data(chat_name,client_name);
			//send_msg(client_name + "has joined the chat");
		}
		//return reply;
	}
	
	public void checkmsg(String msg) throws IOException
	{
		System.out.println("inside checkmsg");
		ps = new PrintStream(socket.getOutputStream());
		is = new DataInputStream(socket.getInputStream());
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
		
		
		else if(msg.substring(0, 15).equals("JOIN_CHATROOM: "))//call the join function and let it handle everything
		{
			//System.out.println("inside join");
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
			is = new DataInputStream(socket.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//PrintWriter pr = new PrintWriter(socket.getOutputStream(), true );
		    ps = new PrintStream(socket.getOutputStream());
			//ps.println("Enter your name:");
			//String name = br.readLine();
			
		    String message = br.readLine();
			System.out.println(message);
			//System.out.println(message.substring(15,(message.indexOf("CLIENT_IP")-2)));
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

