package fr.inria.convecs.sbpmn.calcul;

import java.util.Date;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;

import fr.inria.convecs.sbpmn.deploy.BPMNProcess;

public class CalculExecutionTimeByInstances {
	
	private BPMNProcess bpmnProcess;
	private Long lastAverage;
	
	public CalculExecutionTimeByInstances(BPMNProcess bpmnProcess) {
		this.bpmnProcess = bpmnProcess;
		this.lastAverage = 0L;
	}
	
	
	public Long getNbrFinishedInstances() {
		
		String processKey = this.bpmnProcess.getProcessId();
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		HistoryService hs = engine.getHistoryService();
		HistoricProcessInstanceQuery hil = hs.createHistoricProcessInstanceQuery()
				.processDefinitionKey(processKey).finished();
		return hil.count();
	}
	
	public List<HistoricProcessInstance> getFinishedInstances() {
		
		String processKey = this.bpmnProcess.getProcessId();
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		HistoryService hs = engine.getHistoryService();
		HistoricProcessInstanceQuery hil = hs.createHistoricProcessInstanceQuery()
				.processDefinitionKey(processKey).finished();
		System.out.println("hil.list:" + hil.list().size());
		return hil.list();
	}
	
	
	public List<HistoricProcessInstance> getFinishedInstances(int NbrInstance) {
		
		String processKey = this.bpmnProcess.getProcessId();
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		HistoryService hs = engine.getHistoryService();
		HistoricProcessInstanceQuery hil = hs.createHistoricProcessInstanceQuery()
				.processDefinitionKey(processKey).orderByProcessInstanceEndTime().desc().finished();
		System.out.println("hil.list:" + hil.list().size());
		
		if(hil.list().size() > NbrInstance) {
			return hil.list().subList(0, NbrInstance - 1);
		}
		
		return hil.list();
	}
	
	
	public Long getRealAverageExecutionTime() {
		
		System.out.println("Real (s): " + calculRealAverageExecutionTime(this.getFinishedInstances()));
		return calculRealAverageExecutionTime(this.getFinishedInstances());
	}
	
	public Long getAverageExecutionTime() {
		return calculAverageExecutionTime(this.getFinishedInstances()); 
	}
	
	public Long getAverageExecutionTime(int NbrInstance) {
		return calculAverageExecutionTime(this.getFinishedInstances(NbrInstance)); 
	}
	
	public Long getAverageExecutionTimeByLastPeriod(int lastPeriod) {
		return calculAverageExecutionTime(this.getFinishedInstancesByPeriod(lastPeriod)); 
	}
	
	
	public List<HistoricProcessInstance> getFinishedInstancesByPeriod(int lastPeriod) {
		
		String processKey = this.bpmnProcess.getProcessId();
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		HistoryService hs = engine.getHistoryService();
		HistoricProcessInstanceQuery hil = hs.createHistoricProcessInstanceQuery()
				.processDefinitionKey(processKey).orderByProcessInstanceEndTime().desc().finished().finishedAfter(new Date(System.currentTimeMillis() - lastPeriod *1000L));
		System.out.println("hil.list:" + hil.list().size());	
		
		return hil.list();
	}
	
	public Long getTotalExecutionTime() {
		return calculTotalExecutionTime(this.getFinishedInstances()); 
	}
	
	public Long getMaxExecutionTime() {
		return calculMaxExecutionTime(this.getFinishedInstances()); 
	}
	
	public Long getMinExecutionTime() {
		return calculMinExecutionTime(this.getFinishedInstances()); 
	}
	
	private Long calculRealAverageExecutionTime(List<HistoricProcessInstance> list_hiInstances) {
		Long sum = 0L;

		Long startTime = Long.MAX_VALUE;
		Long endTime = 0L;
		if (list_hiInstances.size() > 0) {

			for (HistoricProcessInstance hiInstance : list_hiInstances) {
				sum = sum + hiInstance.getDurationInMillis() / 1000;
				
				if (startTime > hiInstance.getStartTime().getTime()) {
					startTime = hiInstance.getStartTime().getTime();
				}
				if (endTime < hiInstance.getEndTime().getTime()) {
					endTime = hiInstance.getEndTime().getTime();
				}
			}

			return ((endTime - startTime) / 1000);
		}
		return sum;

	}
	
	private Long calculAverageExecutionTime(List<HistoricProcessInstance> list_hiInstances) {
		Long sum = 0L;
		if (list_hiInstances.size() > 0) {
			for (HistoricProcessInstance hiInstance : list_hiInstances) {
				sum = sum + hiInstance.getDurationInMillis() / 1000;

			}
			this.lastAverage = sum / list_hiInstances.size();
			return sum / list_hiInstances.size();
		}
		
		return this.lastAverage;
		//return sum;
	}
	
	private Long calculTotalExecutionTime(List<HistoricProcessInstance> list_hiInstances) {
		Long sum = 0L;
		if (list_hiInstances.size() > 0) {
			for (HistoricProcessInstance hiInstance : list_hiInstances) {
				sum = sum + hiInstance.getDurationInMillis() / 1000;

			}
		}
		return sum;
	}
	
	private Long calculMinExecutionTime(List<HistoricProcessInstance> list_hiInstances) {
		Long minExecutionTime = Long.MAX_VALUE;
		for (HistoricProcessInstance hiInstance : list_hiInstances) {
			if (hiInstance.getDurationInMillis() < minExecutionTime) {
				minExecutionTime = hiInstance.getDurationInMillis();
			}

		}
		return minExecutionTime / 1000;

	}
	
	private Long calculMaxExecutionTime(List<HistoricProcessInstance> list_hiInstances) {
		Long maxExecutionTime = 0L;
		for (HistoricProcessInstance hiInstance : list_hiInstances) {
			if (hiInstance.getDurationInMillis() > maxExecutionTime) {
				maxExecutionTime = hiInstance.getDurationInMillis();
			}
		}
		return maxExecutionTime / 1000;
	}
}
