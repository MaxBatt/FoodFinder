package de.hdm.foodfinder.orm;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.gson.Gson;

/**
 * Persistence
 * 
 * Stellt Verbindung zur DB her
 * 
 * @author Max Batt
 *
 */
public abstract class Persistence {

	protected static Connection conn = null;
	 
    // Hostname
    private static String dbHost = "localhost";
 
    // Port -- Standard: 3306
    private static String dbPort = "8889";
 
    // Datenbankname
    private static String database = "foodfinder";
 
    // Datenbankuser
    private static String dbUser = "root";
 
    // Datenbankpasswort
    private static String dbPassword = "root";

     
    public static void makeConnection()
    {
        if(conn == null)
 {
			try {
				// Datenbanktreiber für ODBC Schnittstellen laden.
				// Für verschiedene ODBC-Datenbanken muss dieser Treiber
				// nur einmal geladen werden.
				Class.forName("com.mysql.jdbc.Driver");

				// Verbindung zur ODBC-Datenbank herstellen.
				// Es wird die JDBC-ODBC-Brücke verwendet.
				conn = DriverManager.getConnection("jdbc:mysql://" + dbHost
						+ ":" + dbPort + "/" + database + "?" + "user="
						+ dbUser + "&" + "password=" + dbPassword);
			} catch (ClassNotFoundException e) {
				System.out.println("Treiber nicht gefunden");
			} catch (SQLException e) {
				System.out.println("Connect nicht moeglich: " + e);
			}
		}
    }
    
    
  //Gibt Objekt als JSON-String zurŸck
	public String getJson(){
		Gson gson = new Gson();
		String json = gson.toJson(this);
		return json;
	}
}

