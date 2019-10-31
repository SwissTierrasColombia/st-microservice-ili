package com.ai.st.microservice.ilivalidator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class StMicroserviceIlivalidatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(StMicroserviceIlivalidatorApplication.class, args);
	}

}
