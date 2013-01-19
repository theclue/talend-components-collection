package org.gabrielebaldassarre.jira.projects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.gabrielebaldassarre.tcomponent.bridge.TalendRow;
import org.gabrielebaldassarre.tcomponent.bridge.TalendColumn;
import org.gabrielebaldassarre.tcomponent.bridge.TalendRowBehaviour;

import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.ProjectRestClient;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Project;

public class TalendRowProjectDetailBehaviour implements TalendRowBehaviour {

	private ProjectRestClient client;
	private ProgressMonitor pm;
	private BasicProject project;
	private Map<TalendColumn, JiraProjectField> associations;

	public TalendRowProjectDetailBehaviour(ProjectRestClient client, ProgressMonitor pm){
		this.client = client;
		this.pm = pm;
		this.associations = new HashMap<TalendColumn, JiraProjectField>();

	}

	public Boolean isValid() {
		return !(project == null);
	}

	public TalendRowProjectDetailBehaviour setProject(BasicProject project){
		this.project = project;
		return this;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void visit(TalendRow row) {
		ResourceBundle rb = ResourceBundle.getBundle("tJiraProjects", Locale.getDefault());
		
		if(!isValid()) throw new IllegalStateException(String.format(Locale.getDefault(), rb.getString("exception.invalidProject")));

		List<Method> methods = null;

		try {
			for(Map.Entry<TalendColumn, JiraProjectField> set : associations.entrySet()){

				List collection = new ArrayList();
				Project p = client.getProject(project.getSelf(), pm);
				methods = set.getValue().getCalleeMethods();

				Object caller = (Object) p;

				Class<?> loopClass = set.getValue().getIterableClass();

				if(set.getValue().isCollection()){
					caller = methods.get(0).invoke(caller, (Object[]) null);

					for(Object item : Iterable.class.cast(caller)){
						loopClass.cast(item);

						for(int i=1; i < methods.size(); i++){
							caller = methods.get(i).invoke(item);
							caller = methods.get(i).getReturnType().cast(caller);
							item = caller;
							if(caller == null) break;
							
						}
						collection.add(caller);
					}
					row.setValue(set.getKey(), collection);
				} else {
					for(Method m : methods){
						caller = m.invoke(caller);
						caller = m.getReturnType().cast(caller);
						if(caller == null) break;
					}
					row.setValue(set.getKey(), caller);
				}
			}

		}  catch (IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		} catch (InvocationTargetException e) {
			throw new ExceptionInInitializerError(e);
		}
		project = null;
	}

	public TalendRowProjectDetailBehaviour setColumnLink(TalendColumn column, JiraProjectField data){
		associations.put(column, data);
		return this;
	}

}