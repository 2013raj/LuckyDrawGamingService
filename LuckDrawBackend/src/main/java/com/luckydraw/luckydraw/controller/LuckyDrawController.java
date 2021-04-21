package com.luckydraw.luckydraw.controller;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@GetMapping("/home")
	public String home(HttpServletRequest request) throws InterruptedException, ExecutionException {
		String path = request.getRequestURI();
		String items[] = path.split("/");
		return "HOME SWEET HOME";
	}
	
	@GetMapping("/")
	public String check(){
		return "API Working Fine";
	}
	
	@PostMapping("/register")
	public String registerUser(@RequestBody User user) {
		return firebaseService.registerNewUser(user);
	}
	
	@GetMapping("/raffle-ticket/{userId}")
	public String generateRaffleTicket(@PathVariable String userId) {
		return firebaseService.generateRaffleTicket(userId);
	}
	
	@PostMapping("/participate")
	public String participateInEvent(@RequestBody RaffleTicket raffleTicket) {
		return firebaseService.participateInEvent(raffleTicket);
	}
	
	
}
