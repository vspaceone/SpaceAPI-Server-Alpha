import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;


public class JsonHandler {
	private String publishPath;
	private SQLHandler sqlHandler;
	private String fileName = "spaceapi.json"; //Dateiname festlegen
	private Double tempMaschinenraum;
	private Double tempBruecke;
	private Double humMaschinenraum;
	private Double humBruecke;
	private boolean stateOpen = false;

	public JsonHandler(String _publishPath, SQLHandler _sqHandler) {
		publishPath = _publishPath;
		sqlHandler = _sqHandler;
	}
	
	public void updateJSON() {
		tempMaschinenraum = sqlHandler.getTemperature("maschinenraum");
		tempBruecke = sqlHandler.getTemperature("bruecke");
		
		humMaschinenraum = sqlHandler.getHumidity("maschinenraum");
		humBruecke = sqlHandler.getHumidity("bruecke");
		
		stateOpen = sqlHandler.getDoorstate();
		
		writeJSON();
	}

	private void writeJSON() {
		try {
			File json = new File(publishPath + fileName); //Pfad+Dateiname
			PrintWriter writer = new PrintWriter(new FileWriter(json));
			String myJSON;
			
			myJSON = new JSONObject()
				.put("api", "0.13")
				.put("space", "vspace.one")
				.put("logo", "https://wiki.vspace.one/lib/exe/fetch.php?cache=&media=verein:logo_vspaceone.png")
				.put("url", "https://vspace.one")
				.put("ext_ccc", "chaostreff")
				.put("location", new HashMap<String, Object>() {
					{
						put("address","Wilhelm-Binder-Str. 19, 78048 VS-Villingen, Germany");
						put("lat", new Double(48.065003));
						put("lon", new Double(8.456495));
					}
				})
				.put("contact", new HashMap<String, String>() {
					{
						put("twitter", "@vspace.one");
						put("email", "hackerspace-vs@lieber-anders.de");
						put("phone", "+49 221 596196638");
					}
				})
				.put("issue_report_channels", new ArrayList<String>() {
					{
						add("twitter");
						add("email");
					}
				})
				.put("sensors", new HashMap<String, ArrayList<HashMap<String, Object>>>() {
					{
						put("temperature", new ArrayList<HashMap<String, Object>>() {
							{
								
								if (tempMaschinenraum!=999.99) {
									add(new HashMap<String, Object>() {
										{
											put("value", tempMaschinenraum);
											put("unit", "°C");
											put("location", "Maschinenraum");
										}
									});
								}
								
								if (tempBruecke!=999.99) {
									add(new HashMap<String, Object>() {
										{
											put("value", tempBruecke);
											put("unit", "°C");
											put("location", "Brücke");
										}
									});
								}
							}
						});
						
						put("humidity", new ArrayList<HashMap<String, Object>>() {
							{
								
								if (humMaschinenraum!=999.99) {
									add(new HashMap<String, Object>() {
										{
											put("value", humMaschinenraum);
											put("unit", "%");
											put("location", "Maschinenraum");
										}
									});
								}
								
								if (humBruecke!=999.99) {
									add(new HashMap<String, Object>() {
										{
											put("value", humBruecke);
											put("unit", "%");
											put("location", "Brücke");
										}
									});
								}
							}
						});
					}})
				.put("state", new HashMap<String, Boolean>() {
					{
						put("open", stateOpen);
					}
				})
				.put("open", stateOpen)
				.toString();
			
			System.out.println("\nJSON erzeugt\n");
			
			writer.print(myJSON);
			writer.close();
		} catch (Exception e) {
			System.out.println("Datei konnte nicht erstellt werden.");
			System.out.println(e);
		}
	}
}
