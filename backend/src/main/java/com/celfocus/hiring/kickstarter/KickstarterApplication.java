package com.celfocus.hiring.kickstarter;

import com.celfocus.hiring.kickstarter.api.CartAPIController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KickstarterApplication {

	private static final Logger logger = LoggerFactory.getLogger(KickstarterApplication.class);
	public static void main(String[] args) {
		logger.info("Application started successfully");
		SpringApplication.run(KickstarterApplication.class, args);
	}

}
