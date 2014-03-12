package com.beehive.objects;

import java.util.ArrayList;

public class AddInfoZone {

	private int id;
	private String name;
	private String description;
	private ArrayList<String> urlPics;
	private String urlPic;
	private boolean isSubZone = true;


	public AddInfoZone(int id, String name, String description, String urlPic, ArrayList<String> urlPics) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.urlPics = urlPics;
		this.urlPic = urlPic;
		this.isSubZone = false;
	}
	
	public AddInfoZone(int id, String name, String description, String urlPic) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.urlPic = urlPic;
		this.isSubZone = true;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<String> getUrlPics() {
		return urlPics;
	}

	public void setUrlPics(ArrayList<String> urlPics) {
		this.urlPics = urlPics;
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

	@Override
	public String toString() {
		return "AddInfoZone [id=" + id + ", name=" + name + ", description="
				+ description + ", urlPics=" + urlPics + ", urlPic=" + urlPic
				+ ", isSubZone=" + isSubZone + "]";
	}
}
