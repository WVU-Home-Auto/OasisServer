package edu.wvu.solar.oasisserver.plugins.exceptions;

public class InvalidParametersException extends RuntimeException{

	private static final long serialVersionUID = -1591151716950691863L;

	public InvalidParametersException(String message){
		super(message);
	}
}
