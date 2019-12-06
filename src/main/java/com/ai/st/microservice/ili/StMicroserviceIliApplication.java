package com.ai.st.microservice.ili;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class StMicroserviceIliApplication {

	public static void main(String[] args) {
		SpringApplication.run(StMicroserviceIliApplication.class, args);
	}

}
