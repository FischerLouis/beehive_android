package com.beehive.objects;

public class Zone {
	
	private int id;
	private String name;
	private double latitude;
	private double longitude;
	private String description;
	private String urlPic;
	private boolean isSubZone = true;
	private int idZone;
	
	// Constructor Zone
	public Zone(int id, String name, double latitude, double longitude,
			String description, String urlPic) {
		super();
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.description = description;
		this.urlPic = urlPic;
		this.isSubZone = false;
	}
	
	// Constructor SubZone
	public Zone(int id, String name, double latitude, double longitude,
			String description, String urlPic, int idZone) {
		super();
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.description = description;
		this.urlPic = urlPic;
		this.isSubZone = true;
		this.idZone = idZone;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrlPic() {
		return urlPic;
	}
	public void setUrlPic(String urlPic) {
		this.urlPic = urlPic;
	}
	public boolean isSubZone() {
		return isSubZone;
	}
	public void setSubZone(boolean isSubZone) {
		this.isSubZone = isSubZone;
	}
	public int getIdZone() {
		return idZone;
	}
	public void setIdZone(int idZone) {
		this.idZone = idZone;
	}
	
	@Override
	public String toString() {
		return "Zone [id=" + id + ", name=" + name + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", description=" + description
				+ ", urlPic=" + urlPic + ", isSubZone=" + isSubZone
				+ ", idZone=" + idZone + "]";
	}
}
