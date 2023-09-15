package com.example.tantra.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.example.tantra.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.threeten.bp.Duration;
import java.time.temporal.ChronoUnit;

@Controller
public class QuizController {
	
	int num[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	String[] ques= {"This is Question 1","This is Question 2","This is Question 3","This is Question 4","This is Question 5",
			"This is Question 6", "This is Question 7", "This is Question 8", "This is Question 9", "This is Question 10"};
	String[] ans = {"A", "A", "A", "B", "B", "B", "C", "C", "D", "D"};
	String[] sub = new String[10];
	
	long times[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	long points[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	boolean last[] = {false, false, false, false, false, false, false, false, false, false}; 
	
	@GetMapping("/start")
	public String start() {
		return "quiz-start.html";
	}
	
	@GetMapping("/ques")
	public String question(Model model) {
		LocalDateTime now = LocalDateTime.now();
		Server.server_time = now;
		model.addAttribute("Question", ques[0]);
		model.addAttribute("A", "Option A");
		model.addAttribute("B", "Option B");
		model.addAttribute("C", "Option C");
		model.addAttribute("D", "Option D");
		model.addAttribute("Id", 0);
		model.addAttribute("verdict", "");
		model.addAttribute("ans", "");
		return "quiz.html";
	}

	@PostMapping("/submit")
    public String submitAnswer(@RequestParam("questionId") String questionId, @RequestParam(name = "answer", defaultValue = "wrong") String answer, Model model) {
		int index = Integer.parseInt(questionId);
		if(answer.equals("wrong")) {
			model.addAttribute("Question", ques[index]);
			model.addAttribute("A", "Option A");
			model.addAttribute("B", "Option B");
			model.addAttribute("C", "Option C");
			model.addAttribute("D", "Option D");
			model.addAttribute("Id", index);
			model.addAttribute("verdict", "");
			model.addAttribute("ans", "");
			return "quiz.html";
		}
		LocalDateTime now = LocalDateTime.now();
		long time = ChronoUnit.SECONDS.between(Server.server_time,now);
		Server.server_time = now;
		times[index]+=time;
		sub[index] = answer;
		if(answer.equals(ans[index])) {
			// Correct Answer
			model.addAttribute("Question", ques[index]);
			model.addAttribute("A", "Option A");
			model.addAttribute("B", "Option B");
			model.addAttribute("C", "Option C");
			model.addAttribute("D", "Option D");
			model.addAttribute("Id", index);
			model.addAttribute("verdict", "Verdict : Correct Answer");
			model.addAttribute("ans", "");
			return "quiz-correct-ans.html";
		}
		else {
			// Wrong Answer
			if(last[index]) {
				// both chances over
				times[index] = -1;
				model.addAttribute("Question", ques[index]);
				model.addAttribute("A", "Option A");
				model.addAttribute("B", "Option B");
				model.addAttribute("C", "Option C");
				model.addAttribute("D", "Option D");
				model.addAttribute("Id", index);
				model.addAttribute("verdict", "Verdict : Wrong Answer (Both chances used)");
				model.addAttribute("ans", "Correct Answer : " + answer);
				return "quiz-correct-ans.html";
			}
			else {
				last[index] = true;
			}
			model.addAttribute("Question", ques[index]);
			model.addAttribute("A", "Option A");
			model.addAttribute("B", "Option B");
			model.addAttribute("C", "Option C");
			model.addAttribute("D", "Option D");
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
		if(index == 10) {
			for(int i=0;i<10;i++) {
				if(times[i] == -1) {points[i] = 0; continue;}
				if(times[i] <= 15) points[i] = 10;
				else if (times[i] <= 30) points[i] = 9;
				else if (times[i] <= 45) points[i] = 8;
				else if (times[i] <= 60) points[i] = 7;
				else if (times[i] <= 75) points[i] = 6;
				else points[i] = 5;
			}
			model.addAttribute("num", num);
	        model.addAttribute("sub", sub);
	        model.addAttribute("ans", ans);
	        model.addAttribute("times", times);
	        model.addAttribute("points", points);
			return "quiz-result.html";
		}
		else {
			model.addAttribute("Question", ques[index]);
			model.addAttribute("A", "Option A");
			model.addAttribute("B", "Option B");
			model.addAttribute("C", "Option C");
			model.addAttribute("D", "Option D");
			model.addAttribute("Id", index);
			model.addAttribute("verdict", "");
			model.addAttribute("ans", "");
			return "quiz.html";
		}
    }	
}
