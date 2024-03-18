package ru.sber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@SpringBootApplication
public class ShareitApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShareitApplication.class, args);
	}
	@Bean
	public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
		return new HiddenHttpMethodFilter();
	}
}
//http://localhost:8080/items/owner/5
//http://localhost:8080/items/create