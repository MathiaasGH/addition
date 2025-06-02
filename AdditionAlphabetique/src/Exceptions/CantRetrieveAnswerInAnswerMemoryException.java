package Exceptions;

public class CantRetrieveAnswerInAnswerMemoryException extends Exception{
	public CantRetrieveAnswerInAnswerMemoryException() {
		super();
		System.out.println("err........................................................................................");
	}
	
	public CantRetrieveAnswerInAnswerMemoryException(String mess) {
		super(mess);
	}
}