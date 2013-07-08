import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;


public class Database {

	public static int portno;
	public static int pred_portno;
	public static int succ_portno;
	
	
	public static String id;
	public static String pred=null;
	public static String succ=null;
	
		
	public static java.util.Hashtable<String , String> KeyValues= new java.util.Hashtable<String , String >();
	
}