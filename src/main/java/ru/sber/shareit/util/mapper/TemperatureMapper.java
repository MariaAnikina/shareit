package ru.sber.shareit.util.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sber.shareit.entity.enums.TemperatureIntervals;

import static ru.sber.shareit.entity.enums.TemperatureIntervals.*;

@Component
@RequiredArgsConstructor
public class TemperatureMapper {
	private final ObjectMapper objectMapper = new ObjectMapper();

	public TemperatureIntervals getTemperatureInCelsiusFromString(String body) {
		try {
			JsonNode jsonNode = objectMapper.readTree(body);
			Double temperature = Double.parseDouble(jsonNode.get("main").findValue("temp").asText()) - 273;
			return temperatureToTemperatureIntervalConverter(temperature);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e); //TODO обработать исключение сервис/маппер, вернуть null
		}
	}

	private TemperatureIntervals temperatureToTemperatureIntervalConverter(Double temperature) {
		if (temperature < VERY_COLD.getStartOfIntervalInclusive()) {
			return null;
		} else if (temperature < VERY_COLD.getEndOfIntervalNotInclusive()) {
			return VERY_COLD;
		} else if (temperature < COLD.getEndOfIntervalNotInclusive()) {
			return COLD;
		} else if (temperature < NEUTRAL.getEndOfIntervalNotInclusive()) {
			return NEUTRAL;
		} else if (temperature < WARM.getEndOfIntervalNotInclusive()) {
			return WARM;
		} else if (temperature < VERY_WARM.getEndOfIntervalNotInclusive()) {
			return VERY_WARM;
		} else {
			return null;
		}
	}
}
