package com.securetransfer.SFTG;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.securetransfer")
@PropertySource("classpath:application.properties")
@EnableJpaAuditing
@EnableScheduling
public class  Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
