import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import javax.xml.crypto.Data;



public class Functions {

	static String genHash(String input) throws NoSuchAlgorithmException {
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		byte[] sha1Hash = sha1.digest(input.getBytes());
		Formatter formatter = new Formatter();
		for (byte b : sha1Hash) {
		formatter.format("%02x", b);
		}
		return formatter.toString();
		}
	
		static byte[] int2byte(int i){
			byte[] result= new byte[2];
			result[0]= (byte)((i>>8)&255);
			result[1]=(byte)((i)&255);
			return result;
		}
		
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		                           /////////// request handler////////////////////////
		
		
		static void request_handler(String[] incoming, String incoming_message) throws NoSuchAlgorithmException{
			
			
			System.out.println("request handler invoked......");
			System.out.println("message type :"+incoming[0]);
			System.out.println("remote portno :"+incoming[1]);
			
			int remote_portno=Integer.parseInt(incoming[1]);			
			String incoming_id= Functions.genHash(Integer.toString(remote_portno));
			
		
			// case when there is only one node and a new node joins...........
			if(Database.id.equals(Functions.genHash(Integer.toString(11108))) && Database.succ.equals(Database.id)){
				System.out.println("case when there is only one node");
				Message response= new Message();
				forward(response.response_create(), remote_portno);
				System.out.println("forwarding response message.......");
				Message update= new Message();
				forward(update.update_create(remote_portno), Database.succ_portno);
				System.out.println("forwarding update message......");
				
				// added because update message wont update the predecessor port no in the 11108 node
				Database.succ_portno=remote_portno;
				Database.succ=Functions.genHash(Integer.toString(remote_portno));
				Database.pred_portno=remote_portno;
				Database.pred=Functions.genHash(Integer.toString(remote_portno));
				
			}
		
			// if you are last node...............
			else if(isLessThan(Database.succ,Database.id)){

				System.out.println("if you are last noed..........");
				if(isLessThan(incoming_id, Database.id)){
					if(isLessThan(incoming_id, Database.succ)){
						Message response = new Message();
						forward(response.response_create(), remote_portno);
						Message update= new Message();
						forward(update.update_create(remote_portno), Database.succ_portno);
						Database.succ_portno=remote_portno;
						Database.succ=Functions.genHash(Integer.toString(remote_portno));
					}
					else forward(incoming_message, Database.pred_portno);
				}
				else {
					Message response = new Message();
					forward(response.response_create(), remote_portno);
					Message update= new Message();
					forward(update.update_create(remote_portno), Database.succ_portno);
					
					Database.succ_portno=remote_portno;
					Database.succ=Functions.genHash(Integer.toString(remote_portno));
				}
			}
			// end of last node handling...........................
			
			// if you are not last node ...........
			else {
				
				System.out.println("if you are not last node.....");
				if(isLessThan(incoming_id, Database.id)) forward(incoming_message, Database.pred_portno);
				else if(isGreaterThan(incoming_id, Database.succ)) forward(incoming_message, Database.succ_portno);
				else if(isLessThan(Database.id, incoming_id) && isGreaterThan(Database.succ, incoming_id)){
					Message response = new Message();
					forward(response.response_create(), remote_portno);
					Message update= new Message();
					forward(update.update_create(remote_portno), Database.succ_portno);
					
					Database.succ_portno=remote_portno;
					Database.succ=Functions.genHash(Integer.toString(remote_portno));
				}
			}
		
			}
			
			
			
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////               response handler.                   /////////////////////////////////
		static void response_handler(String[] incoming){
			System.out.println("response handler invoked.........");
			int pred_port;
			int succ_port;
			
			pred_port=Integer.parseInt(incoming[1]);
			succ_port=Integer.parseInt(incoming[2]);
			
			Database.pred_portno=pred_port;
			System.out.println("predecessor updated");
			Database.succ_portno=succ_port;
			System.out.println("succesor updated");
			
			try {
				Database.pred=genHash(Integer.toString(Database.pred_portno));
				Database.succ=genHash(Integer.toString(Database.succ_portno));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("error in response message handling....");
			}
			
		}
				
		static void update_handler(String[] incoming){
			System.out.println("update handler invoked..........");
			int pred_port;
			
			pred_port=Integer.parseInt(incoming[1]);
			
			Database.pred_portno=pred_port;
			System.out.println("predecessor updated..........");
			try {
				Database.pred=genHash(Integer.toString(Database.pred_portno));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("error in update message handler.......");
			}
			Message m= new Message();
			forward(m.movekeys_create(), pred_port);
			
		}
		
		
		
		static void query_handler(String[] incoming,String incoming_message){
			System.out.println("query handler invoked............");
			int remote_portno=0;
			remote_portno=Integer.parseInt(incoming[1]);
			
			String querystring=null;
			
			//case when there it is gdump message......
			
			if(incoming[2].equals("####")){
				
				querystring=incoming[2];
				//if you are not the one who queried......
				
				if(remote_portno != Database.portno){
					
					Message m= new Message();
					forward(m.queryhit_create(querystring), remote_portno);
					forward(incoming_message, Database.succ_portno);
				}
				// you are the one who queried........
				else{
				
					if(Database.KeyValues.size()!=0){
						java.util.Iterator<String> it = Database.KeyValues.keySet().iterator();
						
						while(it.hasNext()){
							String key= it.next();
							System.out.println("Key : "+key);
							String value= Database.KeyValues.get(key);
							System.out.println("Value :"+value);
							System.out.println("==============================================");
							
						}// end of while loop..
					}// end o fif case

				}
				
			}// end of gdump message handling........ 
			
			//case when it is single query message................
			else {
				querystring= incoming[2];
				String key=null;
				try {
					key=Functions.genHash(querystring);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("some error in single query handling........");
				}
				
			if(Database.KeyValues.containsKey(key)) {
				Message m= new Message();
				Functions.forward(m.queryhit_create(incoming[2]), remote_portno);
			}else{
			    Functions.forward(incoming_message, Database.succ_portno);	
			}
			}// end of single query handling...............
			
		}// end of query handler.......
		
		
		
		static void queryhit_handler(String[] incoming){
			System.out.println("query hit invoked...........");
			int hits=0;
			hits=(incoming.length-1);
			
			System.out.println("hits in the query hit are :"+hits);
			
			for(int i=1; i<=hits; i++){
				String[] key_value= incoming[i].split("\\*");
				String key=key_value[0];
				String value=key_value[1];
				System.out.println("key : "+key );
				System.out.println("value"+value);
				System.out.println("=========================================");
			}
			
		}
				
		static void movekeys_handler(String[] incoming){
			System.out.println("move keys handler invoked............");
			if(incoming[1].equals("####")){
				
			}else {
			
				int hits=(incoming.length-1);
				for(int i=1; i<=hits; i++){
					String[] pair=incoming[i].split("---");
					Database.KeyValues.put(pair[0], pair[1]);
				}
			}
			
			
			
		}
				
		static void insert_handler(String [] incoming, String incoming_message){
			System.out.println("insert handler invoked..............");
			
			String key=null;
			try {
				key = Functions.genHash(incoming[1]);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("error in the insert handler hash generation..........");
			}
			
			String value=incoming[1]+"*"+incoming[2];
			
			
			//case when there is only one node............
			if((Database.portno==11108) && Database.id.equals(Database.succ)){
				Database.KeyValues.put(key, value);
				System.out.println("stored in the local database......");
			} 
			//if you are first node ........
			else if(isLessThan(Database.id, Database.pred)){
				if(isLessThan(key, Database.id)){
					Database.KeyValues.put(key, value);
					System.out.println("stored in the local database......");
				}
				else {
					if(isGreaterThan(key, Database.pred)){
						Database.KeyValues.put(key, value);
						System.out.println("stored in the local database......");
					}
					else forward(incoming_message, Database.succ_portno);
				}
			}
			
			// if you are not the first node.........
			else {
				if(isLessThan(key, Database.id)){
					if(isGreaterThan(key, Database.pred)){
						Database.KeyValues.put(key, value);
						System.out.println("stored in the local database......");
					}
					else forward(incoming_message, Database.pred_portno);
				}
				else {
					forward(incoming_message, Database.succ_portno);
				}
			}
					
		}
			
		
		// helper functions.........................................
		static boolean isLessThan(String a, String b){
			return a.compareTo(b)<0;
		};
		
		static void forward(String  incoming, int remote_port){
			Socket sock=null;
			try {
				sock= new Socket("localhost", remote_port);
				DataOutputStream dos= new DataOutputStream(sock.getOutputStream());
				byte[] outbytes= incoming.getBytes();
				
				//PrintWriter pw= new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
				dos.write(outbytes);
				dos.close();
				sock.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("dude there is an error in forward() method........");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("dude there is an error in forward() method........");
			}
			
		}
		
		static boolean isGreaterThan(String a, String b){
			return a.compareTo(b)>0;
		}
		
		static String arr2str(String [] incoming){
			int length= incoming.length;
			
			String out="#";
			
			for(int i=0; i<length ; i++){
				out= out+":"+incoming[i];
			}
			return out;
		}
}
