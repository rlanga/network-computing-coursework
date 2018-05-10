import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class ParkingServer {
  public static void main(String[] args) throws IOException {

	ServerSocket ParkingServerSocket = null;
    boolean listening = true;
    String ParkingServerName = "Parking Server";
    int ParkingServerNumber = 4545;
    Map<String, Integer> parkingSpotCounters = new HashMap<>();
    parkingSpotCounters.put("0", 20);
    parkingSpotCounters.put("1", 20);

    //Create the shared object in the global scope...
    SharedParkingState parkingStateObject = new SharedParkingState(parkingSpotCounters);
        
    // Make the server socket
    try {
      ParkingServerSocket = new ServerSocket(ParkingServerNumber);
    } catch (IOException e) {
      System.err.println("Could not start " + ParkingServerName + " specified port.");
      System.exit(-1);
    }
    System.out.println(ParkingServerName + " started");
    
    while (listening){
      Socket client = ParkingServerSocket.accept();
      new ParkingServerThread(client, parkingStateObject).start();
      System.out.println("New " + ParkingServerName + " thread started.");
    }
    ParkingServerSocket.close();
  }
}