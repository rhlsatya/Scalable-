
import java.net.*;
import java.io.*;

 class ServerThread extends Thread {

	Socket socket;
	private final ServerThread[] threads;
	private DataInputStream is = null;
	private PrintStream ps = null;
	private int port;
	private BufferedReader br;
	public String client_name;
	public volatile static int join_id[][] = new int[100][2];
	public int room_ref;
	int counter; //to keep track of join id
	ServerThread(Socket socket, ServerThread[] threads, int join_id[][], int chat_id[][], int port, int counter)
	{
		this.socket = socket;
		this.threads = threads;
		this.port = port;
		ServerThread.join_id = join_id;
		this. counter = counter;
	}
	
	public void start_conversation() throws IOException
	{
		String message;
		while((message = br.readLine())!=null)
		{
			//System.out.println(message);
			
			if(message.equals("LEAVE_CHATROOM: " + room_ref + "\n" + 
					"JOIN_ID: " + counter + "\n" + 
					"CLIENT_NAME: " + client_name))
			{
				leave_chat();
				break;
			}
			
			for(int i = 0;i<100; i ++)
			{
				if(threads[i] != null && threads[i] != this && join_id[i][1] == room_ref)
				{
					threads[i].ps.println(client_name+" : "+message);
				}
			}
		}
	}
	
	public void leave_chat() throws IOException
	{
		ps.println("LEFT_CHATROOM: " + room_ref + "\n" + 
				"JOIN_ID: " + counter);
		for(int i = 0;i<100; i ++)
		{
			if(threads[i] != null && threads[i] != this && join_id[i][1] == room_ref)
			{
				threads[i].ps.println(client_name+" has left the chatroom");
			}
		}
		
		socket.close();
		Thread.currentThread().interrupt();
		return;
		
	}
	
	public void join_chat(String msg)throws IOException
	{
		String chat_name = msg.substring(15,(msg.indexOf("CLIENT_IP")-2));
		client_name = msg.substring(msg.lastIndexOf(32));
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
			 room_ref = create_room_ref(chat_name); // creates a unique room reference using ascii values
			ps.println("JOINED_CHATROOM:"+ chat_name + "\n" + 
					"SERVER_IP:" + Inet4Address.getLocalHost().getHostAddress() + "\n" + 
					"PORT: " + port + "\n" + 
					"ROOM_REF: " + room_ref + "\n" + 
					"JOIN_ID: " + counter);
			//System.out.println("All satisfied");
			
			assign_id();
			 
			for(int i = 0;i<100; i ++)
			{
				if(threads[i] != null && join_id[i][1] == room_ref)//if they're in the same chat room
				{
					System.out.println("chat joined");
					threads[i].ps.println(client_name+" joined the chat");
				}
			}
			
			start_conversation();
			
			//add_data(chat_name,client_name);
			//send_msg(client_name + "has joined the chat");
		}
		//return reply;
	}
	
	
	public int create_room_ref(String chat_name)
	{
		int ref = 0;
		
		for(int i = 0; i<chat_name.length(); i++)
		{
			
			int x  = chat_name.charAt(i);
			ref = ref*10 + x;
		}
		
		return ref;
	}
	
	public void assign_id()
	{
		for(int i = 0; i < 100; i++)
		{
			if(threads[i] == this)
			{
				join_id[i][0] = counter;
				join_id[i][1] = room_ref;
				break;
			}
		}
	}
	
	public void checkmsg(String msg) throws IOException
	{
		//System.out.println("inside checkmsg");
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
			ps.println("HELO text" + "\n" + "IP:"+InetAddress.getLocalHost().getHostAddress()+"\n" + "Port:7899" + "\n" + "StudentID:16300602" + "\n");
		}
		
		
		else if(msg.substring(0, 15).equals("JOIN_CHATROOM: "))//call the join function and let it handle everything
		{
			//System.out.println("inside join");
			join_chat(msg);
		}
		
		else
		{
			ps.println("Incorrect Protocol. Terminating Connection..");
			socket.close();
			Thread.currentThread().interrupt();
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
			
		    
			socket.close();
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
}

