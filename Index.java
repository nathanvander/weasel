package weasel;
import apollo.iface.*;
import java.util.*;

/**
* This is kind of a replacement for Servlet.  But we don't want it doing clever stuff.
* All it does is get the request, get the data from the database, format it in html
* and return it.
*
* This is kind of like a function - it calculate the results.  It could update the database
* but it is supposed to be read-only
*/
public class Index implements WebObject {

	//default index page
	public String display(DataStore ds, String resource,Hashtable params) {
		StringBuffer sb=new StringBuffer("<html><b>Weasel Index Page</b><br>");
		sb.append("You requested "+resource+"<br>");
		if (params!=null) {
			sb.append("params:<br>");
			Enumeration e=params.keys();
			while (e.hasMoreElements()) {
				String k=(String)e.nextElement();
				String v=(String)params.get(k);
				sb.append(k+" = "+v+"<br>");
			}
		}
		sb.append("</html>");
		return sb.toString();
	}
}