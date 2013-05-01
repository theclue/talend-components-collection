/*
	This file is part of tTwitterInput Talend component

    Talend Bridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Talend Bridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Nome-Programma.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gabrielebaldassarre.twitter.tweet;

import java.io.UnsupportedEncodingException;
import java.lang.StringBuilder;
import java.lang.IllegalArgumentException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import java.net.URLEncoder;

import twitter4j.Query;



/**
 * This class helps to build a valid query string to send to Twitter API, using
 * a Talend Open Studio additive filtering approach.
 * 
 * @author Gabriele Baldassarre
 *
 */
public class TwitterQueryBuilder{

	public static String LINKFRAGMENT = "filter:links";
	public static String QUESTIONFRAGMENT = "?";
	public static int DEFAULTTPP = 100;

	private TwitterQueryBuilderLogicalOperator op;
	private TwitterQueryBuilderAttitude attitude;
	private TwitterQueryBuilderResultTypes rt;
	private StringBuilder sb;
	private Map<String, TwitterQueryBuilderOperator> queryFragments;
	private boolean filterLinks = false;
	private boolean filterQuestions = false;
	private String lang;

	private Query query;
	private String advanced;
	private String since;
	private Long sinceId;
	private String until;
	private Long maxId;
	private Integer count;

	public TwitterQueryBuilder(){
		queryFragments = new HashMap<String, TwitterQueryBuilderOperator>();
	}


	/**
	 * Set the operator between query fragmens, ie 'AND', 'OR'. Logical operator are defined with an enum.
	 * 
	 * @param operator the operator
	 * @return a reference to the builder itself
	 */
	public TwitterQueryBuilder logicalOperator(TwitterQueryBuilderLogicalOperator operator){
		op = operator;
		attitude(TwitterQueryBuilderAttitude.NOFILTER);
		count(DEFAULTTPP);
		return this;
	}

	/**
	 * Add a condition
	 * 
	 * @param query the keyword or the set of keywords for the condition
	 * @param op the operator that apply to query (ie. <em>"EXCLUDE, "INCLUDE"...</em>)
	 * @return a reference to the builder itself
	 */
	public TwitterQueryBuilder condition(String query, TwitterQueryBuilderOperator op){
		ResourceBundle rb = ResourceBundle.getBundle("tTwitterInput", Locale.getDefault());
		if(query == null || query.length()==0){
			throw new IllegalArgumentException(rb.getString("exception.queryNull"));
		}

		queryFragments.put(query, op);

		return this;
	}
	
	/**
	 * Set directly a query, bypassing the builder
	 * 
	 * @param query a valid twitter query string
	 * @return a reference to the builder itsel
	 */
	public TwitterQueryBuilder advancedQuery(String query){
		ResourceBundle rb = ResourceBundle.getBundle("tTwitterInput", Locale.getDefault());
		if(query == null || query.length()==0){
			throw new IllegalArgumentException(rb.getString("exception.queryNull"));
		}
		
		this.advanced = query;
		return this;
	}

	/**
	 * Add a including condition
	 * 
	 * @param query the keyword or the set of keywords for the condition
	 * @return a reference to the builder itself
	 */
	public TwitterQueryBuilder condition(String query){
		return condition(query, null);
	}

	/**
	 * Set if you want to filter only links
	 * 
	 * @param filter set the filtering condition
	 * @return a reference to the builder itself
	 */
	public TwitterQueryBuilder filterLinks(boolean filter){
		this.filterLinks = filter;
		return this;
	}

	/**
	 * Get the actual filtering link condition
	 * 
	 * @return true if filtering link condition is enable
	 */
	public boolean getFilterLinksCondition(){
		return filterLinks;
	}

	/**
	 * Set if you want to just get tweets expressing questions
	 * 
	 * @param filter the filtering condition
	 * @return a reference to the builder itself
	 */
	public TwitterQueryBuilder filterQuestions(boolean filter){
		this.filterQuestions = filter;
		return this;
	}
	
	/**
	 * Get the actual filtering questions condition
	 * 
	 * @return true if filtering questions condition is enable
	 */
	public boolean getFilterQuestionsCondition(){
		return filterQuestions;
	}
	
	/**
	 * Get the actual advanced query condition, if available
	 * 
	 * @return the query string
	 */
	public String getAdvancedQuery(){
		return advanced;
	}

	/**
	 * Set if you want to filter tweets by attitude
	 * 
	 * @param attitude the filtering conditions
	 * @return a reference to the builder itself
	 */
	public TwitterQueryBuilder attitude(TwitterQueryBuilderAttitude attitude){
		this.attitude = attitude;
		return this;
	}

	/**
	 * Get the actual filtering attitude condition
	 * 
	 * @return true if filtering by attitude condition is enable
	 */	
	public TwitterQueryBuilderAttitude getAttitude(){
		return attitude;
	}

	/**
	 * Build a valid Twitter API {@link Query}ery from the inputed conditions
	 * 
	 * @return a reference to a valid {@link Query} object
	 */
	public Query build(){

		ResourceBundle rb = ResourceBundle.getBundle("tTwitterInput", Locale.getDefault());
		String item;

		query = new Query();
		sb = new StringBuilder();

		Iterator<Entry<String, TwitterQueryBuilderOperator>> qf = queryFragments.entrySet().iterator();
		while (qf.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry f = (Map.Entry)qf.next();

			if(TwitterQueryBuilderLogicalOperator.OR.equals(op) && TwitterQueryBuilderOperator.EXCLUDE.equals(f.getValue()))
				throw new IllegalStateException(rb.getString("exception.excludingOr"));

			item = ((f.getValue() == null ? TwitterQueryBuilderOperator.INCLUDE : f.getValue()).toString() + f.getKey());
			if(sb.length()>0) sb.append(op.toString());
			sb.append((item.split("\\s+").length <= 1) ? item : ("\"" + item + "\""));

			qf.remove(); // avoids a ConcurrentModificationException
		}

		if(getFilterLinksCondition()) sb.append(" " + TwitterQueryBuilder.LINKFRAGMENT);
		if(getFilterQuestionsCondition()) sb.append(" " + TwitterQueryBuilder.QUESTIONFRAGMENT);
		if(!TwitterQueryBuilderAttitude.NOFILTER.equals(getAttitude())) sb.append(" " + getAttitude().toString());

		try {
			query.setQuery(URLEncoder.encode(sb.toString(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
		if(lang != null) query.lang(lang);
		if(rt != null) query.resultType(rt.toString());
		if(since != null) query.since(since);
		if(sinceId != null) query.sinceId(sinceId);
		if(until != null) query.until(until);
		if(maxId != null) query.maxId(maxId);
		if(count != null) query.count(count);
		return query;

	}

	/**
	 * Set the language filtering condition
	 * 
	 * @param lang the language to filter tweets written into
	 * @return a reference to the builder itself
	 */
	public TwitterQueryBuilder lang(String lang){
		this.lang = lang;
		return this;
	}
	
	/**
	 * Set the result types filtering condition
	 * 
	 * @param lang the result type filter
	 * @return a reference to the builder itself
	 */
	public TwitterQueryBuilder resultType(TwitterQueryBuilderResultTypes resultType){
		rt = resultType;
		return this;
	}

	/**
	 * Set the since filtering condition (in valid string rapresentation of dates in twitter API)
	 * 
	 * @param the date in a string format like 'YYYY-MM-DD' to filter from
	 * @return a reference to the builder itself
	 */
	public TwitterQueryBuilder since(String since){
		this.since = since;
		return this;
	}

	/**
	 * Filter only tweets newer than the one with given ID
	 * 
	 * @param sinceId the ID to filter out older tweets
	 * @return a reference to the builder itself
	 */
	public TwitterQueryBuilder sinceId(long sinceId){
		this.sinceId = sinceId;
		return this;
	}

	/**
	 * Set the since filtering condition (in valid string rapresentation of dates in twitter API)
	 * 
	 * @param the date in a string format like 'YYYY-MM-DD' to filter to
	 * @return a reference to the builder itself
	 */
	public TwitterQueryBuilder until(String until){
		this.until = until;
		return this;
	}

	/**
	 * Filter only tweets older than the one with given ID
	 * 
	 * @param maxId the ID to filter out newer tweets
	 * @return a reference to the builder itself
	 */
	public TwitterQueryBuilder maxId(long maxId){
		this.maxId = maxId;
		return this;
	}

	/**
	 * Set the numbers of tweets per page (default: 100)
	 * 
	 * @param count the number of tweets per page in result sets
	 * @return a reference to the builder itself
	 */
	public TwitterQueryBuilder count(int count){
		this.count = (count > DEFAULTTPP || count <= 0 ? DEFAULTTPP : count);
		return this;
	}

	@Override
	public boolean equals(Object e){
		ResourceBundle rb = ResourceBundle.getBundle("tTwitterInput", Locale.getDefault());
		if(e instanceof TwitterQueryBuilder) throw new IllegalArgumentException(rb.getString("exception.wrongComparing"));
		if(e == null) throw new IllegalArgumentException(rb.getString("exception.comparingNull"));
		return ((TwitterQueryBuilder)e).build().equals(query);
	}
}