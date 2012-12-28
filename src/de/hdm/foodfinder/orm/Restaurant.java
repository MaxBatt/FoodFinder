package de.hdm.foodfinder.orm;

//import hdm.stuttgart.esell.errors.ESellException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.mysql.jdbc.Statement;

import de.hdm.foodfinder.errors.ffException;

public class Restaurant extends Persistence {

	private Integer id;
	private Integer ownerID;
	private String name;
	private String street;
	private String streetNumber;
	private String city;
	private String postcode;
	private String country;
	private String longitude;
	private String latitude;
	
	/*
	 * Öffentlicher Konstruktor
	 * Dieser wird benutzt, um ein Objekt aus Benutzereingaben zu erzeugen und in der DB zu speichern 
	 */
	public Restaurant(String name, String street, String streetNumber, String city, String postcode, String country, String longitude, String latitude) {
		this.name 			= name;
		this.street 		= street;
		this.streetNumber 	= streetNumber;
		this.city 			= city;
		this.postcode 		= postcode;
		this.country		= country;
		this.longitude 		= longitude;
		this.latitude 		= latitude;
	}
	
	/*
	 * Privater Konstruktor
	 * Dieser wird benutzt, um ein Objekt aus einem Datensatz zu erzeugen
	 * Nimmt als Parameter ein ResultSet einer Abfrage entgegen
	 */
	private Restaurant(ResultSet result) throws SQLException{
		this.id 			= result.getInt("id");
		this.ownerID 		= result.getInt("owner_id");
		this.name			= result.getString("name");
		this.street 		= result.getString("street");
		this.streetNumber 	= result.getString("street_number");
		this.city 			= result.getString("city");
		this.postcode 		= result.getString("postcode");
		this.country 			= result.getString("country");
		this.longitude 		= result.getString("longitude");
		this.latitude 		= result.getString("latitude");
	}


	/*
	 * Ruft den Datensatz für eine gegebene RestaurantID ab und mappt diesen auf ein Objekt
	 */
	public static Restaurant getRestaurant(int id)  throws ffException  {

		makeConnection();
		PreparedStatement preparedStatement = null;

		if (conn != null) {
			try {

				// Statement vorbereiten
				String sql = "SELECT * FROM restaurants WHERE (id=?)";
				preparedStatement = conn.prepareStatement(sql);
				preparedStatement.setInt(1, id);

				// Statement absetzen und in resultSet speichern
				ResultSet result = preparedStatement.executeQuery();

				// Restaurant-Objekt anlegen
				if (result.next()) {
					return new Restaurant(result);	
				}
				else
					 throw new ffException(ffException.ErrorCode.NO_ENTRY_ERR); 
			} catch (SQLException e) {
				// ToDo
				e.printStackTrace();
				throw new ffException(ffException.ErrorCode.DB_ERR);
			}
		}
		else
			throw new ffException(ffException.ErrorCode.DB_ERR);
	}

	/*
	 * Legt in der DB für das aktuelle Objekt einen Datensatz an
	 */
	public void insert() throws ffException 
	{
		makeConnection();
		PreparedStatement preparedStatement = null;
		ResultSet res;

		if (conn != null) {
			try {

				// Statement vorbereiten
				
				String sql = "INSERT INTO restaurants (owner_id, name, street, street_number, city, postcode, country, longitude, latitude) VALUES (?,?,?,?,?,?,?,?,?)";
				
				preparedStatement = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);
				
				if (this.ownerID != null)
					preparedStatement.setInt(1, this.ownerID);
				else
					preparedStatement.setNull(1, Types.INTEGER);
				
				preparedStatement.setString(2, this.name);
				preparedStatement.setString(3, this.street);
				preparedStatement.setString(4, this.streetNumber);
				preparedStatement.setString(5, this.city);
				preparedStatement.setString(6, this.postcode);
				preparedStatement.setString(7, this.country);
				preparedStatement.setString(8, this.longitude);
				preparedStatement.setString(9, this.latitude);
				
				// System.out.println(preparedStatement);
				
				// Statement absetzen
				preparedStatement.execute();

				// InsertID ermitteln
				res = preparedStatement.getGeneratedKeys();
				if (res.next()) {
					this.id = res.getInt(1);
					return;
				} else {
					throw new ffException(ffException.ErrorCode.INSERT_ERR);
				}

			} catch (SQLException e) {
				// ToDo
				e.printStackTrace();
				throw new ffException(ffException.ErrorCode.DB_ERR);
			}
		}
		else	
			throw new ffException(ffException.ErrorCode.DB_ERR);
	}

	// Updatet den Datensatz mit aktuellen Werten des Objekts
	public void update() throws ffException {

		if (id == null) // Restaurant wurde noch nicht mit der DB abgeglichen.
			throw new ffException(ffException.ErrorCode.NO_ENTRY_ERR);
		
		makeConnection();
		PreparedStatement preparedStatement = null;

		if (conn != null) {
			try {
				// Statement vorbereiten
				String sql = "UPDATE restaurants SET owner_id = ?, name = ?, street = ?, street_number = ?, city = ?, postcode = ?, country = ?, longitude = ?, latitude = ? WHERE id = ?";
				
				preparedStatement = conn.prepareStatement(sql);
				
				preparedStatement.setInt(1, this.ownerID);
				preparedStatement.setString(2, this.name);
				preparedStatement.setString(3, this.street);
				preparedStatement.setString(4, this.streetNumber);
				preparedStatement.setString(5, this.city);
				preparedStatement.setString(6, this.postcode);
				preparedStatement.setString(7, this.country);
				preparedStatement.setString(8, this.longitude);
				preparedStatement.setString(9, this.latitude);
				preparedStatement.setInt(10, this.id);

				// System.out.println(preparedStatement);

				// Statement absetzen
				int affectedRows = preparedStatement.executeUpdate();

				if (affectedRows > 0)
					return;
				else
					throw new ffException(ffException.ErrorCode.UPDATE_ERR);

			} catch (SQLException e) {
				// ToDo
				// e.printStackTrace();
				throw new ffException(ffException.ErrorCode.DB_ERR);
			}
		}
		throw new ffException(ffException.ErrorCode.DB_ERR);
	
	
	}
	
	// Restaurant-Datensatz löschen
	public void delete() throws ffException {

		if (id == null) // Restaurant wurde noch nicht mit der DB abgeglichen.
			throw new ffException(ffException.ErrorCode.NO_ENTRY_ERR);
			
		makeConnection();
		PreparedStatement preparedStatement = null;

		if (conn != null) {
			try {
				// Statement vorbereiten
				String sql = "DELETE FROM restaurants WHERE id = ?";
				preparedStatement = conn.prepareStatement(sql);
				preparedStatement.setInt(1, this.id);

				// Statement absetzen
				int affectedRows = preparedStatement.executeUpdate();

				if (affectedRows > 0)
					 return;
				else
					throw new ffException(ffException.ErrorCode.DELETE_ERR);

			} catch (SQLException e) {
				// ToDo
				// e.printStackTrace();
				throw new ffException(ffException.ErrorCode.DB_ERR);
			}
		}
		throw new ffException(ffException.ErrorCode.DB_ERR);
	}

	
	//Empfängt als Parameter Sortierung, Start-Zeile und Limit
	//für Order einfach den Namen des jeweiligen Tabellenfelds benutzen
	public static RestaurantList getRestaurantList(String order, int start, int limit) throws ffException{
		makeConnection();
    	PreparedStatement preparedStatement = null;
    	
        if(conn != null)
        {
            try {
            	//Statement vorbereiten
                String sql = "SELECT * from restaurants ORDER BY ? LIMIT ?, ?";
                preparedStatement = conn.prepareStatement(sql);
                
                preparedStatement.setString(1, order);
                preparedStatement.setInt(2, start);
                preparedStatement.setInt(3, limit);
                
                //Statement absetzen
                ResultSet result = preparedStatement.executeQuery();
                
                ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();
                
                //Für jeden Datensatz ein Objekt anlegen und in die Liste packen
                while(result.next())
                {
                	Restaurant restaurant = new Restaurant(result);
                	restaurantList.add(restaurant);
                }
                
                return new RestaurantList(restaurantList);
                
            } catch (SQLException e) {
                e.printStackTrace();
                throw new ffException(ffException.ErrorCode.DB_ERR);
            }
        }
        else
        	throw new ffException(ffException.ErrorCode.DB_ERR);
	}
	
	
	/*
	
	//Gibt die PetitionList eines bestimmten Users zurück
		//Empfängt als Parameter UserID,  Sortierung, Start-Zeile und Limit
		public static PetitionList getPetitionListByUser(int userID, String order, int start, int limit) throws ESellException{
			
			makeConnection();
	    	PreparedStatement preparedStatement = null;
	    	
	        if(conn != null)
	        {
	            try {
	            	//Statement vorbereiten
	                String sql = "SELECT * from petitions WHERE user_id = ? ORDER BY ? LIMIT ?, ?";
	                preparedStatement = conn.prepareStatement(sql);
	                
	                preparedStatement.setInt(1, userID);
	                preparedStatement.setString(2, order);
	                preparedStatement.setInt(3, start);
	                preparedStatement.setInt(4, limit);
	                
	                //Statement absetzen
	                ResultSet result = preparedStatement.executeQuery();
	                
	                ArrayList<Petition> petitionList = new ArrayList<Petition>();
	                
	                //Für jeden Datensatz ein Objekt anlegen und in die Liste packen
	                while(result.next())
	                {
	                	Petition petition = new Petition(result);
	                	petitionList.add(petition);
	                }
	                
	                return new PetitionList(petitionList);
	            } catch (SQLException e) {
	                e.printStackTrace();
	                throw new ESellException(ESellException.ErrorCode.DB_ERR);
	            }
	        }
	        else
	        	throw new ESellException(ESellException.ErrorCode.DB_ERR);
		}
	
		
		//Gibt die PetitionList einer bestimmten Kategorie zurück
				//Empfängt als Parameter CategoryID,  Sortierung, Start-Zeile und Limit
				public static PetitionList getPetitionListByCategory(int categoryID, String order, int start, int limit) throws ESellException{
					
					makeConnection();
			    	PreparedStatement preparedStatement = null;
			    	
			        if(conn != null)
			        {
			            try {
			            	//Statement vorbereiten
			                String sql = "SELECT * from petitions WHERE category_id = ? ORDER BY ? LIMIT ?, ?";
			                preparedStatement = conn.prepareStatement(sql);
			                
			                preparedStatement.setInt(1, categoryID);
			                preparedStatement.setString(2, order);
			                preparedStatement.setInt(3, start);
			                preparedStatement.setInt(4, limit);
			                
			                //Statement absetzen
			                ResultSet result = preparedStatement.executeQuery();
			                
			                ArrayList<Petition> petitionList = new ArrayList<Petition>();
			                
			                //Für jeden Datensatz ein Objekt anlegen und in die Liste packen
			                while(result.next())
			                {
			                	Petition petition = new Petition(result);
			                	petitionList.add(petition);
			                }
			                
			                return new PetitionList(petitionList);
			            } catch (SQLException e) {
			                e.printStackTrace();
			                throw new ESellException(ESellException.ErrorCode.DB_ERR);
			            }
			        }
			        else
			        	throw new ESellException(ESellException.ErrorCode.DB_ERR);
				}
	*/

	
	public static class RestaurantList{
		
		private ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();
		
		//Konstruktor
				private RestaurantList(ArrayList<Restaurant> restaurantList)
				{
					this.restaurantList = restaurantList;
				}
				
				public String getJson(){
					Gson gson = new Gson();
					String json = gson.toJson(this.restaurantList);
					return json; 
				}
		
	}
}
