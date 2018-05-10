import com.google.gson.JsonParser;
import java.net.*;
import java.io.*;

public class ParkingServerThread extends Thread {

	
  private Socket parkingSocket = null;
  private SharedParkingState sharedParkingStateObject;
  private String parkingServerThreadName;
   
  //Setup the thread
  	public ParkingServerThread(Socket parkingSocket, SharedParkingState sharedObject) {
	
//	  super(ParkingServerThreadName);
	  this.parkingSocket = parkingSocket;
	  sharedParkingStateObject = sharedObject;
	}

  public void run() {
    try {
      PrintWriter out = new PrintWriter(parkingSocket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(parkingSocket.getInputStream()));
      String inputLine, outputLine;

      while ((inputLine = in.readLine()) != null) {
          if (parkingServerThreadName==null)
          {
              //Assign the thread a name based on the client ID
              parkingServerThreadName = (new JsonParser()).parse(inputLine).getAsJsonObject().get("ClientId").toString();
              this.setName(parkingServerThreadName);
          }

    	  // Get a lock before processing the request then release the lock after processing
    	  try {

              if (this.getName().contains("Entrance")) {
                  sharedParkingStateObject.checkForFreeSpot();
              }
              else {
                  sharedParkingStateObject.acquireParkingCounterLock();
              }
              outputLine = sharedParkingStateObject.processInput(inputLine);
    		  out.println(outputLine);
    		  sharedParkingStateObject.releaseParkingCounterLock();
    	  }
    	  catch(InterruptedException e) {
    		  System.err.println("Failed to get lock when reading:"+e);
    	  }
      }

       out.close();
       in.close();
       parkingSocket.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}