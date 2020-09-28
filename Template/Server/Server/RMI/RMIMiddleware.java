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


public class RMIMiddleware implements IResourceManager {

    private static final int PORT = 1095;
    private static String s_serverName = "Server";
    private static String s_rmiPrefix = "group_49_";
    
    public static void main(String args[])
	{
		if (args.length > 0)
		{
			s_serverName = args[0];
		}
			
		// Create the RMI server entry 
		try {
            // Create the resource managers for flights, rooms, and cars
            



			// Create a new Server object
			RMIMiddleware server = new RMIMiddleware();

			// Dynamically generate the stub (client proxy)
			IResourceManager resourceManager = (IResourceManager)UnicastRemoteObject.exportObject(server, 0);

			// Bind the remote object's stub in the registry
			Registry l_registry;
			try {
				l_registry = LocateRegistry.createRegistry(PORT);
			} catch (RemoteException e) {
				l_registry = LocateRegistry.getRegistry(PORT);
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
    
}
