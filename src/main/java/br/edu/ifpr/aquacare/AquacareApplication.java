package br.edu.ifpr.aquacare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling 
public class AquacareApplication {

	public static void main(String[] args) {
		SpringApplication.run(AquacareApplication.class, args);
	}

}
