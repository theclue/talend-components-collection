package org.gabrielebaldassarre.customcode.jri;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;

public class tJRISymbol {

	private tJRIOutputType type;
	private String name;
	private REXP results;
	private String canonical;

	public tJRISymbol(String name, tJRIOutputType type){
		this.type = type;
		this.name = name;
	}

	public tJRIOutputType getOutputType() {
		return type;
	}

	public String getOutputName() {
		return name;
	}

	public tJRISymbol evaluate(Rengine client){
		results = client.eval(name);
		canonical = results.asSymbolName();
		return this;
	}

	public double[] getResultsDouble(){

		return results.asDoubleArray();
	}

	public int[] getResultsInt(){

		return results.asIntArray();
	}
	public String[] getResultsString(){

		return results.asStringArray();
	}

	public RVector getResultsVector(){

		return results.asVector();
	}
	
	public int size(){
		switch(results.getType()){
		case REXP.XT_INT:
		case REXP.XT_DOUBLE:
		case REXP.XT_STR:
		case REXP.XT_BOOL:
		case REXP.XT_FACTOR:
			return 1;
		case REXP.XT_ARRAY_INT:
			return results.asIntArray().length;		
		case REXP.XT_ARRAY_DOUBLE:
			return results.asDoubleArray().length;
		case REXP.XT_ARRAY_STR:
			return results.asStringArray().length;
		case REXP.XT_VECTOR:
			return results.asVector().size();
		}
		return results.asStringArray().length;
	}


	public String getCanonicalName() {
		return canonical;
	}

}
