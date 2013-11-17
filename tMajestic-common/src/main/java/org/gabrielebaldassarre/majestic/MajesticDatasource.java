package org.gabrielebaldassarre.majestic;

/**
 * This structure is simply provided to enumerate the constant fragment used to select a Majestic datasource
 * 
 * @author Gabriele Baldassarre
 *
 */
public enum MajesticDatasource{
	
	FRESH("fresh"),
	HISTORIC("historic");
	
	private String datasource;
	
	
	private MajesticDatasource(String datasource){
		this.datasource = datasource;
	}
	
	/**
	 * Return the current datasource
	 * 
	 */
	public String toString(){
		return datasource;
	}
}