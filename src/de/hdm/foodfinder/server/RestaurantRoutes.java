package de.hdm.foodfinder.server;

import static spark.Spark.get;

import com.google.gson.Gson;

import de.hdm.foodfinder.orm.*;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * RestaurantRoutes
 * 
 * Erstellt Server-Routen für Restaurant-Abfragen Bis jetzt gibt es nur die
 * Route /restaurants für die Restaurantliste
 * 
 * @author Max Batt
 * 
 */

public class RestaurantRoutes {

	public RestaurantRoutes() {

		// Restaurants mit Parametern
		get(new Route("/restaurants") {
			@Override
			public Object handle(Request req, Response res) {
				res.type("application/json");

				try {
					// JSON Helper
					Gson gson = new Gson();

					// Parameter auswerten

					String latitude = req.queryParams("latitude");
					String longitude = req.queryParams("longitude");
					String dishes = req.queryParams("dishes");
					String region = req.queryParams("region");
					String categories = req.queryParams("categories");
					String distance = req.queryParams("distance");

					// Aus Dishes und Categories String-Arrays machen
					String[] dishesArr;
					int[] categoriesArr;

					// String Arrays in JSON wandeln
					dishesArr = gson.fromJson(dishes, String[].class);
					categoriesArr = gson.fromJson(categories, int[].class);

					// RestaurantListe aus Params erstellen
					Restaurant.RestaurantList list = Restaurant
							.getRestaurantList(latitude, longitude, dishesArr,
									region, categoriesArr, distance, "id", "0",
									"20");

					// Ergebnis als JSON zurückgeben
					return list.getJson();

				} catch (Exception e) {
					res.status(Router.HTTP_SERVER_ERROR);
					return e.getMessage();
				}
			}
		});

	}
}
