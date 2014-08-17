package net.galaxygaming.dispenser.exception;

public class InvalidInputException extends Exception {
	private static final long serialVersionUID = -7700986873885669523L;

	private String message;
	
	public InvalidInputException() {}
	
	public InvalidInputException(String message) {
		super(message);
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return message;
	}
}