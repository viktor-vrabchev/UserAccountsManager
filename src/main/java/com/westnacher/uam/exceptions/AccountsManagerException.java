package com.westnacher.uam.exceptions;

import org.springframework.http.HttpStatus;

public class AccountsManagerException extends RuntimeException {

	private static final long serialVersionUID = 1336424367266406457L;

	private HttpStatus status;

	public AccountsManagerException() {
		super();
	}

	public AccountsManagerException(String message) {
		super(message);
	}

	public AccountsManagerException(HttpStatus status) {
		super();
		this.status = status;
	}

	public AccountsManagerException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

}
