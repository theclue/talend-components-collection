package org.gabrielebaldassarre.customcode.jri;

import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.ResourceBundle;

import org.gabrielebaldassarre.tcomponent.bridge.TalendFlowBehaviour;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

public class tJRIStandardClientImpl extends Observable implements tJRIClient, RMainLoopCallbacks {

	protected Rengine re;
	
	public tJRIStandardClientImpl(List<String> args) {
		
		String [] s = new String[args.size()];
		args.toArray(s);
		
		 this.re = new Rengine(s, false, this);
	}

	public void rBusy(Rengine re, int arg1) {
    	ResourceBundle rb = ResourceBundle.getBundle("tJRI", Locale.getDefault());
    	if(arg1==1){
		setChanged();
		notifyObservers(new tJRILogger("USER_DEF_LOG", Thread.currentThread().getId(), "INFO", rb.getString("log.rbusy")));
    	}
	}

	public String rChooseFile(Rengine re, int arg1) {
		ResourceBundle rb = ResourceBundle.getBundle("tJRI", Locale.getDefault());
		throw new UnsupportedOperationException(rb.getString("unsupported.operation"));
	}

	public void rFlushConsole(Rengine re) {
	}

	public void rLoadHistory(Rengine re, String arg1) {
	}

	 public String rReadConsole(Rengine re, String prompt, int addToHistory) {
return null;
	    }

	public void rSaveHistory(Rengine re, String arg1) {
	}

	public void rShowMessage(Rengine re, String message) {
		setChanged();
		notifyObservers(new tJRILogger("USER_DEF_LOG", Thread.currentThread().getId(), "INFO", message));

	}

	public void rWriteConsole(Rengine re, String message, int oType) {
		setChanged();
		String type;
		if(oType==0){
			type = "INFO";
		} else {
			type = "WARN";
		}
		notifyObservers(new tJRILogger("USER_DEF_LOG", Thread.currentThread().getId(), type, message));

	}
	
	public TalendFlowBehaviour eval(String rCode) {
		re.eval(rCode);
		return new tJRIFlowBehaviour();		
	}

	public void q() {
		re.end();	
	}


}
