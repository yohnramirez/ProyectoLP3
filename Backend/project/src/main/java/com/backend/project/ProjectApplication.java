package com.backend.project;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class ProjectApplication {

	private static final Logger logger = LoggerFactory.getLogger(ProjectApplication.class);

	public static void main(String[] args) {
		loadEnv();
		SpringApplication.run(ProjectApplication.class, args);
	}

	private static void loadEnv() {
		try {
			Dotenv dot = Dotenv.configure().ignoreIfMissing().load();
			logger.info("Dotenv loaded successfully from: {}", dot.get("PWD", "unknown"));

			setSystemProperty(dot, "URL_DB");
			setSystemProperty(dot, "USER_DB");
			setSystemProperty(dot, "PASSWORD_DB");
			setSystemProperty(dot, "AUTH_SUPABASE_URL");
			setSystemProperty(dot, "AUTH_SUPABASE_KEY");
			setSystemProperty(dot, "SUPABASE_SERVICE_ROLE_KEY");
		} catch (Exception e) {
			logger.error("Failed to load .env file: {}", e.getMessage(), e);
		}
	}

	private static void setSystemProperty(Dotenv dotenv, String key) {
		String value = dotenv.get(key, System.getenv(key));
		if (value != null) {
			System.setProperty(key, value);
			logger.info("Set system property: {}={}", key, value.length() > 50 ? value.substring(0, 50) + "..." : value);
		} else {
			logger.warn("Environment variable {} not found", key);
		}
	}
}
