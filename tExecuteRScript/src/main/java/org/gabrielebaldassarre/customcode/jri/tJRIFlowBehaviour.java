package org.gabrielebaldassarre.customcode.jri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.gabrielebaldassarre.tcomponent.bridge.TalendColumn;
import org.gabrielebaldassarre.tcomponent.bridge.TalendFlow;
import org.gabrielebaldassarre.tcomponent.bridge.TalendFlowBehaviour;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRow;


public class tJRIFlowBehaviour implements TalendFlowBehaviour, Iterable<TalendRow> {
	
	private tJRIClient client;
	private boolean valid;
	private String loop;
	private Map<String, tJRIOutputType> symbolMap;
	private TalendFlow target;
	private Map<TalendColumn, String> associations;

	public tJRIFlowBehaviour(tJRIClient client){
		this.client = client;
		this.valid = false;
		this.symbolMap = new HashMap<String, tJRIOutputType>();
		this.associations = new HashMap<TalendColumn, String>();
	}

	public void visit(TalendFlow table) {
		
		
		this.target = table;
		// Chiama eval per prendere il REXP per ogni variabile linkata....
		if(target != null && !column.getFlow().equals(target)){
			throw new IllegalArgumentException(String.format(rb.getString("exception.columnNotInFlow"), column.getName(), target.getName()));
		}

	}

	public Boolean isValid() {
		return valid;
	}

	public Iterator<TalendRow> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setLoopVariable(String symbol){
		loopVariable(symbol);
	}
	
	public tJRIFlowBehaviour loopVariable(String symbol){
		this.loop = symbol;
		return this;
	}
	
	public void setOutputSymbol(String symbol, tJRIOutputType type){
		outputSymbol(symbol, type);
	}
	
	public tJRIFlowBehaviour outputSymbol(String symbol, tJRIOutputType type){
		symbolMap.put(symbol, type);
		return this;
	}
	
	public tJRIFlowBehaviour columnLink(String symbol, TalendColumn column) {
		ResourceBundle rb = ResourceBundle.getBundle("tJRI", Locale.getDefault());
		
		if(client == null) return this;
		
		if(column == null) throw new IllegalArgumentException(rb.getString("exception.columnIsNull"));
				
		associations.put(column, symbol);
		return this;
	}

}
