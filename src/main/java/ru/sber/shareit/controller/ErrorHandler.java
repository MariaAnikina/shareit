package ru.sber.shareit.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.sber.shareit.exception.*;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@Slf4j
@ControllerAdvice
@Controller
@RequestMapping("/error")
public class ErrorHandler {
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@GetMapping("/valid")
	public String handleMethodArgumentNotValidException(MethodArgumentNotValidException e, Model model) {
		String error = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
		log.error(error);
		model.addAttribute("response", new ErrorResponse(error, HttpStatus.BAD_REQUEST));
		return "error-page";
	}

	@ExceptionHandler({ItemUnavailableException.class, BookingEndTimeException.class, BookingTimeException.class,
			BookingStateException.class, BookingStatusException.class, ConstraintViolationException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@GetMapping("/bad_request")
	public String handleBadRequest(RuntimeException e, Model model) {
		log.error(e.getMessage());
		model.addAttribute("response", new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST));
		return "error-page";
	}

	@ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class,
			BookingNotFoundException.class, ItemRequestNotFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@GetMapping("/not_found")
	public String handleNotFoundException(RuntimeException e, Model model) {
		log.error(e.getMessage());
		model.addAttribute("response", new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND));
		return "error-page";
	}

	@ExceptionHandler({UserAlreadyExistsException.class})
	@ResponseStatus(HttpStatus.CONFLICT)
	@GetMapping("/conflict")
	public String handleAlreadyExistsException(RuntimeException e, Model model) {
		log.error(e.getMessage());
		model.addAttribute("response", new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT));
		return "error-page";

	}

	@ExceptionHandler({ItemOwnerException.class})
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@GetMapping("/forbidden")
	public String handleWrongItemOwnerException(RuntimeException e, Model model) {
		log.error(e.getMessage());
		model.addAttribute("response", new ErrorResponse(e.getMessage(), HttpStatus.FORBIDDEN));
		return "error-page";
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@GetMapping
	public String handleInternalError(Throwable e, Model model) {
		log.error(e.getMessage());
		model.addAttribute(
				"response",
				new ErrorResponse("Произошла непредвиденная ошибка.", HttpStatus.INTERNAL_SERVER_ERROR)
		);
		return "error-page";
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class ErrorResponse {
		private final String error;
		private final HttpStatus httpStatus;
	}
}
