package edu.wvu.solar.oasisserver.plugins.exceptions;

public class InvalidDeviceException extends Exception{

	private static final long serialVersionUID = -986959504654297277L;

	public InvalidDeviceException(String message){
		super(message);
	}
}
