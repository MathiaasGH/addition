package Exceptions;

/**
 * Classe pour gérer des exceptions
 * @author Mathias DEVILLIERS
 */
public class ProblemException extends Exception{
	
	public ProblemException() {
		super();
		System.out.println("PE exception");
	}
	
	public ProblemException(String mess) {
		super(mess);
	}
}
