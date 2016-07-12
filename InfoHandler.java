import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class InfoHandler extends Thread{
	private JsonHandler jsonHandler;
	private Socket clientSocket;
	private static boolean status; //Gibt intern den Status des Space an

	private String password = "********"; //Passwort festlegen (für GitHub entfernen)

	public InfoHandler(JsonHandler _jsonHandler, Socket _clientSocket) {
		jsonHandler = _jsonHandler;
		clientSocket = _clientSocket;
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

				if (message!=null) {
					if (message.equals("open")||message.equals("opened")) { //Status geöffnet
						if (pwCheck(outWriter)) { //Passwort checken
							status = true;
							jsonHandler.updateStatusOpen();

							outWriter.print("\n\rDer vspace.one ist nun geöffnet.\n\r\n\r");
							outWriter.flush();
						}
						break;
					} else if (message.equals("close")||message.equals("closed")) { //Status geschlossen
						if (pwCheck(outWriter)) { //Passwort checken
							status = false;
							jsonHandler.updateStatusClosed();

							outWriter.print("\n\rDer vspace.one ist nun geschlossen.\n\r\n\r");
							outWriter.flush();
						}
						break;
					} else if (message.equals("status")) { //Öffnungsstatus ausgeben
						if (status) {
							outWriter.print("\n\rDer vspace.one ist geöffnet. Komm vorbei!\n\r\n\r");
							outWriter.flush();
						} else {
							outWriter.print("\n\rDer vspace.one ist leider geschlossen.\n\r\n\r");
							outWriter.flush();
						}

						break;
					} else if (message.equals("help")) { //Hilfe ausgeben
						outWriter.print("\n\rFunktionen:\n\r"
								+ "\"closed\" oder \"close\" --- Setzt den Status auf \"geschlossen\"\n\r"
								+ "\"opened\" oder \"open\" --- Setzt den Status auf \"geöffnet\"\n\r"
								+ "\"status\" --- Gibt den aktuellen Status wieder\n\r"
								+ "\"exit\" oder \"quit\" --- Schließt die Verbindung\n\r"
								+ "\"set temp *Raum* *Wert*\" --- Setzt die Temperatur im jeweiligen Raum\n\r"
								+ "\"set hum *Raum* *Wert*\" --- Setzt die Luftfeuchtigkeit im jeweiligen Raum\n\r"
								+ "\"help\" --- Zeigt diese Hilfe an\n\r\n\r"
								+ "-->   ");
						outWriter.flush();
					} else if (message.startsWith("set temp")) {
						if (pwCheck(outWriter)) {
							String[] elements;
							elements = message.split(" ");

							if(elements[2].equals("maschinenraum")) {
								jsonHandler.setTempMaschinenraum(Integer.parseInt(elements[3]));
								outWriter.print("\n\rTemperatur im Maschinenraum auf "
										+ (Double.valueOf(elements[3])/100) + " Grad gesetzt.\n\r\n\r");
								outWriter.flush();
							} else if(elements[2].equals("bruecke")) {
								jsonHandler.setTempBruecke(Integer.parseInt(elements[3]));
								outWriter.print("\n\rTemperatur in der Brücke auf " 
										+ (Double.valueOf(elements[3])/100) + " Grad gesetzt.\n\r\n\r");
								outWriter.flush();
							}

							if (status) {
								jsonHandler.updateStatusOpen();
							} else {
								jsonHandler.updateStatusClosed();
							}
						}

						break;	
					} else if (message.startsWith("set hum")) {
						if (pwCheck(outWriter)) {
							String[] elements;
							elements = message.split(" ");

							if(elements[2].equals("maschinenraum")) {
								jsonHandler.setHumMaschinenraum(Integer.parseInt(elements[3]));
								outWriter.print("\n\rLuftfäuchtigkeit im Maschinenraum auf "
										+ (Double.valueOf(elements[3])/100) + "% gesetzt.\n\r\n\r");
								outWriter.flush();
							} else if(elements[2].equals("bruecke")) {
								jsonHandler.setHumBruecke(Integer.parseInt(elements[3]));
								outWriter.print("\n\rLuftfäuchtigkeit in der Brücke auf " 
										+ (Double.valueOf(elements[3])/100) + "% gesetzt.\n\r\n\r");
								outWriter.flush();
							}

							if (status) {
								jsonHandler.updateStatusOpen();
							} else {
								jsonHandler.updateStatusClosed();
							}
						}

						break;
					} else if (message.equals("exit")|message.equals("quit")) { //Socket schlißen bei "exit" oder "quit"
						break;
					} else {
						outWriter.print("\n\rUnbekannter Befehl, \"help\" für Hilfe\n\r\n\r"
								+ "-->   ");
						outWriter.flush();
					}
				} 
			}
			System.out.println("Socket closed: " + clientSocket);
			clientSocket.close(); //Socket schließen
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
