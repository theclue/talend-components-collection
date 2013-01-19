package org.gabrielebaldassarre.jira.projects;

import java.util.Arrays;
import java.util.Iterator;

import org.gabrielebaldassarre.tcomponent.bridge.TalendColumn;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRow;

import com.atlassian.jira.rest.client.domain.BasicProject;

public class JiraProjectIterator implements Iterator<BasicProject> {
	
	private Iterator<TalendRow> projects;
	private TalendColumn keyColumn;
	
	public JiraProjectIterator(TalendFlowBasicProjectBehaviour p){
		projects = Arrays.asList(p.getProjectList().getRows()).iterator();
		keyColumn = p.getKeyColumn();
	}

	public boolean hasNext() {
		return projects.hasNext();
	}

	public BasicProject next() {
		return (BasicProject)projects.next().getTalendValue(keyColumn.getName()).getValue();
	}

	public void remove() {
		throw new UnsupportedOperationException();

	}

}
