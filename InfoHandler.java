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
			outWriter.print("\nWillkommen auf vspace.one\n\n"
					+ "Über dieses Interface kannst du den Öffnungsstatus des vspace.one ändern.\n"
					+ "Wenn du Fragen zur Funktion hast tippe \"help\" oder schaue im Wiki nach.\n\n"
					+ "-->   ");
			outWriter.flush();

			while(true) {
				BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				message = inBuffer.readLine();

				if (message!=null) {
					if (message.equalsIgnoreCase("open")||message.equalsIgnoreCase("opened")) { //Status geöffnet
						if (pwCheck(outWriter)) { //Passwort checken
							status = true;
							jsonHandler.updateStatusOpen();

							outWriter.print("\nDer vspace.one ist nun geöffnet.\n\n");
							outWriter.flush();
						}
						break;
					} else if (message.equalsIgnoreCase("close")||message.equalsIgnoreCase("closed")) { //Status geschlossen
						if (pwCheck(outWriter)) { //Passwort checken
							status = false;
							jsonHandler.updateStatusClosed();

							outWriter.print("\nDer vspace.one ist nun geschlossen.\n\n");
							outWriter.flush();
						}
						break;
					} else if (message.equalsIgnoreCase("status")) { //Öffnungsstatus ausgeben
						if (status) {
							outWriter.print("\nDer vspace.one ist geöffnet. Komm vorbei!\n\n");
							outWriter.flush();
						} else {
							outWriter.print("\nDer vspace.one ist leider geschlossen.\n\n");
							outWriter.flush();
						}

						break;
					} else if (message.equalsIgnoreCase("help")) { //Hilfe ausgeben
						outWriter.print("\nFunktionen:\n"
								+ "\"closed\" oder \"close\" --- Setzt den Status auf \"geschlossen\"\n"
								+ "\"opened\" oder \"open\" --- Setzt den Status auf \"geöffnet\"\n"
								+ "\"status\" --- Gibt den aktuellen Status wieder\n"
								+ "\"exit\" oder \"quit\" --- Schließt die Verbindung\n"
								+ "\"help\" --- Zeigt diese Hilfe an\n\n"
								+ "-->   ");
						outWriter.flush();
					} else if (message.equalsIgnoreCase("exit")|message.equalsIgnoreCase("quit")) { //Socket schlißen bei "exit" oder "quit"
						break;
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

		_outWriter.print("\nBitte Passwort eingeben\n\n-->   ");
		_outWriter.flush();

		BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		givenPassword = inBuffer.readLine();

		if (givenPassword.equals(password)) {
			_outWriter.print("\nPasswort korrekt\n");
			_outWriter.flush();

			return true;
		}

		_outWriter.print("\nPasswort FALSCH\n");
		_outWriter.flush();
		return false;
	}
}
