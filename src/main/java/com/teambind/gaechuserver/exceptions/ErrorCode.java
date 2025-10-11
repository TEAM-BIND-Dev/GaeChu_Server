package com.teambind.gaechuserver.exceptions;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
	LIKE_NOT_FOUND("LIKE_001", "Like Not Found", HttpStatus.NOT_FOUND),
	INVALID_CATEGORY("CAT_001", "허용하지않은 카테고리입니다", HttpStatus.BAD_REQUEST),
	;
	
	
	private final String errCode;
	private final String message;
	private final HttpStatus status;
	
	ErrorCode(String errCode, String message, HttpStatus status) {
		
		this.status = status;
		this.errCode = errCode;
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "ErrorCode{" +
				" status='" + status + '\'' +
				"errCode='" + errCode + '\'' +
				", message='" + message + '\'' +
				'}';
	}
	
}
