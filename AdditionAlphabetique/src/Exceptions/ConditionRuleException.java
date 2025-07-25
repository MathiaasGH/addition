package Exceptions;

/**
 * Classe pour gérer des exceptions
 * @author Mathias DEVILLIERS
 */
public class ConditionRuleException extends Exception{
	public ConditionRuleException() {
		super();
		System.out.println("CRE exception");
	}
	
	public ConditionRuleException(String mess) {
		super(mess);
	}
}
