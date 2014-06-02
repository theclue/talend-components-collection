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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import java.util.List;
import java.util.Observable;

import org.gabrielebaldassarre.tcomponent.bridge.TalendColumn;
import org.gabrielebaldassarre.tcomponent.bridge.TalendFlow;
import org.gabrielebaldassarre.tcomponent.bridge.TalendFlowBehaviour;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRow;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRowFactory;
import org.gabrielebaldassarre.twitter.commodities.logger.TwitterLogger;

import twitter4j.Query;
import twitter4j.Twitter;
import twitter4j.QueryResult;
import twitter4j.TwitterBase;
import twitter4j.TwitterException;
import twitter4j.Status;

/**
 * This {@link TalendFlowBehaviour} performs a set of Twitter searches and store results in a {@link TalendFlow}, one resultset (and one query) per {@link TalendRow}.
 * 
 * @author Gabriele Baldassarre
 *
 */
public class TalendFlowQueryResultBehaviour extends Observable implements TalendFlowBehaviour, Iterable<TalendRow>{

	private TalendFlow target;

	private boolean valid = false;

	private Twitter client;
	private Query q;
	private QueryResult qr;
	private int limit;
	private int retrieved;

	private HashMap<TalendColumn, QueryResultField> associations;

	/**
	 * Build a {@link TalendFlow} visitor to perform search operation.
	 * 
	 * @param client a valid {@link Twitter} already authenticated client
	 * @param q a valid query object, eventually built using {@link TwitterQueryBuilder}
	 * @param limit the maximum number of tweets to ask for
	 */
	public TalendFlowQueryResultBehaviour(TwitterBase client, Query q, int limit) {
		ResourceBundle rb = ResourceBundle.getBundle("tTwitterInput", Locale.getDefault());
		

		if(!client.getClass().getInterfaces()[0].equals(Twitter.class)) throw new IllegalArgumentException(rb.getString("exception.illegalConnection"));
		
		this.client = (Twitter)client;
		this.limit = limit;
		this.q = q;
		this.retrieved = 0;
		this.associations = new HashMap<TalendColumn, QueryResultField>();

	}

	/**
	 * Get the maximum number of tweets to get
	 * 
	 * @return the limit
	 */
	public int getLimit(){
		return limit;
	}

	/**
	 * Return the number of tweets already saved in the current set of queries
	 * 
	 * @return the number of tweet retrieved
	 */
	public int alreadyRetrieved(){
		return retrieved;
	}

	/**
	 * Set the query to issue against Twitter, but doesn't perform it.
	 * 
	 * @param q the query that will be issued
	 * @return a reference to this visitor itself
	 */
	public TalendFlowQueryResultBehaviour query(Query q){
		this.q = q;
		return this;
	}

	/**
	 * Check the validity of this visitor. It's false if a {@link TalendFlow} was not defined already as a valid output storage for search operations.
	 *
	 * @return true if this visitor is valid and ready to be used, false otherwise
	 * 
	 */
	public Boolean isValid() {
		return valid;
	}

	/**
	 * Perform a single twitter search and stores output in target {@link TalendFlow}
	 * 
	 * @throws RuntimeException if something went wrong during a search
	 * @throws IllegalStateException if target flow was not visited before triggering the search
	 * @throws IllegalStateException if no valid query object was passed to this visitor before triggering the search
	 */
	@SuppressWarnings("static-access")
	public void search() throws IllegalStateException{
		ResourceBundle rb = ResourceBundle.getBundle("tTwitterInput", Locale.getDefault());

		if(valid == false) throw new IllegalStateException(rb.getString("exception.visitTargetBefore"));
		if(q == null) throw new IllegalStateException(rb.getString("exception.defineQueryBefore"));


		TalendRowFactory rowFactory = target.getModel().getRowFactory();
		TalendRow current;
		List<Status> statusSet = null;
		int howMany = 0;

		try {

			qr = client.search(q);

			howMany = getLimit() - alreadyRetrieved() > qr.getTweets().size() ? qr.getTweets().size() : getLimit() - alreadyRetrieved();
			current = rowFactory.newRow(target);

			Iterator<Entry<TalendColumn, QueryResultField>> i = associations.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry<TalendColumn, QueryResultField> row = (Map.Entry<TalendColumn, QueryResultField>)i.next();
				
				if(target != null && !row.getKey().getFlow().equals(target)){
					throw new IllegalArgumentException(String.format(rb.getString("exception.columnNotInFlow"), row.getKey().getName(), target.getName()));
				}
				
				switch(row.getValue()){
				case COMPLETED_IN:
					current.setValue(row.getKey(), qr.getCompletedIn());
					break;
				case COUNT:
					current.setValue(row.getKey(), qr.getCount());
					break;
				case HAS_NEXT:
					current.setValue(row.getKey(), qr.hasNext());
					break;
				case MAX_ID:
					current.setValue(row.getKey(), qr.getMaxId());
					break;
				case NEXT_QUERY:
					current.setValue(row.getKey(), qr.nextQuery());
					break;
				case QUERY:
					current.setValue(row.getKey(), qr.getQuery());
					break;
				case REFRESH_URL:
					current.setValue(row.getKey(), qr.getRefreshURL());
					break;
				case SINCE_ID:
					current.setValue(row.getKey(), qr.getSinceId());
					break;
				case STATUS_SET:
					statusSet =  new ArrayList<Status>(getLimit() - alreadyRetrieved() < qr.getTweets().size() ? qr.getTweets().subList(0, getLimit() - alreadyRetrieved()) : qr.getTweets());

					current.setValue(row.getKey(), statusSet);
					break;
				default:
					throw new IllegalArgumentException(String.format(rb.getString("exception.unparseableColumn"), row.getKey().getName()));

				}
			}
		} catch (TwitterException te) {
			try {
			switch(te.getStatusCode()){
			case 503:
				/* Four seconds courtesy delay because of a fail whale */
				setChanged();
				notifyObservers(new TwitterLogger("USER_DEF_LOG", Thread.currentThread().getId(), "WARNING", String.format(Locale.getDefault(), rb.getString("log.failWhale"), 4)));

					Thread.currentThread().sleep(4000);
				
				break;
			case 420: /* Wait a safety-interval because of a rate limit */
			case 429:
				setChanged();
				notifyObservers(new TwitterLogger("USER_DEF_LOG", Thread.currentThread().getId(), "WARNING", String.format(Locale.getDefault(), rb.getString("log.rateLimit"), te.getRetryAfter())));

				Thread.currentThread().sleep(te.getRetryAfter());
				break;
			default:
				throw new RuntimeException(te);
			}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		this.retrieved += (statusSet != null ? statusSet.size() : 0);

		setChanged();
		notifyObservers(new TwitterLogger("USER_DEF_LOG", Thread.currentThread().getId(), "INFO", String.format(Locale.getDefault(), rb.getString("log.searchStatus"), qr.getCompletedIn(),  howMany, alreadyRetrieved(), getLimit() - alreadyRetrieved())));
		if(getLimit() - alreadyRetrieved() == 0 || qr.hasNext() == false){
			setChanged();
			notifyObservers(new TwitterLogger("USER_DEF_LOG", Thread.currentThread().getId(), "INFO", String.format(rb.getString("log.searchEnd"), alreadyRetrieved())));

		}
	}

	/**
	 * Visit a target {@link TalendFlow} to store search results
	 * 
	 * @param target the target flow to visit
	 * @throws IllegalStateException if target is null or not valid
	 */
	public void visit(TalendFlow target) throws IllegalStateException {
		ResourceBundle rb = ResourceBundle.getBundle("tTwitterInput", Locale.getDefault());
		if(target == null) throw new IllegalArgumentException(rb.getString("exception.nullTargetFlow"));
		
		if(!target.hasColumns()) initFlow(target);
		
		this.target = target;
		valid = true;
	}

	/**
	 * Init the flow to be used to store the queryresult pages
	 * 
	 * @param target the target flow to init
	 */
	public void initFlow(TalendFlow target) {
		
		// Clean the target flow
		target.truncate();
		
		// Prepare columns for query results data flow
		if (!target.hasColumn("statusSet")){
			target.addColumn("statusSet", QueryResultField.STATUS_SET.getTalendType(), null, false);
			setColumnLink(target.getColumn("statusSet"),  QueryResultField.STATUS_SET);
		}
		if (!target.hasColumn("hasNext")) {
			target.addColumn("hasNext", QueryResultField.HAS_NEXT.getTalendType(), false, false);
			setColumnLink(target.getColumn("hasNext"),  QueryResultField.HAS_NEXT);
		}
		
	}

	/**
	 * Iterator to navigate resultset as a {@link Collection}
	 * 
	 * @return Iterator 
	 * 
	 */
	public Iterator<TalendRow> iterator() {
		return new QueryResultIterator(this);
	}

	/**
	 * Get a reference to target flow this visitor belongs to
	 * 
	 * @return a reference to TargetFlow instance
	 */
	public TalendFlow getTargetFlow(){
		return (valid == true ? target : null);
	}

	/**
	 * Return the current query
	 * 
	 * @return a reference to Query
	 */
	public Query getQuery(){
		return q;
	}
	
	/**
	 * Link a column of visiting {@link TalendFlow} to a propert type as described on {@link QueryResultField}
	 * 
	 * @param column the column to associate with
	 * @param data the type of output; if null, no link is estabilished
	 * @return a reference to the visitor itself
	 */
	public TalendFlowQueryResultBehaviour setColumnLink(TalendColumn column, QueryResultField data) {
		ResourceBundle rb = ResourceBundle.getBundle("tTwitterInput", Locale.getDefault());
		
		if(data == null) return this;
		
		if(column == null) throw new IllegalArgumentException(rb.getString("exception.columnIsNull"));
		
		if(target != null && !column.getFlow().equals(target)){
			throw new IllegalArgumentException(String.format(rb.getString("exception.columnNotInFlow"), column.getName(), target.getName()));
		}
		
		associations.put(column, data);
		return this;
	}

}