package com.teambind.gaechuserver.exceptions;

public class CustomException extends RuntimeException {
	private final ErrorCode errorcode;
	
	public CustomException(ErrorCode errorcode) {
		
		super(errorcode.toString());
		this.errorcode = errorcode;
	}
	
	public HttpStatus getStatus() {
		return errorcode.getStatus();
	}
}
