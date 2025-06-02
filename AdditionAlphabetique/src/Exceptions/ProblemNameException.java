package Exceptions;

public class ProblemNameException extends ProblemException{
	
	public ProblemNameException() {
		super();
		System.out.println("PNE exception");
	}
	
	public ProblemNameException(String mess) {
		super(mess);
	}
}
