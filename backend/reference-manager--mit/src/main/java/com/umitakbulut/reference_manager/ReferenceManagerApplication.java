package com.umitakbulut.reference_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(title = "Reference Manager API", version = "1.0", description = "API dokümantasyonu")
)
@SpringBootApplication
public class ReferenceManagerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReferenceManagerApplication.class, args);
	}
}