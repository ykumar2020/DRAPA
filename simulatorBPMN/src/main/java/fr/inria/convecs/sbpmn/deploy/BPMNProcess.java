package fr.inria.convecs.sbpmn.deploy;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;

public class BPMNProcess {
	private String processId;
	private String bpmnName;
	private Random random;
	
	
	public BPMNProcess(String processId, String bpmnName) {
		super();
		this.processId = processId;
		this.bpmnName = bpmnName;
		
		this.random = new Random(50); //random for exclusive gateway
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
	public String getBpmnName() {
		return bpmnName;
	}
	
	public void setBpmnName(String bpmnName) {
		this.bpmnName = bpmnName;
	}
	
	@Override
	public String toString() {
		return "Process [processId=" + processId + ", bpmnName=" + bpmnName + "]";
	}
	
	public void Deployment() {
		
		System.out.println("Deployment: " + this.getBpmnName() + ".bpmn; ID: " + this.getProcessId());

		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		RepositoryService service = engine.getRepositoryService();
		Deployment deploy = service.createDeployment().addClasspathResource("BPMN/" + this.getBpmnName() + ".bpmn")
				.name(this.getProcessId()).deploy();
		System.out.println(" -- Deployment Success -- ");
		System.out.println("Process Id: " + deploy.getId() + "; Process Name: " + deploy.getName());
	}
	
	
	// Create instances without variables
	public void generateInstances(int nbraInstance) {
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = engine.getRuntimeService();

		String key = this.getProcessId();

		for (int i = 1; i <= nbraInstance; i++) {

			runtimeService.startProcessInstanceByKey(key);

//			System.out.println("Process de finition ID：" + processInstance.getProcessDefinitionId());
//			System.out.println("Process Instance ID：" + processInstance.getId());
		}
	}
	
	// Create instances with variables
	public void generateInstances(int nbrInstance, Map<String, Object> bpmnVariables) {

		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = engine.getRuntimeService();

		Map<String, Object> variables = new HashMap<>();
		
		variables.putAll(bpmnVariables);

		String key = this.getProcessId();
		int random_number;
		for (int i = 1; i <= nbrInstance; i++) {

			System.out.println("-" + i + "-:");
			
			
			for (String v_key: variables.keySet()) {
				//random_number = 1 + (int)(Math.random()*((int)(variables.get(v_key))));
				
				//random_number = 1 + new Random().nextInt(10);
				random_number = 1 + random.nextInt(10);
				
				variables.put(v_key, random_number);
				System.out.println("random:" + random_number);
			}
			
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, variables);

			System.out.println("Process de finition ID：" + processInstance.getProcessDefinitionId());
			System.out.println("Process Instance ID：" + processInstance.getId());
		}
	}
	
}
