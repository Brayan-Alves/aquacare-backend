package br.edu.ifpr.aquacare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class AquacareApplication {

	public static void main(String[] args) {
		SpringApplication.run(AquacareApplication.class, args);
	}

}
