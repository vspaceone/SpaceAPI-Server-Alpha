import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


public class SpaceAPI {
	
	public static void main(String[] args) {
		try {
			new SpaceAPI(args);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private SpaceAPI(String[] locations) throws IOException {
		try {
			ServerSocket serverSocket = new ServerSocket(Integer.parseInt(locations[0])); //Serverport ist Argument 0

			System.out.println("SpaceAPI Server started (Port: " + locations[0] + ")\n");

			
			final JsonHandler jsonHandler = new JsonHandler(locations[1]); //Pfad des JSON ist Argument 1
			jsonHandler.updateStatusClosed(); //initiales schreiben der Datei
			
			while (true) {
				try {
					Socket clientSocket = serverSocket.accept(); //auf Client warten
					System.out.println("New Socket: " + clientSocket);
					new InfoHandler(jsonHandler, clientSocket).start(); //Client an InfoHandler übergeben
				} catch (SocketException ex) {
					serverSocket.close();
					main(locations); //Server neu starten
				}
			}
		} catch (ArrayIndexOutOfBoundsException ix) {
			System.out.println("Not enough arguments!");
			System.out.println("SpaceAPI Server stopped");
			System.exit(0);
		} catch (BindException bx) {
			System.out.println("Port " + locations[0] + " is not available");
			System.out.println("Please try again with different Port");
			System.out.println("SpaceAPI Server stopped");
			System.exit(0);
		} catch (NumberFormatException nx) {
			System.out.println("Argument \"" + locations[0] + "\" should be a number!");
			System.out.println("SpaceAPI Server stopped");
			System.exit(0);
		}
	}

}