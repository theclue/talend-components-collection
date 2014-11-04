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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.gabrielebaldassarre.tcomponent.bridge.TalendRow;

import twitter4j.Query;
import twitter4j.Status;

/**
 * This class gives the possibility to iterate through a Twitter Query result set retrieved
 * using a {@link TalendFlowQueryResultBehaviour} issued query.
 * 
 * @author Gabriele Baldassarre
 *
 */
public class QueryResultIterator implements Iterator<TalendRow> {

	private TalendFlowQueryResultBehaviour t;
	private TalendRow current;
	
	/**
	 * Create the {@link Iterator}
	 * @param t a valid visitor to iterate through
	 */
	public QueryResultIterator(TalendFlowQueryResultBehaviour t) {
		this.t = t;
	}

	/**
	 * Check if the collection has a next value
	 * 
	 */
	public boolean hasNext() {	
		// Check against the limit of tweets
		if(t.getQuery() == null || t.alreadyRetrieved() >= t.getLimit()) return false;
		
		// Perform the search and get the last page of results
		t.search();
		current = t.getTargetFlow().getRow(t.getTargetFlow().countRows() - 1);
		List<Status> tweets = (List<Status>) current.getValue(t.getTargetFlow().getColumn("statusSet").getName());

		// No tweets were found
		if(tweets == null || tweets.size() == 0) return false;
		
		// Since the paging query won't work, manually construct the query for the second page
		Query temp = t.getQuery();
		temp.setMaxId((tweets.get(tweets.size() - 1)).getId() -1l);
		t.query(temp);
		
		// If here, we have valid results
		return true;
		
	}

	/**
	 * Get the next result set available
	 * 
	 * @return a reference to a {@link TalendRow} that stores current result set.
	 */
	public TalendRow next() {

		return current;
	}

	/**
	 * This method is not supported and it always throws an exception.
	 * @throws UnsupportedOperationException
	 */
	public void remove() {
		throw new UnsupportedOperationException();

	}
}
