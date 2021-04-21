package com.luckydraw.luckydraw.services;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.luckydraw.luckydraw.entities.RaffleTicket;
import com.luckydraw.luckydraw.entities.User;

@Service
public class FirebaseService {
	
	// Validates user id from Database
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
	// Registers User and return UID 
	public String registerNewUser(User user) {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("users").document();
		docRef.set(user);
		return docRef.getId();
	}
	
	// Generates a raffle-ticket for a user for participation in a lucky draw event
	public String generateRaffleTicket(String uid) {
		if(uid==null) {
			return "User Id Not Found";
		}
		if(!this.validateUserId(uid)) {
			return "Invalid User ID";
		}
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("raffle-tickets").document();
		String docId = docRef.getId();
		docRef.set(new RaffleTicket(docId,uid));
		return docId;
	}
	
	public String participateInEvent(RaffleTicket raffleTicket) {
		// two things has to be done: 
		// one change in raffle ticket 
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("raffle-tickets").document(raffleTicket.getRid());
		Map<String,Object> m = new HashMap<String,Object>();
		m.put("eid",raffleTicket.getEid());
		m.put("participated",true);
		docRef.update(m);
		
		//and another in events table
		Map<String,Object> nm = new HashMap<String,Object>();
		nm.put("rid",raffleTicket.getRid());
		dbFirestore.collection("events").document(raffleTicket.getEid())
			.collection("participated").document(raffleTicket.getUid()).set(nm);
		
		return "Participation Successful";
	}
	
	
}
