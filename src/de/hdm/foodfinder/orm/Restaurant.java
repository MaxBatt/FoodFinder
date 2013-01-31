package de.hdm.foodfinder.orm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.mysql.jdbc.Statement;

import de.hdm.foodfinder.errors.ffException;

/**
 * Restaurant
 * 
 * Serverseitige Represäntation eines Restaurants Bietet CRUD-Funktionalität für
 * Restaurant Bietet mit getRestaurantList die Möglichkeit nach bestimmten
 * Suchkriterien eine RestaurantListe aus der DB abzurufen
 * 
 * @author Max Batt
 * 
 *         basierend auf Petition.class by Max Batt & Andrian Riedl, eSell
 *         DBServer: https://github.com/MaxBatt/eSellDBServer
 * 
 */
public class Restaurant extends Persistence {

	private Integer id;
	private Integer ownerID;
	private String name;
	private String street;
	private String streetNumber;
	private String city;
	private String postcode;
	private String country;
	private String latitude;
	private String longitude;
	private String distance;

	private ArrayList<String> dishes;
	private ArrayList<String> regions;
	private ArrayList<String> categories;
	private ArrayList<String> photos;
	private String avgRating;

	/**
	 * Öffentlicher Konstruktor Dieser wird benutzt, um ein Objekt aus
	 * Benutzereingaben zu erzeugen und in der DB zu speichern
	 */
	public Restaurant(String name, String street, String streetNumber,
			String city, String postcode, String country, String longitude,
			String latitude) {
		this.name = name;
		this.street = street;
		this.streetNumber = streetNumber;
		this.city = city;
		this.postcode = postcode;
		this.country = country;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Privater Konstruktor Dieser wird benutzt, um ein Objekt aus einem
	 * Datensatz zu erzeugen Nimmt als Parameter ein ResultSet einer Abfrage
	 * entgegen
	 */
	private Restaurant(ResultSet result) throws SQLException, ffException {
		this.id = result.getInt("id");
		this.ownerID = result.getInt("owner_id");
		this.name = result.getString("name");
		this.street = result.getString("street");
		this.streetNumber = result.getString("street_number");
		this.city = result.getString("city");
		this.postcode = result.getString("postcode");
		this.country = result.getString("country");
		this.longitude = result.getString("longitude");
		this.latitude = result.getString("latitude");
		double distance = result.getDouble("distance");
		DecimalFormat df = new DecimalFormat("0.00");
		this.distance = df.format(distance) + "km";

		this.dishes = getDishesById(result.getInt("id"));
		this.regions = getRegionsById(result.getInt("id"));
		this.categories = getCategoriesById(result.getInt("id"));
		this.photos = getPhotosById(result.getInt("id"));
		this.avgRating = getAvgRatingById(result.getInt("id"));

	}

	/**
	 * Ruft den Datensatz für eine gegebene RestaurantID ab und mappt diesen auf
	 * ein Objekt
	 */
	public static Restaurant getRestaurant(int id) throws ffException {

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
				} else
					throw new ffException(ffException.ErrorCode.NO_ENTRY_ERR);
			} catch (SQLException e) {
				// ToDo
				e.printStackTrace();
				throw new ffException(ffException.ErrorCode.DB_ERR);
			}
		} else
			throw new ffException(ffException.ErrorCode.DB_ERR);
	}

	/**
	 * Legt in der DB für das aktuelle Objekt einen Datensatz an
	 */
	public void insert() throws ffException {
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
		} else
			throw new ffException(ffException.ErrorCode.DB_ERR);
	}

	/**
	 * Updatet den Datensatz mit aktuellen Werten des Objekts
	 */
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

	/**
	 * Restaurant-Datensatz löschen
	 */
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

	/**
	 * Gibt eine Liste aller Restaurants mit den gegebenen Suchkriterien aus
	 * Params: aktuelle Koordinaten longi und lati, arrays mit Suchkriterien
	 * (Kategorien und Gerichte: categories, dishes, distance (Umkreis) order:
	 * Sortierung (Name des Tabellenfelds), start und limit für
	 * Listenlimitierung
	 */
	public static RestaurantList getRestaurantList(String latitude,
			String longitude, String[] dishes, String region, int[] categories,
			String distance, String order, String start, String limit)
			throws ffException {
		makeConnection();
		PreparedStatement preparedStatement = null;

		if (conn != null) {

			try {
				// Statement vorbereiten
				String sql = "SELECT DISTINCT r. * , ( 6371 * acos( cos( radians( "
						+ latitude
						+ " ) ) * cos( radians( latitude ) ) * cos( radians( longitude ) - radians( "
						+ longitude
						+ " ) ) + sin( radians( "
						+ latitude
						+ " ) ) * sin( radians( latitude ) ) ) ) AS distance "
						+ " FROM restaurants r, dishes d, categories c, regions reg, res_has_dish rhd, res_has_reg rhr, res_has_cat rhc "
						+ " WHERE ("
						+ " r.id = rhd.restaurant_id"
						+ " AND rhd.dish_id = d.id"
						+ " AND r.id = rhc.restaurant_id"
						+ " AND rhc.category_id = c.id"
						+ " AND r.id = rhr.restaurant_id"
						+ " AND rhr.region_id = reg.id )";

				if (categories.length > 0 || dishes.length > 0
						|| region.length() > 0) {
					sql += " AND (";
				}

				if (dishes.length > 0) {
					sql += "( ";
					for (String d : dishes) {
						sql += "d.name LIKE '%" + d.replace("_", " ")
								+ "%' OR ";
					}
					sql = sql.substring(0, sql.length() - 3);
					sql += " ) OR ";
				}

				if (region.length() > 0) {
					sql += " ( reg.name LIKE '" + region.replace("_", " ")
							+ "' ) OR ";
				}

				if (categories.length > 0) {
					sql += " ( ";
					for (int c : categories) {
						sql += "rhc.category_id = " + c + " OR ";
					}
					sql = sql.substring(0, sql.length() - 3);
					sql += " ) OR ";
				}

				if (categories.length > 0 || dishes.length > 0
						|| region.length() > 0) {
					sql = sql.substring(0, sql.length() - 3);
					sql += ")";
				}

				sql += " HAVING distance <=  " + distance
						+ " ORDER BY distance ASC " + " LIMIT " + start + ", "
						+ limit;

				System.out.println(sql);

				// Statement absetzen
				preparedStatement = conn.prepareStatement(sql);
				ResultSet result = preparedStatement.executeQuery();

				ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();

				// Für jeden Datensatz ein Objekt anlegen und in die Liste
				// packen
				while (result.next()) {
					Restaurant restaurant = new Restaurant(result);
					restaurantList.add(restaurant);
				}

				return new RestaurantList(restaurantList);

			} catch (SQLException e) {
				e.printStackTrace();
				throw new ffException(ffException.ErrorCode.DB_ERR);
			}
		} else
			throw new ffException(ffException.ErrorCode.DB_ERR);
	}

	/**
	 * Innere Klasse RestaurantList
	 * 
	 * Hält eine Liste von RestaurantObjekten
	 */
	public static class RestaurantList {

		private ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();

		// Konstruktor
		private RestaurantList(ArrayList<Restaurant> restaurantList) {
			this.restaurantList = restaurantList;
		}

		public String getJson() {
			Gson gson = new Gson();
			String json = gson.toJson(this.restaurantList);
			return json;
		}

	}

	/*
	 * Methoden zum Abrufen von zusätzlichen Restaurant-Daten: Gerichte,
	 * Regionen, Kategorien, Fotos, Bewertung
	 */

	/**
	 * Ermittelt Gerichte eines Restaurants
	 */
	private ArrayList<String> getDishesById(int id) throws ffException {
		makeConnection();
		PreparedStatement preparedStatement = null;

		if (conn != null) {

			try {
				// Statement vorbereiten
				String sql = "SELECT d.name FROM restaurants r, res_has_dish rhd, dishes d"
						+ " WHERE r.id = "
						+ id
						+ " AND r.id = rhd.restaurant_id"
						+ " AND rhd.dish_id = d.id";

				System.out.println(sql);

				// Statement absetzen
				preparedStatement = conn.prepareStatement(sql);
				ResultSet result = preparedStatement.executeQuery();

				ArrayList<String> dishes = new ArrayList<String>();
				// Für jeden Datensatz ein Objekt anlegen und in die Liste
				// packen
				while (result.next()) {
					String dish = result.getString("name");
					dishes.add(dish);
				}
				return dishes;

			} catch (SQLException e) {
				e.printStackTrace();
				throw new ffException(ffException.ErrorCode.DB_ERR);
			}
		} else
			throw new ffException(ffException.ErrorCode.DB_ERR);

	}

	/**
	 * Ermittelt Regionen eines Restaurants
	 * 
	 * @param id
	 * @return
	 * @throws ffException
	 */
	private ArrayList<String> getRegionsById(int id) throws ffException {
		makeConnection();
		PreparedStatement preparedStatement = null;

		if (conn != null) {

			try {
				// Statement vorbereiten
				String sql = "SELECT reg.name FROM restaurants r, res_has_reg rhr, regions reg"
						+ " WHERE r.id = "
						+ id
						+ " AND r.id = rhr.restaurant_id"
						+ " AND rhr.region_id = reg.id";

				System.out.println(sql);

				// Statement absetzen
				preparedStatement = conn.prepareStatement(sql);
				ResultSet result = preparedStatement.executeQuery();

				ArrayList<String> regions = new ArrayList<String>();

				// Für jeden Datensatz ein Objekt anlegen und in die Liste
				// packen
				while (result.next()) {
					String dish = result.getString("name");
					regions.add(dish);
				}

				return regions;

			} catch (SQLException e) {
				e.printStackTrace();
				throw new ffException(ffException.ErrorCode.DB_ERR);
			}
		} else
			throw new ffException(ffException.ErrorCode.DB_ERR);

	}

	/**	
	 * Ermittelt Kategorien eines Restaurants
	 * 
	 * @param id
	 * @return
	 * @throws ffException
	 */
	private ArrayList<String> getCategoriesById(int id) throws ffException {
		makeConnection();
		PreparedStatement preparedStatement = null;

		if (conn != null) {

			try {
				// Statement vorbereiten
				String sql = "SELECT cat.name FROM restaurants r, res_has_cat rhc, categories cat"
						+ " WHERE r.id = "
						+ id
						+ " AND r.id = rhc.restaurant_id"
						+ " AND rhc.category_id= cat.id";

				System.out.println(sql);

				// Statement absetzen
				preparedStatement = conn.prepareStatement(sql);
				ResultSet result = preparedStatement.executeQuery();

				ArrayList<String> categories = new ArrayList<String>();

				// Für jeden Datensatz ein Objekt anlegen und in die Liste
				// packen
				while (result.next()) {
					String dish = result.getString("name");
					categories.add(dish);
				}

				return categories;

			} catch (SQLException e) {
				e.printStackTrace();
				throw new ffException(ffException.ErrorCode.DB_ERR);
			}
		} else
			throw new ffException(ffException.ErrorCode.DB_ERR);

	}

	/**	
	 * Ermittelt Fotos eines Restaurants
	 * 
	 * @param id
	 * @return
	 * @throws ffException
	 */
	private ArrayList<String> getPhotosById(int id) throws ffException {
		makeConnection();
		PreparedStatement preparedStatement = null;

		if (conn != null) {

			try {
				// Statement vorbereiten
				String sql = "SELECT * FROM photos WHERE restaurant_id = " + id;

				System.out.println(sql);

				// Statement absetzen
				preparedStatement = conn.prepareStatement(sql);
				ResultSet result = preparedStatement.executeQuery();

				ArrayList<String> photos = new ArrayList<String>();

				// Für jeden Datensatz ein Objekt anlegen und in die Liste
				// packen
				while (result.next()) {
					String photo = result.getString("path");
					photos.add(photo);
				}

				return photos;

			} catch (SQLException e) {
				e.printStackTrace();
				throw new ffException(ffException.ErrorCode.DB_ERR);
			}
		} else
			throw new ffException(ffException.ErrorCode.DB_ERR);

	}

	/** 
	 * Ermittelt durchschnittliche Bewertung eines Restaurants
	 * 
	 * @param id
	 * @return
	 * @throws ffException
	 */
	private String getAvgRatingById(int id) throws ffException {
		makeConnection();
		PreparedStatement preparedStatement = null;

		if (conn != null) {

			try {
				// Statement vorbereiten
				String sql = "SELECT AVG(rating) AS avg_rating FROM ratings WHERE restaurant_id ="
						+ id;

				System.out.println(sql);

				// Statement absetzen
				preparedStatement = conn.prepareStatement(sql);
				ResultSet result = preparedStatement.executeQuery();

				String avgRating = "";

				// Für jeden Datensatz ein Objekt anlegen und in die Liste
				// packen
				while (result.next()) {
					avgRating = result.getString("avg_rating");
				}

				return avgRating;

			} catch (SQLException e) {
				e.printStackTrace();
				throw new ffException(ffException.ErrorCode.DB_ERR);
			}
		} else
			throw new ffException(ffException.ErrorCode.DB_ERR);

	}

}
