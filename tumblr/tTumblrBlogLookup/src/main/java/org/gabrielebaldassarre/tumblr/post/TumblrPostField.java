package org.gabrielebaldassarre.tumblr.post;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.gabrielebaldassarre.tcomponent.bridge.TalendType;


/**
 * This enum describes all the supported fields you can get from a Tumblr Post
 * 
 * @author Gabriele Baldassarre
 *
 */
public enum TumblrPostField {

	// Generics
	POST_ID(TalendType.LONG, Arrays.asList(TumblrPostType.values())),
	BLOG_NAME(TalendType.STRING, Arrays.asList(TumblrPostType.values())),
	DATE_GMT(TalendType.STRING, Arrays.asList(TumblrPostType.values())),
	FORMAT(TalendType.STRING, Arrays.asList(TumblrPostType.values())),
	NOTE_COUNT(TalendType.LONG, Arrays.asList(TumblrPostType.values())),
	POST_URL(TalendType.STRING, Arrays.asList(TumblrPostType.values())),
	REBLOGGED_FROM_ID(TalendType.LONG, Arrays.asList(TumblrPostType.values())),
	REBLOGGED_FROM_NAME(TalendType.STRING, Arrays.asList(TumblrPostType.values())),
	REBLOG_KEY(TalendType.STRING, Arrays.asList(TumblrPostType.values())),
	SLUG(TalendType.STRING, Arrays.asList(TumblrPostType.values())),
	SOURCE_TITLE(TalendType.STRING, Arrays.asList(TumblrPostType.values())),
	SOURCE_URL(TalendType.STRING, Arrays.asList(TumblrPostType.values())),
	STATE(TalendType.STRING, Arrays.asList(TumblrPostType.values())),
	TAGS(TalendType.LIST, Arrays.asList(TumblrPostType.values())),
	TIMESTAMP(TalendType.LONG, Arrays.asList(TumblrPostType.values())),
	TYPE(TalendType.STRING, Arrays.asList(TumblrPostType.values())),
	IS_BOOKMARKLET(TalendType.BOOLEAN, Arrays.asList(TumblrPostType.values())),
	IS_LINKED(TalendType.BOOLEAN, Arrays.asList(TumblrPostType.values())),
	IS_MOBILE(TalendType.BOOLEAN, Arrays.asList(TumblrPostType.values())),
	
	// Photo
	PHOTO_HEIGHT(TalendType.LONG, TumblrPostType.PHOTO),
	PHOTO_WIDTH(TalendType.LONG, TumblrPostType.PHOTO),
	PHOTO_URL(TalendType.STRING, TumblrPostType.PHOTO),

	// Chat, Quote, Link
	TITLE(TalendType.STRING, Arrays.asList(new TumblrPostType[]{TumblrPostType.TEXT, TumblrPostType.CHAT, TumblrPostType.LINK})),
	BODY(TalendType.STRING, Arrays.asList(new TumblrPostType[]{TumblrPostType.TEXT, TumblrPostType.CHAT})),
	DIALOGUE(TalendType.STRING, TumblrPostType.CHAT),
	TEXT(TalendType.LONG, TumblrPostType.QUOTE),
	SOURCE(TalendType.STRING, TumblrPostType.QUOTE),
	DESCRIPTION(TalendType.STRING, TumblrPostType.LINK),
	LINK_URL(TalendType.STRING, TumblrPostType.LINK),
	
	// Various
	ALBUM_ART_URL(TalendType.STRING, TumblrPostType.AUDIO),
	ALBUM_NAME(TalendType.STRING, TumblrPostType.AUDIO),
	ARTIST_NAME(TalendType.STRING, TumblrPostType.AUDIO),
	CAPTION(TalendType.STRING, Arrays.asList(new TumblrPostType[]{TumblrPostType.PHOTO, TumblrPostType.AUDIO, TumblrPostType.VIDEO})),
	EMBED_CODE(TalendType.STRING, Arrays.asList(new TumblrPostType[]{TumblrPostType.AUDIO, TumblrPostType.VIDEO})),
	PLAY_COUNT(TalendType.INTEGER, TumblrPostType.AUDIO),
	TRACK_NAME(TalendType.INTEGER, TumblrPostType.AUDIO),
	TRACK_NUMBER(TalendType.INTEGER, TumblrPostType.AUDIO),
	YEAR(TalendType.INTEGER, TumblrPostType.AUDIO),
	THUMBNAIL_WIDTH(TalendType.INTEGER, TumblrPostType.VIDEO),
	THUMBNAIL_HEIGHT(TalendType.INTEGER, TumblrPostType.VIDEO),
	THUMBNAIL_URL(TalendType.STRING, TumblrPostType.VIDEO),
	ASKING_NAME(TalendType.STRING, TumblrPostType.ANSWER),
	ASKING_URL(TalendType.STRING, TumblrPostType.ANSWER),
	QUESTION(TalendType.STRING, TumblrPostType.ANSWER),
	
	JSON(TalendType.STRING, Arrays.asList(TumblrPostType.values()));
	
	private TalendType type;
	private List<TumblrPostType> postTypes;

	private TumblrPostField(TalendType type, List<TumblrPostType> postTypes){
		this.type = type;
		this.postTypes = postTypes;
	}

	private TumblrPostField(TalendType type, TumblrPostType postType){
		this.type = type;
		postTypes = new ArrayList<TumblrPostType>(1);
		postTypes.add(postType);
	}

	
	public TalendType getTalendType(){
		return type;
	}

	public List<TumblrPostType> definedIn(){
		return postTypes;
	}

}