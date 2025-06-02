package Exceptions;

public class NoSuchRuleException extends Exception{
	public NoSuchRuleException() {
		super();
	}
	
	public NoSuchRuleException(String mess) {
		super(mess);
	}
}
