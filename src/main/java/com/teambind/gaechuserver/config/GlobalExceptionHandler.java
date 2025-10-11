package com.teambind.gaechuserver.config;

import com.teambind.gaechuserver.exceptions.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<?> handleCustomException(CustomException ex) {
		
		return ResponseEntity
				.status(ex.getStatus())
				.body(ex.getMessage());
	}
}
