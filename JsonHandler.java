import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class JsonHandler {
	private String publishPath;
	private String fileName = "spaceapi.json"; //Dateiname festlegen
	
	public JsonHandler(String _publishPath) {
		publishPath = _publishPath;
	}
	
	void updateStatusClosed() { //TODO: JSON dynamisch generieren

		String statusJSON = " { \"api\": \"0.13\","
				+ " \"space\": \"vspace.one\","
				+ " \"logo\": \"https://wiki.vspace.one/lib/exe/fetch.php?cache=&media=verein:logo_vspaceone.png\","
				+ " \"url\": \"http://vspace.one\","
				+ " \"location\": { \"address\": \"Wilhelm-Binder-Str. 19, 78048 VS-Villingen, Germany\", \"lon\": 8.456495, \"lat\": 48.065003 },"
				+ " \"contact\": { \"twitter\": \"@vspaceone\", \"email\": \"hackerspace-vs@lieber-anders.de\" },"
				+ " \"issue_report_channels\": [ \"twitter\", \"email\" ],"
				+ " \"state\": { \"open\": false } }"; //JSON für "geschlossen"

		try {
			File json = new File(publishPath + fileName); //Pfad+Dateiname
			PrintWriter writer = new PrintWriter(new FileWriter(json));
			writer.print(statusJSON); //String in Datei schrieben
			writer.close();
			
			System.out.println("Status: Closed");
		} catch (IOException ex) {
			System.out.println("Could not update JSON!");
			System.out.println(ex);
		}
	}
	
	void updateStatusOpen() {

		String statusJSON = " { \"api\": \"0.13\","
				+ " \"space\": \"vspace.one\","
				+ " \"logo\": \"https://wiki.vspace.one/lib/exe/fetch.php?cache=&media=verein:logo_vspaceone.png\","
				+ " \"url\": \"http://vspace.one\","
				+ " \"location\": { \"address\": \"Wilhelm-Binder-Str. 19, 78048 VS-Villingen, Germany\", \"lon\": 8.456495, \"lat\": 48.065003 },"
				+ " \"contact\": { \"twitter\": \"@vspaceone\", \"email\": \"hackerspace-vs@lieber-anders.de\" },"
				+ " \"issue_report_channels\": [ \"twitter\", \"email\" ],"
				+ " \"state\": { \"open\": true } }"; //JSON für "geöffnet"
		try {
			File json = new File(publishPath + fileName); //Pfad+Dateiname
			PrintWriter writer = new PrintWriter(new FileWriter(json)); 
			writer.print(statusJSON); //String in Datei schrieben
			writer.close();
			
			System.out.println("Status: Open");
		} catch (IOException ex) {
			System.out.println("Could not update JSON!");
			System.out.println(ex);
		}
	}

}
