# SpaceAPI Server Alpha
Interface um die SpaceAPI zu ändern.


Über TCP Verbindungen (durch z.B. Telnet) lässt sich der Öffnungstatus ändern, Temperaturen für verschiedenen Räume setzten und der Status abfragen.\\
Der Zustände werden in einer mySQL Datenbenk gespeichert und verwaltet.\\
Aus der Datenbank wird das SpaceAPI JSON generiert.\\
Über einen IRC-Bot wird der Status mitgeteilt und kann abgefragt werden.


Kommandozeilenargumente: Config-File


## Config-File Optionen
Port:XXXX (Interface-Port)\\
ExportPath:/foo/bar/ (Pfad zum JSON)\\
TelnetPassword:XXXX (Passwort für änderungen)\\

DBHostname:XXXX (Hostname der Dtaenbank)\\
DBPort:XXXX (Datenbankport)\\
DBName:XXXX (Name der Datenbank)\\
DBUser:XXXX (Datenbankuser)\\
DBPassword:XXXX (Datenbankpasswort)\\

IRCUser:XXXX (IRC Username)\\
IRCServer:XXXX (IRC Server/Network)\\
IRCPort:XXXX (IRC Port)\\
IRCChannel:XXXX (IRC Channel)
