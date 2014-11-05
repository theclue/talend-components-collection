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
along with this package.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gabrielebaldassarre.tumblr.post;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.gabrielebaldassarre.tcomponent.bridge.TalendRow;

import com.tumblr.jumblr.types.Post;


/**
 * This class gives the possibility to iterate through a Tumblr Query result set retrieved
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
		// Check against the limit of posts or if no other query can be built
		if(t.alreadyRetrieved() >= t.getLimit()) return false;
		
		// Perform the search and get the last page of results
		t.search();
		current = t.getTargetFlow().getRow(t.getTargetFlow().countRows() - 1);
		List<Post> posts = (List<Post>) current.getValue(t.getTargetFlow().getColumn("postList").getName());

		// No posts were found
		if(posts == null || posts.size() == 0) return false;
		
		// Update the 'before' field for the next query
		t.before(posts.get(posts.size() -1).getTimestamp() -1l);
		
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
