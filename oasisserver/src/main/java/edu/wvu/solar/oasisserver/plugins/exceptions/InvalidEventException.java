package edu.wvu.solar.oasisserver.plugins.exceptions;

public class InvalidEventException extends RuntimeException{

	private static final long serialVersionUID = -7641168921694551790L;
	
	public InvalidEventException(String message){
		super(message);
	}
}
