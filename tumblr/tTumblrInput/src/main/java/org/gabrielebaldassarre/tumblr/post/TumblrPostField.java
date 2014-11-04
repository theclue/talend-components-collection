package org.gabrielebaldassarre.tumblr.post;

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

import org.gabrielebaldassarre.tcomponent.bridge.TalendType;


/**
* This enum describes all the supported fields you can get from a Tumblr Post
* 
* @author Gabriele Baldassarre
*
*/
public class TumblrPostField {

public static final TalendType POST_ID = TalendType.LONG;
public static final TalendType BLOG_NAME = TalendType.STRING;
public static final TalendType DATE_GMT = TalendType.STRING;
public static final TalendType FORMAT= TalendType.STRING;
public static final TalendType NOTE_COUNT = TalendType.LONG;
public static final TalendType POST_URL = TalendType.STRING;
public static final TalendType REBLOGGED_FROM_ID = TalendType.LONG;
public static final TalendType REBLOGGED_FROM_NAME = TalendType.STRING;
public static final TalendType REBLOG_KEY = TalendType.STRING;
public static final TalendType SLUG = TalendType.STRING;
public static final TalendType OURCE_TITLE = TalendType.STRING;
public static final TalendType SOURCE_URL = TalendType.STRING;
public static final TalendType STATE = TalendType.STRING;
public static final TalendType TAGS = TalendType.LIST;
public static final TalendType TIMESTAMP = TalendType.LONG;
public static final TalendType TYPE = TalendType.STRING;
public static final TalendType IS_BOOKMARKLET = TalendType.BOOLEAN;
public static final TalendType IS_LINKED = TalendType.BOOLEAN;
public static final TalendType IS_MOBILE = TalendType.BOOLEAN;

public Class<?> getTalendType(String field) throws NoSuchFieldException, SecurityException{
	return this.getClass().getField(field).getType();
}


}