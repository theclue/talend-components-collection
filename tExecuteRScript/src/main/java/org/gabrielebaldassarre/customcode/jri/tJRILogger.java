package org.gabrielebaldassarre.customcode.jri;

public class tJRILogger {
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
	public tJRILogger(String category, long threadId, String severity, String message){
	
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
