package org.gabrielebaldassarre.twitter.tweet;

/**
 * This structure is simply provided to enumerate the constant fragment used to combine and/or elements in a twitter API query
 * 
 * @author Gabriele Baldassarre
 *
 */
public enum TwitterQueryBuilderOperator{
	
	INCLUDE(""),
	EXCLUDE("-"),
	TO("to:"),
	AT("@"),
	FROM("from:");
	
	private String operator;
	
	
	private TwitterQueryBuilderOperator(String operator){
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