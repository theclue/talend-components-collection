package org.gabrielebaldassarre.jira.issues;

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
import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;

public class TalendRowIssueDetailBehaviour implements TalendRowBehaviour {

	private final IssueRestClient client;
	private final ProgressMonitor pm;
	private BasicIssue issue;
	private Map<TalendColumn, JiraIssueField> associations;

	public TalendRowIssueDetailBehaviour(IssueRestClient client, ProgressMonitor pm){
		this.client = client;
		this.pm = pm;
		this.associations = new HashMap<TalendColumn, JiraIssueField>();

	}

	public Boolean isValid() {
		return !(issue == null);
	}

	public TalendRowIssueDetailBehaviour setIssue(BasicIssue issue){
		this.issue = issue;
		return this;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void visit(TalendRow row) {
		ResourceBundle rb = ResourceBundle.getBundle("tJiraIssuesInput", Locale.getDefault());
		
		if(!isValid()) throw new IllegalStateException(String.format(Locale.getDefault(), rb.getString("exception.invalidIssue")));

		List<Method> methods = null;

		try {
			for(Map.Entry<TalendColumn, JiraIssueField> set : associations.entrySet()){

				List collection = new ArrayList();
				Issue p = client.getIssue(issue.getKey(), pm);
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
		issue = null;
	}

	public TalendRowIssueDetailBehaviour setColumnLink(TalendColumn column, JiraIssueField data){
		associations.put(column, data);
		return this;
	}

}