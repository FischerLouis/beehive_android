package com.beehive.objects;

public class RealTimeInfoZone {

	private int id;
	private int occupancy;
	private String bestTime;
	private String queue;
	private int thresholdMin;
	private int thresholdMax;

	public RealTimeInfoZone(int id, int occupancy, String bestTime, String queue, int thresholdMin, int thresholdMax) {
		super();
		this.id = id;
		this.occupancy = occupancy;
		this.bestTime = bestTime;
		this.queue = queue;
		this.thresholdMin = thresholdMin;
		this.thresholdMax = thresholdMax;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOccupancy() {
		return occupancy;
	}
	public void setOccupancy(int occupancy) {
		this.occupancy = occupancy;
	}
	public String getBestTime() {
		return bestTime;
	}
	public void setBestTime(String bestTime) {
		this.bestTime = bestTime;
	}
	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public int getThresholdMin() {
		return thresholdMin;
	}

	public void setThresholdMin(int thresholdMin) {
		this.thresholdMin = thresholdMin;
	}

	public int getThresholdMax() {
		return thresholdMax;
	}

	public void setThresholdMax(int thresholdMax) {
		this.thresholdMax = thresholdMax;
	}

	@Override
	public String toString() {
		return "RealTimeInfoZone [id=" + id + ", occupancy=" + occupancy
				+ ", bestTime=" + bestTime + ", queue=" + queue
				+ ", thresholdMin=" + thresholdMin + ", thresholdMax="
				+ thresholdMax + "]";
	}
}
