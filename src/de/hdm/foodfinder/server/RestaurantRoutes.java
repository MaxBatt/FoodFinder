package de.hdm.foodfinder.server;

import static spark.Spark.get;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import de.hdm.foodfinder.orm.*;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Routen für Suchen
 * 
 * Alle Kaufgesuche eines Nutzers: GET /user/:id/petitions Alle Kaufgesuche
 * einer Kategorie: GET /category/:catId/petitions Alle Kaufgesuche: GET
 * /petitions Alle Kategorien: GET /categories (Alle Nutzer: GET /users)
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

					// return "lat: " + latitude + " long: " + longitude +
					// " dishes: " + dishes + " region " + region +
					// " categories: " + categories + " distance: " + distance;

					String[] dishesArr;
					int[] categoriesArr;
					dishesArr = gson.fromJson(dishes, String[].class);
					categoriesArr = gson.fromJson(categories, int[].class);

					Restaurant.RestaurantList list = Restaurant
							.getRestaurantList(latitude, longitude, dishesArr,
									region, categoriesArr, distance, "id", "0",
									"20");

					System.out.println(list.getJson());
					
					
					
					return list.getJson();

					// return "lol";

				} catch (Exception e) {
					res.status(Router.HTTP_SERVER_ERROR);
					return e.getMessage();
				}
			}
		});

		

	}
}
