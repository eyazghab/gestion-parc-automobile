package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Active la planification des tâches

public class GestionParcAuto1Application {

	public static void main(String[] args) {
		SpringApplication.run(GestionParcAuto1Application.class, args);
	}

}
