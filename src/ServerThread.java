
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
	public volatile static int join_id[][] = new int[100][100];
	public volatile static int chat_id[][] = new int[100][100];
	public int room_ref;
	int counter; //to keep track of join id
	ServerThread(Socket socket, ServerThread[] threads, int join_id[][], int chat_id[][], int port, int counter)
	{
		this.socket = socket;
		this.threads = threads;
		this.port = port;
		ServerThread.join_id = join_id;
		ServerThread.chat_id = chat_id;
		this. counter = counter;
	}
	/*
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
		
	}*/
	
	public void join_chat(String msg)throws IOException // add the else conditions to all of them
	{	
		String chat_name = msg.substring(15);
		String client_ip = br.readLine();
		
		String port_no = br.readLine();
		
		String name = br.readLine();
		
		client_name = name.substring(13);
		
		//System.out.println("chat - "+chat_name+"\n"+"client - "+ client_name);
		//System.out.println(msg.substring((msg.indexOf(chat_name)+chat_name.length()+2), (msg.indexOf(chat_name)+chat_name.length()+38)));
		//String reply = null;
		int flag = 0;
		if(chat_name.equals(null))//check if chat room name is given 
		{
			//System.out.println("Condition 1");
			flag++;
		}
		//check for the format of the message - KEEP AN EYE TO TEST - USING TWO \\???
		else if(!client_ip.equals("CLIENT_IP: 0") && !port_no.equals("PORT: 0"))
		{
			flag++;
		}
		
		
		//confirm a client name
		else if(client_name.equals(null))
		{
			//System.out.println("Condition 3");
			flag++;
		}
		else if(!name.equals("CLIENT_NAME: "+client_name))
		{
			flag++;
		}
		if(flag == 0)
		{
			room_ref = get_room_ref(chat_name); // creates a unique room reference using ascii values
			ps.println("JOINED_CHATROOM:"+ chat_name + "\n" + 
					"SERVER_IP:" + Inet4Address.getLocalHost().getHostAddress() + "\n" + 
					"PORT: " + port + "\n" + 
					"ROOM_REF: " + room_ref + "\n" + 
					"JOIN_ID: " + counter);
			//System.out.println("All satisfied");
			
			assign_ids();
			broadcast_message(client_name + " joined the chat", room_ref);
			//start_conversation();
			
			//add_data(chat_name,client_name);
			//send_msg(client_name + "has joined the chat");
		}
		//return reply;
	}
	
	public void kill_service()
	{
		for(int i = 0; i < 100; i++)
		{
			if(join_id[i][0] == counter)
			{
				
				for(int j = 1; j<100; j++)
				{
					if(join_id[i][j] == 0)
					{
						break;
					}
					else
					{
						remove_ids(join_id[i][j]);
						broadcast_message(client_name + " left the room", join_id[i][j]);
					}
				}
				
				
				break;
			}
		}
		
	}
	
	
	public void leave_chat(String msg) throws IOException
	{
		int ref = Integer.parseInt(msg.substring(17));
		String join = br.readLine();
		int join_ids = Integer.parseInt(join.substring(10));
		String name = br.readLine();
		String clnt_name = name.substring(13);
		
		
		//System.out.println("chat - "+chat_name+"\n"+"client - "+ client_name);
		//System.out.println(msg.substring((msg.indexOf(chat_name)+chat_name.length()+2), (msg.indexOf(chat_name)+chat_name.length()+38)));
		//String reply = null;
		int flag = 0;
		if(join_ids != counter || !clnt_name.equals(client_name))//check if chat room name is given 
		{
			//System.out.println("Condition 1");
			flag++;
		}
		
		else if(!join.equals("JOIN_ID: " + counter) && !name.equals("CLIENT_NAME: " + clnt_name))
		{
			flag++;
		}
		int flag2 = 0;
		
		for(int i = 0;i<100; i ++)
		{
			if(join_id[i][0] == join_ids) 
			{
				for(int j = 1; j < 100; j++)
				{
					if(join_id[i][j] == 0)
					{
						break;
					}
					else if(join_id[i][j] == ref)
					{
						flag2++;
					}					
				}
				break;
			}
		}
		if(flag == 0 && flag2 > 0)
		{
			broadcast_message(client_name + " left the room", ref);
			ps.println("LEFT_CHATROOM:" + ref + "\n" + 
					"JOIN_ID: " + join_ids);
			remove_ids(ref);
		}
		
	}
	
	public void send_message(String msg) throws IOException
	{
		int ref = Integer.parseInt(msg.substring(6));
		String join = br.readLine();
		int join_ids = Integer.parseInt(join.substring(10));
		String name = br.readLine();
		String clnt_name = name.substring(13);
		String mesg = br.readLine();
		String message = mesg.substring(9);//have to work on string terminated with /n/n
		
		//System.out.println("chat - "+chat_name+"\n"+"client - "+ client_name);
		//System.out.println(msg.substring((msg.indexOf(chat_name)+chat_name.length()+2), (msg.indexOf(chat_name)+chat_name.length()+38)));
		//String reply = null;
		int flag = 0;
		if(join_ids != counter || !clnt_name.equals(client_name) || message.equals(null))//check if chat room name is given 
		{
			//System.out.println("Condition 1");
			flag++;
		}
		
		else if(!join.equals("JOIN_ID: " + counter) && !name.equals("CLIENT_NAME: " + clnt_name) && !mesg.equals("MESSAGE: " + message))
		{
			flag++;
		}
		int flag2 = 0;
		
		for(int i = 0;i<100; i ++)
		{
			if(join_id[i][0] == join_ids) 
			{
				for(int j = 1; j < 100; j++)
				{
					if(join_id[i][j] == 0)
					{
						break;
					}
					else if(join_id[i][j] == ref)
					{
						flag2++;
					}					
				}
				break;
			}
		}
		if(flag == 0 && flag2 > 0)
		{
			broadcast_message(message, ref);
			ps.println("CHAT: " + ref + "\n" + 
					"CLIENT_NAME: " + clnt_name + "\n" + 
					"MESSAGE: " + mesg + "\n\n");
		}
		//check for the format of the message - KEEP AN EYE TO TEST - USING TWO \\???
		//confirm a client name
		
	}
	
	public void broadcast_message(String message, int ref)
	{
		
		
		
		for(int i = 0;i<100; i ++)
		{
			if(chat_id[i][0] == ref) // get the room and then send everyone in that room a message
			{
				for(int j = 0; j < 100; j++)
				{
					if(chat_id[i][j] == 0)
					{
						break;
					}
					else
					{
						for(int k = 0; k<100; k++)
						{
							if(threads[k] != null && join_id[k][0] == chat_id[i][j] && threads[k] != this) 
							{
								threads[k].ps.println(message);
								break;
							}
						}
					}
				}
				break;
			}
		}
	}
	public int get_room_ref(String chat_name)
	{
		int ref = 0;
		
		for(int i = 0; i<chat_name.length(); i++)
		{
			
			int x  = chat_name.charAt(i);
			ref = ref*10 + x;
		}
		
		return ref;
	}
	public void remove_ids(int ref)
	{
		
		int i = 0;
		while(chat_id[i][0] != 0 && i < 100)
		{
			if(chat_id[i][0] == room_ref)
			{
				for(int j = 1; j < 100; j++)
				{
					if(chat_id[i][j] == counter)
					{
						for(int k = j; k < 100; k++)
						{
							if(k < 99)
							{
								chat_id[i][k] = chat_id[i][k+1];
								if(chat_id[i][k+1] == 0)
								{
									break;
								}
							}
							else 
							{
								chat_id[i][k] = 0;
							}
						}
						break;
					}

				}
				break;
			}
			i++;
		}
			
		
		for(i = 0; i < 100; i++)
		{
			if(join_id[i][0] == counter)
			{
				
				for(int j = 1; j<100; j++)
				{
					if(join_id[i][j] == ref)
					{
						for(int k = j; k < 100; k++)
						{
							if(k < 99)
							{
								chat_id[i][k] = chat_id[i][k+1];
								if(chat_id[i][k+1] == 0)
								{
									break;
								}
							}
							else 
							{
								chat_id[i][k] = 0;
							}
						}
						break;
					}
				
				}
				break;
			}
		}
	}
	public void assign_ids()
	{
		int flag = 0;
		int i = 0;
		while(chat_id[i][0] != 0 && i < 100)
		{
			if(chat_id[i][0] == room_ref)
			{
				for(int j = 1; j < 100; j++)
				{
					if(chat_id[i][j] == 0)
					{
						chat_id[i][j] = counter;
						break;
					}
				}
				flag++;
				break;
			}
			i++;
		}
		if(flag == 0 && i<99)// in case the chatroom is new
		{
			chat_id[i][0] = room_ref;
			chat_id[i][1] = counter;
		}
		
		for(i = 0; i < 100; i++)
		{
			if(threads[i] == this)
			{
				//because the counter is local var 
				//id is reassigned to the same value while the chatroom is added 
				join_id[i][0] = counter;
				for(int j = 1; j<100; j++)
				{
					if(join_id[i][j] == 0)
					{
						join_id[i][j] = room_ref;
						break;
					}
				}
				//join_id[i][1] = room_ref;
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
			ps.println("Killing Service");
			kill_service();
			socket.close();
			Thread.currentThread().interrupt();
			return;
		}
		else if(msg.equals("HELO text"))
		{
			ps.println("HELO text" + "\n" + "IP:"+InetAddress.getLocalHost().getHostAddress()+"\n" + "Port:7899" + "\n" + "StudentID:16300602" + "\n");
		}
		
		
		else if(msg.startsWith("JOIN_CHATROOM: "))//call the join function and let it handle everything
		{
			//System.out.println("inside join");
			join_chat(msg);
		}
		
		else if(msg.startsWith("CHAT: "))//call the send message function and let it handle everything
		{
			//System.out.println("inside join");
			send_message(msg);
		}
		
		else if(msg.startsWith("LEAVE_CHATROOM: "))//call the leave function and let it handle everything
		{
			//System.out.println("inside join");
			leave_chat(msg);
		}
		
		else
		{
			ps.println("Incorrect Protocol. Try again");
		}	
	}
	
	
	
	public void run()
	{
		try
		{
			is = new DataInputStream(socket.getInputStream());
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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

