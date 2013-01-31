package de.hdm.foodfinder.server;

import org.apache.log4j.Logger;

/**
 * Router Enthält MainMethode. Führt Routing aus
 * 
 * @author Max Batt
 * 
 *         angelehnt an Router von Matthias Zaunseder
 *         https://github.com/zauni/WebAppArchitecture
 * 
 */
public class Router {

	public static final int HTTP_OKAY = 200;
	public static final int HTTP_CREATED = 201;
	public static final int HTTP_SERVER_ERROR = 500;

	public static Logger LOG = Logger.getLogger(Router.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Restaurant Routen
		new RestaurantRoutes();
	}

}
