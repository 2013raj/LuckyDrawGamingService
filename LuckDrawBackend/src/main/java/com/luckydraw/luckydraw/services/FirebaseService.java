package com.luckydraw.luckydraw.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.luckydraw.luckydraw.entities.Event;
import com.luckydraw.luckydraw.entities.RaffleTicket;
import com.luckydraw.luckydraw.entities.User;

@Service
public class FirebaseService {
	
	/// Validates User ID from Database
	private boolean validateUserId(String userId) {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("users").document(userId);
		try {
			return docRef.get().get().exists();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean validateEventId(String eventId) {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("events").document(eventId);
		try {
			return docRef.get().get().exists();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/// Fetches UserName using user ID from DB
	private String getUserName(String userId) throws InterruptedException, ExecutionException {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("users").document(userId);
		ApiFuture<DocumentSnapshot> future  = docRef.get();
		DocumentSnapshot document = future.get();
		return document.getData().get("name").toString();
	}
	
	/// Populated events table with prizes and date-time [ADMIN_ACCESS_ONLY] 
	public String populateEventsTable() {
		String prizes[]= {"Washing Machine", "Refrigerator","Mobile Phone", "Laptop", "Bluetooth Earphones", "Microwave"};
		LocalDate myDateObj = LocalDate.now();
		String time = "08:00 AM";
		Firestore dbFirestore = FirestoreClient.getFirestore();
		
		// For two months 
		for(int i=-10;i<=30;i++) {
			String incrementedDate = LocalDate.parse(myDateObj.toString()).plusDays(i).toString();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
			String formattedDate = LocalDate.parse(incrementedDate).format(myFormatObj);
			
			DocumentReference docRef = dbFirestore.collection("events").document();
			Event newEvent = new Event(docRef.getId(),formattedDate,time,prizes[Math.abs(i % prizes.length)],"Not Announced");
			docRef.set(newEvent);
		}
		return "SUCCESS";
	}
	
	/// Registers user and return User ID
	public ResponseEntity<HashMap<String,String>> registerNewUser(User user) {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("users").document();
		docRef.set(user);
		String uid = docRef.getId();
		
		HashMap<String,String> responseObj = new HashMap<String,String>();
		responseObj.put("uid", uid);
		responseObj.put("text","User Registration Successful");
		ResponseEntity<HashMap<String,String>> response = new ResponseEntity<>(responseObj,HttpStatus.OK);
		return response;
	}
	
	/// Generates a raffle-ticket for a user for participation in a lucky draw event
	public ResponseEntity<HashMap<String,String>> generateRaffleTicket(String uid) {
		HashMap<String,String> responseObj = new HashMap<String,String>();
		
		if(uid==null) {
			responseObj.put("text","User ID not found");
			return new ResponseEntity<>(responseObj,HttpStatus.NOT_FOUND);
		}
		if(!this.validateUserId(uid)) {
			responseObj.put("text","Invalid User ID");
			return new ResponseEntity<>(responseObj,HttpStatus.NOT_FOUND);
		}
		
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("raffle-tickets").document();
		String rid = docRef.getId();
		docRef.set(new RaffleTicket(rid,uid));
		
		responseObj.put("rid", rid);
		responseObj.put("uid", uid);
		responseObj.put("text","Raffle Ticket Generated");
		ResponseEntity<HashMap<String,String>> response = new ResponseEntity<>(responseObj,HttpStatus.OK);
		return response;
	}
	
	/// Participates user with raffle-ticket in an event
	public ResponseEntity<HashMap<String,String>> participateInEvent(RaffleTicket raffleTicket) {
		HashMap<String,String> responseObj = new HashMap<String,String>();
		
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("raffle-tickets").document(raffleTicket.getRid());
		// Get uId from rId
		String userId="";
		boolean participated=false;
		try {
			ApiFuture<DocumentSnapshot> future  = docRef.get();
			DocumentSnapshot document = future.get();
			userId = document.getData().get("uid").toString();
			participated = (boolean) document.getData().get("participated");
		}
		catch(Exception e) {
			e.printStackTrace();
			responseObj.put("text","Invalid Raffle-ticket Id");
			return new ResponseEntity<>(responseObj,HttpStatus.NOT_FOUND);
		}
		
		// two checks: 
		// if raffle-ticket is unused
		if(participated) {
			responseObj.put("text","This raffle ticket has already been used");
			return new ResponseEntity<>(responseObj,HttpStatus.BAD_REQUEST);
		}
		
		
		// if user has participated in following event
		DocumentReference eventDocRef =  dbFirestore.collection("events").document(raffleTicket.getEid())
				.collection("participated").document(userId);
		try {
			if(eventDocRef.get().get().exists()) {
				responseObj.put("text","User has already participated in this event");
				return new ResponseEntity<>(responseObj,HttpStatus.BAD_REQUEST);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		// two changes has to be done in DB: 
		// one change in raffle ticket 
		Map<String,Object> m = new HashMap<String,Object>();
		m.put("eid",raffleTicket.getEid());
		m.put("participated",true);
		docRef.update(m);
		
		//and another change in events table
		Map<String,Object> nm = new HashMap<String,Object>();
		nm.put("rid",raffleTicket.getRid());
		dbFirestore.collection("events").document(raffleTicket.getEid())
			.collection("participated").document(userId).set(nm);
		
		responseObj.put("uid",userId);
		responseObj.put("rid",raffleTicket.getRid());
		responseObj.put("eid",raffleTicket.getEid());
		responseObj.put("text","Participation Successful! All the best.");
		return new ResponseEntity<>(responseObj,HttpStatus.OK);
	}
	
	/// Gets winners of all events in the last one week
	public ResponseEntity<HashMap<String,String>> fetchWinners() throws InterruptedException, ExecutionException{
		HashMap<String,String> responseObj = new HashMap<String,String>();
		Firestore dbFirestore = FirestoreClient.getFirestore();
		LocalDate myDateObj = LocalDate.now();
		
		//fetches winner for past week
		for(int i=0;i<7;i++) {
			String incrementedDate = LocalDate.parse(myDateObj.toString()).minusDays(i).toString();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
			String formattedDate = LocalDate.parse(incrementedDate).format(myFormatObj);
			
			String winner="";
			try {
				ApiFuture<QuerySnapshot> future= dbFirestore.collection("events").whereEqualTo("date", formattedDate).get();
				List<QueryDocumentSnapshot> documents = future.get().getDocuments();
				DocumentSnapshot document = documents.get(0);;
				winner = document.getData().get("winner").toString();
				if(!winner.equals("Not Announced")) {
					winner = this.getUserName(winner);
				}
				responseObj.put(formattedDate, winner);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return new ResponseEntity<>(responseObj,HttpStatus.OK);
	}
	
	/// Gets event details for past and upcoming week
	public ResponseEntity<HashMap<String,Object>> fetchEvents() throws InterruptedException, ExecutionException{
		HashMap<String,Object> responseObj = new HashMap<String,Object>();
		Firestore dbFirestore = FirestoreClient.getFirestore();
		LocalDate myDateObj = LocalDate.now();
		
		// events for past and upcoming week
		for(int i=-7;i<7;i++) {
			String incrementedDate = LocalDate.parse(myDateObj.toString()).plusDays(i).toString();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
			String formattedDate = LocalDate.parse(incrementedDate).format(myFormatObj);
			
			Event event;
			try {
				ApiFuture<QuerySnapshot> future= dbFirestore.collection("events").whereEqualTo("date", formattedDate).get();
				List<QueryDocumentSnapshot> documents = future.get().getDocuments();
				DocumentSnapshot document = documents.get(0);
				
				Map<String, Object> res = document.getData();
				event = new Event(res.get("eid").toString(),
						res.get("date").toString(),res.get("time").toString(),
						res.get("prize").toString(),res.get("winner").toString());
				responseObj.put(document.getId(), event);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return new ResponseEntity<>(responseObj,HttpStatus.OK);
	}
	
	
	public ResponseEntity<HashMap<String,String>> computeWinner(HashMap<String,String> map) throws InterruptedException, ExecutionException {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		HashMap<String,String> responseObj = new HashMap<String,String>();
		
		String key = map.get("key");
		String eventId = map.get("eid");
		
		//TODO:Validate Admin-Key
		
		if(!this.validateEventId(eventId)) {
			responseObj.put("text","Invalid Event ID");
			return new ResponseEntity<>(responseObj,HttpStatus.NOT_FOUND);
		}
		
		List<String> participantsId = new ArrayList<String>();  
		
		try {
			ApiFuture<QuerySnapshot> future = dbFirestore.collection("events").
					document(eventId).collection("participated").get();
			List<QueryDocumentSnapshot> documents = future.get().getDocuments();
			for (QueryDocumentSnapshot document : documents) {
			  participantsId.add(document.getId());
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		if(participantsId.size()==0) {
			responseObj.put("text","No Participants found for this event");
			return new ResponseEntity<>(responseObj,HttpStatus.NOT_FOUND);
		}
		
		// Randomly select one ID from the list to compute winner
		Random randomizer = new Random();
		String winnerId= participantsId.get(randomizer.nextInt(participantsId.size()));
		String winnerName = this.getUserName(winnerId);
		
		// Save winner ID for the event
		Map<String,Object> m = new HashMap<String,Object>();
		m.put("winner",winnerId);
		dbFirestore.collection("events").document(eventId).update(m);
		
		
		responseObj.put("winnerId", winnerId);
		responseObj.put("winnerName", winnerName);
		responseObj.put("eid",eventId);
		responseObj.put("text","Congratulations!");
		return new ResponseEntity<>(responseObj,HttpStatus.OK);
	}
		
	
}
