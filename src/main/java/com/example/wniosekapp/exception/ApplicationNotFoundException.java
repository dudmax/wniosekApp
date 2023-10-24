package com.example.wniosekapp.exception;

public class ApplicationNotFoundException extends RuntimeException {

	public ApplicationNotFoundException(Long applicationId) {
		super("Application not found for ID: " + applicationId);
	}

}
