package org.gabrielebaldassarre.twitter.tweet;

import twitter4j.Query;
import twitter4j.Query.ResultType;;

/**
 * This structure is simply provided to enumerate the constant fragment used to combine and/or elements in a twitter API query
 * 
 * @author Gabriele Baldassarre
 *
 */
public enum TwitterQueryBuilderResultTypes{
	
	MIXED(Query.ResultType.mixed),
	POPULAR(Query.ResultType.popular),
	RECENT(Query.ResultType.recent);
	
	private Query.ResultType rt;
	
	
	private TwitterQueryBuilderResultTypes(Query.ResultType rt){
		this.rt = rt;
	}
	
	/**
	 * Return the result type to be used in Twitter API queries, in a friendly format
	 * 
	 */
	public String toString(){
		return rt.toString();
	}
	
	/**
	 * Return the result type to be used in Twitter API queries, in a native format
	 * 
	 */	
	public Query.ResultType getNativeType(){
		return rt;
	}
	
}