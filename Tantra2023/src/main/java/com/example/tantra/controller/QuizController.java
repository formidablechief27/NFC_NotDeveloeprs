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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
public class QuizController {
	
	String subject = "";
	int i = -1;
	int num[][] = { { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 },
			{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 } };
	String[][] ques = {
			{ "What is the core of building financial literacy?", "What is one primary benefit of budgeting mentioned in the passage?", "How does budgeting help prevent excessive spending?", "What financial space does budgeting create?",
					"Which budgeting method allocates 50% of income to essential needs, 30% to wants, and 20% to savings or debt repayment?", "In the Zero-Based Method, what happens to every dollar of income?", "What does the Envelope Method involve?", "How does budgeting relate to financial literacy?",
					"What is the primary purpose of the 50-30-20 budgeting method?", "What does budgeting allow individuals to do with their finances?" },
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
	String[][] ans = { { "B", "B", "C", "C", "C", "C", "B", "C", "D", "B" },
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
			{ "Investing in stocks", "Generating more income", "It encourages borrowing money for nonessentials", "Space for more debt", "Zero-Based Method", "It's invested in stocks", "Storing money in a physical envelope under the mattress", "It has no relationship with financial literacy",
					"To spend all income on wants", "To spend all income on wants" },
			{ "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A",
					"Option A", "Option A" },
			{ "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A",
					"Option A", "Option A" },
			{ "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A",
					"Option A", "Option A" },
			{ "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A", "Option A",
					"Option A", "Option A" } };
	String[][] opB = {
			{ "Developing good spending habits", "Monitoring and controlling expenditures", "It promotes reckless spending on luxury items", "Space for investments only", "Envelope Method", "It's left unallocated", "Digitally separating money for different expenses", "It is the only aspect of financial literacy",
					"To save 50% of income", "It helps them track and manage expenses" },
			{ "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B",
					"Option B", "Option B" },
			{ "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B",
					"Option B", "Option B" },
			{ "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B",
					"Option B", "Option B" },
			{ "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B", "Option B",
					"Option B", "Option B" } };
	String[][] opC = {
			{ "Saving for retirement", "Maximizing investment returns", "It prioritizes spending on essentials and monitors discretionary spending", "Space for essentials and savings", "50-30-20 Method", "It's assigned a purpose, either for expenses or savings", "Keeping all money in a single account", "It is a core component of developing financial literacy",
					"To allocate money equally to all expenses", "It guarantees financial success" },
			{ "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C",
					"Option C", "Option C" },
			{ "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C",
					"Option C", "Option C" },
			{ "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C",
					"Option C", "Option C" },
			{ "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C", "Option C",
					"Option C", "Option C" } };
	String[][] opD = {
			{ "Paying off debts", "Increasing credit card limits", "It eliminates all discretionary spending", "Space for nonessential spending", "80-10-10 Method", "It's donated to charity", "Investing money in envelopes", "It is only important for financial experts",
					"To prioritize essential needs and savings", "It eliminates the need for financial goals" },
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
	
	@GetMapping("/quiz1")
	public String quiz1() {
		i = 0;
		subject = "Budgeting";
		return "quiz-start.html";
	}
	
	@GetMapping("/quiz2")
	public String quiz2() {
		i = 1;
		subject = "Improving Credit";
		return "quiz-start.html";
	}
	
	@GetMapping("/quiz3")
	public String quiz3() {
		i = 2;
		subject = "Saving";
		return "quiz-start.html";
	}
	
	@GetMapping("/quiz4")
	public String quiz4() {
		i = 3;
		subject = "Borrowing and repaying debt";
		return "quiz-start.html";
	}
	
	@GetMapping("/quiz5")
	public String quiz5() {
		i = 4;
		subject = "Investing";
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
	
	public PriorityQueue<long[]> queue(int index){
		PriorityQueue<long[]> queue = new PriorityQueue<long[]>((a,b)->{
			if (a[index] < b[index]){
                return 1;
        	}
        	else if (a[index] == b[index] && a[index+1] > b[index+1]) {
        		return 1;
        	}
        	else {
        		return -1;
        	}
		});
        return queue;
	}
	
	@GetMapping("/leaderboard1")
	public String leaderboard1(Model model) {
		String answer = "Budgeting";
		HashMap<String, Integer> keys = new HashMap<>();
		TreeMap<Integer, int[]> arr[] = new TreeMap[(int)1e5];
		String keynames[] = new String[(int)1e5];
		PriorityQueue<long[]> queue = queue(0);
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(answer);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    arr[keys.size()+1] = new TreeMap<>();
                    keys.put(key, keys.size()+1);
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
				for(Map.Entry<String, Integer> entry : keys.entrySet()) {
					TreeMap<Integer, int[]> map = get(answer, entry.getKey());
					arr[entry.getValue()] = map;
					keynames[entry.getValue()] = entry.getKey();
					int pts = 0;
					int time = 0;
					for(Map.Entry<Integer, int[]> ent : map.entrySet()) {
						int a[] = ent.getValue();
						pts += a[0];
						if(a[1] != -1) time += a[1];
					}
					queue.add(new long[] {pts, time, entry.getValue()});
				}
				ArrayList<Integer> rank = new ArrayList<>();
				ArrayList<String> users = new ArrayList<>();
				ArrayList<Integer> points = new ArrayList<>();
				ArrayList<Integer> penalty = new ArrayList<>();
				ArrayList<Integer> score[] = new ArrayList[11];
				for(int i=1;i<=10;i++) score[i] = new ArrayList<>();
				int r = 1;
				while(!queue.isEmpty()) {
					long curr[] = queue.poll();
					int index = (int)curr[2];
					String getname = find(keynames[index]);
					users.add(getname);
					points.add((int)curr[0]);
					penalty.add((int)curr[1]);
					rank.add(r++);
					TreeMap<Integer, int[]> cmap = arr[index];
					for(Map.Entry<Integer, int[]> entry : cmap.entrySet()) {
						int pt = entry.getValue()[0];
						score[entry.getKey()].add(pt);
					}
				}
				model.addAttribute("subject", answer);
				model.addAttribute("rank", rank);
				model.addAttribute("names", users);
				model.addAttribute("pts", points);
				model.addAttribute("penalty", penalty);
				for(int i=1;i<=10;i++) model.addAttribute("score"+i, score[i]);
				return "leaderboard.html";
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
	
	@GetMapping("/leaderboard2")
	public String leaderboard2(Model model) {
		String answer = "Improving Credit";
		HashMap<String, Integer> keys = new HashMap<>();
		TreeMap<Integer, int[]> arr[] = new TreeMap[(int)1e5];
		String keynames[] = new String[(int)1e5];
		PriorityQueue<long[]> queue = queue(0);
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(answer);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    arr[keys.size()+1] = new TreeMap<>();
                    keys.put(key, keys.size()+1);
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
				for(Map.Entry<String, Integer> entry : keys.entrySet()) {
					TreeMap<Integer, int[]> map = get(answer, entry.getKey());
					arr[entry.getValue()] = map;
					keynames[entry.getValue()] = entry.getKey();
					int pts = 0;
					int time = 0;
					for(Map.Entry<Integer, int[]> ent : map.entrySet()) {
						int a[] = ent.getValue();
						pts += a[0];
						if(a[1] != -1) time += a[1];
					}
					queue.add(new long[] {pts, time, entry.getValue()});
				}
				ArrayList<Integer> rank = new ArrayList<>();
				ArrayList<String> users = new ArrayList<>();
				ArrayList<Integer> points = new ArrayList<>();
				ArrayList<Integer> penalty = new ArrayList<>();
				ArrayList<Integer> score[] = new ArrayList[11];
				for(int i=1;i<=10;i++) score[i] = new ArrayList<>();
				int r = 1;
				while(!queue.isEmpty()) {
					long curr[] = queue.poll();
					int index = (int)curr[2];
					String getname = find(keynames[index]);
					users.add(getname);
					points.add((int)curr[0]);
					penalty.add((int)curr[1]);
					rank.add(r++);
					TreeMap<Integer, int[]> cmap = arr[index];
					for(Map.Entry<Integer, int[]> entry : cmap.entrySet()) {
						int pt = entry.getValue()[0];
						score[entry.getKey()].add(pt);
					}
				}
				model.addAttribute("subject", answer);
				model.addAttribute("rank", rank);
				model.addAttribute("names", users);
				model.addAttribute("pts", points);
				model.addAttribute("penalty", penalty);
				for(int i=1;i<=10;i++) model.addAttribute("score"+i, score[i]);
				return "leaderboard.html";
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
	
	@GetMapping("/leaderboard3")
	public String leaderboard3(Model model) {
		String answer = "Saving";
		HashMap<String, Integer> keys = new HashMap<>();
		TreeMap<Integer, int[]> arr[] = new TreeMap[(int)1e5];
		String keynames[] = new String[(int)1e5];
		PriorityQueue<long[]> queue = queue(0);
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(answer);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    arr[keys.size()+1] = new TreeMap<>();
                    keys.put(key, keys.size()+1);
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
				for(Map.Entry<String, Integer> entry : keys.entrySet()) {
					TreeMap<Integer, int[]> map = get(answer, entry.getKey());
					arr[entry.getValue()] = map;
					keynames[entry.getValue()] = entry.getKey();
					int pts = 0;
					int time = 0;
					for(Map.Entry<Integer, int[]> ent : map.entrySet()) {
						int a[] = ent.getValue();
						pts += a[0];
						if(a[1] != -1) time += a[1];
					}
					queue.add(new long[] {pts, time, entry.getValue()});
				}
				ArrayList<Integer> rank = new ArrayList<>();
				ArrayList<String> users = new ArrayList<>();
				ArrayList<Integer> points = new ArrayList<>();
				ArrayList<Integer> penalty = new ArrayList<>();
				ArrayList<Integer> score[] = new ArrayList[11];
				for(int i=1;i<=10;i++) score[i] = new ArrayList<>();
				int r = 1;
				while(!queue.isEmpty()) {
					long curr[] = queue.poll();
					int index = (int)curr[2];
					String getname = find(keynames[index]);
					users.add(getname);
					points.add((int)curr[0]);
					penalty.add((int)curr[1]);
					rank.add(r++);
					TreeMap<Integer, int[]> cmap = arr[index];
					for(Map.Entry<Integer, int[]> entry : cmap.entrySet()) {
						int pt = entry.getValue()[0];
						score[entry.getKey()].add(pt);
					}
				}
				model.addAttribute("subject", answer);
				model.addAttribute("rank", rank);
				model.addAttribute("names", users);
				model.addAttribute("pts", points);
				model.addAttribute("penalty", penalty);
				for(int i=1;i<=10;i++) model.addAttribute("score"+i, score[i]);
				return "leaderboard.html";
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
	
	@GetMapping("/leaderboard4")
	public String leaderboard4(Model model) {
		String answer = "Borrowing and repaying debt";
		HashMap<String, Integer> keys = new HashMap<>();
		TreeMap<Integer, int[]> arr[] = new TreeMap[(int)1e5];
		String keynames[] = new String[(int)1e5];
		PriorityQueue<long[]> queue = queue(0);
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(answer);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    arr[keys.size()+1] = new TreeMap<>();
                    keys.put(key, keys.size()+1);
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
				for(Map.Entry<String, Integer> entry : keys.entrySet()) {
					TreeMap<Integer, int[]> map = get(answer, entry.getKey());
					arr[entry.getValue()] = map;
					keynames[entry.getValue()] = entry.getKey();
					int pts = 0;
					int time = 0;
					for(Map.Entry<Integer, int[]> ent : map.entrySet()) {
						int a[] = ent.getValue();
						pts += a[0];
						if(a[1] != -1) time += a[1];
					}
					queue.add(new long[] {pts, time, entry.getValue()});
				}
				ArrayList<Integer> rank = new ArrayList<>();
				ArrayList<String> users = new ArrayList<>();
				ArrayList<Integer> points = new ArrayList<>();
				ArrayList<Integer> penalty = new ArrayList<>();
				ArrayList<Integer> score[] = new ArrayList[11];
				for(int i=1;i<=10;i++) score[i] = new ArrayList<>();
				int r = 1;
				while(!queue.isEmpty()) {
					long curr[] = queue.poll();
					int index = (int)curr[2];
					String getname = find(keynames[index]);
					users.add(getname);
					points.add((int)curr[0]);
					penalty.add((int)curr[1]);
					rank.add(r++);
					TreeMap<Integer, int[]> cmap = arr[index];
					for(Map.Entry<Integer, int[]> entry : cmap.entrySet()) {
						int pt = entry.getValue()[0];
						score[entry.getKey()].add(pt);
					}
				}
				model.addAttribute("subject", answer);
				model.addAttribute("rank", rank);
				model.addAttribute("names", users);
				model.addAttribute("pts", points);
				model.addAttribute("penalty", penalty);
				for(int i=1;i<=10;i++) model.addAttribute("score"+i, score[i]);
				return "leaderboard.html";
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
	
	@GetMapping("/leaderboard5")
	public String leaderboard5(Model model) {
		String answer = "Investing";
		HashMap<String, Integer> keys = new HashMap<>();
		TreeMap<Integer, int[]> arr[] = new TreeMap[(int)1e5];
		String keynames[] = new String[(int)1e5];
		PriorityQueue<long[]> queue = queue(0);
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(answer);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    arr[keys.size()+1] = new TreeMap<>();
                    keys.put(key, keys.size()+1);
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
				for(Map.Entry<String, Integer> entry : keys.entrySet()) {
					TreeMap<Integer, int[]> map = get(answer, entry.getKey());
					arr[entry.getValue()] = map;
					keynames[entry.getValue()] = entry.getKey();
					int pts = 0;
					int time = 0;
					for(Map.Entry<Integer, int[]> ent : map.entrySet()) {
						int a[] = ent.getValue();
						pts += a[0];
						if(a[1] != -1) time += a[1];
					}
					queue.add(new long[] {pts, time, entry.getValue()});
				}
				ArrayList<Integer> rank = new ArrayList<>();
				ArrayList<String> users = new ArrayList<>();
				ArrayList<Integer> points = new ArrayList<>();
				ArrayList<Integer> penalty = new ArrayList<>();
				ArrayList<Integer> score[] = new ArrayList[11];
				for(int i=1;i<=10;i++) score[i] = new ArrayList<>();
				int r = 1;
				while(!queue.isEmpty()) {
					long curr[] = queue.poll();
					int index = (int)curr[2];
					String getname = find(keynames[index]);
					users.add(getname);
					points.add((int)curr[0]);
					penalty.add((int)curr[1]);
					rank.add(r++);
					TreeMap<Integer, int[]> cmap = arr[index];
					for(Map.Entry<Integer, int[]> entry : cmap.entrySet()) {
						int pt = entry.getValue()[0];
						score[entry.getKey()].add(pt);
					}
				}
				model.addAttribute("subject", answer);
				model.addAttribute("rank", rank);
				model.addAttribute("names", users);
				model.addAttribute("pts", points);
				model.addAttribute("penalty", penalty);
				for(int i=1;i<=10;i++) model.addAttribute("score"+i, score[i]);
				return "leaderboard.html";
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
	
	public void DataEntry() {
		Map<String, Object> userData = new HashMap<>();
        for(int i=0;i<10;i++) {
        	userData.put(Integer.toString(i+1), points[this.i][i] + " " + times[this.i][i]);
        }
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
        databaseReference = FirebaseDatabase.getInstance()
                .getReference(subject)
                .child(Server.ID);
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