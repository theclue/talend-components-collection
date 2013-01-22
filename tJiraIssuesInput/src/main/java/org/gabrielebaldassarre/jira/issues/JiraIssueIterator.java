package org.gabrielebaldassarre.jira.issues;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import org.gabrielebaldassarre.jira.issues.TalendFlowBasicIssueBehaviour;
import org.gabrielebaldassarre.tcomponent.bridge.TalendColumn;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRow;

import com.atlassian.jira.rest.client.domain.BasicIssue;

public class JiraIssueIterator implements Iterator<BasicIssue> {

	private Iterator<TalendRow> issues;
	private TalendColumn keyColumn;
	
	public JiraIssueIterator(TalendFlowBasicIssueBehaviour p){
		issues = Arrays.asList(p.getIssuesList().getRows()).iterator();
		keyColumn = p.getKeyColumn();
	}

	public boolean hasNext() {
		return issues.hasNext();
	}

	public BasicIssue next() {
		return (BasicIssue)issues.next().getTalendValue(keyColumn.getName()).getValue();
	}

	public void remove() {
		ResourceBundle rb = ResourceBundle.getBundle("tJiraIssuesInput", Locale.getDefault());
		throw new UnsupportedOperationException(String.format(Locale.getDefault(), rb.getString("exception.unsupportedIterator")));

	}

}
