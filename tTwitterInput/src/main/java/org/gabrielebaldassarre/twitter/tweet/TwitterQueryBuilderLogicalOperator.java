package org.gabrielebaldassarre.twitter.tweet;

/**
 * This structure is simply provided to enumerate the constant fragment used to combine and/or elements in a twitter API query
 * 
 * @author Gabriele Baldassarre
 *
 */
public enum TwitterQueryBuilderLogicalOperator{
	
	AND(" "),
	OR(" OR ");
	
	private String operator;
	
	
	private TwitterQueryBuilderLogicalOperator(String operator){
		this.operator = operator;
	}
	
	/**
	 * Return the operator fragment to be used in Twitter API queries
	 * 
	 */
	public String toString(){
		return operator;
	}
}