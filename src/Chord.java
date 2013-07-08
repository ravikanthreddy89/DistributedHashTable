import java.security.NoSuchAlgorithmException;


public class Chord {

	public static void main(String args[]){
		
		// if this node is bootstrap node..............
		if(Integer.parseInt(args[0])==11108){
			Database.portno=11108;
			Database.succ_portno=11108;
			Database.pred_portno=11108;
			try {
				Database.id=Functions.genHash(Integer.toString(Database.portno));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("error in id hash generation !!!!");
			}
			
			try {
				Database.succ=Functions.genHash(Integer.toString(Database.succ_portno));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("error in succ id hash  generation.......dude!!!!");
			}
			
			
			try {
				Database.pred=Functions.genHash(Integer.toString(Database.pred_portno));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("error in pred id hash generation.......dude!!!!");
			}
		}
		
		// for other nodes..............
		else
			{
			Database.portno=Integer.parseInt(args[0]);
			try {
				Database.id=Functions.genHash(Integer.toString(Database.portno));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("error in id generation.......dude!!!!");
			}
			}
		
		
		new Server().start();
		new CommandHandler().start();
		
	}
}
