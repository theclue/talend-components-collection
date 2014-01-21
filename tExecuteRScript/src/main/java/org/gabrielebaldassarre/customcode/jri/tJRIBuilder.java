/*
	This file is part of tJRI Talend Component

    Talend Bridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Talend Bridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this component.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gabrielebaldassarre.customcode.jri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class tJRIBuilder {

	private List<String> args;
	private tJRIClientType type = tJRIClientType.STANDARD;
	
	public tJRIBuilder(){
		this.args = new ArrayList<String>(); 
	}
	
	public tJRIBuilder arg(String arg){
		args.add(arg);
		return this;
	}
	
	public tJRIBuilder args(String args, String separator){
		return args(args.split(Pattern.quote(separator)));
	}
	
	public tJRIBuilder args(String[] args){
		this.args.addAll(Arrays.asList(args));
		return this;
	}
	
	public tJRIBuilder args(List<String> args){
		this.args.addAll(args);
		return this;
	}
	
	public tJRIBuilder clientType(tJRIClientType type){
		this.type = type;
		return this;
		
	}

	public tJRIClientType getType() {
		return type;
	}
	
	public List<String> getArgs(){
		return args;
	}

}
