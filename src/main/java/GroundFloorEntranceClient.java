import com.google.gson.JsonObject;

import java.io.*;
import java.net.*;

public class GroundFloorEntranceClient {
    public static void main(String[] args) throws IOException {

        // Set up the socket, in and out variables

        Socket ParkingClientSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        int ServerSocketNumber = 4545;
        String ParkingServerName = "localhost";
        JsonObject message = new JsonObject();
        message.addProperty("ClientId", "Ground Floor Entrance");
        message.addProperty("FloorNumber", 0);
        try {
            ParkingClientSocket = new Socket(ParkingServerName, ServerSocketNumber);
            out = new PrintWriter(ParkingClientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(ParkingClientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: localhost ");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: "+ ServerSocketNumber);
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;

        System.out.println("Initialised " + message.get("ClientId") + " client and IO connections");

        while (true) {
            System.out.println("\nWelcome to Acme Parking. Please press any button to enter...");
            fromUser = stdIn.readLine();
            System.out.println("Please note:- Waiting times can vary according to availability of spaces" + '\n');
            if (fromUser != null) {
//                message.addProperty("Value", -1);
                out.println(message.toString());
                System.out.println("****** Looking for space, Please wait... ******" + '\n');
            }
            fromServer = in.readLine();
            System.out.println("Server: " + fromServer);
        }

    }
}
