package com.example.restophoto;

public class Resto {
	
	
	// General information
	String name = "";       // REQUIRED: restaurant name	
	String restoURL = "";   // restaurant webpage
	int open = -1;          // 0 false, 1 true, -1 N/A
	
	// Position
	double lattitude = 0;
	double longitude = 0;  
	double distance = -1;   // REQUIRED: distance to query coordinates
	
	// Address
	String street = "";
	String city = "";
	String countryCode = "";
	int houseCode = -1;
	int postCode = -1;
	
	// Review
	double rating = -1;      // REQUIRED
	double ratingMax = -1;   // REQUIRED
	String reviewURL = "";   // REQUIRED : webpage of the review
}
