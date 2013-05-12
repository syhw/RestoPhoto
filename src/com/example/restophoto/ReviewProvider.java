package com.example.restophoto;

import java.util.List;

public interface ReviewProvider {
	String getName();
		
	List<Resto> getNearbyRestaurants(double latitude, double longitude);
	
	String getReview(Resto resto);
	
	String getReviewURL(Resto resto);
}
