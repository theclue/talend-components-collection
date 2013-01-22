package org.gabrielebaldassarre.jira.issues;

import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import org.gabrielebaldassarre.tcomponent.bridge.TalendColumn;
import org.gabrielebaldassarre.tcomponent.bridge.TalendFlow;
import org.gabrielebaldassarre.tcomponent.bridge.TalendFlowBehaviour;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRowFactory;

import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.SearchRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;

public class TalendFlowBasicIssueBehaviour implements TalendFlowBehaviour, Iterable<BasicIssue>{
	
	private ProgressMonitor pm;
	final private SearchRestClient client;
	private TalendColumn key;
	private String jql;
	private Integer maxResults = 2000;
	private int startsAt = 0;
	
	private TalendFlow target;
	
	
	private boolean valid = false;

	public TalendFlowBasicIssueBehaviour(SearchRestClient client, ProgressMonitor pm) {
		this.client = client;
		this.pm = pm;
	}
	
	public TalendFlowBasicIssueBehaviour setKey(TalendColumn key){
		this.key = key;
		return this;
	}

	public TalendFlowBasicIssueBehaviour setQuery(String jql){
		this.jql = jql;
		return this;
	}

	public TalendFlowBasicIssueBehaviour setMaximumResults(Integer max){
		this.maxResults = max;
		return this;
	}
	
	public Boolean isValid() {
		return valid;
	}

	public void visit(TalendFlow targetFlow) throws IllegalStateException {
		ResourceBundle rb = ResourceBundle.getBundle("tJiraIssuesInput", Locale.getDefault());
		
		if(key == null || targetFlow.getColumn(key.getName()) == null) throw new IllegalStateException(String.format(Locale.getDefault(), rb.getString("exception.issuesListKeyNotFound"), key, targetFlow.getName()));
		
		if(this.maxResults <= 0) throw new IllegalArgumentException(String.format(Locale.getDefault(), rb.getString("exception.invalidMaxBoundaries")));
		
		Iterable<BasicIssue> issuesList = this.client.searchJql(jql, maxResults, startsAt, pm).getIssues();
		
		for(BasicIssue issue : issuesList){
			TalendRowFactory rowFactory = targetFlow.getModel().getRowFactory();
         		rowFactory.newRow(targetFlow).setValue(key, issue);
         }
		targetFlow.commit();
		
		target = targetFlow;
		valid = true;
		
	}
	
	public TalendFlow getIssuesList(){
		return target;
	}

	public Iterator<BasicIssue> iterator() {
		return new JiraIssueIterator(this);
	}
	
	public TalendColumn getKeyColumn(){
		return key;
	}
		
}