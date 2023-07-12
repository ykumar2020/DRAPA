package fr.inria.convecs.sbpmn.calcul;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricFormProperty;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;

import fr.inria.convecs.sbpmn.common.BPMNResources;
import fr.inria.convecs.sbpmn.common.Resource;
import fr.inria.convecs.sbpmn.deploy.BPMNProcess;

public class CalculResourcesUsageByTasks {

	private BPMNProcess bpmnProcess;
	private BPMNResources bpmnResources;

	public CalculResourcesUsageByTasks(BPMNProcess bpmnProcess, BPMNResources bpmnResources) {
		this.bpmnProcess = bpmnProcess;
		this.bpmnResources = bpmnResources;
	}
	
	public CalculResourcesUsageByTasks(BPMNProcess bpmnProcess) {
		this.bpmnProcess = bpmnProcess;
	}
	
	
	public Map<String, Integer> calculTotalRes(){
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		HistoryService hs = engine.getHistoryService();
		
		Map<String, Map<String, Integer>> TaskResources = new HashMap<>();
		
		Map<String, Integer> calculResources = new HashMap<>();
		
		
		List<HistoricTaskInstance> list_hisTasks = this.calculHistoricTasks(); 
		
		for(HistoricTaskInstance his_task : list_hisTasks) {
			
			Map<String, Integer> Resources = new HashMap<>();
			List<HistoricDetail> hds = hs.createHistoricDetailQuery().formProperties().list();
			for (HistoricDetail hd : hds) {
				if (hd.getTaskId().equals(his_task.getId())) {
					HistoricFormProperty hfp = (HistoricFormProperty) hd;
//					 System.out.println(hd.getTaskId() + ","+ hfp.getPropertyId() + "," +
//					 hfp.getPropertyValue());
					
					 Resources.put(hfp.getPropertyId(), Integer.parseInt(hfp.getPropertyValue()));
					 
					 if(!calculResources.containsKey(hfp.getPropertyId()) && !hfp.getPropertyId().equals("duration")) {
							calculResources.put(hfp.getPropertyId(), 0);
						}
					
				}
			}
			TaskResources.put(his_task.getId(), Resources);
			
		}
		
		for (Map<String, Integer> res : TaskResources.values()) {
			//int duration = res.get("duration");
			for(String ky : res.keySet()) {
				
				calculResources.put(ky, calculResources.get(ky) + res.get(ky));
				
			}
		}
		if(calculResources.keySet().size() > 0) {
			System.out.println("Resource usage -:");
		}
		for(String resKey: calculResources.keySet()) {
			System.out.println("\u001b[35;1m Resource: " + resKey + ", Usage:" + calculResources.get(resKey) + " \u001b[0m");
		}
		//System.out.println("totalRes:" + calculResources);
		
		return calculResources;
	}
	
	
	
	public Map<String, Integer> calculTotalRes(int minutes){
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		HistoryService hs = engine.getHistoryService();
		
		Map<String, Map<String, Integer>> TaskResources = new HashMap<>();
		
		Map<String, Integer> calculResources = new HashMap<>();
		
		
		List<HistoricTaskInstance> list_hisTasks = this.calculHistoricTasksLastDuration(minutes);
		//List<HistoricTaskInstance> list_hisTasks = this.calculHistoricTasksLastDurationBySeconds(minutes);
		
		for(HistoricTaskInstance his_task : list_hisTasks) {
			
			Map<String, Integer> Resources = new HashMap<>();
			List<HistoricDetail> hds = hs.createHistoricDetailQuery().formProperties().list();
			for (HistoricDetail hd : hds) {
				if (hd.getTaskId().equals(his_task.getId())) {
					HistoricFormProperty hfp = (HistoricFormProperty) hd;
//					 System.out.println(hd.getTaskId() + ","+ hfp.getPropertyId() + "," +
//					 hfp.getPropertyValue());
					
					 Resources.put(hfp.getPropertyId(), Integer.parseInt(hfp.getPropertyValue()));
					 
					 if(!calculResources.containsKey(hfp.getPropertyId()) && !hfp.getPropertyId().equals("duration")) {
							calculResources.put(hfp.getPropertyId(), 0);
						}
					
				}
			}
			TaskResources.put(his_task.getId(), Resources);
			
		}
		
		for (Map<String, Integer> res : TaskResources.values()) {
			//int duration = res.get("duration");
			for(String ky : res.keySet()) {
				
				calculResources.put(ky, calculResources.get(ky) + res.get(ky));
				
			}
		}
		//System.out.println("totalRes_minutes:" + calculResources);
		
		if(calculResources.keySet().size() > 0) {
			System.out.println("Resource usage (last: "+ minutes + " minutes) - :");
		}
		for(String resKey: calculResources.keySet()) {
			System.out.println("\u001b[35;1m Resource: " + resKey + ", Usage:" + calculResources.get(resKey) + " \u001b[0m");
		}
		
		return calculResources;
	}
	
	private List<HistoricTaskInstance> calculHistoricTasks() {

		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		HistoryService hs = engine.getHistoryService();
		HistoricTaskInstanceQuery hil_tasks = hs.createHistoricTaskInstanceQuery()
				.processDefinitionKey(this.bpmnProcess.getProcessId()).finished();
		List<HistoricTaskInstance> list_his_tasks = hil_tasks.list();
		return list_his_tasks;
	}

	private List<HistoricTaskInstance> calculHistoricTasksAfterDate(Date endDate) {

		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		HistoryService hs = engine.getHistoryService();
		HistoricTaskInstanceQuery hil_tasks = hs.createHistoricTaskInstanceQuery()
				.processDefinitionKey(this.bpmnProcess.getProcessId()).taskCompletedAfter(endDate).finished();
		List<HistoricTaskInstance> list_his_tasks = hil_tasks.list();
		return list_his_tasks;
	}

	private List<HistoricTaskInstance> calculHistoricTasksBeforeDate(Date endDate) {

		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		HistoryService hs = engine.getHistoryService();
		HistoricTaskInstanceQuery hil_tasks = hs.createHistoricTaskInstanceQuery()
				.processDefinitionKey(this.bpmnProcess.getProcessId()).taskCompletedBefore(endDate).finished();
		List<HistoricTaskInstance> list_his_tasks = hil_tasks.list();
		return list_his_tasks;
	}
	
	
	private Date getLastOneTime() {
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		HistoryService hs = engine.getHistoryService();
		HistoricTaskInstanceQuery hil_tasks = hs.createHistoricTaskInstanceQuery()
				.processDefinitionKey(this.bpmnProcess.getProcessId()).orderByHistoricTaskInstanceEndTime().desc().finished();
		List<HistoricTaskInstance> list_his_tasks = hil_tasks.list();
		return  list_his_tasks.get(0).getEndTime();
	}
	
	private List<HistoricTaskInstance> calculHistoricTasksLastDuration(int minutes) {
		
		Date endTime = this.getLastOneTime();
		Date filtreDate = new Date(endTime.getTime() - minutes * 60000L);
		
		return calculHistoricTasksAfterDate(filtreDate);
	}
	
private List<HistoricTaskInstance> calculHistoricTasksLastDurationBySeconds(int seconds) {
		
		Date endTime = this.getLastOneTime();
		Date filtreDate = new Date(endTime.getTime() - seconds * 1000L);
		
		return calculHistoricTasksAfterDate(filtreDate);
	}
}
