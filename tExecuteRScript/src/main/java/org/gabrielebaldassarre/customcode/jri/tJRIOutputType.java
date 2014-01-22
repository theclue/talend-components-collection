package org.gabrielebaldassarre.customcode.jri;

import org.gabrielebaldassarre.tcomponent.bridge.TalendType;

public enum tJRIOutputType {
	BOOLEAN(TalendType.BOOLEAN),
	DOUBLE(TalendType.DOUBLE),
	FACTOR(TalendType.STRING),
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
