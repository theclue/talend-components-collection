package org.gabrielebaldassarre.customcode.jri;

import org.gabrielebaldassarre.tcomponent.bridge.TalendFlowBehaviour;

public interface tJRIClient  {
	
	public tJRIFlowBehaviour eval(String rCode);
	
	public void q();
	
	public void evaluateSymbol(tJRISymbol symbol);
	
	public void notify(tJRILogger log);

}
