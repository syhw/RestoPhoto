package com.example.restophoto;

import java.util.List;
import com.example.restophoto.Resto;

public interface ReviewProvider {
		
	String getName();
	
	void getNearbyRestaurants(double latitude, double longitude);
	
	String getReview(Resto resto);
	
	String getReviewURL(Resto resto);
}
