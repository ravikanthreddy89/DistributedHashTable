import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Map.Entry;

import javax.swing.text.html.HTMLDocument.Iterator;


public class Message {

	
	String request;
	String response;
	String update;
	String query;
	String queryhit;
	String movekeys;
	String insert;
	
	public String request_create() {
		 request= "request"+":"+Integer.toString(Database.portno);
		return request;
	}
	
	public String response_create() {
		response= "response"+":"+ Integer.toString(Database.portno)+":"+Integer.toString(Database.succ_portno);
		//response= Functions.arr2str(res);
		return response;
		
	}
	
	public String update_create(int i){
		update= "update"+":"+ Integer.toString(i);
		//update=Functions.arr2str(upd);
		return update;
	}
	
	public String query_create(String query_string) {
		query= "query"+":"+Integer.toString(Database.portno)+":"+query_string ;
		//query=Functions.arr2str(qry);
		return query;
	}

	public String queryhit_create(String query_string) {
		
		queryhit= "queryhit";
		//send all the values stored in local database..........
		if(query_string.equals("####")){
		 java.util.Iterator<String> itr= Database.KeyValues.keySet().iterator();

		 while(itr.hasNext()){
			 queryhit=queryhit+":"+Database.KeyValues.get(itr.next());
		 }
		}
		//send only the hit value...........
		
		else {
		 
			System.out.println("case when it is single query...");
			String k=null;
			try {
				k = Functions.genHash(query_string);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("dude error in queryhit_creator for one key");
			}
			queryhit= queryhit+":"+Database.KeyValues.get(k);
		}
		System.out.println("query hit is : "+ queryhit);
		return queryhit;
	
	}

	public String movekeys_create() {
		System.out.println("movekeys create invoked........");
		
		movekeys="movekeys";
		String pred_id=Database.pred;
		
		int hits=0;
		java.util.Iterator<String> it = Database.KeyValues.keySet().iterator();
		while(it.hasNext()){
			String key=it.next();
			if(Functions.isLessThan(key, pred_id)) {
			      hits++;	
				}
		}// end of while loop........
 
		if(hits!=0){
			java.util.Iterator<String> it2 = Database.KeyValues.keySet().iterator();
			while(it2.hasNext()){
				String key=it2.next();
				if(Functions.isLessThan(key, pred_id)) {
				 movekeys=movekeys+":"+key+"---"+Database.KeyValues.get(key);
				// Database.KeyValues.remove(key);
				 
				}
				}
		
			Enumeration<String> keys= Database.KeyValues.keys();
			
			while(keys.hasMoreElements()){
				String key=keys.nextElement();
				if(Functions.isLessThan(key, pred_id)) {
				 //movekeys=movekeys+":"+key+"---"+Database.KeyValues.get(key);
				 Database.KeyValues.remove(key);
			}
			
        		}
		}
		else movekeys=movekeys+":"+"####";
			
		return movekeys;
	}
	
	public String insert_create(String key, String value){
		insert= "insert"+":"+ key+":"+ value;
		//insert=Functions.arr2str(insrt);
		return insert;
		
	}

}
