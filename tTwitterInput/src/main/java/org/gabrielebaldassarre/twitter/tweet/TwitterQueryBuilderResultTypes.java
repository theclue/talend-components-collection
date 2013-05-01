package org.gabrielebaldassarre.twitter.tweet;

import twitter4j.Query;

/**
 * This structure is simply provided to enumerate the constant fragment used to combine and/or elements in a twitter API query
 * 
 * @author Gabriele Baldassarre
 *
 */
public enum TwitterQueryBuilderResultTypes{
	
	MIXED(Query.MIXED),
	POPULAR(Query.POPULAR),
	RECENT(Query.RECENT);
	
	private String rt;
	
	
	private TwitterQueryBuilderResultTypes(String rt){
		this.rt = rt;
	}
	
	/**
	 * Return the result types to be used in Twitter API queries
	 * 
	 */
	public String toString(){
		return rt;
	}
}