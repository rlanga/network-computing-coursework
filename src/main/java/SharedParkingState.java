import java.util.Map;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SharedParkingState{

	private Map<String, Integer> parkingSpotCounters;
	public ReentrantLock lock = new ReentrantLock(true); //initialise the reentrant lock with queuing fairness enabled
	private Condition isThereSpace = lock.newCondition();
	private boolean freeSpot = true;

// Constructor
	SharedParkingState(Map<String, Integer> parkingCounters) {
		parkingSpotCounters = parkingCounters;
	}

	  //Checks if there is a free spot before acquiring a lock on the counter
	  public void checkForFreeSpot() throws InterruptedException {
	        Thread me = Thread.currentThread(); // get a ref to the current thread

			System.out.println(me.getName()+" is attempting to acquire a lock!");
			lock.lock();
			while (!freeSpot){ //while loop prevents spurious wake-ups
				System.out.println(me.getName() + " wanted a spot but there are no spots at all available...");
				isThereSpace.await(); //suspends the thread until a signal is sent out that there is a free spot
			}
			System.out.println(me.getName()+" found a free spot somewhere so got a lock!");
		  }

		  //checks if the lock is free before acquiring it
		  public void acquireParkingCounterLock() {
			Thread me = Thread.currentThread(); // get a ref to the current thread
			System.out.println(me.getName()+" is attempting to acquire a lock!");
			lock.lock();  //if the lock is in use, this will automatically suspend the thread until the lock is free
			System.out.println(me.getName()+" got a lock!");
		}

		  // Releases a lock to when a thread is finished
		  public void releaseParkingCounterLock() {
			  lock.unlock();
			  Thread me = Thread.currentThread(); // get a ref to the current thread
			  System.out.println(me.getName()+" released a lock!" + "\n");
		  }
	
    /* The processInput method */
	public String processInput(String theInput) {
    		String theOutput = null;
    		JsonObject parsedInput = (new JsonParser()).parse(theInput).getAsJsonObject();
			String floorNumber = parsedInput.get("FloorNumber").toString();

			//processing logic for the entrance clients
			if (parsedInput.get("ClientId").toString().contains("Entrance")) {
				if (Integer.parseInt(floorNumber)==0) { //ground floor entrance client
					if (parkingSpotCounters.get(floorNumber)==0) {
						parkingSpotCounters.put("1",parkingSpotCounters.get("1") - 1);
						theOutput = "There are no spaces available on this floor. Please go to the first floor...";
					}
					else {
						parkingSpotCounters.put(floorNumber,parkingSpotCounters.get(floorNumber) - 1);
						theOutput = "There is a parking space available now. Please enter...";
					}
				}

				if (Integer.parseInt(floorNumber)==1) { //first floor entrance client
					if (parkingSpotCounters.get(floorNumber)==0) {
						parkingSpotCounters.put("0",parkingSpotCounters.get("0") - 1);
						theOutput = "There are no spaces available on this floor. Please go to the ground floor...";
					}
					else {
						parkingSpotCounters.put(floorNumber,parkingSpotCounters.get(floorNumber) - 1);
						theOutput = "There is a parking space available now. Please enter...";
					}
				}
				//updates the boolean value to inform future entrance clients if there is no space left
				freeSpot = parkingSpotCounters.get("0")!=0 || parkingSpotCounters.get("1")!=0;
			}

			//processing logic for the exit clients
			else {
				parkingSpotCounters.put(floorNumber,parkingSpotCounters.get(floorNumber) + 1);
				if (!freeSpot) {freeSpot=true;} //notify waiting entrance clients of a free spot if there isn't any
				if (lock.hasWaiters(isThereSpace)) { //Checks if there are any threads waiting on the given condition
					isThereSpace.signal(); //This is here because only the exit thread should signal a free space
				}
				theOutput = "Thank you for using Acme parking and have a nice day! :)";
			}
 
     		//Return the output message to the ParkingServer
    		System.out.println("Number of available spots: " + "groundFloor: " + parkingSpotCounters.get("0")
					+ " firstFloor: " + parkingSpotCounters.get("1"));
    		return theOutput;
    	}
}

