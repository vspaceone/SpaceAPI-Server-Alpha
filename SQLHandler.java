import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SQLHandler {
	private String dbHostname;
	private Integer dbPort;
	private String dbName;
	private String dbUser;
	private String dbPassword;

	private Connection conn;


	public  SQLHandler(String _dbHostname, Integer _dbPort, String _dbName, String _dbUser, String _dbPassword) { //for future use
		dbHostname = _dbHostname;
		dbPort = _dbPort;
		dbName = _dbName;
		dbUser = _dbUser;
		dbPassword = _dbPassword;
	}


	private Connection getConnection() {
		Connection _conn;

		try {
			_conn = DriverManager.getConnection(
					"jdbc:mysql://" + dbHostname + ":" + this.dbPort + "/" + dbName, dbUser, dbPassword);

			return _conn;
		} catch (SQLException ex) {
			System.out.println("SQL Connection Error");
			System.out.println(ex);
			return null;
		}
	}

	public void setDoorstate(boolean doorstate) {
		conn = getConnection();
		
		try {
			String query;
			Statement stmt = conn.createStatement();

			query = "INSERT INTO DoorState (state, Time) "
					+ "VALUES (" + doorstate + ", '" + getSqlDate() + "');";
			stmt.executeUpdate(query);
		} catch (SQLException ex) {
			System.out.println("SQL Error: Could not set doorstate");
			System.out.println(ex);
		}
	}

	public void setTemperature(Double temperature, String room) {
		conn = getConnection();
		
		try {
			String query;
			String roomid;
			ResultSet rs;
			Statement stmt = conn.createStatement();

			query = "SELECT Room_ID FROM Rooms WHERE AltName LIKE '" + room + "';";
			rs = stmt.executeQuery(query);
			rs.next();
			roomid = rs.getString(1);

			query = "INSERT INTO SensorData ( Type, Value, Unit, Time, Room_ID) "
					+ "VALUES ('temperature'," + temperature + ", '°C', '" + getSqlDate() + "'," + roomid + ");";
			stmt.executeUpdate(query);
			conn.close();
		} catch (SQLException ex) {
			System.out.println("SQL Error: Could not set temperature");
			System.out.println(ex);
		}
	}

	public void setHumidity(Double humidity, String room) {
		conn = getConnection();
		
		try {
			String query;
			String roomid;
			ResultSet rs;
			Statement stmt = conn.createStatement();

			query = "SELECT Room_ID FROM Rooms WHERE AltName LIKE '" + room + "';";
			rs = stmt.executeQuery(query);
			rs.next();
			roomid = rs.getString(1);

			query = "INSERT INTO SensorData (Type, Value, Unit, Time, Room_ID) "
					+ "VALUES ('humidity'," + humidity + ", '%', '" + getSqlDate() + "'," + roomid + ");";
			stmt.executeUpdate(query);
			
			conn.close();
		} catch (SQLException ex) {
			System.out.println("SQL Error: Could not set humidity");
			System.out.println(ex);
		}
	}
	
	public Boolean getDoorstate() {
		conn = getConnection();
		
		try {
			String query;
			ResultSet rs;
			Statement stmt = conn.createStatement();

			query = "SELECT state FROM `DoorState` ORDER BY Time DESC LIMIT 1;";
			rs = stmt.executeQuery(query);
			rs.next();
			
			return rs.getBoolean(1);
		} catch (SQLException ex) {
			System.out.println("SQL Error: Could not get doorstate");
			System.out.println(ex);
			return false;
		}
	}
	
	public Double getTemperature(String room) {
		conn = getConnection();
		
		try {
			String query;
			ResultSet rs;
			Statement stmt = conn.createStatement();

			query = "SELECT Value "
					+ "FROM SensorData, Rooms "
					+ "WHERE Type LIKE 'temperature' "
					+ "AND AltName LIKE '" + room + "' "
					+ "AND SensorData.Room_ID = Rooms.Room_ID "
					+ "AND SensorData.Time >= DATE_SUB(NOW(), INTERVAL 30 MINUTE) "
					+ "ORDER BY Time DESC "
					+ "LIMIT 1;";
			
			rs = stmt.executeQuery(query);
			rs.next();
			
			return rs.getDouble(1);
		} catch (SQLException ex) {
			System.out.println("No recent temperature data for: " + room);
			return 999.99;
		}
	}
	
	public Double getHumidity(String room) {
		conn = getConnection();
		
		try {
			String query;
			ResultSet rs;
			Statement stmt = conn.createStatement();
			
			query = "SELECT Value "
					+ "FROM SensorData, Rooms "
					+ "WHERE Type LIKE 'humidity' "
					+ "AND AltName LIKE '" + room + "' "
					+ "AND SensorData.Room_ID = Rooms.Room_ID "
					+ "AND SensorData.Time >= DATE_SUB(NOW(), INTERVAL 30 MINUTE) "
					+ "ORDER BY Time DESC "
					+ "LIMIT 1;";

			rs = stmt.executeQuery(query);
			rs.next();
			
			return rs.getDouble(1);
		} catch (SQLException ex) {
			System.out.println("No recent humidity data for: " + room);
			return 999.99;
		}
	}


	private String getSqlDate() {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");

		return ft.format(dNow);
	}
}
