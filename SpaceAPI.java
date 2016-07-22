import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SpaceAPI {

	public static void main(String[] args) {
		try {
			new SpaceAPI(args);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private Integer port = 8080;
	private String exportPath = "";
	private String telnetPassword = "0000";

	private String dbHostname = "localhost";
	private Integer dbPort = 3306;
	private String dbName = "spaceapi";
	private String dbUser = "spaceapi";
	private String dbPassword = "0000";

	private String ircServer = "irc.freenode.net";
	private int ircPort = 6667;
	private String ircChannel = "#spaceapi";
	private String ircUser = "SpaceAPI Bot";


	private SpaceAPI(String[] arguments) throws IOException {
		try {
			readConfig(arguments[0]);

			ServerSocket serverSocket = new ServerSocket(port); //Serverport ist Argument 0

			System.out.println("SpaceAPI Server started (Port: " + port + ")\n");

			final SQLHandler sqlHandler = new SQLHandler(dbHostname, dbPort, dbName, dbUser, dbPassword);

			final JsonHandler jsonHandler = new JsonHandler(exportPath, sqlHandler); //Pfad des JSON ist Argument 1
			jsonHandler.updateJSON(); //initiales schreiben der Datei

			final IRCBot ircBot = new IRCBot(ircServer, ircPort, ircChannel, ircUser, sqlHandler);
			ircBot.start();

			while (true) {
				try {
					Socket clientSocket = serverSocket.accept(); //auf Client warten
					System.out.println(getDate() + " New Socket: " + clientSocket);
					new InfoHandler(jsonHandler, sqlHandler, ircBot, clientSocket, telnetPassword).start();	//Client an InfoHandler übergeben
				} catch (SocketException ex) {
					serverSocket.close();
					main(arguments); //Server neu starten
				}
			}
		} catch (ArrayIndexOutOfBoundsException ix) {
			System.out.println("Not enough arguments!");
			System.out.println("SpaceAPI Server stopped");
			System.exit(0);
		} catch (BindException bx) {
			System.out.println("Port " + port + " is not available");
			System.out.println("Please try again with different Port");
			System.out.println("SpaceAPI Server stopped");
			System.exit(0);
		} catch (NumberFormatException nx) {
			System.out.println("Argument \"" + port + "\" should be a number!");
			System.out.println("SpaceAPI Server stopped");
			System.exit(0);
		}
	}

	private String getDate() {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat ("HH:mm:ss dd.MM.yyyy");

		return ft.format(dNow);
	}

	private void readConfig(String configPath) {
		BufferedReader buffer = null;
		FileInputStream fiStream = null;

		try {
			buffer = new BufferedReader(new InputStreamReader(fiStream = new FileInputStream(configPath)));
		} catch (FileNotFoundException io) {
			System.out.println("Config File not found!");	
			System.exit(1);
		}

		if (fiStream!=null&&buffer!=null) {
			String line;
			String words[];

			while(true) {
				try {
					line = buffer.readLine();

					if (line == null)
						return;

					line=line.trim();

					if (line.startsWith("#") || line.isEmpty())
						continue;

					words = line.split(":");

					if (words[0].equalsIgnoreCase("Port")) {
						port = Integer.parseInt(words[1]);

					} else if (words[0].equalsIgnoreCase("ExportPath")) {
						exportPath = words[1];

					} else if (words[0].equalsIgnoreCase("TelnetPassword")) {
						telnetPassword = words[1];

					} else if (words[0].equalsIgnoreCase("dbHostname")) {
						dbHostname = words[1];

					} else if (words[0].equalsIgnoreCase("dbPort")) {
						dbPort = Integer.parseInt(words[1]);

					} else if (words[0].equalsIgnoreCase("dbName")) {
						dbName = words[1];

					} else if (words[0].equalsIgnoreCase("dbUser")) {
						dbUser = words[1];

					} else if (words[0].equalsIgnoreCase("dbPassword")) {
						dbPassword = words[1];

					} else if (words[0].equalsIgnoreCase("IRCUser")) {
						ircUser = words[1];
						
					} else if (words[0].equalsIgnoreCase("IRCServer")) {
						ircServer = words[1];
						
					} else if (words[0].equalsIgnoreCase("IRCPort")) {
						ircPort = Integer.parseInt(words[1]);
						
					} else if (words[0].equalsIgnoreCase("IRCChannel")) {
						ircChannel = words[1];

					} else {
						System.out.println("Config Option: " + words[0] + " not recognized.");
					}
				} catch (IOException e) {
					System.out.println("Could not read Config File.");
					System.out.println(e);
				}

			}
		}
	}

}