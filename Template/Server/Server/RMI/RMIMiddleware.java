package Server.RMI;

import Server.Interface.*;
import Server.Common.*;

import java.rmi.NotBoundException;
import java.util.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * 1)
 * On startup it should be given the list of ResourceManagers 
 * and implements the same interface as the ResourceManager
 * 
 * 2)
 * Create ResourceManagers for each type of resource (one for each of flights, cars,
 * and rooms). For simplicity, you can keep only one implementation providing the
 * entire interface – the Middleware will only call the flight-methods on the flightResourceManager
 * 
 * 3)
 * Decide how to handle customers – either with an additional server, through replication
 * at the ResourceManagers, or at the Middleware (which then becomes a type of
 * ResourceManager)
 * 
 * 4)
 * Add an additional function bundle to the Middleware which reserves a set of flights,
 * and possibly a car and/or room at the final destination
 */

/**
 * - the middleware object will implement the interface, for each method it will identify which
 *      resource manager handles the request, then forward the request to that resource manager.
 *      Each resource manager has its own data (stored in hashmap), the list of resource managers is
 *      stored in the middleware object.
 * 
 * 
 * - The middleware object is the main server object that is binded to the rmi registry for all the clients to communicate with.
 */


public class RMIMiddleware extends ResourceManager {

    private static final int s_serverPort = 1095;
    private static String s_serverName = "Server";
	private static String s_rmiPrefix = "group_49_";
	private static String s_serverHost = "localhost";
	
	// Resource Managers
	private static Vector<String> serverNames;
	private static HashMap<String, IResourceManager> resource_managers_hash;
    
    public static void main(String args[])
	{
		// Retrieve the resource manager server names from command line arguments
		if (args.length == 3)
		{
			serverNames.add(args[0]);
			System.out.println("" + args[0]);
			serverNames.add(args[1]);
			System.out.println("" + args[1]);
			serverNames.add(args[2]);
			System.out.println("" + args[2]);
		}
		else
		{
			System.err.println((char)27 + "need 3 arguments exactly");
			System.exit(1);
		}

		
		// Create the RMI server entry 
		try {
			// Create a new Server object
			RMIMiddleware server = new RMIMiddleware(s_serverName);

			// Get references to RMI Resource Manager servers (i.e. Flights, Cars, Rooms)
			server.connectToResourceManagers(serverNames);
			

			// Dynamically generate the stub (client proxy)
			IResourceManager resourceManager = (IResourceManager)UnicastRemoteObject.exportObject(server, 0);

			// Bind the remote object's stub in the registry
			Registry l_registry;
			try {
				l_registry = LocateRegistry.createRegistry(s_serverPort);
			} catch (RemoteException e) {
				l_registry = LocateRegistry.getRegistry(s_serverPort);
			}
			final Registry registry = l_registry;
			registry.rebind(s_rmiPrefix + s_serverName, resourceManager);

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						registry.unbind(s_rmiPrefix + s_serverName);
						System.out.println("'" + s_serverName + "' resource manager unbound");
					}
					catch(Exception e) {
						System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
						e.printStackTrace();
					}
				}
			});                                       
			System.out.println("'" + s_serverName + "' resource manager server ready and bound to '" + s_rmiPrefix + s_serverName + "'");
		}
		catch (Exception e) {
			System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}

		// Create and install a security manager
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new SecurityManager());
		}
	}
	
	// Loop through list of resource manager server names, retrieve their references and add to hash map
	public void connectToResourceManagers(Vector<String> resourceManagerNames)
	{
		for(String resourceManagerName : resourceManagerNames) {
			connectToResourceManager(s_serverHost, s_serverPort, resourceManagerName);
		}
	}

	// Get references to RMI Resource Manager servers (i.e. Flights, Cars, Rooms)
	public void connectToResourceManager(String server, int port, String name) {
		try {
			boolean first = true;
			while (true) {
				try {
					Registry registry = LocateRegistry.getRegistry(server, port);
					IResourceManager rm_temp = (IResourceManager)registry.lookup(s_rmiPrefix + name);
					resource_managers_hash.put(name, rm_temp);
					System.out.println("Connected to '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix + name + "]");
					break;
				}
				catch (NotBoundException|RemoteException e) {
					if (first) {
						System.out.println("Waiting for '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix + name + "]");
						first = false;
					}
				}
				Thread.sleep(500);
			}
		}
		catch (Exception e) {
			System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public RMIMiddleware(String name)
	{
		super(name);
	}
    
}
