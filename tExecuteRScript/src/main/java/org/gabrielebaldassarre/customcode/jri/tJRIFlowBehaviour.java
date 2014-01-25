package org.gabrielebaldassarre.customcode.jri;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import org.gabrielebaldassarre.tcomponent.bridge.TalendColumn;
import org.gabrielebaldassarre.tcomponent.bridge.TalendFlow;
import org.gabrielebaldassarre.tcomponent.bridge.TalendFlowBehaviour;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRow;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRowFactory;


public class tJRIFlowBehaviour implements TalendFlowBehaviour {

	private tJRIClient client;
	private boolean valid;
	private String loop;
	private Map<TalendColumn, tJRISymbol> symbolMap;
	private TalendFlow target;

	public tJRIFlowBehaviour(tJRIClient client){
		this.client = client;
		this.valid = false;
		this.symbolMap = new HashMap<TalendColumn, tJRISymbol>();
	}

	public void visit(TalendFlow table) {
		ResourceBundle rb = ResourceBundle.getBundle("tJRI", Locale.getDefault());
		
		this.target = table;

		TalendRowFactory rowFactory = target.getModel().getRowFactory();
		TalendRow current;
		
		int loopSize = 0;
		
		if(loop != null){
			tJRISymbol loopVector = new tJRISymbol(loop, tJRIOutputType.VECTOR);
			client.evaluateSymbol(loopVector);
			loopSize = loopVector.size();
			client.notify(new tJRILogger("USER_DEF_LOG", Thread.currentThread().getId(), "INFO", String.format(rb.getString("log.loopnumber"), loopVector.size())));
		} else {
			if(symbolMap.size() > 0) client.notify(new tJRILogger("USER_DEF_LOG", Thread.currentThread().getId(), "INFO", rb.getString("log.loopdefault")));
		}

		// Evaluate all symbols
		Iterator<Entry<TalendColumn, tJRISymbol>> evs = symbolMap.entrySet().iterator();
		while (evs.hasNext()) {
			Map.Entry<TalendColumn, tJRISymbol> sym = (Map.Entry<TalendColumn, tJRISymbol>)evs.next();
			client.evaluateSymbol(sym.getValue());
			if(loop == null && sym.getValue().size() > loopSize) loopSize = sym.getValue().size();
		}

		for(int l = 0; l<loopSize; l++){

			current = rowFactory.newRow(target);

			Iterator<Entry<TalendColumn, tJRISymbol>> i = symbolMap.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry<TalendColumn, tJRISymbol> row = (Map.Entry<TalendColumn, tJRISymbol>)i.next();

				if(target != null && !row.getKey().getFlow().equals(target)){
					throw new IllegalArgumentException(String.format(rb.getString("exception.columnNotInFlow"), row.getKey().getName(), target.getName()));
				}
				
			if(row.getValue().size() > l) {
				
				switch(row.getValue().getOutputType()){
				case DOUBLE:
					switch(row.getKey().getType()){
					case BIGDECIMAL:
						current.setValue(row.getKey(), new BigDecimal(row.getValue().getResultsDouble()[l]));
						break;
					case BOOLEAN:
						current.setValue(row.getKey(), row.getValue().getResultsDouble()[l]==0d? false : true);
						break;
					case DOUBLE:
						current.setValue(row.getKey(), row.getValue().getResultsDouble()[l]);
						break;
					case FLOAT:
						current.setValue(row.getKey(), Float.parseFloat(String.valueOf(row.getValue().getResultsDouble()[l])));
						break;
					case INTEGER:
						current.setValue(row.getKey(), Integer.parseInt(String.valueOf(Math.round(row.getValue().getResultsDouble()[l]))));
						break;
					case LONG:
						current.setValue(row.getKey(), Long.parseLong(String.valueOf(Math.round(row.getValue().getResultsDouble()[l]))));
						break;
					case STRING:
						current.setValue(row.getKey(), String.valueOf(row.getValue().getResultsDouble()[l]));
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));

					}
					break;
				case INT:
					switch(row.getKey().getType()){
					case BIGDECIMAL:
						current.setValue(row.getKey(), new BigDecimal(row.getValue().getResultsInt()[l]));
						break;
					case BOOLEAN:
						current.setValue(row.getKey(), row.getValue().getResultsInt()[l]==0? false : true);
						break;
					case DOUBLE:
						current.setValue(row.getKey(), row.getValue().getResultsInt()[l]);
						break;
					case FLOAT:
						current.setValue(row.getKey(), Float.parseFloat(String.valueOf(row.getValue().getResultsInt()[l])));
						break;
					case INTEGER:
						current.setValue(row.getKey(), row.getValue().getResultsInt()[l]);
						break;
					case LONG:
						current.setValue(row.getKey(), Long.parseLong(String.valueOf(row.getValue().getResultsInt()[l])));
						break;
					case STRING:
						current.setValue(row.getKey(), String.valueOf(row.getValue().getResultsInt()[l]));
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));

					}
					break;
				case STRING:
					switch(row.getKey().getType()){
					case STRING:
						current.setValue(row.getKey(), row.getValue().getResultsString()[l]);
						break;
					default:
						throw new IllegalArgumentException(String.format(rb.getString("exception.uncastableColumn"), row.getKey().getType().getTypeString(), row.getKey().getName()));					
					}
					break;				
				default:
					break;
				}

			}
			
			}
			target.commit();
			valid = true;
		}


	}

	public Boolean isValid() {
		return valid;
	}

	public void setLoopVariable(String symbol){
		loopVariable(symbol);
	}

	public tJRIFlowBehaviour loopVariable(String symbol){
		this.loop = symbol;
		return this;
	}
	
	public tJRIFlowBehaviour loopVariable(){
		this.loop = null;
		return this;
	}

	public void setOutputSymbol(TalendColumn column, tJRISymbol symbol){
		outputSymbol(column, symbol);
	}

	public tJRIFlowBehaviour outputSymbol(TalendColumn column, tJRISymbol symbol){
		ResourceBundle rb = ResourceBundle.getBundle("tJRI", Locale.getDefault());
		if(client == null) return this;

		if(column == null) throw new IllegalArgumentException(rb.getString("exception.columnIsNull"));
		symbolMap.put(column, symbol);
		return this;
	}
}
