package com.example.com.reeder.gotoblu;


public class Position {
	double longitude;
	double latitude;
	
	public Position(){
		longitude = 0;
		latitude = 0;
	}
	
	public Position(double lat, double lon){
		longitude = lon;
		latitude = lat;
	}
	
	public double getLongitude(){
		return longitude;
	}
	
	public double getLatitude(){
		return latitude;
	}
	
	public void setLong(double lon){
		longitude = lon;
	}
	
	public void setLat(double lat){
		latitude = lat;
	}
	
	public String toString(){
		return "Lon: " + longitude + " Lat: "+ latitude;
	}
}
