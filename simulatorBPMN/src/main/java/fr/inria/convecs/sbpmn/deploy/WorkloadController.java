package fr.inria.convecs.sbpmn.deploy;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import fr.inria.convecs.sbpmn.executor.ExecuteRuntimeTasks;

public class WorkloadController {
	
	private BPMNController bpmn;
	private int nbrOfInstances;
	private Map<String, Object> variables;
	private int PERIOD;
	
	public WorkloadController(BPMNController bpmn, int nbrOfInstances, Map<String, Object> variables, int period) {
		this.bpmn = bpmn;
		this.nbrOfInstances = nbrOfInstances;
		this.variables = new HashMap<>();
		this.variables.putAll(variables);
		this.PERIOD = period;
	}
	
	public void startWorkload() {
		Timer timer = new Timer();
		timer.schedule(new Workload(this.bpmn, this.nbrOfInstances, this.variables), 0, this.PERIOD);
		
	}
}
