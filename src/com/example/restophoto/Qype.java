package com.example.restophoto;

import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Qype implements ReviewProvider {

	@Override
	public String getName() {
		return "Qype";
	}
	
	@Override
	public List<Resto> getNearbyRestaurants(double latitude, double longitude) {
		HttpClient mClient= new DefaultHttpClient();
		HttpGet get = new HttpGet(String.format("http://api.qype.com/v1/positions/%f,%f/places?consumer_key=EYGgS1vansn8b7DemMOw", latitude, longitude));
		HttpResponse res;
		try {
			res = mClient.execute(get);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		 
		return null;
	}

	@Override
	public String getReview(Resto resto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReviewURL(Resto resto) {
		return resto.reviewURL;
	}

}
