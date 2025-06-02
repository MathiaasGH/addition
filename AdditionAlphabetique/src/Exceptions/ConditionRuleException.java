package Exceptions;

public class ConditionRuleException extends Exception{
	public ConditionRuleException() {
		super();
		System.out.println("CRE exception");
	}
	
	public ConditionRuleException(String mess) {
		super(mess);
	}
}
