import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;


public class Server extends Thread {

	ServerSocket server=null;
	Socket client=null;
	
	public Server(){
		
	}
	public void run() {
		try {
			server=new ServerSocket(Database.portno);
			while(true){
				client=server.accept();
				BufferedReader br= new BufferedReader(new InputStreamReader(client.getInputStream()));
				String incoming=br.readLine();
				br.close();
				client.close();
				
				System.out.println("incoming messag = "+incoming);
				String[] message=incoming.split(":");
				if(message[0].equals("request")){
					System.out.println("request message rxd");
					try {
						Functions.request_handler(message, incoming);
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
					
				else if(message[0].equals("response")){
					System.out.println("response message rxd");
					Functions.response_handler(message);
				}
				else if(message[0].equals("update")){
					System.out.println("update message rxd");
					Functions.update_handler(message);
				}
				else if(message[0].equals("query")) {
					System.out.println("query message rxd");
					Functions.query_handler(message, incoming);
				}
				else if(message[0].equals("queryhit")) {
					System.out.println("query hit message rxd");
					Functions.queryhit_handler(message);
				}
				else if(message[0].equals("movekeys")){
					System.out.println("movekeys message rxd");
					Functions.movekeys_handler(message);
				}
				else if(message[0].equals("insert")){
					System.out.println("insert message rxd");
					Functions.insert_handler(message, incoming);
				}
				else System.out.println("invalid message rxd");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("unable to create server socket");
		}
	}
}
