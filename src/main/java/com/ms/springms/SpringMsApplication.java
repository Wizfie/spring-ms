package com.ms.springms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.ms.springms")
public class SpringMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMsApplication.class, args);
	}

}
