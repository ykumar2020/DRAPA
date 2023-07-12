package fr.inria.convecs.sbpmn.deploy;

import java.util.Map;

public class BPMNController {
	
	private BPMNProcess bpmnProcess;
	private String processId;
	private String processName;
	
	public BPMNController(String processId, String processName){
		this.bpmnProcess = new BPMNProcess(processId, processName);
		this.processId = processId;
		this.processName = processName;
	}
	
	public BPMNProcess getBpmnProcess() {
		return bpmnProcess;
	}

	public void setBpmnProcess(BPMNProcess bpmnProcess) {
		this.bpmnProcess = bpmnProcess;
	}

	public boolean bpmnDeployment() {
		this.bpmnProcess.Deployment();
		return true;
	}
	
	public boolean generateInstances(int nbrInstance) {
		this.bpmnProcess.generateInstances(nbrInstance);
		return true;
	}
	
	public boolean generateInstances(int nbrInstance, Map<String, Object> bpmnVariables) {
		this.bpmnProcess.generateInstances(nbrInstance, bpmnVariables);
		return true;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}
}
