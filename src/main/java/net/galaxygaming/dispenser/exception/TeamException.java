package net.galaxygaming.dispenser.exception;

public class TeamException extends Exception {
	private static final long serialVersionUID = -5078864905689474417L;
	
	private String message;
	
	public TeamException() {}
	
	public TeamException(String message) {
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