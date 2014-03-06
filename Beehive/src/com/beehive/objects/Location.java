package com.beehive.objects;

public class Location {

	private int id;
	private String name;
	private String url_pics;
	private double latitude;
	private double longitude;
	private double occupancy;
	private String timeToGo;
	private Boolean isCategory = false;

	public Location(int id, String name, String url_pics, double latitude, double longitude, double occupancy, String timeToGo) {
		super();
		this.id = id;
		this.name = name;
		this.url_pics = url_pics;
		this.latitude = latitude;
		this.longitude = longitude;
		this.occupancy = occupancy;
		this.timeToGo = timeToGo;
	}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl_pics() {
		return url_pics;
	}
	public void setUrl_pics(String url_pics) {
		this.url_pics = url_pics;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getOccupancy() {
		return occupancy;
	}
	public void setOccupancy(float occupancy) {
		this.occupancy = occupancy;
	}
	public String getTimeToGo() {
		return timeToGo;
	}
	public void setTimeToGo(String timeToGo) {
		this.timeToGo = timeToGo;
	}

	public Boolean getIsCategory() {
		return isCategory;
	}

	public void setIsCategory(Boolean isCategory) {
		this.isCategory = isCategory;
	}

}
