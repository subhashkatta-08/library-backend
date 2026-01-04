package com.library.exceptions;

public class AdminNotFoundException extends RuntimeException {
	
	public AdminNotFoundException() {
		
	}
	
	public AdminNotFoundException(String msg) {
		super(msg);
		
	}

}
