package org.gabrielebaldassarre.jira.projects;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.gabrielebaldassarre.tcomponent.bridge.TalendType;

import com.atlassian.jira.rest.client.domain.BasicComponent;
import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rest.client.domain.Version;
import com.google.common.collect.Iterables;

public enum JiraProjectField {
	
	KEY(false, null, "getKey"),
	SELF_URI(false, null, "getSelf"),
	SELF_STRING(false, null, "getSelf", "toString"),
	COMPONENTS(true, BasicComponent.class, "getComponents"),
	COMPONENTS_NAME(true, BasicComponent.class, "getComponents", "getName"),
	COMPONENTS_SELF_URI(true, BasicComponent.class, "getComponents", "getSelf"),
	COMPONENTS_SELF_STRING(true, BasicComponent.class, "getComponents", "getSelf", "toString"),
	DESCRIPTION(false, null, "getDescription"),
	LEAD(false, null,"getLead"),
	LEAD_NAME(false, null, "getLead", "getName"),
	LEAD_DISPLAYNAME(false, null, "getLead", "getDisplayName"),
	LEAD_SELF_URI(false, null, "getLead", "getSelf"),
	LEAD_SELF_STRING(false, null, "getLead", "getSelf", "toString"),
	URI(false, null, "getUri"),
	URI_STRING(false, null, "getUri", "toString"),
	VERSIONS(true, Version.class, "getVersions"),
	VERSIONS_DESCRIPTION(true, Version.class, "getVersions", "getDescription"),
	VERSIONS_NAME(true, Version.class, "getVersions", "getName"),
	VERSIONS_RELEASE_DATE(true, Version.class, "getVersions", "getReleaseDate", "toDate"),
	VERSIONS_SELF_URI(true, Version.class, "getVersions", "getSelf"),
	VERSIONS_SELF_STRING(true, Version.class, "getVersions", "getSelf", "toString"),
	VERSIONS_IS_ARCHIVED(true, Version.class, "getVersions", "isArchived"),
	VERSIONS_IS_RELEASED(true, Version.class, "getVersions", "isReleased");
	
	private ArrayList<Method> m;
	private ArrayList<Class<?>> r;
	private boolean mustLoop;
	private Class<?> loopClass;
	
	private JiraProjectField(boolean mustLoop, Class<?> loopClass, String... methods) {
		
		this.r = new ArrayList<Class<?>>(methods.length);
		this.m = new ArrayList<Method>(methods.length);
		this.mustLoop = mustLoop;
		this.loopClass = loopClass;
		
		Class<?> initClass = Project.class;

		for(int i=0; i< methods.length; i++){
			try {
				
				Method cm = initClass.getMethod(methods[i]);
				r.add(cm.getReturnType());
				m.add(cm);
				
				if(i==0 && cm.getReturnType().equals(Iterable.class)){
					initClass = loopClass;
				} else {
					initClass = cm.getReturnType();
				}
				
			} catch (SecurityException e) {
				throw new ExceptionInInitializerError(e);
			} catch (NoSuchMethodException e) {
				throw new ExceptionInInitializerError(e);
			}
			
		}
		
	}
	
	public List<Class<?>> getReturnTypes(){
		return r;
	}
	
	
	public List<Method> getCalleeMethods(){
		return m;
	}
	
	public TalendType getTalendType(){
		if(isCollection()) return TalendType.LIST;
		return TalendType.buildFrom(Iterables.getLast(r, null));
	}
	
	public boolean isCollection(){
		return mustLoop;
	}
	
	public Class<?> getIterableClass(){
		return loopClass;
	}

}
