package weasel;
import apollo.iface.*;
import java.util.Hashtable;

/**
* This is kind of a replacement for Servlet.  But we don't want it doing clever stuff.
* All it does is get the request, get the data from the database, format it in html
* and return it.
*
* This is kind of like a function - it calculate the results.  It could update the database
* but it is supposed to be read-only
*/
public interface WebObject {

	//the output is in text/html format
	//the resource is the path to the filename requested
	//the hashtable will have the params in it
	public String display(DataStore ds, String resource,Hashtable params);
}