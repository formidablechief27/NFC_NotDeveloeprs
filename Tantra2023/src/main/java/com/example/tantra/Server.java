package com.example.tantra;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
@SpringBootApplication
public class Server {
	public static String ID = "";
	public static LocalDateTime server_time;
	public static void main(String args[]) {
		ClassLoader classloader = Server.class.getClassLoader();
		File file = new File(Objects.requireNonNull(classloader.getResource("key.json")).getFile());
		try {
			InputStream serviceAccount =
	                classloader.getResourceAsStream("key.json");

	        FirebaseOptions options = new FirebaseOptions.Builder()
	                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
	                .setDatabaseUrl("https://codetantra2023-default-rtdb.firebaseio.com")
	                .build();

	        FirebaseApp.initializeApp(options);
	        SpringApplication.run(Server.class, args);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
