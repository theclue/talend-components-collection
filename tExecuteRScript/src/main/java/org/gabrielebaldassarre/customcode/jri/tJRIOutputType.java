package org.gabrielebaldassarre.customcode.jri;

import org.gabrielebaldassarre.tcomponent.bridge.TalendType;

public enum tJRIOutputType {
	DOUBLE(TalendType.DOUBLE),
	INT(TalendType.INTEGER),
	STRING(TalendType.STRING),
	VECTOR(TalendType.BYTE_ARRAY);

	private TalendType type;


	private tJRIOutputType(TalendType type){
		this.type = type;
	}
	
	public TalendType getTalendType(){
		return type;
	}
}
