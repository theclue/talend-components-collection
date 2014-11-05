package org.gabrielebaldassarre.tumblr.post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gabrielebaldassarre.tcomponent.bridge.TalendType;

/*
This file is part of Tumblr Component Package for Talend
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

public enum TumblrPostType {
	
TEXT("text"),
PHOTO("photo"),
QUOTE("quote"),
LINK("link"),
CHAT("chat"),
AUDIO("audio"),
VIDEO("video"),
ANSWER("answer");
	
	private String postType;
	private List<TumblrPostField> fields;
	
	private TumblrPostType(String type){
		this.postType = type;
		fields = new ArrayList<TumblrPostField>();
		
		for (TumblrPostField currentField : Arrays.asList(TumblrPostField.values())) {
			for (TumblrPostType currentType : currentField.definedIn()) {
				if(currentType.equals(this)) fields.add(currentField);
			}
		}	
	}
	
	public String toString(){
		return postType;
	}
	
	public List<TumblrPostField> getAvailableFields(){
		return fields;
	}
}