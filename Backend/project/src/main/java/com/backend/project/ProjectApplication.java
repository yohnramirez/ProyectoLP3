package com.backend.project;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {
		loadEnv();
		SpringApplication.run(ProjectApplication.class, args);
	}

	private static void loadEnv() {
		Dotenv dot = Dotenv.load();

		System.setProperty("URL_DB", dot.get("URL_DB"));
		System.setProperty("USER_DB", dot.get("USER_DB"));
		System.setProperty("PASSWORD_DB", dot.get("PASSWORD_DB"));
	}
}
