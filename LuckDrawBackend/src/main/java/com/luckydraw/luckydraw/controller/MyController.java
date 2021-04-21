package com.luckydraw.luckydraw.controller;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luckydraw.luckydraw.entities.Person;
import com.luckydraw.luckydraw.services.FirebaseService;

@RestController
public class MyController {
	
	@Autowired 
	FirebaseService firebaseService;
	
	@GetMapping("/home")
	public String home() throws InterruptedException, ExecutionException {
		return firebaseService.saveUserDetails(new Person("Ritik"));
	}
	
	@GetMapping("/")
	public String check(){
		return "API Working Fine";
	}

}
