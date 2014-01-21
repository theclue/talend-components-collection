package org.gabrielebaldassarre.customcode.jri;

import org.gabrielebaldassarre.tcomponent.bridge.TalendFlowBehaviour;
import org.rosuda.JRI.RMainLoopCallbacks;

public interface tJRIClient  {
	
	public TalendFlowBehaviour eval(String rCode);
	
	public void q();

}
