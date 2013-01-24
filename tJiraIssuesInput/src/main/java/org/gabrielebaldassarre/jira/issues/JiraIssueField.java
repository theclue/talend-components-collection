package org.gabrielebaldassarre.jira.issues;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.gabrielebaldassarre.tcomponent.bridge.TalendType;

import com.atlassian.jira.rest.client.domain.BasicComponent;
import com.atlassian.jira.rest.client.domain.Comment;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.Version;
import com.google.common.collect.Iterables;

public enum JiraIssueField {
	
	KEY(false, null, "getKey"),
	SELF_URI(false, null, "getSelf"),
	SELF_STRING(false, null, "getSelf", "toString"),
	SUMMARY(false, null, "getSummary", "toString"),
	PROJECT(false, null, "getProject"),
	PROJECT_KEY(false, null, "getProject", "getKey"),
	PROJECT_NAME(false, null, "getProject", "getName"),
	PROJECT_SELF_URI(false, null, "getProject", "getSelf"),
	PROJECT_SELF_STRING(false, null, "getProject", "getSelf", "toString"),
	ASSIGNEE(false, null, "getAssignee"),
	ASSIGNEE_NAME(false, null, "getAssignee", "getName"),
	ASSIGNEE_DISPLAYNAME(false, null, "getAssignee", "getDisplayName"),
	ASSIGNEE_SELF_URI(false, null, "getAssignee", "getSelf"),
	ASSIGNEE_SELF_STRING(false, null, "getAssignee", "getSelf", "toString"),
	COMMENTS(true, Comment.class, "getComments"),
	COMMENTS_SELF_URI(true, Comment.class, "getComments", "getSelf"),
	COMMENTS_SELF_STRING(true, Comment.class, "getComments", "getSelf", "toString"),
	COMMENTS_CREATIONDATE(true, Comment.class, "getComments", "getCreationDate", "toDate"),
	COMMENTS_BODY(true, Comment.class, "getComments", "getBody"),
	COMMENTS_NAME(true, Comment.class, "getComments", "getAuthor", "getName"),
	COMMENTS_DISPLAYNAME(true, Comment.class, "getComments", "getAuthor", "getDisplayName"),
	COMMENTS_NAME_SELF_STRING(true, Comment.class, "getComments", "getAuthor", "getSelf", "toString"),
	COMMENTS_NAME_SELF_URI(true, Comment.class, "getComments", "getAuthor", "getSelf"),
	COMPONENTS(true, BasicComponent.class, "getComponents"),
	COMPONENTS_NAME(true, BasicComponent.class, "getComponents", "getName"),
	COMPONENTS_SELF_URI(true, BasicComponent.class, "getComponents", "getSelf"),
	COMPONENTS_SELF_STRING(true, BasicComponent.class, "getComponents", "getSelf", "toString"),
	FIX_VERSIONS(true, Version.class, "getFixVersions"),
	FIX_VERSIONS_NAME(true, Version.class, "getFixVersions", "getName"),
	FIX_VERSIONS_SELF_URI(true, Version.class, "getFixVersions", "getSelf"),
	FIX_VERSIONS_SELF_STRING(true, Version.class, "getFixVersions", "getSelf", "toString"),
	REPORTER(false, null, "getReporter"),
	REPORTER_NAME(false, null, "getReporter", "getName"),
	REPORTER_DISPLAYNAME(false, null, "getReporter", "getDisplayName"),
	REPORTER_SELF_URI(false, null, "getReporter", "getSelf"),
	REPORTER_SELF_STRING(false, null, "getReporter", "getSelf", "toString"),
	RESOLUTION(false, null, "getResolution", "getName"),
	ORIGINAL_ESTIMATE(false, null, "getTimeTracking", "getOriginalEstimateMinutes"),
	REMAINING_ESTIMATE(false, null, "getTimeTracking", "getRemainingEstimateMinutes"),
	TIME_SPENT(false, null, "getTimeTracking", "getTimeSpentMinutes"),
	STATUS(false, null, "getStatus", "getName"),
	UPDATE_DATE(false, null, "getUpdateDate", "toDate"),
	CREATION_DATE(false, null, "getCreationDate", "toDate");
	
	private ArrayList<Method> m;
	private ArrayList<Class<?>> r;
	private boolean mustLoop;
	private Class<?> loopClass;
	
	private JiraIssueField(boolean mustLoop, Class<?> loopClass, String... methods) {
		
		this.r = new ArrayList<Class<?>>(methods.length);
		this.m = new ArrayList<Method>(methods.length);
		this.mustLoop = mustLoop;
		this.loopClass = loopClass;
		
		Class<?> initClass = Issue.class;

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