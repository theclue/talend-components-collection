/**
 * 
 */
package org.gabrielebaldassarre.majestic.backlink;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import org.gabrielebaldassarre.tcomponent.bridge.TalendColumn;
import org.gabrielebaldassarre.tcomponent.bridge.TalendFlow;
import org.gabrielebaldassarre.tcomponent.bridge.TalendFlowBehaviour;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRow;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRowFactory;

import com.majesticseo.external.rpc.APIService;
import com.majesticseo.external.rpc.DataTable;
import com.majesticseo.external.rpc.Response;

import org.gabrielebaldassarre.majestic.MajesticBacklink;
import org.gabrielebaldassarre.majestic.MajesticDatasource;
import org.gabrielebaldassarre.majestic.MajesticEndpoint;
import org.gabrielebaldassarre.majestic.MajesticLogger;

/**
 * @author Gabriele Baldassarre
 *
 */
public class TalendFlowBacklinkBehaviour extends Observable implements TalendFlowBehaviour {

	private int count = 100;
	private int maxSourceUrlRef = -1;
	private int maxSameSourceUrl = -1;
	private boolean keepDeleted = true;

	private MajesticDatasource datasource;
	private APIService api;
	private String token;
	private String status;
	private String error_message;
	private String full_error;
	private int totalBacklinks = 0;
	
	private SimpleDateFormat dateFormat;
	private HashMap<TalendColumn, MajesticBacklink> associations;


	private TalendFlow target;

	private boolean valid = false;
	private String item;


	public TalendFlowBacklinkBehaviour(String apiKey, MajesticEndpoint endpoint, String token, MajesticDatasource datasource, String item){
		ResourceBundle rb = ResourceBundle.getBundle("tMajesticBacklinkInput", Locale.getDefault());

		if(apiKey == null) throw new IllegalArgumentException(rb.getString("exception.nullApiKey"));
		if(token == null) throw new IllegalArgumentException(rb.getString("exception.nullToken"));

		dateFormat = new SimpleDateFormat("yyyy-mm-dd"); 

		this.datasource = datasource;
		this.api = new APIService(apiKey, endpoint.toString());
		this.token = token;
		this.item = item;

		this.associations = new HashMap<TalendColumn, MajesticBacklink>();
	}

	/**
	 * Visit a target {@link TalendFlow} to store search results
	 * 
	 * @param target the target flow to visit
	 * @throws IllegalStateException if target is null or not valid
	 */

	public void visit(TalendFlow target) throws IllegalStateException {
		ResourceBundle rb = ResourceBundle.getBundle("tMajesticBacklinkInput", Locale.getDefault());
		if(target == null) throw new IllegalArgumentException(rb.getString("exception.nullTargetFlow"));

		TalendRowFactory rowFactory = target.getModel().getRowFactory();
		TalendRow current;

		setChanged();
		notifyObservers(new MajesticLogger("USER_DEF_LOG", Thread.currentThread().getId(), "DEBUG", String.format(rb.getString("log.visitStart"), target.getName())));

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("item", item);
		parameters.put("datasource", datasource.toString());
		parameters.put("count", Integer.toString(count));
		parameters.put("Mode", keepDeleted == true ? "1" : "0");
		if(maxSourceUrlRef > 0) parameters.put("MaxSourceURLsPerRefDomain", Integer.toString(maxSourceUrlRef));
		if(maxSameSourceUrl > 0) parameters.put("MaxSameSourceURLs", Integer.toString(maxSameSourceUrl));

		Response response = api.executeOpenAppRequest("GetBackLinkData", parameters, token);

		if (response.isOK())
		{
			// print the results table
			DataTable results = response.getTableForName("BackLinks");
			
			// log query metadata
			setChanged();
			notifyObservers(new MajesticLogger("USER_DEF_LOG", Thread.currentThread().getId(), "INFO", String.format(rb.getString("log.metadata"), results.getTableParams())));
			
			totalBacklinks = Integer.parseInt(response.getParamForName("TotalBackLinks") != null ? response.getParamForName("TotalBackLinks") : "0");
			
			for (Map<String, String> link : results.getTableRows()){
				current = rowFactory.newRow(target);

				Iterator<Entry<TalendColumn, MajesticBacklink>> i = associations.entrySet().iterator();
				while (i.hasNext()) {
					Map.Entry<TalendColumn, MajesticBacklink> row = (Map.Entry<TalendColumn, MajesticBacklink>)i.next();

					if(target != null && !row.getKey().getFlow().equals(target)){
						throw new IllegalArgumentException(String.format(rb.getString("exception.columnNotInFlow"), row.getKey().getName(), target.getName()));
					}

					switch(row.getValue()){
					case SOURCE_URL:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("SourceURL"));
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case ANCHOR_TEXT:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("AnchorText"));
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case DATE:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("Date"));
							break;
						case DATE:
							try {
								current.setValue(row.getKey(), dateFormat.parse(link.get("Date")));
							} catch (ParseException e) {
								current.setValue(row.getKey(), null);
								setChanged();
								notifyObservers(new MajesticLogger("USER_DEF_LOG", Thread.currentThread().getId(), "WARNING", String.format(rb.getString("warning.unparsableDate"), link.get("Date"))));
							}
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case FLAG_REDIRECT:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("FlagRedirect"));
							break;
						case BIGDECIMAL:
							current.setValue(row.getKey(), new BigDecimal(link.get("FlagRedirect")));
							break;
						case FLOAT:
							current.setValue(row.getKey(), new Float(link.get("FlagRedirect")));
							break;
						case DOUBLE:
							current.setValue(row.getKey(), new Double(link.get("FlagRedirect")));
							break;
						case INTEGER:
							current.setValue(row.getKey(), new Integer(link.get("FlagRedirect")));
							break;
						case BYTE:
							current.setValue(row.getKey(), new Byte(link.get("FlagRedirect")));
							break;
						case BOOLEAN:
							current.setValue(row.getKey(), "1".equals(link.get("FlagRedirect")) ? true : false);
							break;
						case LONG:
							current.setValue(row.getKey(), new Long(link.get("FlagRedirect")));
							break;
						case CHARACTER:
							current.setValue(row.getKey(), "0".equals(link.get("FlagRedirect")) ? '0' : '1');
							break;
						case SHORT:
							current.setValue(row.getKey(), new Short(link.get("FlagRedirect")));
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case FLAG_FRAME:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("FlagFrame"));
							break;
						case BIGDECIMAL:
							current.setValue(row.getKey(), new BigDecimal(link.get("FlagFrame")));
							break;
						case FLOAT:
							current.setValue(row.getKey(), new Float(link.get("FlagFrame")));
							break;
						case DOUBLE:
							current.setValue(row.getKey(), new Double(link.get("FlagFrame")));
							break;
						case INTEGER:
							current.setValue(row.getKey(), new Integer(link.get("FlagFrame")));
							break;
						case BYTE:
							current.setValue(row.getKey(), new Byte(link.get("FlagFrame")));
							break;
						case BOOLEAN:
							current.setValue(row.getKey(), "1".equals(link.get("FlagFrame")) ? true : false);
							break;
						case LONG:
							current.setValue(row.getKey(), new Long(link.get("FlagFrame")));
							break;
						case CHARACTER:
							current.setValue(row.getKey(), "0".equals(link.get("FlagFrame")) ? '0' : '1');
							break;
						case SHORT:
							current.setValue(row.getKey(), new Short(link.get("FlagFrame")));
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case FLAG_NOFOLLOW:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("FlagNoFollow"));
							break;
						case BIGDECIMAL:
							current.setValue(row.getKey(), new BigDecimal(link.get("FlagNoFollow")));
							break;
						case FLOAT:
							current.setValue(row.getKey(), new Float(link.get("FlagNoFollow")));
							break;
						case DOUBLE:
							current.setValue(row.getKey(), new Double(link.get("FlagNoFollow")));
							break;
						case INTEGER:
							current.setValue(row.getKey(), new Integer(link.get("FlagNoFollow")));
							break;
						case BYTE:
							current.setValue(row.getKey(), new Byte(link.get("FlagNoFollow")));
							break;
						case BOOLEAN:
							current.setValue(row.getKey(), "1".equals(link.get("FlagNoFollow")) ? true : false);
							break;
						case LONG:
							current.setValue(row.getKey(), new Long(link.get("FlagNoFollow")));
							break;
						case CHARACTER:
							current.setValue(row.getKey(), "0".equals(link.get("FlagNoFollow")) ? '0' : '1');
							break;
						case SHORT:
							current.setValue(row.getKey(), new Short(link.get("FlagNoFollow")));
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case FLAG_IMAGES:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("FlagImages"));
							break;
						case BIGDECIMAL:
							current.setValue(row.getKey(), new BigDecimal(link.get("FlagImages")));
							break;
						case FLOAT:
							current.setValue(row.getKey(), new Float(link.get("FlagImages")));
							break;
						case DOUBLE:
							current.setValue(row.getKey(), new Double(link.get("FlagImages")));
							break;
						case INTEGER:
							current.setValue(row.getKey(), new Integer(link.get("FlagImages")));
							break;
						case BYTE:
							current.setValue(row.getKey(), new Byte(link.get("FlagImages")));
							break;
						case BOOLEAN:
							current.setValue(row.getKey(), "1".equals(link.get("FlagImages")) ? true : false);
							break;
						case LONG:
							current.setValue(row.getKey(), new Long(link.get("FlagImages")));
							break;
						case CHARACTER:
							current.setValue(row.getKey(), "0".equals(link.get("FlagImages")) ? '0' : '1');
							break;
						case SHORT:
							current.setValue(row.getKey(), new Short(link.get("FlagImages")));
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case FLAG_DELETED:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("FlagDeleted"));
							break;
						case BIGDECIMAL:
							current.setValue(row.getKey(), new BigDecimal(link.get("FlagDeleted")));
							break;
						case FLOAT:
							current.setValue(row.getKey(), new Float(link.get("FlagDeleted")));
							break;
						case DOUBLE:
							current.setValue(row.getKey(), new Double(link.get("FlagDeleted")));
							break;
						case INTEGER:
							current.setValue(row.getKey(), new Integer(link.get("FlagDeleted")));
							break;
						case BYTE:
							current.setValue(row.getKey(), new Byte(link.get("FlagDeleted")));
							break;
						case BOOLEAN:
							current.setValue(row.getKey(), "1".equals(link.get("FlagDeleted")) ? true : false);
							break;
						case LONG:
							current.setValue(row.getKey(), new Long(link.get("FlagDeleted")));
							break;
						case CHARACTER:
							current.setValue(row.getKey(), "0".equals(link.get("FlagDeleted")) ? '0' : '1');
							break;
						case SHORT:
							current.setValue(row.getKey(), new Short(link.get("FlagDeleted")));
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case FLAG_ALT_TEXT:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("FlagAltText"));
							break;
						case BIGDECIMAL:
							current.setValue(row.getKey(), new BigDecimal(link.get("FlagAltText")));
							break;
						case FLOAT:
							current.setValue(row.getKey(), new Float(link.get("FlagAltText")));
							break;
						case DOUBLE:
							current.setValue(row.getKey(), new Double(link.get("FlagAltText")));
							break;
						case INTEGER:
							current.setValue(row.getKey(), new Integer(link.get("FlagAltText")));
							break;
						case BYTE:
							current.setValue(row.getKey(), new Byte(link.get("FlagAltText")));
							break;
						case BOOLEAN:
							current.setValue(row.getKey(), "1".equals(link.get("FlagAltText")) ? true : false);
							break;
						case LONG:
							current.setValue(row.getKey(), new Long(link.get("FlagAltText")));
							break;
						case CHARACTER:
							current.setValue(row.getKey(), "0".equals(link.get("FlagAltText")) ? '0' : '1');
							break;
						case SHORT:
							current.setValue(row.getKey(), new Short(link.get("FlagAltText")));
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;
					case FLAG_MENTION:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("FlagMention"));
							break;
						case BIGDECIMAL:
							current.setValue(row.getKey(), new BigDecimal(link.get("FlagMention")));
							break;
						case FLOAT:
							current.setValue(row.getKey(), new Float(link.get("FlagMention")));
							break;
						case DOUBLE:
							current.setValue(row.getKey(), new Double(link.get("FlagMention")));
							break;
						case INTEGER:
							current.setValue(row.getKey(), new Integer(link.get("FlagMention")));
							break;
						case BYTE:
							current.setValue(row.getKey(), new Byte(link.get("FlagMention")));
							break;
						case BOOLEAN:
							current.setValue(row.getKey(), "1".equals(link.get("FlagMention")) ? true : false);
							break;
						case LONG:
							current.setValue(row.getKey(), new Long(link.get("FlagMention")));
							break;
						case CHARACTER:
							current.setValue(row.getKey(), "0".equals(link.get("FlagMention")) ? '0' : '1');
							break;
						case SHORT:
							current.setValue(row.getKey(), new Short(link.get("FlagMention")));
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case TARGET_URL:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("TargetURL"));
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case FIRST_INDEXED_DATE:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("FirstIndexedDate"));
							break;
						case DATE:
							try {
								current.setValue(row.getKey(), dateFormat.parse(link.get("FirstIndexedDate")));
							} catch (ParseException e) {
								current.setValue(row.getKey(), null);
								setChanged();
								notifyObservers(new MajesticLogger("USER_DEF_LOG", Thread.currentThread().getId(), "WARNING", String.format(rb.getString("warning.unparsableDate"), link.get("FirstIndexedDate"))));
							}
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;
					case LAST_SEEN_DATE:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("LastSeenDate"));
							break;
						case DATE:
							try {
								current.setValue(row.getKey(), dateFormat.parse(link.get("LastSeenDate")));
							} catch (ParseException e) {
								current.setValue(row.getKey(), null);
								setChanged();
								notifyObservers(new MajesticLogger("USER_DEF_LOG", Thread.currentThread().getId(), "WARNING", String.format(rb.getString("warning.unparsableDate"), link.get("LastSeenDate"))));
							}
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case DATE_LOST:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("DateLost"));
							break;
						case DATE:
							try {
								current.setValue(row.getKey(), dateFormat.parse(link.get("DateLost")));
							} catch (ParseException e) {
								current.setValue(row.getKey(), null);
								setChanged();
								notifyObservers(new MajesticLogger("USER_DEF_LOG", Thread.currentThread().getId(), "WARNING", String.format(rb.getString("warning.unparsableDate"), link.get("DateLost"))));
							}
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case REASON_LOST:
						switch(row.getKey().getType()){
						case STRING:
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case LINK_TYPE:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("LinkType"));
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case LINK_SUBTYPE:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("LinkSubType"));
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case TARGET_CITATION_FLOW:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("TargetCitationFlow"));
							break;
						case BIGDECIMAL:
							current.setValue(row.getKey(), new BigDecimal(link.get("TargetCitationFlow")));
							break;
						case FLOAT:
							current.setValue(row.getKey(), new Float(link.get("TargetCitationFlow")));
							break;
						case DOUBLE:
							current.setValue(row.getKey(), new Double(link.get("TargetCitationFlow")));
							break;
						case INTEGER:
							current.setValue(row.getKey(), new Integer(link.get("TargetCitationFlow")));
							break;
						case BYTE:
							current.setValue(row.getKey(), new Byte(link.get("TargetCitationFlow")));
							break;
						case LONG:
							current.setValue(row.getKey(), new Long(link.get("TargetCitationFlow")));
							break;
						case SHORT:
							current.setValue(row.getKey(), new Short(link.get("TargetCitationFlow")));
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case TARGET_TRUST_FLOW:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("TargetTrustFlow"));
							break;
						case BIGDECIMAL:
							current.setValue(row.getKey(), new BigDecimal(link.get("TargetTrustFlow")));
							break;
						case FLOAT:
							current.setValue(row.getKey(), new Float(link.get("TargetTrustFlow")));
							break;
						case DOUBLE:
							current.setValue(row.getKey(), new Double(link.get("TargetTrustFlow")));
							break;
						case INTEGER:
							current.setValue(row.getKey(), new Integer(link.get("TargetTrustFlow")));
							break;
						case BYTE:
							current.setValue(row.getKey(), new Byte(link.get("TargetTrustFlow")));
							break;
						case LONG:
							current.setValue(row.getKey(), new Long(link.get("TargetTrustFlow")));
							break;
						case SHORT:
							current.setValue(row.getKey(), new Short(link.get("TargetTrustFlow")));
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case SOURCE_CITATION_FLOW:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("SourceCitationFlow"));
							break;
						case BIGDECIMAL:
							current.setValue(row.getKey(), new BigDecimal(link.get("SourceCitationFlow")));
							break;
						case FLOAT:
							current.setValue(row.getKey(), new Float(link.get("SourceCitationFlow")));
							break;
						case DOUBLE:
							current.setValue(row.getKey(), new Double(link.get("SourceCitationFlow")));
							break;
						case INTEGER:
							current.setValue(row.getKey(), new Integer(link.get("SourceCitationFlow")));
							break;
						case BYTE:
							current.setValue(row.getKey(), new Byte(link.get("SourceCitationFlow")));
							break;
						case LONG:
							current.setValue(row.getKey(), new Long(link.get("SourceCitationFlow")));
							break;
						case SHORT:
							current.setValue(row.getKey(), new Short(link.get("SourceCitationFlow")));
							break;
						default:
							throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));
						}
						break;	
					case SOURCE_TRUST_FLOW:
						switch(row.getKey().getType()){
						case STRING:
							current.setValue(row.getKey(), link.get("SourceTrustFlow"));
							break;
						case BIGDECIMAL:
							current.setValue(row.getKey(), new BigDecimal(link.get("SourceTrustFlow")));
							break;
						case FLOAT:
							current.setValue(row.getKey(), new Float(link.get("SourceTrustFlow")));
							break;
						case DOUBLE:
							current.setValue(row.getKey(), new Double(link.get("SourceTrustFlow")));
							break;
						case INTEGER:
							current.setValue(row.getKey(), new Integer(link.get("SourceTrustFlow")));
							break;
						case BYTE:
							current.setValue(row.getKey(), new Byte(link.get("SourceTrustFlow")));
							break;
						case LONG:
							current.setValue(row.getKey(), new Long(link.get("SourceTrustFlow")));
							break;
						case SHORT:
							current.setValue(row.getKey(), new Short(link.get("SourceTrustFlow")));
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

		} else { // KO
			
			status = response.getCode();
			error_message = response.getErrorMessage();
			full_error = response.getFullError();
			
			throw new RuntimeException(response.getErrorMessage());
		}

		this.target = target;
		valid = response.isOK();
	}

	public Boolean isValid() {
		return valid;
	}

	public void setCount(int count){
		this.count = count;
	}

	public void setMaxSourceUrlRef(int max){
		this.maxSourceUrlRef = max;
	}

	public void setMaxSameSourceUrl(int max){
		this.maxSameSourceUrl = max;
	}

	public void getDeletedLinks(boolean keep){
		keepDeleted = keep;
	}

	public String getStatus(){
		return status;
	}


	public String getDetailedError() {
		return full_error;
	}


	public String getErrorMessage() {
		return error_message;
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
	 * Link a column of visiting {@link TalendFlow} to a proper type as described on {@link MajesticBacklink}
	 * 
	 * @param column the column to associate with
	 * @param data the type of output; if null, no link is estabilished
	 * @return a reference to the visitor itself
	 */

	public TalendFlowBacklinkBehaviour setColumnLink(TalendColumn column, MajesticBacklink data) {
		ResourceBundle rb = ResourceBundle.getBundle("tMajesticBacklinkInput", Locale.getDefault());

		if(data == null) return this;

		if(column == null) throw new IllegalArgumentException(rb.getString("exception.columnIsNull"));

		if(target != null && !column.getFlow().equals(target)){
			throw new IllegalArgumentException(String.format(rb.getString("exception.columnNotInFlow"), column.getName(), target.getName()));
		}

		associations.put(column, data);
		return this;
	}

	public Integer getTotalBacklinks() {
		return totalBacklinks;
	}

}