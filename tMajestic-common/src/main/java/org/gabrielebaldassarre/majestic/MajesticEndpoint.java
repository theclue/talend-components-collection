package org.gabrielebaldassarre.majestic;

/**
 * This structure is simply provided to enumerate the constant fragment used to select a Majestic endpoint
 * 
 * @author Gabriele Baldassarre
 *
 */
public enum MajesticEndpoint{
	
	DEVELOPMENT("http://developer.majesticseo.com/api_command"),
	LIVE("http://enterprise.majesticseo.com/api_command");
	
	private String endpoint;
	
	private MajesticEndpoint(String endpoint){
		this.endpoint = endpoint;
	}
	
	/**
	 * Return the current endpoint
	 * 
	 */
	public String toString(){
		return endpoint;
	}
}