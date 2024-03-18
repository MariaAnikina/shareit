package ru.sber.shareit.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemperatureIntervals {
	VERY_COLD (-50.0, -15.0),
	COLD (-15.0, 0.0),
	NEUTRAL (0.0, 10.0),
	WARM (10.0, 20.0),
	VERY_WARM (20.0, 50.0),
	ALWAYS_RELEVANT (-50.0, 50.0),
	;

	private final Double startOfIntervalInclusive;
	private final Double endOfIntervalNotInclusive;

}
