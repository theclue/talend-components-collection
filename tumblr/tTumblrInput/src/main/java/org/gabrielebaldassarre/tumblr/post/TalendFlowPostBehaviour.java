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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import java.math.BigDecimal;

import org.gabrielebaldassarre.tcomponent.bridge.TalendFlow;
import org.gabrielebaldassarre.tcomponent.bridge.TalendFlowBehaviour;
import org.gabrielebaldassarre.tcomponent.bridge.TalendColumn;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRow;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRowFactory;
import org.gabrielebaldassarre.tcomponent.bridge.TalendType;
import org.gabrielebaldassarre.tcomponent.bridge.TalendValue;

import com.google.common.base.Joiner;
import com.tumblr.jumblr.types.AnswerPost;
import com.tumblr.jumblr.types.AudioPost;
import com.tumblr.jumblr.types.ChatPost;
import com.tumblr.jumblr.types.LinkPost;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.QuotePost;
import com.tumblr.jumblr.types.TextPost;
import com.tumblr.jumblr.types.VideoPost;

/**
 * This visitor parse a tumblr resut set and fit a tumblr data flow
 * 
 * @author Gabriele Baldassarre
 *
 */
public class TalendFlowPostBehaviour extends Observable implements TalendFlowBehaviour {

	private Map<TalendColumn, TumblrPostField> associations;
	private TalendValue resultSet;
	private boolean valid;
	private String entitiesSepatator;

	/**
	 * Build a visitor to parse a result set gained from {@link TalendFlowQueryResultBehaviour}
	 * 
	 * @param entitiesSeparator the substring to use as separator for serialized entity list, ie '|' or ','
	 */
	public TalendFlowPostBehaviour(String entitiesSeparator){
		this.associations = new HashMap<TalendColumn, TumblrPostField>();
		this.entitiesSepatator = entitiesSeparator;
	}
	
	/**
	 * Check if this flow is valid
	 * 
	 */
	public Boolean isValid() {
		return valid;
	}


	/**
	 * Set the string to be used as serialized entities list separator, ie <em>"|", ","...</em>
	 * 
	 * @param entitiesSeparator the string to use
	 */
	public void setEntitiesSeparator(String entitiesSeparator){
		this.entitiesSepatator = entitiesSeparator;
	}

	/**
	 * Visit a target {@link TalendFlow} for parsed or raw json output.
	 * 
	 * @param target the data flow to fill
	 */
	public void visit(TalendFlow target) {
		ResourceBundle rb = ResourceBundle.getBundle("tTumblrInput", Locale.getDefault());

		TalendRowFactory rowFactory = target.getModel().getRowFactory();
		valid = false;

		@SuppressWarnings("unchecked")
		List<Post> posts = (List<Post>)resultSet.getValue();
		for (int i = 0; i < posts.size(); i++) {

			Post post = posts.get(i);

			TumblrPostType currentType = TumblrPostType.getInstanceFromTumblr(post.getType());

				TalendRow current = rowFactory.newRow(target);

				Iterator<Entry<TalendColumn, TumblrPostField>> col = associations.entrySet().iterator();
				while (col.hasNext()) {

					Map.Entry<TalendColumn, TumblrPostField> row = (Map.Entry<TalendColumn, TumblrPostField>)col.next();

					if(target != null && !row.getKey().getFlow().equals(target)){
						throw new IllegalArgumentException(String.format(rb.getString("exception.columnNotInFlow"), row.getKey().getName(), target.getName()));
					}
					
					if(row.getValue().definedIn().contains(currentType)){

						switch(row.getValue()){

						case ALBUM_ART_URL:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), ((AudioPost)post).getAlbumArtUrl());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case ARTIST_NAME:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), ((AudioPost)post).getArtistName());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case ASKING_URL:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), ((AnswerPost)post).getAskingUrl());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case ALBUM_NAME:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), ((AudioPost)post).getAlbumName());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case ASKING_NAME:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), ((AnswerPost)post).getAskingName());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case BLOG_NAME:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), (post).getBlogName());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case BODY:
							String body = null;
							switch(currentType){
							case TEXT:
								body = ((TextPost)post).getBody();
								break;
							case CHAT:
								body = ((ChatPost)post).getBody();
								break;
							default:
								break;
							}
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), body);
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case CAPTION:
							String caption = null;
							switch(currentType){
							case PHOTO:
								caption = ((PhotoPost)post).getCaption();
								break;
							case AUDIO:
								caption = ((AudioPost)post).getCaption();
								break;
							case VIDEO:
								caption = ((VideoPost)post).getCaption();
								break;
							default:
								break;
							}
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), caption);
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case DATE_GMT:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), post.getDateGMT());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case DESCRIPTION:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), ((LinkPost)post).getDescription());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case DIALOGUE:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), ((ChatPost)post).getDialogue());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case EMBED_CODE:
							String embed = null;
							switch (currentType) {
							case AUDIO:
								embed = ((AudioPost)post).getEmbedCode();
								break;
							case VIDEO:
								embed = ((VideoPost)post).getVideos().get(0).getEmbedCode();
								break;
							default:
								break;
							}
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), embed);
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case FORMAT:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), post.getFormat());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case IS_BOOKMARKLET:
							switch(row.getKey().getType()){
							case BIGDECIMAL:
								current.setValue(row.getKey(), new BigDecimal(post.isBookmarklet() ? 1 : 0));
								break;
							case BOOLEAN:
								current.setValue(row.getKey(), post.isBookmarklet());
								break;
							case BYTE:
								current.setValue(row.getKey(), (byte)(post.isBookmarklet() ? 1 : 0));
								break;
							case CHARACTER:
								current.setValue(row.getKey(), (post.isBookmarklet() ? '1' : '0'));
								break;
							case DOUBLE:
								current.setValue(row.getKey(), (double)(post.isBookmarklet() ? 1d : 0d));
								break;
							case FLOAT:
								current.setValue(row.getKey(), (float)(post.isBookmarklet() ? 1f : 0f));
								break;
							case INTEGER:
								current.setValue(row.getKey(), (post.isBookmarklet() ? 1 : 0));
								break;
							case LONG:
								current.setValue(row.getKey(), (long)(post.isBookmarklet() ? 1l : 0l));
								break;
							case SHORT:
								current.setValue(row.getKey(), (short)(post.isBookmarklet() ? (short)1 : (short)0));
								break;
							case STRING:
								current.setValue(row.getKey(), (post.isBookmarklet() ? "1" : "0"));
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));

							}
							break;
						case IS_LINKED:
							switch(row.getKey().getType()){
							case BIGDECIMAL:
								current.setValue(row.getKey(), new BigDecimal(post.isLiked() ? 1 : 0));
								break;
							case BOOLEAN:
								current.setValue(row.getKey(), post.isLiked());
								break;
							case BYTE:
								current.setValue(row.getKey(), (byte)(post.isLiked() ? 1 : 0));
								break;
							case CHARACTER:
								current.setValue(row.getKey(), (post.isLiked() ? '1' : '0'));
								break;
							case DOUBLE:
								current.setValue(row.getKey(), (double)(post.isLiked() ? 1d : 0d));
								break;
							case FLOAT:
								current.setValue(row.getKey(), (float)(post.isLiked() ? 1f : 0f));
								break;
							case INTEGER:
								current.setValue(row.getKey(), (post.isLiked() ? 1 : 0));
								break;
							case LONG:
								current.setValue(row.getKey(), (long)(post.isLiked() ? 1l : 0l));
								break;
							case SHORT:
								current.setValue(row.getKey(), (short)(post.isLiked() ? (short)1 : (short)0));
								break;
							case STRING:
								current.setValue(row.getKey(), (post.isLiked() ? "1" : "0"));
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));

							}
							break;
						case IS_MOBILE:
							switch(row.getKey().getType()){
							case BIGDECIMAL:
								current.setValue(row.getKey(), new BigDecimal(post.isMobile() ? 1 : 0));
								break;
							case BOOLEAN:
								current.setValue(row.getKey(), post.isMobile());
								break;
							case BYTE:
								current.setValue(row.getKey(), (byte)(post.isMobile() ? 1 : 0));
								break;
							case CHARACTER:
								current.setValue(row.getKey(), (post.isMobile() ? '1' : '0'));
								break;
							case DOUBLE:
								current.setValue(row.getKey(), (double)(post.isMobile() ? 1d : 0d));
								break;
							case FLOAT:
								current.setValue(row.getKey(), (float)(post.isMobile() ? 1f : 0f));
								break;
							case INTEGER:
								current.setValue(row.getKey(), (post.isMobile() ? 1 : 0));
								break;
							case LONG:
								current.setValue(row.getKey(), (long)(post.isMobile() ? 1l : 0l));
								break;
							case SHORT:
								current.setValue(row.getKey(), (short)(post.isMobile() ? (short)1 : (short)0));
								break;
							case STRING:
								current.setValue(row.getKey(), (post.isMobile() ? "1" : "0"));
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));

							}
							break;
						case LINK_URL:
							switch(row.getKey().getType()){	
							case STRING:
								current.setValue(row.getKey(), ((LinkPost)post).getLinkUrl());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));

							}
							break;
						case NOTE_COUNT:
							switch(row.getKey().getType()){
							case BIGDECIMAL:
								current.setValue(row.getKey(), new BigDecimal(post.getNoteCount()));
								break;
							case DOUBLE:
								current.setValue(row.getKey(), new Double(post.getNoteCount().toString()));
								break;
							case FLOAT:
								current.setValue(row.getKey(), new Float(post.getNoteCount().toString()));
								break;
							case LONG:
								current.setValue(row.getKey(), post.getNoteCount());
								break;
							case INTEGER:
								current.setValue(row.getKey(), (post.getNoteCount().intValue()));
								break;
							case STRING:
								current.setValue(row.getKey(), String.valueOf((post.getNoteCount())));
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case PHOTO_HEIGHT:
							switch(row.getKey().getType()){
							case BIGDECIMAL:
								current.setValue(row.getKey(), new BigDecimal(((PhotoPost)post).getPhotos().get(0).getOriginalSize().getHeight()));
								break;
							case DOUBLE:
								current.setValue(row.getKey(), new Double(((PhotoPost)post).getPhotos().get(0).getOriginalSize().getHeight()));
								break;
							case FLOAT:
								current.setValue(row.getKey(), new Float(((PhotoPost)post).getPhotos().get(0).getOriginalSize().getHeight()));
								break;
							case LONG:
								current.setValue(row.getKey(), new Long(((PhotoPost)post).getPhotos().get(0).getOriginalSize().getHeight()));
								break;
							case INTEGER:
								current.setValue(row.getKey(), (new Long (((PhotoPost)post).getPhotos().get(0).getOriginalSize().getHeight())).intValue());
								break;
							case STRING:
								current.setValue(row.getKey(), String.valueOf(((PhotoPost)post).getPhotos().get(0).getOriginalSize().getHeight()));
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case PHOTO_WIDTH:
							switch(row.getKey().getType()){
							case BIGDECIMAL:
								current.setValue(row.getKey(), new BigDecimal(((PhotoPost)post).getPhotos().get(0).getOriginalSize().getWidth()));
								break;
							case DOUBLE:
								current.setValue(row.getKey(), new Double(((PhotoPost)post).getPhotos().get(0).getOriginalSize().getWidth()));
								break;
							case FLOAT:
								current.setValue(row.getKey(), new Float(((PhotoPost)post).getPhotos().get(0).getOriginalSize().getWidth()));
								break;
							case LONG:
								current.setValue(row.getKey(), new Long(((PhotoPost)post).getPhotos().get(0).getOriginalSize().getWidth()));
								break;
							case INTEGER:
								current.setValue(row.getKey(), (new Long (((PhotoPost)post).getPhotos().get(0).getOriginalSize().getWidth())).intValue());
								break;
							case STRING:
								current.setValue(row.getKey(), String.valueOf(((PhotoPost)post).getPhotos().get(0).getOriginalSize().getWidth()));
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case PHOTO_URL:
							switch(row.getKey().getType()){	
							case STRING:
								current.setValue(row.getKey(), ((PhotoPost)post).getPhotos().get(0).getOriginalSize().getUrl());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));

							}
							break;
						case PLAY_COUNT:
							switch(row.getKey().getType()){
							case BIGDECIMAL:
								current.setValue(row.getKey(), new BigDecimal(((AudioPost)post).getPlayCount()));
								break;
							case DOUBLE:
								current.setValue(row.getKey(), new Double(((AudioPost)post).getPlayCount()));
								break;
							case FLOAT:
								current.setValue(row.getKey(), new Float(((AudioPost)post).getPlayCount()));
								break;
							case LONG:
								current.setValue(row.getKey(), new Long(((AudioPost)post).getPlayCount()));
								break;
							case STRING:
								current.setValue(row.getKey(), String.valueOf(((AudioPost)post).getPlayCount()));
								break;
							case INTEGER:
								current.setValue(row.getKey(), ((AudioPost)post).getPlayCount());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case POST_ID:
							switch(row.getKey().getType()){
							case LONG:
								current.setValue(row.getKey(), post.getId());
								break;
							case STRING:
								current.setValue(row.getKey(), String.valueOf(post.getId()));
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case POST_URL:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), post.getPostUrl());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case QUESTION:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), ((AnswerPost)post).getQuestion());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case REBLOG_KEY:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), post.getReblogKey());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case REBLOGGED_FROM_ID:
							switch(row.getKey().getType()){
							case LONG:
								current.setValue(row.getKey(), post.getRebloggedFromId());
								break;
							case STRING:
								current.setValue(row.getKey(), String.valueOf(post.getRebloggedFromId()));
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case REBLOGGED_FROM_NAME:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), post.getRebloggedFromName());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case SLUG:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), post.getSlug());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;	
						case SOURCE:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), ((QuotePost)post).getSource());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case SOURCE_TITLE:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), post.getSourceTitle());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case SOURCE_URL:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), post.getSourceUrl());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case STATE:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), post.getState());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case TAGS:
							switch(row.getKey().getType()){
							case STRING:
							case LIST:
								current.setValue(row.getKey(), !TalendType.STRING.equals(row.getKey().getType()) ? post.getTags() : Joiner.on(getEntitiesSeparator()).join(post.getTags()));
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case TEXT:	
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), ((QuotePost)post).getText());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case THUMBNAIL_HEIGHT:	
							switch(row.getKey().getType()){
							case BIGDECIMAL:
								current.setValue(row.getKey(), new BigDecimal(((VideoPost)post).getThumbnailHeight()));
								break;
							case DOUBLE:
								current.setValue(row.getKey(), new Double(((VideoPost)post).getThumbnailHeight()));
								break;
							case FLOAT:
								current.setValue(row.getKey(), new Float(((VideoPost)post).getThumbnailHeight()));
								break;
							case LONG:
								current.setValue(row.getKey(), new Long(((VideoPost)post).getThumbnailHeight()));
								break;
							case STRING:
								current.setValue(row.getKey(), String.valueOf(((VideoPost)post).getThumbnailHeight()));
								break;
							case INTEGER:
								current.setValue(row.getKey(), ((VideoPost)post).getThumbnailHeight());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case THUMBNAIL_WIDTH:	
							switch(row.getKey().getType()){
							case BIGDECIMAL:
								current.setValue(row.getKey(), new BigDecimal(((VideoPost)post).getThumbnailWidth()));
								break;
							case DOUBLE:
								current.setValue(row.getKey(), new Double(((VideoPost)post).getThumbnailWidth()));
								break;
							case FLOAT:
								current.setValue(row.getKey(), new Float(((VideoPost)post).getThumbnailWidth()));
								break;
							case LONG:
								current.setValue(row.getKey(), new Long(((VideoPost)post).getThumbnailWidth()));
								break;
							case STRING:
								current.setValue(row.getKey(), String.valueOf(((VideoPost)post).getThumbnailWidth()));
								break;
							case INTEGER:
								current.setValue(row.getKey(), ((VideoPost)post).getThumbnailWidth());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case THUMBNAIL_URL:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), ((VideoPost)post).getThumbnailUrl());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case TIMESTAMP:
							switch(row.getKey().getType()){
							case DATE:
								current.setValue(row.getKey(), new Date(post.getTimestamp()*1000));
								break;
							case LONG:
								current.setValue(row.getKey(), post.getTimestamp());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case TITLE:
							String title = null;
							switch (currentType) {
							case TEXT:
								title = ((TextPost)post).getTitle();
								break;
							case CHAT:
								title = ((ChatPost)post).getTitle();
								break;
							case LINK:
								title = ((LinkPost)post).getTitle();
								break;
							default:
								break;
							}
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), title);
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case TRACK_NAME:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), ((AudioPost)post).getTrackName());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case TRACK_NUMBER:
							switch(row.getKey().getType()){
							case BIGDECIMAL:
								current.setValue(row.getKey(), new BigDecimal(((AudioPost)post).getTrackNumber()));
								break;
							case DOUBLE:
								current.setValue(row.getKey(), new Double(((AudioPost)post).getTrackNumber()));
								break;
							case FLOAT:
								current.setValue(row.getKey(), new Float(((AudioPost)post).getTrackNumber()));
								break;
							case LONG:
								current.setValue(row.getKey(), new Long(((AudioPost)post).getTrackNumber()));
								break;
							case STRING:
								current.setValue(row.getKey(), String.valueOf(((AudioPost)post).getTrackNumber()));
								break;
							case INTEGER:
								current.setValue(row.getKey(), ((AudioPost)post).getTrackNumber());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case TYPE:
							switch(row.getKey().getType()){
							case STRING:
								current.setValue(row.getKey(), post.getType());
								break;
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;
						case YEAR:
							switch(row.getKey().getType()){
							case BIGDECIMAL:
								current.setValue(row.getKey(), new BigDecimal(((AudioPost)post).getYear()));
								break;
							case DOUBLE:
								current.setValue(row.getKey(), new Double(((AudioPost)post).getYear()));
								break;
							case FLOAT:
								current.setValue(row.getKey(), new Float(((AudioPost)post).getYear()));
								break;
							case LONG:
								current.setValue(row.getKey(), new Long(((AudioPost)post).getYear()));
								break;
							case STRING:
								current.setValue(row.getKey(), String.valueOf(((AudioPost)post).getYear()));
								break;
							case INTEGER:
								current.setValue(row.getKey(), ((AudioPost)post).getYear());
								break;							
							default:
								throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
							}
							break;							
						default:
							current.setValue(row.getKey(), null);
						} // column-switch
					} // type-matching
					else {
						current.setValue(row.getKey(), null);
					}

				} // while

		} // post-loop
		valid = true;
}
private String getEntitiesSeparator() {
		return entitiesSepatator;
	}

/**
 * Link a column of visiting {@link TalendFlow} to a proper type as described on {@link TweetField}
 * 
 * @param column the column to associate with
 * @param data the type of output; if null, no link is established
 * @return a reference to the visitor itself
 */
public TalendFlowPostBehaviour setColumnLink(TalendColumn column, TumblrPostField data){
	ResourceBundle rb = ResourceBundle.getBundle("tTumblrInput", Locale.getDefault());

	if(data == null) return this;
	if(column == null) throw new IllegalArgumentException(rb.getString("exception.columnIsNull"));

	associations.put(column, data);
	return this;
}

/**
 * Set a result set from {@link TalendFlowQueryResultBehaviour} to be used as input for this parser
 * 
 * @param resultSet the {@link TalendValue} to be used as input
 */
public void setInput(TalendValue resultSet) {
	ResourceBundle rb = ResourceBundle.getBundle("tTumblrInput", Locale.getDefault());
	if(resultSet == null) throw new RuntimeException(rb.getString("exception.inputIsNull"));
	this.resultSet = resultSet;

}
}
