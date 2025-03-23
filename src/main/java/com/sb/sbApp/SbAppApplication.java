package com.sb.sbApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/app")
public class SbAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbAppApplication.class, args);
	}
	@GetMapping("/hello")
	public String get(){
		return "hello";
	}

}
