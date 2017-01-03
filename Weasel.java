package weasel;
import java.net.*;
import java.io.*;
import apollo.iface.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
* Weasel is a sly little webserver.  I call it Weasel because it sounds kind of like WebServer.
* This is intended for a little intranet.  It wraps around the Apollo engine.
* It is designed to be read-only, to display reports.  If you need to update the data,
* use another program, like Vos.
*
* This is NOT a file-server, unlike 99.9% of the webservers out there.  It runs objects
* that output HTML.
*/

public class Weasel implements Runnable {
	public final static int PORT=18080;
	public final static int POOL_SIZE=2;
	private static Factory factory;
	private ServerSocket serverSocket;
	private DataStore apollo;

	public Weasel(DataStore apollo) throws IOException {
		serverSocket=new ServerSocket(PORT);
		factory=new Factory(POOL_SIZE);
		System.out.println("Weasel listening on port "+PORT+" with "+POOL_SIZE+" threads working");
		this.apollo=apollo;
	}

	public void run() {
		try {
			while (true) {
				Socket socket=serverSocket.accept();

				//create a ConnectionHandler to manage it
				ConnectionHandler handler=new ConnectionHandler(socket,apollo);
				//put it on the queue
				factory.execute(handler);
				//let the client close the socket
				//sock.close();
			}
		} catch (IOException x) {
			System.out.println("Server stopped");
		}
	}


	//======================================
	public static void main(String[] args) throws IOException {
		//get the datastore object
		//this depends on the Apollo server running
		String host="localhost";
		if (args!=null && args.length>0) {
			host=args[0];
		}
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            DataStore ds = (DataStore) registry.lookup("DataStore");

			//now start up weasel
			Weasel w=new Weasel(ds);
			new Thread(w).start();
        } catch (Exception e) {
            System.err.println("exception: " + e.toString());
        }
	}
}