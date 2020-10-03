// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package Server.RMI;

import Server.Interface.*;
import Server.Common.*;

import java.rmi.NotBoundException;
import java.util.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.PrintWriter;


public class RMIResourceManager extends ResourceManager 
{
	private static String s_serverName = "Server";
	private static String s_rmiPrefix = "group_49_";

	

	public static void main(String args[])
	{

		// create the resource manager object that this server is in charge of 
		RMIResourceManager server = new RMIResourceManager("");

		// start the server
		try
		{
			//comment this line and uncomment the next one to run in multiple threads.
			// server.runServer();
			runServerThread(server);
		}
		catch (IOException e)
		{ }

	}



	public Vector<String> convertMessageToVector(String m) {
		Vector<String> messVector = new Vector<String>();
		String subMess = m.substring(1, m.length()-1);
		String[] delinMess = subMess.split(",");
		for(String s : delinMess) {
			messVector.add(s.trim());
		}
		return messVector;
	}

	public static void runServerThread(RMIResourceManager server) throws IOException
	{
		ServerSocket serverSocket = new ServerSocket(9090);
		System.out.println("Server ready...");
		while (true)
		{
			Socket socket=serverSocket.accept();
			server.new serverSocketThread(socket, server).start();
		}
	}

	public class serverSocketThread extends Thread 
	{
		Socket socket;
		RMIResourceManager server;
		
		public serverSocketThread (Socket socket, RMIResourceManager server) {
			this.socket = socket;
			this.server = server;
		}

		public serverSocketThread (Socket socket) {
			this.socket = socket;
		}

		public void run()
		{
			try
			{
				BufferedReader inFromMiddleware= new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter outToMiddleware = new PrintWriter(socket.getOutputStream(), true);
				String message = null;
				while ((message = inFromMiddleware.readLine())!=null) {
					// switch over the first argument and locate the correct method
					// then convert the values to the right types
					// then call the method on the server's RM object
					// then send back the result to the middleware
					Vector<String> messVector = convertMessageToVector(message);  
					for(String s : messVector) {
						System.out.println(s);
					}
					String cmnd = messVector.elementAt(0);
					// switch over the first argument and call on the correct server with the same string
					if(cmnd.equalsIgnoreCase(Command.AddFlight.name())) {
						int id = toInt(messVector.elementAt(1));
						int flightNum = toInt(messVector.elementAt(2));
						int flightSeats = toInt(messVector.elementAt(3));
						int flightPrice = toInt(messVector.elementAt(4));
						int result = server.addFlight(id, flightNum, flightSeats, flightPrice) ? 1 : 0;
						outToMiddleware.println(result);
						System.out.println("finished at flights rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.AddCars.name())) {
						int id = toInt(messVector.elementAt(1));
						String location = messVector.elementAt(2);
						int numCars = toInt(messVector.elementAt(3));
						int price = toInt(messVector.elementAt(4));
						int result = server.addCars(id, location, numCars, price) ? 1 : 0;
						outToMiddleware.println(result);
						System.out.println("finished at cars rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.AddRooms.name())) {
						int id = toInt(messVector.elementAt(1));
						String location = messVector.elementAt(2);
						int numRooms = toInt(messVector.elementAt(3));
						int price = toInt(messVector.elementAt(4));
						int result = server.addRooms(id, location, numRooms, price) ? 1 : 0;
						outToMiddleware.println(result);
						System.out.println("finished at rooms rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.AddCustomer.name())) {
						int id = toInt(messVector.elementAt(1));
						int result = server.newCustomer(id);
						outToMiddleware.println(result);
						System.out.println("finished at rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.AddCustomerID.name())) {
						int id = toInt(messVector.elementAt(1));
						int customerID = toInt(messVector.elementAt(2));
						int result = server.newCustomer(id, customerID) ? 1 : 0;
						outToMiddleware.println(result);
						System.out.println("finished at rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.DeleteCustomer.name())) {
						int id = toInt(messVector.elementAt(1));
						int customerID = toInt(messVector.elementAt(2));
						int result = server.deleteCustomer(id, customerID) ? 1 : 0;
						outToMiddleware.println(result);
						System.out.println("finished at rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.DeleteFlight.name())) {
						int id = toInt(messVector.elementAt(1));
						int flightNum = toInt(messVector.elementAt(2));
						int result = server.deleteFlight(id, flightNum) ? 1 : 0;
						outToMiddleware.println(result);
						System.out.println("finished at flights rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.DeleteCars.name())) {
						int id = toInt(messVector.elementAt(1));
						String location = messVector.elementAt(2);
						int result = server.deleteCars(id, location) ? 1 : 0;
						outToMiddleware.println(result);
						System.out.println("finished at cars rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.DeleteRooms.name())) {
						int id = toInt(messVector.elementAt(1));
						String location = messVector.elementAt(2);
						int result = server.deleteRooms(id, location) ? 1 : 0;
						outToMiddleware.println(result);
						System.out.println("finished at rooms rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.QueryFlight.name())) {
						int id = toInt(messVector.elementAt(1));
						int flightNum = toInt(messVector.elementAt(2));
						int result = server.queryFlight(id, flightNum);
						outToMiddleware.println(result);
						System.out.println("finished at flights rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.QueryCars.name())) {
						int id = toInt(messVector.elementAt(1));
						String location = messVector.elementAt(2);
						int result = server.queryCars(id, location);
						outToMiddleware.println(result);
						System.out.println("finished at cars rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.QueryRooms.name())) {
						int id = toInt(messVector.elementAt(1));
						String location = messVector.elementAt(2);
						int result = server.queryRooms(id, location);
						outToMiddleware.println(result);
						System.out.println("finished at rooms rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.QueryFlightPrice.name())) {
						int id = toInt(messVector.elementAt(1));
						int flightNum = toInt(messVector.elementAt(2));
						int result = server.queryFlightPrice(id, flightNum);
						outToMiddleware.println(result);
						System.out.println("finished at flights rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.QueryCarsPrice.name())) {
						int id = toInt(messVector.elementAt(1));
						String location = messVector.elementAt(2);
						int result = server.queryCarsPrice(id, location);
						outToMiddleware.println(result);
						System.out.println("finished at cars rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.QueryRoomsPrice.name())) {
						int id = toInt(messVector.elementAt(1));
						String location = messVector.elementAt(2);
						int result = server.queryRoomsPrice(id, location);
						outToMiddleware.println(result);
						System.out.println("finished at rooms rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.QueryCustomer.name())) {
						int id = toInt(messVector.elementAt(1));
						int customerID = toInt(messVector.elementAt(2));
						String result = server.queryCustomerInfo(id, customerID);
						System.out.println(result);

						// error occuring here where multiline string needs to be sent to the middleware
						outToMiddleware.println(result);
						outToMiddleware.flush();
						System.out.println("finished at rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.ReserveFlight.name())) {
						int id = toInt(messVector.elementAt(1));
						int customerID = toInt(messVector.elementAt(2));
						int flightNum = toInt(messVector.elementAt(3));
						int result = server.reserveFlight(id, customerID, flightNum) ? 1 : 0;
						outToMiddleware.println(result);
						System.out.println("finished at flights rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.ReserveCar.name())) {
						int id = toInt(messVector.elementAt(1));
						int customerID = toInt(messVector.elementAt(2));
						String location = messVector.elementAt(3);
						int result = server.reserveCar(id, customerID, location) ? 1 : 0;
						outToMiddleware.println(result);
						System.out.println("finished at cars rm");
					}
					else if(cmnd.equalsIgnoreCase(Command.ReserveRoom.name())) {
						int id = toInt(messVector.elementAt(1));
						int customerID = toInt(messVector.elementAt(2));
						String location = messVector.elementAt(3);
						int result = server.reserveRoom(id, customerID, location) ? 1 : 0;
						outToMiddleware.println(result);
						System.out.println("finished at rooms rm");
					}
					

				}
				socket.close();
			}
			catch (IOException e) {}
		}
	}

	public static int toInt(String string) throws NumberFormatException
	{
		return (Integer.valueOf(string)).intValue();
	}




	public RMIResourceManager(String name)
	{
		super(name);
	}
}
