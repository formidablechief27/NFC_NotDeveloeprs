package com.example.tantra.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.tantra.Server;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@Controller
public class CommentController {
	
	ArrayList<ArrayList<String>> comments = new ArrayList<>();
	ArrayList<ArrayList<String>> names = new ArrayList<>();
	HashMap<Integer, String> map = new HashMap<>();
	
	@GetMapping("/comment1")
	public String comm1(Model model) {
		comments = new ArrayList<>();
		names = new ArrayList<>();
		map = new HashMap<>();
		initializeList(1);
		model.addAttribute("pageNumber", Integer.toString(1));
		model.addAttribute("commentList", comments);
		model.addAttribute("nameList", names);
		return "comments-page.html";
	}
	
	@GetMapping("/comment2")
	public String comm2(Model model) {
		comments = new ArrayList<>();
		names = new ArrayList<>();
		map = new HashMap<>();
		initializeList(2);
		model.addAttribute("pageNumber", Integer.toString(2));
		model.addAttribute("commentList", comments);
		model.addAttribute("nameList", names);
		return "comments-page.html";
	}
	
	@GetMapping("/comment3")
	public String comm3(Model model) {
		comments = new ArrayList<>();
		names = new ArrayList<>();
		map = new HashMap<>();
		initializeList(3);
		model.addAttribute("pageNumber", Integer.toString(3));
		model.addAttribute("commentList", comments);
		model.addAttribute("nameList", names);
		return "comments-page.html";
	}
	
	@GetMapping("/comment4")
	public String comm4(Model model) {
		comments = new ArrayList<>();
		names = new ArrayList<>();
		map = new HashMap<>();
		initializeList(4);
		model.addAttribute("pageNumber", Integer.toString(4));
		model.addAttribute("commentList", comments);
		model.addAttribute("nameList", names);
		return "comments-page.html";
	}
	
	@GetMapping("/comment5")
	public String comm5(Model model) {
		comments = new ArrayList<>();
		names = new ArrayList<>();
		map = new HashMap<>();
		initializeList(5);
		model.addAttribute("pageNumber", Integer.toString(5));
		model.addAttribute("commentList", comments);
		model.addAttribute("nameList", names);
		return "comments-page.html";
	}
	
	@PostMapping("/new-comment")
	public String newComment(@RequestParam("newCommentText") String replyText, @RequestParam("pageNumber") String pageNumber, Model model){
		updateList(names.size(), pageNumber, replyText);
		model.addAttribute("pageNumber", pageNumber);
		model.addAttribute("commentList", comments);
		model.addAttribute("nameList", names);
		return "comments-page.html";
    }
	
	@PostMapping("/submit-reply")
    public String submitReply(@RequestParam("parentCommentIndex") int parentCommentIndex, 
                              @RequestParam("replyText") String replyText, @RequestParam("pageNumber") String pageNumber, Model model) {
		updateList(parentCommentIndex, pageNumber, replyText);
		model.addAttribute("pageNumber", pageNumber);
		model.addAttribute("commentList", comments);
		model.addAttribute("nameList", names);
        return "comments-page.html";
    }
	
	public void updateList(int index, String number, String reply) {
		String name = find(Server.ID);
		if(index == names.size()) {
			ArrayList<String> l1 = new ArrayList<String>();
			ArrayList<String> l2 = new ArrayList<String>();
			l1.add(name);
			l2.add(reply);
			names.add(l1);
			comments.add(l2);
			map.put(index, Server.ID);
		}
		else {
			names.get(index).add(name);
			comments.get(index).add(reply);
		}
		Map<String, Object> userData = new HashMap<>();
        userData.put(Server.ID + " " + reply, "");
        if(index == names.size()) {
        	DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference("comments")
                    .child(number).child(Server.ID);
            databaseReference.updateChildren(userData, new DatabaseReference.CompletionListener() {
    			@Override
    			public void onComplete(DatabaseError error, DatabaseReference ref) {
    				if (error != null) {
                        error.toException().printStackTrace();
                    } else {
                    }
    			}
            });
        }
        else {
        	String Id = map.get(index);
        	DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference("comments")
                    .child(number).child(Id);
            databaseReference.updateChildren(userData, new DatabaseReference.CompletionListener() {
    			@Override
    			public void onComplete(DatabaseError error, DatabaseReference ref) {
    				if (error != null) {
                        error.toException().printStackTrace();
                    } else {
                    }
    			}
            });
        }
	}
	
	public void initializeList(int number) {
		ArrayList<String> keys = new ArrayList<>();
		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("comments").child(Integer.toString(number));
		CompletableFuture<ArrayList<String>> future = new CompletableFuture<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                	String key = childSnapshot.getKey();
                	//String val = childSnapshot.getValue(String.class);
                	keys.add(key);
                }
                future.complete(keys);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            	future.completeExceptionally(databaseError.toException());
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
        try {
			ArrayList<String> list = future.get();
			int count = 0;
			for(String ele : list) {
				work(number, ele, count++);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void work(int number, String ele, int count) {
		CompletableFuture<ArrayList<String>> future = new CompletableFuture<>();
		ArrayList<String> l1 = new ArrayList<String>();
		FirebaseDatabase.getInstance().getReference("comments").child(Integer.toString(number)).child(ele).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				for(DataSnapshot childSnapshot : snapshot.getChildren()) {
					String key = childSnapshot.getKey();
					l1.add(key);
				}
				future.complete(l1);
			}
			@Override
			public void onCancelled(DatabaseError error) {
				future.completeExceptionally(error.toException());
			}
		});
		try {
			ArrayList<String> rest = future.get();
			ArrayList<String> names = new ArrayList<>();
			ArrayList<String> comments = new ArrayList<>();
			for(String element : rest) {
				String ID = element.substring(0, element.indexOf(" "));
				names.add(find(ID));
				String reply = element.substring(element.indexOf(" ") + 1, element.length());
				comments.add(reply);
			}
			this.comments.add(comments);
			this.names.add(names);
			map.put(count, ele);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
}
