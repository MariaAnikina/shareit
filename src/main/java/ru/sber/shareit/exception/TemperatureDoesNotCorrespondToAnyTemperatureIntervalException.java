package ru.sber.shareit.exception;

public class TemperatureDoesNotCorrespondToAnyTemperatureIntervalException extends RuntimeException {
	public TemperatureDoesNotCorrespondToAnyTemperatureIntervalException(String message) {
		super(message);
	}
}
