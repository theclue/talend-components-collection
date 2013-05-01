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
 * This enum describes all the supported fields you can get from a Tweet
 * 
 * @author Gabriele Baldassarre
 *
 */
public enum TweetField {

	STATUS_ID(TalendType.LONG),
	REPLYTO_SCREEN_NAME(TalendType.STRING),
	REPLYTO_USERID(TalendType.LONG),
	REPLYTO_STATUSID(TalendType.LONG),
	LOCATION(TalendType.STRING),
	RETWEET_COUNT(TalendType.LONG),
	SOURCE(TalendType.STRING),
	TEXT(TalendType.STRING),
	FROM_NAME(TalendType.STRING),
	FROM_SCREEN_NAME(TalendType.STRING),
	FROM_USERID(TalendType.LONG),
	IS_FAVORITED(TalendType.BOOLEAN),
	IS_POSSIBLY_SENSITIVE(TalendType.BOOLEAN),
	IS_RETWEET(TalendType.BOOLEAN),
	HASHTAGS(TalendType.LIST),
	URL_ENTITIES(TalendType.LIST),
	USER_MENTIONS(TalendType.LIST),
	CREATION_DATE(TalendType.DATE),
	URL_ENTITIES_STRING(TalendType.STRING),
	USER_MENTIONS_SCREEN_NAME(TalendType.STRING),
	JSON(TalendType.STRING);
	
	private TalendType type;


	private TweetField(TalendType type){
		this.type = type;
	}
	
	public TalendType getTalendType(){
		return type;
	}


}
