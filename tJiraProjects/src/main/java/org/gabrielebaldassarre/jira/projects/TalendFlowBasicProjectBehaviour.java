package org.gabrielebaldassarre.jira.projects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.gabrielebaldassarre.tcomponent.bridge.TalendColumn;
import org.gabrielebaldassarre.tcomponent.bridge.TalendFlow;
import org.gabrielebaldassarre.tcomponent.bridge.TalendFlowBehaviour;
import org.gabrielebaldassarre.tcomponent.bridge.TalendListFactory;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRowFactory;

import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.ProjectRestClient;
import com.atlassian.jira.rest.client.domain.BasicProject;

public class TalendFlowBasicProjectBehaviour implements TalendFlowBehaviour, Iterable<BasicProject>{
	
	private ProgressMonitor pm;
	final private ProjectRestClient client;
	private TalendColumn key;
	private List<String> filterKey;
	private boolean invertFilter = false;
	
	private TalendFlow target;
	
	
	private boolean valid = false;

	public TalendFlowBasicProjectBehaviour(ProjectRestClient client, ProgressMonitor pm) {
		this.client = client;
		this.pm = pm;
		this.filterKey = TalendListFactory.getInstance(String.class).newTalendList(new ArrayList<String>());
		
	}
	
	public TalendFlowBasicProjectBehaviour setKey(TalendColumn key){
		this.key = key;
		return this;
	}
	
	public TalendFlowBasicProjectBehaviour addFilterCondition(String projectName){
		filterKey.add(projectName);
		return this;
	}
	
	public TalendFlowBasicProjectBehaviour setFilterExclusionMode(boolean exclude){
		this.invertFilter = exclude;
		return this;
	}
	
	public Boolean isValid() {
		return valid;
	}

	public void visit(TalendFlow targetFlow) throws IllegalStateException {
		ResourceBundle rb = ResourceBundle.getBundle("tJiraProjects", Locale.getDefault());
		
		if(key == null || targetFlow.getColumn(key.getName()) == null) throw new IllegalStateException(String.format(Locale.getDefault(), rb.getString("exception.projectListKeyNotFound"), key, targetFlow.getName()));
		
		if(!key.isKey()) throw new IllegalStateException(String.format(Locale.getDefault(), rb.getString("exception.projectListNotKey"), key, targetFlow.getName()));
		
		Iterable<BasicProject> projectList = this.client.getAllProjects(pm);
		
		for(BasicProject project : projectList){
			TalendRowFactory rowFactory = targetFlow.getModel().getRowFactory();
        	if(invertFilter^(filterKey.contains(project.getKey()))){
        		rowFactory.newRow(targetFlow).setValue(key, project);
        	}
        }
		targetFlow.commit();
		
		target = targetFlow;
		valid = true;
		
	}
	
	public TalendFlow getProjectList(){
		return target;
	}

	public Iterator<BasicProject> iterator() {
		return new JiraProjectIterator(this);
	}
	
	public TalendColumn getKeyColumn(){
		return key;
	}
	
}