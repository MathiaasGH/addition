package Exceptions;

public class CantRetrieveAnswerInAnswerMemoryException extends Exception{
	public CantRetrieveAnswerInAnswerMemoryException() {
		super();
	}
	
	public CantRetrieveAnswerInAnswerMemoryException(String mess) {
		super(mess);
	}
}