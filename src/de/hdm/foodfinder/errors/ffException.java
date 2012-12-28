package de.hdm.foodfinder.errors;


import com.google.gson.Gson;

/*
 * ffException
 * Führt einen Katalog mit Fehlermeldungen.
 * Je nach errorCode, mit dem instanziiert wird, wird eine entsprechende Fehlermeldung zurückgegeben. 
 * 
 */

public class ffException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ErrorCode errorCode;

	/*
	 * Konstruktor
	 * Muss mit ErrorCode aufgerufen werden
	 * 
	 */
	public ffException(ErrorCode errorCode) {
		super(errorCode.message);
		
		this.errorCode = errorCode;

	}
	
	public ErrorCode getErrorCode() {
		return errorCode;
	}
	
	public static enum ErrorCode {
		DB_ERR("Fehler beim Verbinden zur Datenbank"),
		DELETE_ERR("Fehler beim Löschen"),
		INSERT_ERR("Fehler beim Speichern in der Datenbank"),
		NO_ENTRY_ERR("Kein DB-Eintrag vorhanden"),
		UPDATE_ERR("Fehler beim Aktualisieren"),
		USR_HAS_PET_ERR("Der Benutzer kann nicht gelöscht werden, weil ihm noch Kaufgesuche zugewiesen sind"),
		USR_EXISTS("Username oder Email schon vorhanden"),
		ERR("Ein unbekannter Fehler ist aufgetreten")
        ;
        public String message;
        ErrorCode(String message) {
            this.message = message;
        }
}
	
	
	//Json ausgeben
	public String getJson(){
		Gson gson = new Gson();
		String json = gson.toJson(this);
		return json; 
	}
	
}
