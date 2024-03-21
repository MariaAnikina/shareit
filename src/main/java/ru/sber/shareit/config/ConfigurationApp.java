package ru.sber.shareit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class ConfigurationApp {
	@Value("${api.weather.key}")
	private String key;
	@Value("${api.weather.link}")
	private String link;
}
