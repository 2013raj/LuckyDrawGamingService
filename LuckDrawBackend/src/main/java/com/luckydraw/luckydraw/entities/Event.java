package com.luckydraw.luckydraw.entities;

public class Event {
	private String eid;
	private String date;
	private String time;
	private String prize;
	private String winner;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getPrize() {
		return prize;
	}
	public void setPrize(String prize) {
		this.prize = prize;
	}
	public String getWinner() {
		return winner;
	}
	public void setWinner(String winner) {
		this.winner = winner;
	}
	public String getEid() {
		return eid;
	}
	public void setEid(String eid) {
		this.eid = eid;
	}
	public Event(String eid, String date, String time, String prize, String winner) {
		super();
		this.eid = eid;
		this.date = date;
		this.time = time;
		this.prize = prize;
		this.winner = winner;
	}
	
	
}
