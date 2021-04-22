package com.luckydraw.luckydraw.entities;

public class RaffleTicket {
	String uid;
	String eid;
	String rid;
	boolean participated;
	
	// This constructor is called when rid is generated
	public RaffleTicket(String rid,String uid) {
		super();
		this.uid = uid;
		this.rid = rid;
		this.participated = false;
	}
	
	public RaffleTicket(String uid,String eid,String rid, boolean participated) {
		super();
		this.uid = uid;
		this.eid = eid;
		this.rid = rid;
		this.participated = participated;
	}

	public RaffleTicket() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "RaffleTicket [uid=" + uid + ", eid=" + eid + ", participated=" + participated + "]";
	}
	
	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public boolean isParticipated() {
		return participated;
	}

	public void setParticipated(boolean participated) {
		this.participated = participated;
	}
	
}
