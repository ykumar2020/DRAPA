package fr.inria.convecs.sbpmn.calcul;

import java.util.Date;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricTaskInstance;

import fr.inria.convecs.sbpmn.deploy.BPMNProcess;

public class CalculExecutionTimeByTasks {
	
	private BPMNProcess bpmnProcess;
	private Date lastEndTimestamps;
	
	public CalculExecutionTimeByTasks(BPMNProcess bpmnProcess) {
		this.bpmnProcess = bpmnProcess;
	}
	
	public Date getLastEndTimestamps() {
		return this.lastEndTimestamps;
	}
	
	public Long getExecutionTimeByTasks() {
		return calculExecutionTimeByTasks();
	}
	
	public Long getExecutionTimeByDuration(int minutes) {
		return calculExecutTionTimeByDuration(minutes);
	}
	
	private Long calculExecutTionTimeByDuration(int minutes) {
		this.calculExecutionTimeByTasks();
		return (this.lastEndTimestamps.getTime() - minutes * 60000L > 0) ? (this.lastEndTimestamps.getTime() - minutes * 60000L) : this.calculExecutionTimeByTasks();
	}
	 
	
	private Long calculExecutionTimeByTasks() {
		
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		HistoryService historyService = engine.getHistoryService();

		Long startTime = 0L;
		Long endTime = 0L;
		
		List<HistoricTaskInstance> hisTaskStart;
		List<HistoricTaskInstance> hisTaskEnd;
		String processKey = this.bpmnProcess.getProcessId();
		
		
		hisTaskStart = historyService.createHistoricTaskInstanceQuery().processDefinitionKey(processKey).finished()
				.orderByHistoricTaskInstanceStartTime().asc().list();
		hisTaskEnd = historyService.createHistoricTaskInstanceQuery().processDefinitionKey(processKey).finished()
				.orderByHistoricTaskInstanceEndTime().desc().list();
		
		if (hisTaskStart.size() > 1 && hisTaskEnd.size() > 1) {
			HistoricTaskInstance firstTask = hisTaskStart.get(0);
			HistoricTaskInstance lastTask = hisTaskEnd.get(0);
			startTime = firstTask.getStartTime().getTime();
			endTime = lastTask.getEndTime().getTime();
			lastEndTimestamps = lastTask.getEndTime();
			//System.out.println();
			return (endTime - startTime) / 1000;
		}
		
		return 0L;
		
	}

}
