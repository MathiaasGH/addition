package Exceptions;

/**
 * Classe pour gérer des exceptions
 * @author Mathias DEVILLIERS
 */
public class ProblemNameException extends ProblemException{
	
	public ProblemNameException() {
		super();
		System.out.println("PNE exception");
	}
	
	public ProblemNameException(String mess) {
		super(mess);
	}
}
