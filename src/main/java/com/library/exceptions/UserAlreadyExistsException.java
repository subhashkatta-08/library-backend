package com.library.exceptions;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class UserAlreadyExistsException extends RuntimeException {
	
	private List<FieldErrorResponse> errors;
	
	public UserAlreadyExistsException(List<FieldErrorResponse> errors) {
		this.errors = errors;
	}

}
