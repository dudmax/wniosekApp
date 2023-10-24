package com.example.wniosekapp.exception;

public class StateNotFoundException extends RuntimeException{

	public StateNotFoundException() {
		super("State not found");
	}

}
