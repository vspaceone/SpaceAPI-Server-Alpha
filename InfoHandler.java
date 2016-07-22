import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class InfoHandler extends Thread{
	private JsonHandler jsonHandler;
	private SQLHandler sqlHandler;
	private IRCBot ircBot;
	private Socket clientSocket;
	private String password;
	

	public InfoHandler(JsonHandler _jsonHandler, SQLHandler _sqlHandler, IRCBot _ircBot, Socket _clientSocket, String _password) {
		jsonHandler = _jsonHandler;
		sqlHandler = _sqlHandler;
		ircBot = _ircBot;
		clientSocket = _clientSocket;
		password = _password;
	}

	public void run() {
		String message;

		try {
			PrintWriter outWriter = new PrintWriter(clientSocket.getOutputStream(), true);
			outWriter.print("\n\rWillkommen auf vspace.one\n\r\n\r"
					+ "Über dieses Interface kannst du den Öffnungsstatus des vspace.one ändern.\n\r"
					+ "Wenn du Fragen zur Funktion hast tippe \"help\" oder schaue im Wiki nach.\n\r\n\r"
					+ "-->   ");
			outWriter.flush();

			while(true) {
				BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				message = inBuffer.readLine();
				message.toLowerCase();
				message = message.trim();

				if (message!=null) {
					if (message.equals("open")||message.equals("opened")) { //Status geöffnet
						
						if (pwCheck(outWriter)) { //Passwort checken
							sqlHandler.setDoorstate(true);
							ircBot.statusOpen();

							outWriter.print("\n\rDer vspace.one ist nun geöffnet.\n\r\n\r");
							outWriter.flush();
						}
						
						break;
					} else if (message.equals("close")||message.equals("closed")) { //Status geschlossen
						
						if (pwCheck(outWriter)) { //Passwort checken
							sqlHandler.setDoorstate(false);
							ircBot.statusClosed();

							outWriter.print("\n\rDer vspace.one ist nun geschlossen.\n\r\n\r");
							outWriter.flush();
						}
						
						break;
					} else if (message.equals("status")) { //Öffnungsstatus ausgeben
						
						if (sqlHandler.getDoorstate()) {
							outWriter.print("\n\rDer vspace.one ist geöffnet. Komm vorbei!\n\r");
							outWriter.flush();
						} else {
							outWriter.print("\n\rDer vspace.one ist leider geschlossen.\n\r\n\r");
							outWriter.flush();
						}
						
						outWriter.print("Die Temperatur im Maschinenraum beträgt: " + sqlHandler.getTemperature("maschinenraum") + "°C.\n\r");
						outWriter.print("Die Temperatur auf der Brücke beträgt: " + sqlHandler.getTemperature("bruecke") + "°C.\n\r\n\r");
						outWriter.print("Die Luftfeuchtigkeit im Maschinenraum beträgt: " + sqlHandler.getHumidity("maschinenraum") + "%\n\r");
						outWriter.print("Die Luftfeuchtigkeit auf der Brücke beträgt: " + sqlHandler.getHumidity("bruecke") + "%.\n\r\n\r");

						break;
					} else if (message.equals("help")) { //Hilfe ausgeben
						
						outWriter.print("\n\rFunktionen:\n\r"
								+ "\"closed\" oder \"close\" --- Setzt den Status auf \"geschlossen\"\n\r"
								+ "\"opened\" oder \"open\" --- Setzt den Status auf \"geöffnet\"\n\r"
								+ "\"status\" --- Gibt den aktuellen Status wieder\n\r"
								+ "\"set temp *Raum* *Wert*\" --- Setzt die Temperatur im jeweiligen Raum\n\r"
								+ "\"set hum *Raum* *Wert*\" --- Setzt die Luftfeuchtigkeit im jeweiligen Raum\n\r"
								+ "\"exit\" oder \"quit\" --- Schließt die Verbindung\n\r"
								+ "\"help\" --- Zeigt diese Hilfe an\n\r\n\r"
								+ "-->   ");
						outWriter.flush();
						
					} else if (message.startsWith("set temp")) { //Temperatur setzen
						
						if (pwCheck(outWriter)) {
							String[] elements;
							elements = message.split(" ");
							
							sqlHandler.setTemperature(Double.parseDouble(elements[3])/100, elements[2]);
							
							outWriter.print("\n\rTemperatur in " + elements[2] + " auf " + Double.parseDouble(elements[3])/100 + "°C gesetzt\n\r\n\r");
							outWriter.flush();
						}

						break;	
					} else if (message.startsWith("set hum")) { //Luftfeuchtigkeit setzen
						
						if (pwCheck(outWriter)) {
							String[] elements;
							elements = message.split(" ");
							
							sqlHandler.setHumidity(Double.parseDouble(elements[3])/100, elements[2]);
							
							outWriter.print("\n\rLuftfeuchtigkeit in " + elements[2] + " auf " + Double.parseDouble(elements[3])/100 + "% gesetzt\n\r\n\r");
							outWriter.flush();
						}

						break;
					} else if (message.equals("exit")|message.equals("quit")) { //Socket schließen bei "exit" oder "quit"
						
						break;
					} else {
						outWriter.print("\n\rUnbekannter Befehl, \"help\" für Hilfe\n\r\n\r"
								+ "-->   ");
						outWriter.flush();
					}
				} 
			}
			System.out.println("Socket closed: " + clientSocket);
			outWriter.close();
			clientSocket.close(); //Socket schließen
			jsonHandler.updateJSON();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private boolean pwCheck(PrintWriter _outWriter) throws IOException {
		String givenPassword;

		_outWriter.print("\n\rBitte Passwort eingeben\n\r\n\r-->   ");
		_outWriter.flush();

		BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		givenPassword = inBuffer.readLine();

		if (givenPassword.equals(password)) {
			_outWriter.print("\n\rPasswort korrekt\n\r");
			_outWriter.flush();

			return true;
		}

		_outWriter.print("\n\rPasswort FALSCH\n\r");
		_outWriter.flush();
		return false;
	}
}
