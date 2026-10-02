package br.edu.ifpr.aquacare;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import me.paulschwarz.springdotenv.spring.DotenvApplicationInitializer;

@SpringBootApplication
@EnableScheduling
public class AquacareApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(AquacareApplication.class)
			.initializers(new DotenvApplicationInitializer())
			.run(args);
	}

}
