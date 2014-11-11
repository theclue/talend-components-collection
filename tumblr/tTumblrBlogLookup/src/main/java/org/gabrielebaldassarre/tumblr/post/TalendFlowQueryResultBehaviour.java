/*
This file is part of Tumblr Component Package for Talend
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
package org.gabrielebaldassarre.tumblr.post;

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



import org.gabrielebaldassarre.tcomponent.bridge.TalendType;
import org.gabrielebaldassarre.tumblr.logger.TumblrLogger;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Post;

/**
 * This {@link TalendFlowBehaviour} performs a set of Tumblr searches and store results in a {@link TalendFlow}, one result set (and one query) per {@link TalendRow}.
 * 
 * @author Gabriele Baldassarre
 *
 */
public class TalendFlowQueryResultBehaviour extends Observable implements TalendFlowBehaviour, Iterable<TalendRow>{

	private TalendFlow target;

	private boolean valid = false;

	private JumblrClient client;
	private String tag;

	private int limit;
	private int retrieved;
	
	private Map<String, String> options;

	/**
	 * Build a {@link TalendFlow} visitor to perform search operation.
	 * 
	 * @param client a valid {@link JumblrClient} already authenticated client
	 * @param q a valid query object, eventually built using {@link TwitterQueryBuilder}
	 * @param limit the maximum number of posts to ask for
	 */
	public TalendFlowQueryResultBehaviour(JumblrClient client, String tag, int limit) {
		ResourceBundle rb = ResourceBundle.getBundle("tTumblrInput", Locale.getDefault());

		if(!client.getClass().equals(JumblrClient.class)) throw new IllegalArgumentException(rb.getString("exception.illegalConnection"));

		this.client = (JumblrClient)client;
		this.limit = limit;
		this.tag = tag;
		this.retrieved = 0;
		
		this.options = new HashMap<String, String>();
	}

	/**
	 * Get the maximum number of posts to get
	 * 
	 * @return the limit
	 */
	public int getLimit(){
		return limit;
	}

	/**
	 * Return the number of posts already saved in the current set of queries
	 * 
	 * @return the number of posts retrieved
	 */
	public int alreadyRetrieved(){
		return retrieved;
	}

	/**
	 * Set the query options to issue against Tumblr, but doesn't perform the query itself.
	 * 
	 * @param query the query options
	 * @return a reference to this visitor itself
	 */
	public TalendFlowQueryResultBehaviour query(Map<String, String> options){
		this.options = options;
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
	 * Perform a single tumblr search and stores output in target {@link TalendFlow}
	 * 
	 * @throws RuntimeException if something went wrong during a search
	 * @throws IllegalStateException if target flow was not visited before triggering the search
	 * @throws IllegalStateException if no valid query object was passed to this visitor before triggering the search
	 */
	@SuppressWarnings("static-access")
	public void search() throws IllegalStateException{
		ResourceBundle rb = ResourceBundle.getBundle("tTumblrInput", Locale.getDefault());

		if(valid == false) throw new IllegalStateException(rb.getString("exception.visitTargetBefore"));
		if(tag == null) throw new IllegalStateException(rb.getString("exception.defineTagBefore"));


		TalendRowFactory rowFactory = target.getModel().getRowFactory();
		TalendRow current;
		List<Post> posts = null;
		int howMany = 0;

			posts = client.tagged(tag, options);

			howMany = getLimit() - alreadyRetrieved() > posts.size() ? posts.size() : getLimit() - alreadyRetrieved();
			current = rowFactory.newRow(target);

			// Store a page of posts in target flow
			current.setValue("postList", new ArrayList<Post>(getLimit() - alreadyRetrieved() < posts.size() ? posts.subList(0, getLimit() - alreadyRetrieved()) : posts));


		this.retrieved += (posts != null ? (getLimit() - alreadyRetrieved() < posts.size() ? getLimit() - alreadyRetrieved() : posts.size()): 0);

		setChanged();
		notifyObservers(new TumblrLogger("USER_DEF_LOG", Thread.currentThread().getId(), "INFO", String.format(Locale.getDefault(), rb.getString("log.searchStatus"),  howMany, alreadyRetrieved(), getLimit() - alreadyRetrieved(), (getBefore() == null ? "current date" : getBefore()))));
		if(getLimit() - alreadyRetrieved() == 0 || posts.size() < 20){
			setChanged();
			notifyObservers(new TumblrLogger("USER_DEF_LOG", Thread.currentThread().getId(), "INFO", String.format(rb.getString("log.searchEnd"), alreadyRetrieved())));

		}
	}

	/**
	 * Visit a target {@link TalendFlow} to store search results
	 * 
	 * @param target the target flow to visit
	 * @throws IllegalStateException if target is null or not valid
	 */
	public void visit(TalendFlow target) throws IllegalStateException {
		ResourceBundle rb = ResourceBundle.getBundle("tTumblrInput", Locale.getDefault());
		if(target == null) throw new IllegalArgumentException(rb.getString("exception.nullTargetFlow"));

		if(!target.hasColumns()) initFlow(target);

		this.target = target;
		valid = true;
	}

	/**
	 * Init the flow to be used to store the results pages
	 * 
	 * @param target the target flow to init
	 */
	public void initFlow(TalendFlow target) {

		// Clean the target flow
		target.truncate();

		// Prepare columns for query results data flow
		if (!target.hasColumn("postList")){
			target.addColumn("postList", TalendType.LIST, null, false);
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
	 * Return the current tag
	 * 
	 * @return a reference to Query
	 */
	public String getTag(){
		return tag;
	}
	
	/**
	 * Return the current query options set
	 * 
	 * @return a reference to the query
	 */
	public Map<String, String> getQuery(){
		return options;
	}
	
	/**
	 * Set the before option
	 * 
	 * @return a reference to the instance itself
	 */
	public TalendFlowQueryResultBehaviour before(Long timestamp){
		options.put("before", timestamp.toString());
		return this;
	}

	/**
	 * Get the before option
	 * 
	 * @return the value for the option
	 */
	public String getBefore(){
		return options.get("before");
	}
	
	/**
	 * Set the filter option
	 * 
	 * @return a reference to the instance itself
	 */
	public TalendFlowQueryResultBehaviour filter(String filter){
		format(filter);
		return this;
	}

	/**
	 * Set the format option
	 * 
	 * @return a reference to instance itself
	 */
	public TalendFlowQueryResultBehaviour format(String format){
		options.put("filter", format != null ? format.toLowerCase().equals("html") ? null : format.toLowerCase() : null);
		return this;
	}

}