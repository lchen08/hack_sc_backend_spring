package com.example.demo.data.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.text.html.HTMLDocument.Iterator;

import com.example.demo.data.Geolocation;
import com.example.demo.data.Product;
import com.example.demo.data.repository.StoreInventoryRepository;

//MongoDB
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;


import static com.mongodb.client.model.Filters.eq;


public class FS_ProductManager implements ProductManager{
	
	// Connect to database
	// String uri = "mongodb://steve:AsecretPassword@13.57.202.190:27017/mycustom_db";
	// MongoClientURI clientUri = new MongoClientURI(uri);
	// MongoClient mongoClient = new MongoClient(clientUri);
	// MongoDatabase mongoDatabase = mongoClient.getDatabase("mycustom_db");
	
	@Autowired
	private StoreInventoryRepository storeInventoryRepository;

	@Override
	public List<Product> getProductRadius(String productName, Geolocation geo, double miles) {
		List<Product> list = new ArrayList();
		Collection<Product> collection = getProductByName(productName);
		for (Product product: collection) {
			if(miles>calcGPSDistance(product.getGeolocation(), geo))
				list.add(product);
		}
		return list;
	}

	@Override
	public Collection<Product>getProductByName(String name) {
		return storeInventoryRepository.findByNameLike(name);
	}
	
	private double calcGPSDistance(Geolocation geo1, Geolocation geo2) {
		final int EARTH_RADIUS = 6371; // Radius of the earth
		final int MILLIMETERS_IN_METER = 1000;
		final double METERS_IN_ONE_MILE = 1609.34;
		double latitude1 = geo1.getLatitude();
		double latitude2 = geo2.getLatitude();
		double longitude1 = geo1.getLongitude();
		double longitude2 = geo2.getLongitude();
	    double latDistance = Math.toRadians(latitude2 - latitude1);
	    double longDistance = Math.toRadians(longitude2 - longitude1);
	    //calculates distance using radians
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
	            * Math.sin(longDistance / 2) * Math.sin(longDistance / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

	    return EARTH_RADIUS * c * MILLIMETERS_IN_METER / METERS_IN_ONE_MILE; //convert mm to mile
	}

}
