
import java.io.*;
import java.net.*;
public class temp_client
{
	public static void main(String args[])throws IOException
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String name = args[0];
			Socket client_s = new Socket("localhost", 7899);
			PrintWriter pr = new PrintWriter(client_s.getOutputStream(), true );
			BufferedReader in = new BufferedReader(new InputStreamReader(client_s.getInputStream()));
			while(true)
			{
				String s = br.readLine();
				pr.println(name+": "+s);
				String st = in.readLine();
				System.out.println(st);
				//in.close();
			}
		}
}
