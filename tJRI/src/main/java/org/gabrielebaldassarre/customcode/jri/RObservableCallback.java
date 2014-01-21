package org.gabrielebaldassarre.customcode.jri;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.ResourceBundle;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

public class RObservableCallback implements RMainLoopCallbacks {

	public void rBusy(Rengine arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public String rChooseFile(Rengine arg0, int arg1) {
		ResourceBundle rb = ResourceBundle.getBundle("tJRI", Locale.getDefault());
		throw new UnsupportedOperationException(rb.getString("unsupported.operation"));
	}

	public void rFlushConsole(Rengine arg0) {
	}

	public void rLoadHistory(Rengine arg0, String arg1) {
	}

	 public String rReadConsole(Rengine re, String prompt, int addToHistory) {
return null;
	    }

	public void rSaveHistory(Rengine arg0, String arg1) {
	}

	public void rShowMessage(Rengine arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public void rWriteConsole(Rengine arg0, String arg1, int arg2) {
		System.out.println(arg2 + arg1);

	}

}
