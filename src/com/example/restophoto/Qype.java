package com.example.restophoto;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.widget.Toast;

public class Qype implements ReviewProvider {
	
	public List<Resto> restos; // TODO put in ReviewProvider
	
	@Override
	public String getName() {
		return "Qype";
	}
	
	@Override
	public void getNearbyRestaurants(double latitude, double longitude) {
		HttpClient mClient= new DefaultHttpClient();
		//new RequestTask().execute(String.format("http://api.qype.com/v1/positions/%f,%f/places?consumer_key=EYGgS1vansn8b7DemMOw&radius=20", latitude, longitude));
        
		HttpGet get = new HttpGet(String.format("http://api.qype.com/v1/positions/%f,%f/places?consumer_key=EYGgS1vansn8b7DemMOw&radius=5", latitude, longitude));
		HttpResponse res;
		try {
			res = mClient.execute(get);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(res.getEntity().getContent());
		    NodeList nodeList = doc.getElementsByTagName("place");
		    for (int i = 0; i < nodeList.getLength(); i++) {
		        Node node = nodeList.item(i);
		        //if (node.getNodeType() == Node.ELEMENT_NODE) {
		            // do something with the current element
		        	Resto tmp = new Resto();
		        	tmp.name = node.getFirstChild().getNodeValue();
		        	NodeList children = node.getChildNodes();
		        	for (int j = 0; j < children.getLength(); j++)
		        	{
		        		Node tmp_node = children.item(j);
		        		if (tmp_node.getNodeName() == "link" )
		        				//&& tmp_node.getAttributes(). == "en") TODO
		        			//tmp.reviewURL = tmp_node.getNodeValue(); TODO
		        			tmp.reviewURL = "http://www.qype.co.uk/place/110906-Chez-Gladines-Paris?utm_source=api&amp;amp;utm_medium=referal&amp;amp;utm_campaign=2739&amp;amp;utm_term=place";
		        	}
		        	restos.add(tmp);
		        //}
		    }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
	
    public Resto findBestMatch() {
    	return restos.get(0);
    }

}
