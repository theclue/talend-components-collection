/*
This file is part of Majestic Talend component common elements

Talend Bridge is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Talend Bridge is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Nome-Programma.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gabrielebaldassarre.majestic;

import org.gabrielebaldassarre.tcomponent.bridge.TalendType;

/**
* This enum describes all the supported fields you can get from a Backlink
* 
* @author Gabriele Baldassarre
*
*/
public enum MajesticBacklink {

	SOURCE_URL(TalendType.STRING),
	ANCHOR_TEXT(TalendType.STRING),
	DATE(TalendType.DATE),
	FLAG_REDIRECT(TalendType.BOOLEAN),
	FLAG_FRAME(TalendType.BOOLEAN),
	FLAG_NOFOLLOW(TalendType.BOOLEAN),
	FLAG_IMAGES(TalendType.BOOLEAN),
	FLAG_DELETED(TalendType.BOOLEAN),
	FLAG_ALT_TEXT(TalendType.BOOLEAN),
	FLAG_MENTION(TalendType.BOOLEAN),
	TARGET_URL(TalendType.STRING),
	FIRST_INDEXED_DATE(TalendType.DATE),
	LAST_SEEN_DATE(TalendType.DATE),
	DATE_LOST(TalendType.DATE),
	REASON_LOST(TalendType.INTEGER),
	LINK_TYPE(TalendType.STRING),
	LINK_SUBTYPE(TalendType.STRING),
	TARGET_CITATION_FLOW(TalendType.INTEGER),
	TARGET_TRUST_FLOW(TalendType.INTEGER),
	SOURCE_CITATION_FLOW(TalendType.INTEGER),
	SOURCE_TRUST_FLOW(TalendType.INTEGER);

	private TalendType type;

	private MajesticBacklink(TalendType type){
		this.type = type;
	}
	
	public TalendType getTalendType(){
		return type;
	}


}