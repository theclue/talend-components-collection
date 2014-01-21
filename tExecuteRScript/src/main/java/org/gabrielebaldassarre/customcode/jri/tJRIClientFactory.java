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

import java.util.Locale;
import java.util.Observable;
import java.util.ResourceBundle;

public class tJRIClientFactory extends Observable {
	
	private tJRIClient CLIENT;
	private final tJRIBuilder setup;

    public tJRIClientFactory(tJRIBuilder setup) {
	this.setup = new tJRIBuilder();
	this.setup.args(setup.getArgs());
	this.setup.clientType(setup.getType());
    }

    public tJRIClientFactory() {
	this.setup = new tJRIBuilder();
    }
    
    public tJRIClient getClient(){
    	ResourceBundle rb = ResourceBundle.getBundle("tJRI", Locale.getDefault());

    		if (CLIENT == null) {
    			switch(setup.getType()){
				case SILENT:
					if(!setup.getArgs().contains("--quiet")&&!setup.getArgs().contains("--silent")&&!setup.getArgs().contains("-q")&&setup.getArgs().contains("--save")){
						setup.arg("--slave");
						setChanged();
						notifyObservers(new tJRILogger("USER_DEF_LOG", Thread.currentThread().getId(), "WARN", rb.getString("log.mandatoryslave")));
					}				
					CLIENT = new tJRISilentClientImpl(setup.getArgs());
					break;
				case STANDARD:
				default:
					if(setup.getArgs().contains("--save")){
						setup.arg("--no-save");
						setChanged();
						notifyObservers(new tJRILogger("USER_DEF_LOG", Thread.currentThread().getId(), "WARN", rb.getString("log.mandatorynosave")));
					}
					CLIENT = new tJRIStandardClientImpl(setup.getArgs());
					break;
    			}
    		}

    	return CLIENT;
    }
}
