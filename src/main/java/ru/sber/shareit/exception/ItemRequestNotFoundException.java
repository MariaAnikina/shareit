package ru.sber.shareit.exception;

public class ItemRequestNotFoundException extends RuntimeException {
	public ItemRequestNotFoundException(String message) {
		super(message);
	}
}
