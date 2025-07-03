package com.Epic.File.Management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class FileManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileManagementApplication.class, args);
	}

}
