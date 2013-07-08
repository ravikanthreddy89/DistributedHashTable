import java.security.NoSuchAlgorithmException;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.text.html.HTMLDocument.Iterator;


public class CommandHandler extends Thread {
	public void run() {
		Scanner input= new Scanner(System.in);
		String cmd=null;
		System.out.println("enter the commands.........");
		
		while(true){
			cmd=input.nextLine();
		
			// join procedure..........................
			if(cmd.equals("join")){
				Message request= new Message();
				Functions.forward(request.request_create(), 11108);
				continue;
			}
			
			else if(cmd.equals("query")){
				String key=input.nextLine();
				Message m= new Message();
				Functions.forward(m.query_create(key), Database.portno);
			}
			else if(cmd.equals("hashtable size")){
				System.out.println("the size of hash table is "+Database.KeyValues.size());
			}
			else if(cmd.equals("ports")){
				System.out.println("Predecessor port = "+Database.pred_portno);
				System.out.println("Succecessor port = "+Database.succ_portno);
				
			}
			// insert procedure............................
			else if(cmd.equals("insert")){
				/*System.out.println("enter the key/file name");
				String key=null;
				try {
					key = Functions.genHash(input.nextLine());
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("enter the value :");
				String value=null;
				try {
					value = Functions.genHash(input.nextLine());
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message m= new Message();
				Functions.forward(m.insert_create(key, value), Database.portno);*/
				
				for(int i=0; i< 50 ; i++ ){
					String key= "key"+i;
					String value= "value"+i;
					
					Message m= new Message();
					Functions.forward(m.insert_create(key, value), Database.portno);
				}
				continue;
			}
			
			// ldump procedure.............................
			else if(cmd.equals("ldump")){
				if(Database.KeyValues.size()==0) System.out.println("local key:value store is empty");
				else {
					java.util.Iterator<String> it = Database.KeyValues.keySet().iterator();
					
					while(it.hasNext()){
						String key= it.next();
						System.out.println("Key : "+key);
						String value= Database.KeyValues.get(key);
						System.out.println("Value :"+value);
						System.out.println("==============================================");
						
					}
				   }
				continue;
				}
		    // gdump procedure................................
			else if(cmd.equals("gdump")){
				Message m= new Message();
				Functions.forward(m.query_create("####"), Database.succ_portno);
				continue;
			}
	     }// end of while(true) loop
	}// end of void run() method..................
}// end of command handler class
