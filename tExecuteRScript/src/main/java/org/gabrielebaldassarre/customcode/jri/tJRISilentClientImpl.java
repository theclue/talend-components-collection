package org.gabrielebaldassarre.customcode.jri;

import java.util.List;
import java.util.Observable;

import org.gabrielebaldassarre.tcomponent.bridge.TalendFlowBehaviour;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class tJRISilentClientImpl extends Observable implements tJRIClient {
	protected Rengine re;

	public tJRISilentClientImpl(List<String> args) {
		
		String [] s = new String[args.size()];
		args.toArray(s);
		
		 this.re = new Rengine(s, false, null);
	}

	public tJRIFlowBehaviour eval(String rCode) {
		re.eval(rCode);
		return new tJRIFlowBehaviour(this);		
	}

	public void q() {
		re.end();	
	}
	
	public void evaluateSymbol(tJRISymbol symbol) {
		symbol.evaluate(re);
	}

	public void notify(tJRILogger log) {
		setChanged();
		notifyObservers(log);		
	}


}
