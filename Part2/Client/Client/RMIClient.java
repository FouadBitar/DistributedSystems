package Client;

import Server.Interface.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;

import java.util.*;
import java.io.*;

public class RMIClient extends Client
{
	private static String s_serverHost = "localhost";
	private static int s_serverPort = 1095;
	private static String s_serverName = "Server";
	private static String s_rmiPrefix = "group_49_";

	public static void main(String args[])
	{	
		if (args.length > 0)
		{
			s_serverHost = args[0];
		}
		if (args.length > 1)
		{
			s_serverName = args[1];
		}
		if (args.length > 2)
		{
			System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUsage: java client.RMIClient [server_hostname [server_rmiobject]]");
			System.exit(1);
		}

		// Set the security policy
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new SecurityManager());
		}

		// Get a reference to the RMIRegister
		try {
			RMIClient client = new RMIClient();
			client.start();
		} 
		catch (Exception e) {    
			System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}

	}

	public RMIClient()
	{
		super();
	}

	public void connectServer()
	{
		
	}

}

