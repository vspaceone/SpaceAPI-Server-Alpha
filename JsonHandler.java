import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class JsonHandler {
	private String publishPath;
	private String fileName = "spaceapi.json"; //Dateiname festlegen
	private double tempMaschinenraum = 999.99;
	private double tempBruecke = 999.99;
	private double humMaschinenraum = 999.99;
	private double humBruecke = 999.99;
	
	public JsonHandler(String _publishPath) {
		publishPath = _publishPath;
	}
	
	public void updateStatusClosed() { //TODO: JSON dynamisch generieren

		String statusJSON = " { \"api\": \"0.13\","
				+ " \"space\": \"vspace.one\","
				+ " \"logo\": \"https://wiki.vspace.one/lib/exe/fetch.php?cache=&media=verein:logo_vspaceone.png\","
				+ " \"url\": \"http://vspace.one\","
				+ " \"location\": { \"address\": \"Wilhelm-Binder-Str. 19, 78048 VS-Villingen, Germany\", \"lon\": 8.456495, \"lat\": 48.065003 },"
				+ " \"contact\": { \"twitter\": \"@vspaceone\", \"email\": \"hackerspace-vs@lieber-anders.de\" },"
				+ " \"issue_report_channels\": [ \"twitter\", \"email\" ],"
				+ sensors()
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
	
	public void updateStatusOpen() {

		String statusJSON = " { \"api\": \"0.13\","
				+ " \"space\": \"vspace.one\","
				+ " \"logo\": \"https://wiki.vspace.one/lib/exe/fetch.php?cache=&media=verein:logo_vspaceone.png\","
				+ " \"url\": \"http://vspace.one\","
				+ " \"location\": { \"address\": \"Wilhelm-Binder-Str. 19, 78048 VS-Villingen, Germany\", \"lon\": 8.456495, \"lat\": 48.065003 },"
				+ " \"contact\": { \"twitter\": \"@vspaceone\", \"email\": \"hackerspace-vs@lieber-anders.de\" },"
				+ " \"issue_report_channels\": [ \"twitter\", \"email\" ],"
				+ sensors()
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
	
	public void setTempMaschinenraum(int temp) {
		tempMaschinenraum = ((double)temp)/100;
		
		System.out.println("Temp Maschinenraum: " + tempMaschinenraum);
		return;
	}
	
	public void setTempBruecke(int temp) {
		tempBruecke = ((double)temp)/100;
		
		System.out.println("Temp Brücke: " + tempMaschinenraum);
		return;
	}
	
	public void setHumMaschinenraum(int hum) {
		humMaschinenraum = ((double)hum)/100;
		
		System.out.println("Hum Maschinenraum: " + humMaschinenraum);
		return;
	}
	
	public void setHumBruecke(int hum) {
		humBruecke = ((double)hum)/100;
		
		System.out.println("Hum Brücke: " + humBruecke);
		return;
	}
	
	private String sensors() {
		String brueckeTemp = "";
		String maschinenraumTemp = "";
		String brueckeHum = "";
		String maschinenraumHum = "";
		String multipelEntries = "";
		
		if (tempBruecke != 999.99) {
			brueckeTemp = "{\"value\" : " + tempBruecke + " , \"unit\" : \"°C\", \"location\" : \"Brücke\"}";
			multipelEntries = ",";
		}
		
		if (tempMaschinenraum != 999.99) {
			maschinenraumTemp = multipelEntries + "{\"value\" : " + tempMaschinenraum + " , \"unit\" : \"°C\", \"location\" : \"Maschinenraum\"}";
		}
		
		multipelEntries = "";
		
		if (humBruecke != 999.99) {
			brueckeHum = "{\"value\" : " + humBruecke + " , \"unit\" : \"%\", \"location\" : \"Brücke\"}";
			multipelEntries = ",";
		}
		
		if (humMaschinenraum != 999.99) {
			maschinenraumHum = multipelEntries + "{\"value\" : " + humMaschinenraum + " , \"unit\" : \"%\", \"location\" : \"Maschinenraum\"}";
		}
		
		
		return " \"sensors\": { \"temperature\": ["+ brueckeTemp + maschinenraumTemp + "]," 
			+ " \"humidity\": ["+ brueckeHum + maschinenraumHum + "]},";
	}

}
