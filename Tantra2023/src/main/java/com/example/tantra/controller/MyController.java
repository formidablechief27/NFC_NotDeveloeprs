package com.example.tantra.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.example.tantra.*;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.SyncTree.CompletionListener;

@Controller
public class MyController {
	
	String pass = "";

    @GetMapping("/hello")
    public String index(Model model) {
        return "login.html";
    }
    
    @GetMapping("/budget")
    public String budget(Model model) {
    	return "budget.html";
    }
    
    @GetMapping("/org")
    public String org(Model model) {
    	return "org.html";
    }
    
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String email, @RequestParam String password, Model model) {
    	if(password.length() < 6) {
    		 return "registration-error";
    	}
    	try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password);
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userRecord.getUid());
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", username);
            userData.put("email", email);
            userData.put("password", password);
            databaseReference.setValue(userData, new DatabaseReference.CompletionListener() {
				@Override
				public void onComplete(DatabaseError error, DatabaseReference ref) {
					if (error != null) {
                        error.toException().printStackTrace();
                    } else {
                    }
				}
            });
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "index.html";
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            return "registration-error";
        }
    }
    
    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password, Model model) {
    	try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            if (userRecord != null) {
                String uid = userRecord.getUid();
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("password");
                CompletableFuture<String> future = new CompletableFuture<>();
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String value = snapshot.getValue(String.class);
                        if (value != null && value.equals(password)) {
                            Server.ID = uid;
                            future.complete(uid);
                        } else {
                            future.completeExceptionally(new RuntimeException("Incorrect password"));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        future.completeExceptionally(error.toException());
                    }
                });
                String uidResult = future.get();
                model.addAttribute("UID", uidResult);
                return "index.html";
            } else {
            	return "login-failure.html";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return "login-failure.html";
    }
    
    public void fileEntry(String email, String password) {
    	
    }
}
