package Exceptions;

public class NoSuchRuleException extends Exception{
	public NoSuchRuleException() {
		super();
		System.out.println("NSRE exception");
	}
	
	public NoSuchRuleException(String mess) {
		super(mess);
	}
}
