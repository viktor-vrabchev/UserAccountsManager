package com.westnacher.uam.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.westnacher.uam.dtos.ApplicationErrorResponse;
import com.westnacher.uam.exceptions.AccountsManagerException;

@RestControllerAdvice
class AccountsManagerExceptionHandler {

	@ExceptionHandler(value = AccountsManagerException.class)
	public ResponseEntity<?> handleConstraintViolation(AccountsManagerException ex) {

		ApplicationErrorResponse apiError = new ApplicationErrorResponse(ex.getStatus(), ex.getLocalizedMessage(),
				ex.getMessage());
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}
}
