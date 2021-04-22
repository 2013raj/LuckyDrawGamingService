package com.luckydraw.luckydraw.controller;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.luckydraw.luckydraw.entities.RaffleTicket;
import com.luckydraw.luckydraw.entities.User;
import com.luckydraw.luckydraw.services.FirebaseService;

@RestController
public class LuckyDrawController {
	
	@Autowired 
	FirebaseService firebaseService;
	
	@GetMapping("/")
	public String check(){
//		return firebaseService.populateEventsTable();
		return "Welcome to Lucky Draw Gaming Service";
	}
	
	@PostMapping("/register")
	public ResponseEntity<HashMap<String,String>> registerUser(@RequestBody User user) {
		return firebaseService.registerNewUser(user);
	}
	
	@GetMapping("/raffle-ticket/{userId}")
	public ResponseEntity<HashMap<String,String>> generateRaffleTicket(@PathVariable String userId) {
		return firebaseService.generateRaffleTicket(userId);
	}
	
	@PostMapping("/participate")
	public ResponseEntity<HashMap<String,String>> participateInEvent(@RequestBody RaffleTicket raffleTicket) {
		return firebaseService.participateInEvent(raffleTicket);
	}
	
	@GetMapping("/winners")
	public ResponseEntity<HashMap<String,String>> getWinnersList() throws InterruptedException, ExecutionException{
		return firebaseService.fetchWinners();
	}
	
	@GetMapping("/events")
	public ResponseEntity<HashMap<String,Object>> getEvents() throws InterruptedException, ExecutionException{
		return firebaseService.fetchEvents();
	}
	
	
	
	
	

	
}
