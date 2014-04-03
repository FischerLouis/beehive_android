package com.beehive.objects;

public class RealTimeInfoZone {

	private int id;
	private String occupancy;
	private String bestTime;

	public RealTimeInfoZone(int id, String occupancy, String bestTime) {
		super();
		this.id = id;
		this.occupancy = occupancy;
		this.bestTime = bestTime;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOccupancy() {
		return occupancy;
	}
	public void setOccupancy(String occupancy) {
		this.occupancy = occupancy;
	}
	public String getBestTime() {
		return bestTime;
	}
	public void setBestTime(String bestTime) {
		this.bestTime = bestTime;
	}

	@Override
	public String toString() {
		return "RealTimeInfoZone [id=" + id + ", occupancy=" + occupancy
				+ ", bestTime=" + bestTime + "]";
	}

}
