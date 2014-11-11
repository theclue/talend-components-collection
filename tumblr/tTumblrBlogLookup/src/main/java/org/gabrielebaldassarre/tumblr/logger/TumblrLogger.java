package org.gabrielebaldassarre.tumblr.logger;

/*
This file is part of tTumblerInput Talend component

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

import java.util.Observable;

/**
* Instances of this class are simply sent to {@link Observable} objects to send some information to logger subsystem
* 
* @author Gabriele Baldassarre
*
*/
public class TumblrLogger {

private String category;
private long threadId;
private String severity;
private String message;

/**
 * These parameters are similar to those sent to tLogCatcher elements
 * 
 * @param category the category of the message, usually 'USER_DEF_LOG'
 * @param threadId the ID from the currently executing Thread
 * @param severity the severity of the message from "TRACE", "DEBUG", "INFO", "WARNING", "ERROR", "FATAL"
 * @param message the message to send to the logger
 */
public TumblrLogger(String category, long threadId, String severity, String message){

	this.category = category;
	this.threadId = threadId;
	this.severity = severity;
	this.message = message;
}

/**
 * @return the category
 */
public String getCategory() {
	return category;
}


/**
 * @return the threadId
 */
public long getThreadId() {
	return threadId;
}


/**
 * @return the severity
 */
public String getSeverity() {
	return severity;
}

/**
 * @return the message
 */
public String getMessage() {
	return message;
}

}

