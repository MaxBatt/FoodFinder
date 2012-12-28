package de.hdm.foodfinder.server;

import de.hdm.foodfinder.orm.*;
import de.hdm.foodfinder.orm.Restaurant.RestaurantList;
import de.hdm.foodfinder.errors.*;

public class Test {

	/**
	 * @param args
	 * @throws ffException 
	 */
	public static void main(String[] args) throws ffException {
		// TODO Auto-generated method stub
		
		//Restaurant r = Restaurant.getRestaurant(3);
		//Restaurant r = new Restaurant("Ess-Punkt", "sdf", "13", "TŸ", "72074", "DE", "9.2323", "40.34");
		//r.insert();
		//r.delete();
		
		//System.out.println(r.getJson());
		
		RestaurantList rList = Restaurant.getRestaurantList("id", 0, 10);
		
		System.out.println(rList.getJson());	
	}

}
