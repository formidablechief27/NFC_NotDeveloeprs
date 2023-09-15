package com.example.tantra.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.example.tantra.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.threeten.bp.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
public class QuizController {
	
	String subject = "";
	int i = -1;
	int num[][] = { { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 },
			{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 } };
	String[][] ques = {
			{ "This is Question 1", "This is Question 2", "This is Question 3", "This is Question 4",
					"This is Question 5", "This is Question 6", "This is Question 7", "This is Question 8",
					"This is Question 9", "This is Question 10" },
			{ "This is Question 1", "This is Question 2", "This is Question 3", "This is Question 4",
					"This is Question 5", "This is Question 6", "This is Question 7", "This is Question 8",
					"This is Question 9", "This is Question 10" },
			{ "This is Question 1", "This is Question 2", "This is Question 3", "This is Question 4",
					"This is Question 5", "This is Question 6", "This is Question 7", "This is Question 8",
					"This is Question 9", "This is Question 10" },
			{ "This is Question 1", "This is Question 2", "This is Question 3", "This is Question 4",
					"This is Question 5", "This is Question 6", "This is Question 7", "This is Question 8",
					"This is Question 9", "This is Question 10" },
			{ "This is Question 1", "This is Question 2", "This is Question 3", "This is Question 4",
					"This is Question 5", "This is Question 6", "This is Question 7", "This is Question 8",
					"This is Question 9", "This is Question 10" } };
	String[][] ans = { { "A", "A", "A", "B", "B", "B", "C", "C", "D", "D" },
			{ "A", "A", "A", "B", "B", "B", "C", "C", "D", "D" }, { "A", "A", "A", "B", "B", "B", "C", "C", "D", "D" },
			{ "A", "A", "A", "B", "B", "B", "C", "C", "D", "D" },
			{ "A", "A", "A", "B", "B", "B", "C", "C", "D", "D" } };
	String[][] sub = new String[5][10];
	long times[][] = { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
	long points[][] = { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
	boolean last[][] = { { false, false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false, false } };
	String[][] opA = {
			{ "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A",
					"Option A", "Option A" },
			{ "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A",
					"Option A", "Option A" },
			{ "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A",
					"Option A", "Option A" },
			{ "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A",
					"Option A", "Option A" },
			{ "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A",
					"Option A", "Option A" } };
	String[][] opB = {
			{ "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B",
					"Option B", "Option B" },
			{ "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B",
					"Option B", "Option B" },
			{ "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B",
					"Option B", "Option B" },
			{ "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B",
					"Option B", "Option B" },
			{ "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B",
					"Option B", "Option B" } };
	String[][] opC = {
			{ "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C",
					"Option C", "Option C" },
			{ "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C",
					"Option C", "Option C" },
			{ "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C",
					"Option C", "Option C" },
			{ "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C",
					"Option C", "Option C" },
			{ "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C",
					"Option C", "Option C" } };
	String[][] opD = {
			{ "Option D", "Option D", "Option D", "Option D", "Option D", "Option D", "Option D", "Option D",
					"Option D", "Option D" },
			{ "Option D", "Option D", "Option D", "Option D", "Option D", "Option D", "Option D", "Option D",
					"Option D", "Option D" },
			{ "Option D", "Option D", "Option D", "Option D", "Option D", "Option D", "Option D", "Option D",
					"Option D", "Option D" },
			{ "Option D", "Option D", "Option D", "Option D", "Option D", "Option D", "Option D", "Option D",
					"Option D", "Option D" },
			{ "Option D", "Option D", "Option D", "Option D", "Option D", "Option D", "Option D", "Option D",
					"Option D", "Option D" } };

	@GetMapping("/start")
	public String start() {
		return "choose-quiz.html";
	}
	
	@PostMapping("/choose")
	public String choose(@RequestParam("selectedOption") String answer) {
		subject = answer;
		if(answer.equals("Budgeting")) {
			i = 0;
		}
		if(answer.equals("Improving Credit")) {
			i = 1;
		}
		if(answer.equals("Saving")) {
			i = 2;
		}
		if(answer.equals("Borrowing and repaying debt")) {
			i = 3;
		}
		if(answer.equals("Investing")) {
			i = 4;
		}
		if(i == -1) return "choose-quiz.html";
		return "quiz-start.html";
		
	}

	@GetMapping("/ques")
	public String question(Model model) {
		LocalDateTime now = LocalDateTime.now();
		Server.server_time = now;
		model.addAttribute("Question", ques[i][0]);
		model.addAttribute("A", opA[i][0]);
		model.addAttribute("B", opB[i][0]);
		model.addAttribute("C", opC[i][0]);
		model.addAttribute("D", opD[i][0]);
		model.addAttribute("Id", 0);
		model.addAttribute("verdict", "");
		model.addAttribute("ans", "");
		return "quiz.html";
	}

	@PostMapping("/submit")
	public String submitAnswer(@RequestParam("questionId") String questionId,
			@RequestParam(name = "answer", defaultValue = "wrong") String answer, Model model) {
		int index = Integer.parseInt(questionId);
		if (answer.equals("wrong")) {
			model.addAttribute("Question", ques[i][index]);
			model.addAttribute("A", opA[i][index]);
			model.addAttribute("B", opB[i][index]);
			model.addAttribute("C", opC[i][index]);
			model.addAttribute("D", opD[i][index]);
			model.addAttribute("Id", index);
			model.addAttribute("verdict", "");
			model.addAttribute("ans", "");
			return "quiz.html";
		}
		LocalDateTime now = LocalDateTime.now();
		long time = ChronoUnit.SECONDS.between(Server.server_time, now);
		Server.server_time = now;
		times[i][index] += time;
		sub[i][index] = answer;
		if (answer.equals(ans[i][index])) {
			// Correct Answer
			model.addAttribute("Question", ques[i][index]);
			model.addAttribute("A", opA[i][index]);
			model.addAttribute("B", opB[i][index]);
			model.addAttribute("C", opC[i][index]);
			model.addAttribute("D", opD[i][index]);
			model.addAttribute("Id", index);
			model.addAttribute("verdict", "Verdict : Correct Answer");
			model.addAttribute("ans", "");
			return "quiz-correct-ans.html";
		} else {
			// Wrong Answer
			if (last[i][index]) {
				// both chances over
				times[i][index] = -1;
				model.addAttribute("Question", ques[i][index]);
				model.addAttribute("A", opA[i][index]);
				model.addAttribute("B", opB[i][index]);
				model.addAttribute("C", opC[i][index]);
				model.addAttribute("D", opD[i][index]);
				model.addAttribute("Id", index);
				model.addAttribute("verdict", "Verdict : Wrong Answer (Both chances used)");
				model.addAttribute("ans", "Correct Answer : " + ans[i][index]);
				return "quiz-correct-ans.html";
			} else {
				last[i][index] = true;
			}
			model.addAttribute("Question", ques[i][index]);
			model.addAttribute("A", opA[i][index]);
			model.addAttribute("B", opB[i][index]);
			model.addAttribute("C", opC[i][index]);
			model.addAttribute("D", opD[i][index]);
			model.addAttribute("Id", index);
			model.addAttribute("verdict", "Verdict : Wrong Answer");
			model.addAttribute("ans", "");
			return "quiz.html";
		}
	}

	@PostMapping("/next")
	public String nextAnswer(@RequestParam("questionId") String questionId, Model model) {
		LocalDateTime now = LocalDateTime.now();
		Server.server_time = now;
		int index = Integer.parseInt(questionId);
		index++;
		long pts = 0;
		if (index == 10) {
			for (int i = 0; i < 10; i++) {
				if (times[this.i][i] == -1) {
					points[this.i][i] = 0;
					continue;
				}
				if (times[this.i][i] <= 15)
					points[this.i][i] = 10;
				else if (times[this.i][i] <= 30)
					points[this.i][i] = 9;
				else if (times[this.i][i] <= 45)
					points[this.i][i] = 8;
				else if (times[this.i][i] <= 60)
					points[this.i][i] = 7;
				else if (times[this.i][i] <= 75)
					points[this.i][i] = 6;
				else
					points[this.i][i] = 5;
				pts += points[this.i][i];
			}
			model.addAttribute("num", num[i]);
			model.addAttribute("sub", sub[i]);
			model.addAttribute("ans", ans[i]);
			model.addAttribute("times", times[i]);
			model.addAttribute("points", points[i]);
			model.addAttribute("subject", subject);
			model.addAttribute("pts", pts);
			DatabaseEntry();
			return "quiz-result.html";
		} else {
			model.addAttribute("Question", ques[this.i][index]);
			model.addAttribute("A", opA[i][index]);
			model.addAttribute("B", opB[i][index]);
			model.addAttribute("C", opC[i][index]);
			model.addAttribute("D", opD[i][index]);
			model.addAttribute("Id", index);
			model.addAttribute("verdict", "");
			model.addAttribute("ans", "");
			return "quiz.html";
		}
	}
	
	public void DatabaseEntry() {
		FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users").child(Server.ID); // Replace with your actual Firebase database reference path
        // Key to check for existence
        String keyToCheck = subject; // Replace with the key you want to check
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(keyToCheck)) {
                	future.complete(true);
                    //System.out.println("Key exists in the database.");
                } else {
                	future.complete(false);
                    //System.out.println("Key does not exist in the database.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error reading data from Firebase: " + databaseError.getMessage());
            }
        });
        try {
			boolean result = future.get();
			if(!result) {
				DataEntry();
			}
			else {
				return;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void DataEntry() {
		Map<String, Object> userData = new HashMap<>();
		Long total = 0L;
        for(int i=0;i<10;i++) {
        	userData.put(Integer.toString(i+1), points[this.i][i]);
        	total += points[this.i][i];
        }
        userData.put("Total", total);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(Server.ID).child(subject);
        databaseReference.setValue(userData, new DatabaseReference.CompletionListener() {
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
