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

import org.gabrielebaldassarre.tcomponent.bridge.TalendType;
/**
 * This enum defines all the possible field available in a query resultset, after a succesfull search.
 * 
 * @author Gabriele Baldassarre
 *
 */
public enum QueryResultField {
	
	/**
	 * Result Set; each element of this list is a tweet
	 */
	STATUS_SET(TalendType.LIST),
	/**
	 * The number of seconds that was needed to fulfill the query
	 */
	COMPLETED_IN(TalendType.DOUBLE),
	/**
	 * The number of tweets per page
	 */
	COUNT(TalendType.INTEGER),
	/**
	 * Tweet Maximum ID retrieved
	 */
	MAX_ID(TalendType.LONG),
	/**
	 * The query issued to Twitter
	 */
	QUERY(TalendType.STRING),
	/**
	 * A URL to refresh current query result
	 */
	REFRESH_URL(TalendType.STRING),
	/**
	 * Tweet Sincd ID retrieved
	 */
	SINCE_ID(TalendType.LONG),
	/**
	 * A boolean value set to true if the current query has tweets to fullfill other pages
	 */
	HAS_NEXT(TalendType.BOOLEAN),
	/**
	 * Reference to a Query object that points to a new resultset, or null if this query has not a next page of results
	 */
	NEXT_QUERY(TalendType.OBJECT);
	
	private TalendType type;

	private QueryResultField(TalendType type){
		this.type = type;
	}
	
	/**
	 * Return a reference to a {@link TalendType} enum depending of the type of current field
	 * @return a reference to a specific TalendType object
	 */
	public TalendType getTalendType(){
		return type;
	}
	
}