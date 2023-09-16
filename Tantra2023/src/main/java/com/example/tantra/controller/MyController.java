package com.example.tantra.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
	
	@GetMapping("/profile")
	public String profile(Model model) {
		int points = 0;
		int p1 = 0, p2 = 0, p3 = 0, p4 = 0, p5 = 0;
		String email = findmore(Server.ID);
		String username = find(Server.ID);
		for(int i=0;i<5;i++) {
			String subject = "";
			if(i == 0) {
				subject = "Budgeting";
			}
			else if (i == 1) {
				subject = "Improving Credit";
			}
			else if (i == 2) {
				subject = "Saving";
			}
			else if (i == 3) {
				subject = "Borrowing and repaying debt";
			}
			else {
				subject = "Investing";
			}
			TreeMap<Integer, int[]> map = get(subject, Server.ID);
			for(Map.Entry<Integer, int[]> ent : map.entrySet()) {
				int a[] = ent.getValue();
				if(i == 0) p1+=a[0];
				if(i == 1) p2+=a[0];
				if(i == 2) p3+=a[0];
				if(i == 3) p4+=a[0];
				if(i == 4) p5+=a[0];
				points += a[0];
			}
		}
		String batch = "Newbie";
		if(points >= 50) {
			batch = "Beginner";
		}
		if(points >= 150) {
			batch = "Intermediate";
		}
		if(points >= 250) {
			batch = "Specialist";
		}
		if(points >= 350) {
			batch = "Expert";
		}
		if(points >= 450) {
			batch = "Master";
		}
		model.addAttribute("p1", p1);
		model.addAttribute("p2", p2);
		model.addAttribute("p3", p3);
		model.addAttribute("p4", p4);
		model.addAttribute("p5", p5);
		model.addAttribute("batch", batch);
		model.addAttribute("name", username);
		model.addAttribute("email", email);
		model.addAttribute("points", points);
		return "profile.html";
	}
	
	@GetMapping("/benefit")
	public String benefit(Model model) {
		return "benefit.html";
	}
	
	@GetMapping("/index")
	public String home(Model model) {
		return "index.html";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		return "about.html";
	}

    @GetMapping("/hello")
    public String index(Model model) {
        return "login.html";
    }
    
    @GetMapping("/budget")
    public String budget(Model model) {
    	return "budget.html";
    }
    
    @GetMapping("/savings")
    public String save(Model model) {
    	return "savings.html";
    }
    
    @GetMapping("/credit")
    public String credit(Model model) {
    	return "credit.html";
    }
    
    @GetMapping("/debit")
    public String debit(Model model) {
    	return "debit.html";
    }
    
    @GetMapping("/invest")
    public String invest(Model model) {
    	return "invest.html";
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
    
    public String find(String key) {
		String ans = "";
		DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(key).child("username");
        CompletableFuture<String> future = new CompletableFuture<>();
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                future.complete(value);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        try {
			return future.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}
    
    public String findmore(String key) {
		String ans = "";
		DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(key).child("email");
        CompletableFuture<String> future = new CompletableFuture<>();
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                future.complete(value);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        try {
			return future.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}
    
    public TreeMap<Integer, int[]> get(String subject, String key){
		TreeMap<Integer, int[]> map = new TreeMap<>();
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(subject).child(key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                	String val = childSnapshot.getValue(String.class);
                	int pts = Integer.parseInt(val.substring(0, val.indexOf(' ')));
                	int time = Integer.parseInt(val.substring(val.indexOf(' ')+1, val.length()));
                	map.put(Integer.parseInt(childSnapshot.getKey()), new int[] {pts, time});
                }
                future.complete(true);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
        try {
			if(future.get()) {
				return map;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}
}
