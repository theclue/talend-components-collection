package org.gabrielebaldassarre.customcode.jri;

public enum tJRIClientType {
	
	SILENT("silent client"),
	STANDARD("standard client");
	
	private String type;
	
	
	private tJRIClientType(String type){
		this.type = type;
	}
	
	public String toString(){
		return type;
	}
}