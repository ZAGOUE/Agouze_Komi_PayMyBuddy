package com.paymybuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.paymybuddy")
public class PaymybuddyApplication {

	public static void main(String[] args) {

		SpringApplication.run(PaymybuddyApplication.class, args);
	}

}
