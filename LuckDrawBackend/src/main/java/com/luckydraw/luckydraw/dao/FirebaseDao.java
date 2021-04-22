package com.luckydraw.luckydraw.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

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

@Repository
public class FirebaseDao {

	public boolean validateUserId(String userId) {
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
		
	public boolean validateEventId(String eventId) {
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
	
	public boolean validateRaffleTicketId(String rid) {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("raffle-tickets").document(rid);
		try {
			return docRef.get().get().exists();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean validateAdminKey(String key) {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("admin-keys").document(key);
		try {
			return docRef.get().get().exists();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getUserName(String userId) throws InterruptedException, ExecutionException {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("users").document(userId);
		ApiFuture<DocumentSnapshot> future  = docRef.get();
		DocumentSnapshot document = future.get();
		return document.getData().get("name").toString();
	}
	
	public void populateEvents(String date, String time, String prize, String winner) {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("events").document();
		Event newEvent = new Event(docRef.getId(),date,time,prize,winner);
		docRef.set(newEvent);
	}
	
	public String registerUserInDatabase(User user) {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("users").document();
		docRef.set(user);
		return docRef.getId();
	}
	
	public String generateRFTicketForUser(String uid) {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("raffle-tickets").document();
		String rid = docRef.getId();
		docRef.set(new RaffleTicket(rid,uid));
		return rid;
	}
	
	public RaffleTicket fetchRFTicketDetails(String rid) throws InterruptedException, ExecutionException {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("raffle-tickets").document(rid);
		ApiFuture<DocumentSnapshot> future  = docRef.get();
		DocumentSnapshot document = future.get();
		Map<String,Object> m = document.getData();
		return new RaffleTicket(Objects.toString(m.get("uid"),null),
				Objects.toString(m.get("eid"),null),Objects.toString(m.get("rid"),null),(boolean) m.get("participated"));
	}
	
	public boolean checkUserParticipationInEvent(String uid, String eid) {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("events").document(eid).
				collection("participated").document(uid);
		try {
			return docRef.get().get().exists();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void saveTicketDetails(RaffleTicket ticket)  {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		dbFirestore.collection("raffle-tickets").document(ticket.getRid()).set(ticket);
	}
	
	public void saveEventParticipation(RaffleTicket ticket) {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("rid",ticket.getRid());
		dbFirestore.collection("events").document(ticket.getEid())
			.collection("participated").document(ticket.getUid()).set(map);
	}
	
	public String getEventWinnerId(String date) throws InterruptedException, ExecutionException {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		ApiFuture<QuerySnapshot> future= dbFirestore.collection("events").whereEqualTo("date", date).get();
		List<QueryDocumentSnapshot> documents = future.get().getDocuments();
		DocumentSnapshot document = documents.get(0);
		return document.getData().get("winner").toString();
	}
	
	public Event getEventOnDate(String date) throws InterruptedException, ExecutionException {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		ApiFuture<QuerySnapshot> future= dbFirestore.collection("events").whereEqualTo("date", date).get();
		List<QueryDocumentSnapshot> documents = future.get().getDocuments();
		DocumentSnapshot document = documents.get(0);
		Map<String, Object> res = document.getData();
		return new Event(res.get("eid").toString(),
				res.get("date").toString(),res.get("time").toString(),
				res.get("prize").toString(),res.get("winner").toString());
	}
	
	public List<String> getParticipantsIdForEvent(String eid) throws InterruptedException, ExecutionException{
		Firestore dbFirestore = FirestoreClient.getFirestore();
		List<String> participantsId = new ArrayList<String>(); 
		ApiFuture<QuerySnapshot> future = dbFirestore.collection("events").
				document(eid).collection("participated").get();
		List<QueryDocumentSnapshot> documents = future.get().getDocuments();
		for (QueryDocumentSnapshot document : documents) {
		  participantsId.add(document.getId());
		}
		return participantsId;
	}
	
	public void saveEventDetails(Event event) {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		dbFirestore.collection("events").document(event.getEid()).set(event);
	}
	
	public Event getEventById(String eid) throws InterruptedException, ExecutionException {
		Firestore dbFirestore = FirestoreClient.getFirestore();
		DocumentReference docRef = dbFirestore.collection("events").document(eid);
		ApiFuture<DocumentSnapshot> future  = docRef.get();
		DocumentSnapshot document = future.get();
		Map<String,Object> res = document.getData();
		return new Event(res.get("eid").toString(),
				res.get("date").toString(),res.get("time").toString(),
				res.get("prize").toString(),res.get("winner").toString());
	}
	
}
