package com.library.exceptions;

import lombok.Data;

@Data
public class FieldErrorResponse{
	
	private String field;
	private String message;
	
	public FieldErrorResponse(String field, String message) {
        this.field = field;
        this.message = message;
    }

}