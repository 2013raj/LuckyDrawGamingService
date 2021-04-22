package com.luckydraw.luckydraw.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.luckydraw.luckydraw.dao.FirebaseDao;
import com.luckydraw.luckydraw.entities.Event;
import com.luckydraw.luckydraw.entities.RaffleTicket;
import com.luckydraw.luckydraw.entities.User;

@Service
public class LuckyDrawService {
	
	@Autowired
	private FirebaseDao firebaseDao;
	
	private static String WINNER_NOT_ANNOUNCED_TEXT="Not Announced";
	
	/// Populated events table with prizes and date-time [ADMIN_ACCESS_ONLY] 
	public String populateEventsTable() {
		final String prizes[]= {"Washing Machine", "Refrigerator","Mobile Phone", "Laptop", "Bluetooth Earphones", "Microwave"};
		final String time = "08:00 AM";
		LocalDate myDateObj = LocalDate.now();
		
		// Populating sufficient data for a period of 60 days
		for(int i=-30;i<=30;i++) {
			String incrementedDate = LocalDate.parse(myDateObj.toString()).plusDays(i).toString();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
			String formattedDate = LocalDate.parse(incrementedDate).format(myFormatObj);
			
			firebaseDao.populateEvents(formattedDate, time, prizes[Math.abs(i % prizes.length)], WINNER_NOT_ANNOUNCED_TEXT);
		}
		return "SUCCESS";
	}
	
	/// Registers user and return User ID
	public ResponseEntity<HashMap<String,String>> registerNewUser(User user) {
		String uid = firebaseDao.registerUserInDatabase(user);
		
		HashMap<String,String> responseObj = new HashMap<String,String>();
		responseObj.put("uid", uid);
		responseObj.put("text","User Registration Successful");
		ResponseEntity<HashMap<String,String>> response = new ResponseEntity<>(responseObj,HttpStatus.OK);
		return response;
	}
	
	/// Generates a raffle-ticket for a user for participation in a lucky draw event
	public ResponseEntity<HashMap<String,String>> generateRaffleTicket(String uid) {
		HashMap<String,String> responseObj = new HashMap<String,String>();
		
		if(!firebaseDao.validateUserId(uid)) {
			responseObj.put("text","Invalid User ID");
			return new ResponseEntity<>(responseObj,HttpStatus.NOT_FOUND);
		}
		
		String rid = firebaseDao.generateRFTicketForUser(uid);
		
		responseObj.put("rid", rid);
		responseObj.put("uid", uid);
		responseObj.put("text","Raffle Ticket Generated");
		ResponseEntity<HashMap<String,String>> response = new ResponseEntity<>(responseObj,HttpStatus.OK);
		return response;
	}
	
	/// Allows user with raffle-ticket to participate in an event
	public ResponseEntity<HashMap<String,String>> participateInEvent(RaffleTicket raffleTicket) throws InterruptedException, ExecutionException {
		HashMap<String,String> responseObj = new HashMap<String,String>();
		
		if(!firebaseDao.validateRaffleTicketId(raffleTicket.getRid())) {
			responseObj.put("text","Invalid Raffle-ticket Id");
			return new ResponseEntity<>(responseObj,HttpStatus.NOT_FOUND);
		}
		
		RaffleTicket ticket = firebaseDao.fetchRFTicketDetails(raffleTicket.getRid());
		
		// [Two Checks Required:]
		// 1. If raffle-ticket is already used
		if(ticket.isParticipated()) {
			responseObj.put("text","This raffle ticket has already been used");
			return new ResponseEntity<>(responseObj,HttpStatus.BAD_REQUEST);
		}
		
		// 2. If user has already participated in the following event
		if(firebaseDao.checkUserParticipationInEvent(ticket.getUid(),raffleTicket.getEid())) {
			responseObj.put("text","User has already participated in this event");
			return new ResponseEntity<>(responseObj,HttpStatus.FORBIDDEN);
		}
		
		// [Two Changes in DB are Required:]
		// 1. In raffle-tickets table
		ticket.setEid(raffleTicket.getEid());
		ticket.setParticipated(true);
		firebaseDao.saveTicketDetails(ticket);
		
		// 2. In events table
		firebaseDao.saveEventParticipation(ticket);
		
		responseObj.put("uid",ticket.getUid());
		responseObj.put("rid",raffleTicket.getRid());
		responseObj.put("eid",raffleTicket.getEid());
		responseObj.put("text","Participation Successful! All the best.");
		return new ResponseEntity<>(responseObj,HttpStatus.OK);
	}
	
	/// Gets winners of all events in the last one week
	public ResponseEntity<HashMap<String,String>> fetchWinners() throws InterruptedException, ExecutionException{
		HashMap<String,String> responseObj = new HashMap<String,String>();
		LocalDate myDateObj = LocalDate.now();
		//fetches winner for past week
		for(int i=0;i<7;i++) {
			String incrementedDate = LocalDate.parse(myDateObj.toString()).minusDays(i).toString();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
			String formattedDate = LocalDate.parse(incrementedDate).format(myFormatObj);
			
			String winner=firebaseDao.getEventWinnerId(formattedDate);
			if(!winner.equals(WINNER_NOT_ANNOUNCED_TEXT)) {
				winner = firebaseDao.getUserName(winner);
			}
			responseObj.put(formattedDate, winner);
		}
		return new ResponseEntity<>(responseObj,HttpStatus.OK);
	}
	
	/// Gets event details for past and upcoming week
	public ResponseEntity<HashMap<String,Object>> fetchEvents() throws InterruptedException, ExecutionException{
		HashMap<String,Object> responseObj = new HashMap<String,Object>();
		LocalDate myDateObj = LocalDate.now();
		// fetches events for past and upcoming week
		for(int i=-7;i<7;i++) {
			String incrementedDate = LocalDate.parse(myDateObj.toString()).plusDays(i).toString();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
			String formattedDate = LocalDate.parse(incrementedDate).format(myFormatObj);
			
			Event event = firebaseDao.getEventOnDate(formattedDate);
			responseObj.put(event.getEid(), event);
		}
		return new ResponseEntity<>(responseObj,HttpStatus.OK);
	}
	
	/// Computes Winners for an event [ACCESS ONLY TO ADMIN THROUGH ADMIN_KEYS]
	public ResponseEntity<HashMap<String,String>> computeWinner(HashMap<String,String> map) throws InterruptedException, ExecutionException {
		HashMap<String,String> responseObj = new HashMap<String,String>();
		
		String key = map.get("key");
		String eventId = map.get("eid");
		
		if(!firebaseDao.validateAdminKey(key)) {
			responseObj.put("text","Invalid Admin Key");
			return new ResponseEntity<>(responseObj,HttpStatus.UNAUTHORIZED);
		}
		
		if(!firebaseDao.validateEventId(eventId)) {
			responseObj.put("text","Invalid Event ID");
			return new ResponseEntity<>(responseObj,HttpStatus.NOT_FOUND);
		}
		
		Event event = firebaseDao.getEventById(eventId);
		
		if(!event.getWinner().equals(WINNER_NOT_ANNOUNCED_TEXT)) {
			responseObj.put("text","Winner for this event is already announced");
			return new ResponseEntity<>(responseObj,HttpStatus.ALREADY_REPORTED);
		}
		
		List<String> participantsId = firebaseDao.getParticipantsIdForEvent(eventId);
		
		if(participantsId.size()==0) {
			responseObj.put("text","No Participants found for this event");
			return new ResponseEntity<>(responseObj,HttpStatus.NOT_FOUND);
		}
		
		// Randomly select one ID from the list to compute winner
		Random randomizer = new Random();
		String winnerId= participantsId.get(randomizer.nextInt(participantsId.size()));
		String winnerName = firebaseDao.getUserName(winnerId);
		
		event.setWinner(winnerId);
		firebaseDao.saveEventDetails(event);
		
		responseObj.put("prize", event.getPrize());
		responseObj.put("winnerId", winnerId);
		responseObj.put("winnerName", winnerName);
		responseObj.put("eid",eventId);
		responseObj.put("text","Congratulations!");
		return new ResponseEntity<>(responseObj,HttpStatus.OK);
	}
	
}
