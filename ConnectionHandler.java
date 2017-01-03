package weasel;
import java.net.*;
import java.io.*;

import apollo.iface.DataStore;
import java.util.Hashtable;

/**
* The ConnectionHandler:
*	1) gets the socket
*	2) identifies which WebObject should handle it
*	3) runs it
*	4) return results to user
*/
public class ConnectionHandler implements Runnable {
	public final static String SERVER="Weasel";
	private DataStore handle;
	private Socket socket;

	public ConnectionHandler(Socket socket,DataStore ds) {
		this.socket=socket;
		this.handle=ds;
	}

  	public void run() {
    	try {

			//get request.  this could be in another method
	      	// Open connections to the socket
	      	BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));

	      	//to be simple, we want this all in one line with the format
	      	// /path/file.html?a=b&c=d

	      	String line=in.readLine();
	      	//sometimes this will be null. just ignore it
	      	if (line==null) {return;}
	      	System.out.println("DEBUG: "+line);

	      	//look for the word "GET"
	      	String[] request=line.split("\\s");

	      	String method=request[0];
	      	String url=request[1];
	      	String resource=null;
			Hashtable params=new Hashtable();

	      	//1. look for a question mark
	      	int q=url.indexOf('?');
	      	if (q==-1) {
				resource=url;
			} else {
				resource=url.substring(0,q);
				String p=url.substring(q+1);

				//split this into pairs
				String[] pair=p.split("&");
				for (int i=0;i<pair.length;i++) {
					String param=pair[i];

					//now split it into key/value
					int e=param.indexOf('=');
					if (e>-1) {
						String k=param.substring(0,e);
						String v=param.substring(e+1);
						params.put(k,v);
					}

				} //end for
			} //end else

			//now calculate the result
			String response=null;
			if (resource==null || resource.equals("/") || resource.contains("index") || resource.endsWith(".ico")) {
				response=(new Index()).display(handle,resource,params);
			} else {
				//this is crazy but just get the classname
				try {
					if (resource.startsWith("/")) {
						//get rid of initial front slash
						String stuff=resource.substring(1);

						String[] parts=stuff.split("/");
						String classname=parts[0];

						//see if this is a java class
						Class c=Class.forName(classname);
						Object o=c.newInstance();
						if (o instanceof WebObject) {
							WebObject wo=(WebObject)o;
							response=wo.display(handle,resource,params);
						} else {
							throw new Exception("invalid webobject "+classname);
						}
					} else {
						throw new Exception("invalid url "+resource);
					}
				} catch (Exception x) {
					//if we can dynamically load it
					String error=x.getClass().getName()+" :"+x.getMessage();
					BufferedWriter out=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					out.write("HTTP/1.0 500 ERROR\r\n");
					out.write("Server: "+SERVER+"\r\n");
					out.write("Content-Type: text/html\r\n");
					out.write("Content-Length: "+error.length()+"\r\n");
					out.write("\r\n");
					out.write(error);
					out.flush();
					out.close();
				}
			}

			//now return response to user
			BufferedWriter out=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			out.write("HTTP/1.0 200 OK\r\n");
			out.write("Server: "+SERVER+"\r\n");
			out.write("Content-Type: text/html\r\n");
			out.write("Content-Length: "+response.length()+"\r\n");
			out.write("\r\n");
			out.write(response);

			//done
			out.flush();
			in.close();
			out.close();
			socket.close();

		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}