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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.gabrielebaldassarre.tcomponent.bridge.TalendFlow;
import org.gabrielebaldassarre.tcomponent.bridge.TalendFlowBehaviour;
import org.gabrielebaldassarre.tcomponent.bridge.TalendColumn;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRow;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRowFactory;
import org.gabrielebaldassarre.tcomponent.bridge.TalendType;
import org.gabrielebaldassarre.tcomponent.bridge.TalendValue;

import com.google.common.base.Joiner;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;
import twitter4j.json.DataObjectFactory;

/**
 * This visitor parse a twitter resultset and fit a twitter data floe
 * 
 * @author Gabriele Baldassarre
 *
 */
public class TalendFlowTweetBehaviour extends Observable implements TalendFlowBehaviour {

	private Map<TalendColumn, TweetField> associations;
	private TalendValue resultSet;
	private boolean valid;
	private boolean includeHash;
	private String entitiesSepatator;

	private static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

	/**
	 * Build a visitor to parse a resultset gained from {@link TalendFlowQueryResultBehaviour}
	 * 
	 * @param entitiesSeparator the substring to use as separator for serialized entity list, ie '|' or ','
	 * @param includeHash true if you prefer to have any entity to be prefixed by its reserver character, ie '#' or '@'
	 */
	public TalendFlowTweetBehaviour(String entitiesSeparator, boolean includeHash){
		this.associations = new HashMap<TalendColumn, TweetField>();
		this.entitiesSepatator = entitiesSeparator;
		this.includeHash = includeHash;
	}

	/**
	 * Check if this flow is valid
	 * 
	 */
	public Boolean isValid() {
		return valid;
	}

	/**
	 * Return the string used to separate serialized entity list
	 * 
	 * @return the string used as separator
	 */
	public String getEntitiesSeparator(){
		return entitiesSepatator;
	}

	/**
	 * Return true if entities are prefixed with their typical character, ie '#' or '@'
	 * 
	 * @return true if entities will be prefixed
	 */
	public boolean includeHash(){
		return includeHash;
	}

	/**
	 * Set to true if you want your entities to be prefixed by a specific character, ie '#' or '@'
	 * 
	 * @param includeHash true if you want the reserved character to be prefixed
	 */
	public void includeHash(boolean includeHash){
		this.includeHash = includeHash;
	}

	/**
	 * Se the string to be used as serialized entities list separator, ie <em>"|", ","...</em>
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
		ResourceBundle rb = ResourceBundle.getBundle("tTwitterInput", Locale.getDefault());

		TalendRowFactory rowFactory = target.getModel().getRowFactory();
		valid = false;

		@SuppressWarnings("unchecked")
		List<Status> tweets = (List<Status>)resultSet.getValue();
		for (int i = 0; i < tweets.size(); i++) {
			Status tweet = tweets.get(i);
			TalendRow current = rowFactory.newRow(target);

			Iterator<Entry<TalendColumn, TweetField>> col = associations.entrySet().iterator();
			while (col.hasNext()) {
				List<String> h;
				List<Long> l;

				Map.Entry<TalendColumn, TweetField> row = (Map.Entry<TalendColumn, TweetField>)col.next();

				if(target != null && !row.getKey().getFlow().equals(target)){
					throw new IllegalArgumentException(String.format(rb.getString("exception.columnNotInFlow"), row.getKey().getName(), target.getName()));
				}

				switch(row.getValue()){
				case CREATION_DATE:
					String literalDate = (new StringBuilder( TalendFlowTweetBehaviour.DATEFORMAT.format(tweet.getCreatedAt()))).toString();

					switch(row.getKey().getType()){
					case BIGDECIMAL:
						current.setValue(row.getKey(), new BigDecimal(literalDate));
					case LONG:
						current.setValue(row.getKey(), Long.parseLong(literalDate));
					case DOUBLE:
						current.setValue(row.getKey(), Double.parseDouble(literalDate));
					case FLOAT:
						current.setValue(row.getKey(), Float.parseFloat(literalDate));
					case INTEGER:
						current.setValue(row.getKey(), Integer.parseInt(literalDate));
					case DATE:
						current.setValue(row.getKey(), tweet.getCreatedAt());
						break;
					case STRING:
						current.setValue(row.getKey(), literalDate);
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case FROM_NAME:
					switch(row.getKey().getType()){
					case STRING:
						current.setValue(row.getKey(), tweet.getUser().getName());
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case FROM_USERID:
					switch(row.getKey().getType()){
					case BIGDECIMAL:
						current.setValue(row.getKey(), new BigDecimal(tweet.getUser().getId()));
						break;
					case DOUBLE:
						current.setValue(row.getKey(), new Double(tweet.getUser().getId()));
						break;
					case FLOAT:
						current.setValue(row.getKey(), new Float(tweet.getUser().getId()));
						break;
					case LONG:
						current.setValue(row.getKey(), new Long(tweet.getUser().getId()));
						break;
					case STRING:
						current.setValue(row.getKey(), String.valueOf((tweet.getUser().getId())));
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case FROM_SCREEN_NAME:
					switch(row.getKey().getType()){
					case STRING:
						current.setValue(row.getKey(), tweet.getUser().getScreenName());
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case HASHTAGS:
					List<HashtagEntity> hashtags = Arrays.asList(tweet.getHashtagEntities());
					h = new ArrayList<String>(hashtags.size());

					for(HashtagEntity hashtag : hashtags) {
						h.add((includeHash() ? "#" : "") + hashtag.getText());
					}
					switch(row.getKey().getType()){
					case STRING:
					case LIST:
						current.setValue(row.getKey(), !TalendType.STRING.equals(row.getKey().getType()) ? h : Joiner.on(getEntitiesSeparator()).join(h));
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case IS_FAVORITED:
					switch(row.getKey().getType()){
					case BIGDECIMAL:
						current.setValue(row.getKey(), new BigDecimal(tweet.isFavorited() ? 1 : 0));
						break;
					case BOOLEAN:
						current.setValue(row.getKey(), tweet.isFavorited());
						break;
					case BYTE:
						current.setValue(row.getKey(), (byte)(tweet.isFavorited() ? 1 : 0));
						break;
					case CHARACTER:
						current.setValue(row.getKey(), (tweet.isFavorited() ? '1' : '0'));
						break;
					case DOUBLE:
						current.setValue(row.getKey(), (double)(tweet.isFavorited() ? 1d : 0d));
						break;
					case FLOAT:
						current.setValue(row.getKey(), (float)(tweet.isFavorited() ? 1f : 0f));
						break;
					case INTEGER:
						current.setValue(row.getKey(), (tweet.isFavorited() ? 1 : 0));
						break;
					case LONG:
						current.setValue(row.getKey(), (long)(tweet.isFavorited() ? 1l : 0l));
						break;
					case SHORT:
						current.setValue(row.getKey(), (short)(tweet.isFavorited() ? (short)1 : (short)0));
						break;
					case STRING:
						current.setValue(row.getKey(), (tweet.isFavorited() ? "1" : "0"));
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					
					}
					break;
				case IS_POSSIBLY_SENSITIVE:
					switch(row.getKey().getType()){
					case BIGDECIMAL:
						current.setValue(row.getKey(), new BigDecimal(tweet.isPossiblySensitive() ? 1 : 0));
						break;
					case BOOLEAN:
						current.setValue(row.getKey(), tweet.isPossiblySensitive());
						break;
					case BYTE:
						current.setValue(row.getKey(), (byte)(tweet.isPossiblySensitive() ? 1 : 0));
						break;
					case CHARACTER:
						current.setValue(row.getKey(), (tweet.isPossiblySensitive() ? '1' : '0'));
						break;
					case DOUBLE:
						current.setValue(row.getKey(), (double)(tweet.isPossiblySensitive() ? 1d : 0d));
						break;
					case FLOAT:
						current.setValue(row.getKey(), (float)(tweet.isPossiblySensitive() ? 1f : 0f));
						break;
					case INTEGER:
						current.setValue(row.getKey(), (tweet.isPossiblySensitive() ? 1 : 0));
						break;
					case LONG:
						current.setValue(row.getKey(), (long)(tweet.isPossiblySensitive() ? 1l : 0l));
						break;
					case SHORT:
						current.setValue(row.getKey(), (short)(tweet.isPossiblySensitive() ? (short)1 : (short)0));
						break;
					case STRING:
						current.setValue(row.getKey(), (tweet.isPossiblySensitive() ? "1" : "0"));
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case IS_RETWEET:
					switch(row.getKey().getType()){
					case BIGDECIMAL:
						current.setValue(row.getKey(), new BigDecimal(tweet.isRetweet() ? 1 : 0));
						break;
					case BOOLEAN:
						current.setValue(row.getKey(), tweet.isRetweet());
						break;
					case BYTE:
						current.setValue(row.getKey(), (byte)(tweet.isRetweet() ? 1 : 0));
						break;
					case CHARACTER:
						current.setValue(row.getKey(), (tweet.isRetweet() ? '1' : '0'));
						break;
					case DOUBLE:
						current.setValue(row.getKey(), (double)(tweet.isRetweet() ? 1d : 0d));
						break;
					case FLOAT:
						current.setValue(row.getKey(), (float)(tweet.isRetweet() ? 1f : 0f));
						break;
					case INTEGER:
						current.setValue(row.getKey(), (tweet.isRetweet() ? 1 : 0));
						break;
					case LONG:
						current.setValue(row.getKey(), (long)(tweet.isRetweet() ? 1l : 0l));
						break;
					case SHORT:
						current.setValue(row.getKey(), (short)(tweet.isRetweet() ? (short)1 : (short)0));
						break;
					case STRING:
						current.setValue(row.getKey(), (tweet.isRetweet() ? "1" : "0"));
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					current.setValue(row.getKey(), tweet.isRetweet());
					break;
				case LOCATION:
					GeoLocation g = tweet.getGeoLocation();
					switch(row.getKey().getType()){
					case STRING:
						current.setValue(row.getKey(), g != null ? String.valueOf(g.getLatitude()) + getEntitiesSeparator() + String.valueOf(g.getLongitude()) : null);
						break;
					case OBJECT:
						current.setValue(row.getKey(), g);
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case REPLYTO_SCREEN_NAME:
					switch(row.getKey().getType()){
					case STRING:
						current.setValue(row.getKey(), tweet.getInReplyToScreenName());
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case REPLYTO_STATUSID:
					switch(row.getKey().getType()){
					case BIGDECIMAL:
						current.setValue(row.getKey(), new BigDecimal(tweet.getInReplyToStatusId()));
						break;
					case DOUBLE:
						current.setValue(row.getKey(), new Double(tweet.getInReplyToStatusId()));
						break;
					case FLOAT:
						current.setValue(row.getKey(), new Float(tweet.getInReplyToStatusId()));
						break;
					case LONG:
						current.setValue(row.getKey(), new Long(tweet.getInReplyToStatusId()));
						break;
					case STRING:
						current.setValue(row.getKey(), String.valueOf((tweet.getInReplyToStatusId())));
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case REPLYTO_USERID:
					switch(row.getKey().getType()){
					case BIGDECIMAL:
						current.setValue(row.getKey(), new BigDecimal(tweet.getInReplyToUserId()));
						break;
					case DOUBLE:
						current.setValue(row.getKey(), new Double(tweet.getInReplyToUserId()));
						break;
					case FLOAT:
						current.setValue(row.getKey(), new Float(tweet.getInReplyToUserId()));
						break;
					case LONG:
						current.setValue(row.getKey(), new Long(tweet.getInReplyToUserId()));
						break;
					case STRING:
						current.setValue(row.getKey(), String.valueOf((tweet.getInReplyToUserId())));
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case RETWEET_COUNT:
					switch(row.getKey().getType()){
					case BIGDECIMAL:
						current.setValue(row.getKey(), new BigDecimal(tweet.getRetweetCount()));
						break;
					case DOUBLE:
						current.setValue(row.getKey(), new Double(tweet.getRetweetCount()));
						break;
					case FLOAT:
						current.setValue(row.getKey(), new Float(tweet.getRetweetCount()));
						break;
					case LONG:
						current.setValue(row.getKey(), new Long(tweet.getRetweetCount()));
						break;
					case STRING:
						current.setValue(row.getKey(), String.valueOf((tweet.getRetweetCount())));
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case SOURCE:
					switch(row.getKey().getType()){
					case STRING:
						current.setValue(row.getKey(), tweet.getSource());
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case STATUS_ID:
					switch(row.getKey().getType()){
					case BIGDECIMAL:
						current.setValue(row.getKey(), new BigDecimal(tweet.getId()));
						break;
					case DOUBLE:
						current.setValue(row.getKey(), new Double(tweet.getId()));
						break;
					case FLOAT:
						current.setValue(row.getKey(), new Float(tweet.getId()));
						break;
					case LONG:
						current.setValue(row.getKey(), new Long(tweet.getId()));
						break;
					case STRING:
						current.setValue(row.getKey(), String.valueOf((tweet.getId())));
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case TEXT:
					switch(row.getKey().getType()){
					case STRING:
						current.setValue(row.getKey(), tweet.getText());
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case URL_ENTITIES:
				case URL_ENTITIES_STRING:
					List<URLEntity> urlEntities = Arrays.asList(tweet.getURLEntities());
					h = new ArrayList<String>(urlEntities.size());

					for(URLEntity urlEntity : urlEntities) {
						h.add(urlEntity.getExpandedURL());
					}
					switch(row.getKey().getType()){
					case STRING:
					case LIST:
						current.setValue(row.getKey(), !TalendType.STRING.equals(row.getKey().getType()) ? h : Joiner.on(getEntitiesSeparator()).join(h));
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case USER_MENTIONS:
					List<UserMentionEntity> userMentionsEntities = Arrays.asList(tweet.getUserMentionEntities());
					l = new ArrayList<Long>(userMentionsEntities.size());

					for(UserMentionEntity userMention : userMentionsEntities) {
						l.add(userMention.getId());
					}
					switch(row.getKey().getType()){
					case STRING:
					case LIST:
						current.setValue(row.getKey(), !TalendType.STRING.equals(row.getKey().getType()) ? l : Joiner.on(getEntitiesSeparator()).join(l));
							break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case USER_MENTIONS_SCREEN_NAME:
					List<UserMentionEntity> userMentionsScreen = Arrays.asList(tweet.getUserMentionEntities());
					h = new ArrayList<String>(userMentionsScreen.size());

					for(UserMentionEntity userMention : userMentionsScreen) {
						h.add((includeHash() ? "@" : "") + userMention.getScreenName());
					}
					switch(row.getKey().getType()){
					case STRING:
					case LIST:
						current.setValue(row.getKey(), !TalendType.STRING.equals(row.getKey().getType()) ? h : Joiner.on(getEntitiesSeparator()).join(h));
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				case JSON:
					switch(row.getKey().getType()){
					case STRING:
						current.setValue(row.getKey(), DataObjectFactory.getRawJSON(tweet));
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
					}
					break;
				default:
					throw new IllegalArgumentException(String.format(rb.getString("exception.unparseableColumn"), row.getKey().getName()));

				}

			}

		}
		valid = true;
	}
	/**
	 * Link a column of visiting {@link TalendFlow} to a proper type as described on {@link TweetField}
	 * 
	 * @param column the column to associate with
	 * @param data the type of output; if null, no link is estabilished
	 * @return a reference to the visitor itself
	 */
	public TalendFlowTweetBehaviour setColumnLink(TalendColumn column, TweetField data){
		ResourceBundle rb = ResourceBundle.getBundle("tTwitterInput", Locale.getDefault());

		if(data == null) return this;
		if(column == null) throw new IllegalArgumentException(rb.getString("exception.columnIsNull"));

		associations.put(column, data);
		return this;
	}

	/**
	 * Set a resultset from {@link TalendFlowQueryResultBehaviour} to be used as input for this parser
	 * 
	 * @param resultSet the {@link TalendValue} to be used as input
	 */
	public void setInput(TalendValue resultSet) {
		ResourceBundle rb = ResourceBundle.getBundle("tTwitterInput", Locale.getDefault());
		if(resultSet == null) throw new RuntimeException(rb.getString("exception.inputIsNull"));
		this.resultSet = resultSet;

	}
}
