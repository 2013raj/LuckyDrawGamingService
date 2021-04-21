package com.luckydraw.luckydraw.services;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.luckydraw.luckydraw.entities.Person;

@Service
public class FirebaseService {
	public String saveUserDetails(Person person) throws InterruptedException, ExecutionException{
		Firestore dbFirestore = FirestoreClient.getFirestore();
		ApiFuture<com.google.cloud.firestore.WriteResult> collectionsApiFuture = dbFirestore.collection("users").document().set(person);
        return collectionsApiFuture.get().getUpdateTime().toString();
	}
}
