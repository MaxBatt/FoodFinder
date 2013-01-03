package de.hdm.foodfinder.server;

import static spark.Spark.get;

import java.util.ArrayList;

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

		// Alle Kaufgesuche eines Nutzers
		get(new Route("/restaurants") {
			@Override
			public Object handle(Request req, Response res) {
				res.type("application/json");

				try {
					/*
					 * String order = req.queryParams("longi"); String start =
					 * req.queryParams("lati"); int[] categories =
					 * req.queryParams("categories");
					 * 
					 * 
					 * 
					 * String order = req.queryParams("order");
					 * System.out.println(req.queryParams("order")); String
					 * start = req.queryParams("start"); String limit =
					 * req.queryParams("limit");
					 * 
					 * if(order == null) order = "id"; if(start == null) start =
					 * "0"; if(limit == null) limit = "20";
					 * 
					 * String longi = "9.06253"; String lati = "48.42918"; int[]
					 * categories = new int[]{1,2,3}; String[] dishes = new
					 * String[]{"Spaghetti", "Tomaten"}; String distance = "15";
					 * 
					 * 
					 * 
					 * Restaurant.RestaurantList list =
					 * Restaurant.getRestaurantList(longi, lati, categories,
					 * dishes, distance, order, start, limit); return
					 * list.getJson();
					 */

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
