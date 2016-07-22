import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.Socket;

public class IRCBot implements Runnable {
	private String server;

	protected void server(String server) {
		this.server = server;
	}

	protected String server() {
		return this.server;
	}

	private int port;

	protected void port(int port) {
		this.port = port;
	}

	protected int port() {
		return this.port;
	}

	private String channel;

	protected void channel(String channel) {
		this.channel = channel;
	}

	protected String channel() {
		return this.channel;
	}

	private String nick, user, name;

	protected void nick(String nick) {
		this.nick = nick;
	}

	protected String nick() {
		return this.nick;
	}

	protected void user(String user) {
		this.user = user;
	}

	protected String user() {
		return this.user;
	}

	protected void name(String name) {
		this.name = name;
	}

	protected String name() {
		return this.name;
	}

	private boolean isActive;

	protected void isActive(boolean bool) {
		this.isActive = bool;
	}

	protected boolean isActive() {
		return this.isActive;
	}

	protected SQLHandler sqlHandler;

	protected IRCBot(SQLHandler _sqlHandler) {
		this.server("irc.freenode.net");
		this.port(6667);
		this.channel("#spaceapi");
		this.nick("SpaceAPI Bot");
		this.user("SpaceAPI Bot");
		this.name("SpaceAPI Bot");
		this.sqlHandler = _sqlHandler;
	}

	protected IRCBot(String _server, int _port, String _channel, String _nick, SQLHandler _sqHandler) {
		this.server(_server);
		this.port(_port);
		this.channel(_channel);
		this.nick(_nick);
		this.user(_nick);
		this.name(_nick);
		sqlHandler = _sqHandler;
	}

	private Socket socket;

	private BufferedReader in;

	private BufferedWriter out;

	protected void start() throws java.io.IOException {
		this.socket = new Socket(this.server(), this.port());
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		if (socket.isConnected()) {
			out.write("NICK " + this.nick() + "\r\n");
			out.write("USER " + this.user() + " \"\" \"\" :" + this.name() + "\r\n");
			out.write("JOIN " + this.channel() + "\r\n");
			out.flush();
			this.isActive(true);
			System.out.println("Connected to IRC");
			new Thread(this).start();
		}
	}

	public void run() {
		String buffer;
		while (this.isActive()) {
			try {
				while ((buffer = in.readLine()) != null) {
					if (buffer.startsWith("PING")) {
						out.write("PONG " + buffer.substring(5) + "\r\n");
						out.flush();
					} else if (buffer.contains("!status")) {
						
						if (sqlHandler.getDoorstate()) {
							out.write("PRIVMSG #vspace.one : Der vspace.one ist geöffnet. Kommt vorbei!\n\r");
						} else {
							out.write("PRIVMSG #vspace.one : Der vspace.one ist leider geschlossen.\n\r");
						}
						
						out.flush();
					} else if (buffer.contains("!temp")) {
						
						out.write("PRIVMSG #vspace.one : Die Temperatur im Maschinenraum beträgt: " 
								+ sqlHandler.getTemperature("maschinenraum") + "°C.\n\r");
						
						out.write("PRIVMSG #vspace.one : Die Temperatur auf der Brücke beträgt: " 
								+ sqlHandler.getTemperature("bruecke") + "°C.\n\r");
						
						out.write("PRIVMSG #vspace.one : Die Luftfeuchtigkeit im Maschinenraum beträgt: " 
								+ sqlHandler.getHumidity("maschinenraum") + "%.\n\r");
						
						out.write("PRIVMSG #vspace.one : Die Luftfeuchtigkeit auf der Brücke beträgt: " 
								+ sqlHandler.getHumidity("bruecke") + "%.\n\r");
						
						out.flush();
					}
				}
			} catch (java.io.IOException e) {
				System.out.println(e);
			}
		}
	}


	public void statusClosed() {

		try {
			out.write("PRIVMSG #vspace.one : Der vspace.one ist jetzt leider geschlossen.\n\r");
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public void statusOpen() {

		try {
			out.write("PRIVMSG #vspace.one : Der vspace.one ist jetzt geöffnet. Kommt vorbei!\n\r");
		} catch (Exception e) {
			System.out.println(e);
		}

	}


}