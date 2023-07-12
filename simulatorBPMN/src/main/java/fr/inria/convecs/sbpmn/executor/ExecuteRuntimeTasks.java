package fr.inria.convecs.sbpmn.executor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;

import fr.inria.convecs.sbpmn.deploy.BPMNController;
import fr.inria.convecs.sbpmn.deploy.BPMNProcess;
import fr.inria.convecs.sbpmn.bpmntask.TaskController;
import fr.inria.convecs.sbpmn.common.BPMNResources;
import fr.inria.convecs.sbpmn.bpmntask.BPMNTask;

public class ExecuteRuntimeTasks {

	private BPMNProcess process;
	private BPMNResources resources;
	
	public ExecuteRuntimeTasks(BPMNController bpmnProcess) {
		
		this.process = bpmnProcess.getBpmnProcess();
	}
	
	public ExecuteRuntimeTasks(BPMNController bpmnProcess, BPMNResources resources) {
		
		this.process = bpmnProcess.getBpmnProcess();
		this.resources = resources;
	}
	
	public void executor(Map<String, Object> variables) {
		
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		TaskService taskService = engine.getTaskService();
		
		List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(this.process.getProcessId()).orderByTaskCreateTime().asc().list();
		
		System.out.println(tasks);
		
		TaskController taskController = new TaskController(this.resources);
		
		LinkedBlockingQueue<String> tasksQueue = new LinkedBlockingQueue<>();
		LinkedBlockingQueue<BPMNTask> bpmntasksQueue = new LinkedBlockingQueue<>(); // Awaiting tasks
		//TreeMap<String, Integer> runningTasks = new TreeMap<>(); // Running tasks
		ConcurrentMap<BPMNTask, Integer> runningTasks = new ConcurrentHashMap<>(); // Running tasks
		
		Random random = new Random(50);
		Random randnbr = new Random(50); 
		
		//int counterofInstance = 4;
		int counterofInstance = 1+ random.nextInt(20);
		
		//int nbrofInstance = 498;
		int nbrofInstance = 498;
		
		do {
			System.out.println(tasks);
		for (Task task : tasks) {
			
			BPMNTask bpmnTask = new BPMNTask(task);
			
			if(!tasksQueue.contains(bpmnTask.getId())) {
				
				tasksQueue.add(bpmnTask.getId());
				bpmnTask.storeTaskInfos();
				
				bpmntasksQueue.add(bpmnTask);
			}else {
				
				System.out.println("********************");
			}
			
			
			//System.out.println(taskController.takeResource(bpmnTask));
			//taskService.complete(bpmnTask.getId());
			System.out.println("id:" + bpmnTask.getId()+ ", duration:"+ bpmnTask.getDuration() +", resources:"+ bpmnTask.getTaskResources()) ;
		}
		
		for(BPMNTask bpmnTask: bpmntasksQueue) {
			if(taskController.takeResource(bpmnTask)) {
				
				//runningTasks.put(bpmnTask.getId(), bpmnTask.getDuration());
				runningTasks.put(bpmnTask, bpmnTask.getDuration());
				
				bpmntasksQueue.remove(bpmnTask);
			}
		}
		//Sleeps
		try {
			Thread.sleep(1000);
			counterofInstance--;
			} catch (Exception e){
			System.exit( 0 ); //退出程序
		}
		
		if(counterofInstance<=0) {
			//this.process.generateInstances(1, variables);
			//counterofInstance = 4;
			
			int temp_instance = 1 + randnbr.nextInt(3);
			if(nbrofInstance < temp_instance) {
				this.process.generateInstances(nbrofInstance, variables);
				nbrofInstance = 0;
			}else {
				this.process.generateInstances(temp_instance, variables);
				nbrofInstance = nbrofInstance - temp_instance;
			}
			
			//this.process.generateInstances(1 + randnbr.nextInt(3), variables);
			counterofInstance = 1+random.nextInt(20);
			
		}
//		Iterator<Map.Entry<BPMNTask, Integer>> entries = runningTasks.entrySet().iterator();
//		while(entries.hasNext()) {
//			Map.Entry<BPMNTask, Integer> entry = entries.next();
//			if(entry.getValue() - 1 == 0) {
//				
//				tasksQueue.remove(entry.getKey());
//				runningTasks.remove(entry.getKey());
//				
//				//1. Complete tasks
//				taskController.executeCourrentTask(entry.getKey());
//				//2. release resources
//				taskController.putResource(entry.getKey());
//				
//			}else {
//				runningTasks.put(entry.getKey(), entry.getValue() - 1);
//			}
//		}
		for(BPMNTask runingtaskid: runningTasks.keySet()) {
			if(runningTasks.get(runingtaskid) - 1 == 0) {
				tasksQueue.remove(runingtaskid.getId());
				runningTasks.remove(runingtaskid);
				
				//1. Complete tasks
				taskController.executeCourrentTask(runingtaskid);
				//2. release resources
				taskController.putResource(runingtaskid);
				
			}else {
				int old_value = runningTasks.get(runingtaskid);
				runningTasks.put(runingtaskid, old_value - 1);
			}
		}
		//
		//for()
		System.out.println("treeMap:" + runningTasks);
		System.out.println("tasksQueue:" + tasksQueue);
		
		tasks = taskService.createTaskQuery().processDefinitionKey(this.process.getProcessId()).orderByTaskCreateTime().asc().list();
		
		}while(nbrofInstance > 0);
		//}while(true);
		//}while(!tasks.isEmpty());
	}
	
	
public void executor() {
		
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		TaskService taskService = engine.getTaskService();
		
		List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(this.process.getProcessId()).orderByTaskCreateTime().asc().list();
		
		System.out.println(tasks);
		
		TaskController taskController = new TaskController(this.resources);
		
		LinkedBlockingQueue<String> tasksQueue = new LinkedBlockingQueue<>();
		LinkedBlockingQueue<BPMNTask> bpmntasksQueue = new LinkedBlockingQueue<>(); // Awaiting tasks
		//TreeMap<String, Integer> runningTasks = new TreeMap<>(); // Running tasks
		ConcurrentMap<BPMNTask, Integer> runningTasks = new ConcurrentHashMap<>(); // Running tasks
		
		int counterofInstance = 4;
		
		do {
			System.out.println(tasks);
		for (Task task : tasks) {
			
			BPMNTask bpmnTask = new BPMNTask(task);
			
			if(!tasksQueue.contains(bpmnTask.getId())) {
				
				tasksQueue.add(bpmnTask.getId());
				bpmnTask.storeTaskInfos();
				
				bpmntasksQueue.add(bpmnTask);
			}else {
				
				System.out.println("********************");
			}
			
			
			//System.out.println(taskController.takeResource(bpmnTask));
			//taskService.complete(bpmnTask.getId());
			System.out.println("id:" + bpmnTask.getId()+ ", duration:"+ bpmnTask.getDuration() +", resources:"+ bpmnTask.getTaskResources()) ;
		}
		
		for(BPMNTask bpmnTask: bpmntasksQueue) {
			if(taskController.takeResource(bpmnTask)) {
				
				//runningTasks.put(bpmnTask.getId(), bpmnTask.getDuration());
				runningTasks.put(bpmnTask, bpmnTask.getDuration());
				
				bpmntasksQueue.remove(bpmnTask);
			}
		}
		//Sleeps
		try {
			Thread.sleep(1000);
			counterofInstance--;
			} catch (Exception e){
			System.exit( 0 ); //退出程序
		}
		
		if(counterofInstance<=0) {
			this.process.generateInstances(1);
			counterofInstance = 4;
		}
//		Iterator<Map.Entry<BPMNTask, Integer>> entries = runningTasks.entrySet().iterator();
//		while(entries.hasNext()) {
//			Map.Entry<BPMNTask, Integer> entry = entries.next();
//			if(entry.getValue() - 1 == 0) {
//				
//				tasksQueue.remove(entry.getKey());
//				runningTasks.remove(entry.getKey());
//				
//				//1. Complete tasks
//				taskController.executeCourrentTask(entry.getKey());
//				//2. release resources
//				taskController.putResource(entry.getKey());
//				
//			}else {
//				runningTasks.put(entry.getKey(), entry.getValue() - 1);
//			}
//		}
		for(BPMNTask runingtaskid: runningTasks.keySet()) {
			if(runningTasks.get(runingtaskid) - 1 == 0) {
				tasksQueue.remove(runingtaskid.getId());
				runningTasks.remove(runingtaskid);
				
				//1. Complete tasks
				taskController.executeCourrentTask(runingtaskid);
				//2. release resources
				taskController.putResource(runingtaskid);
				
			}else {
				int old_value = runningTasks.get(runingtaskid);
				runningTasks.put(runingtaskid, old_value - 1);
			}
		}
		//
		//for()
		System.out.println("treeMap:" + runningTasks);
		System.out.println("tasksQueue:" + tasksQueue);
		
		tasks = taskService.createTaskQuery().processDefinitionKey(this.process.getProcessId()).orderByTaskCreateTime().asc().list();
		
		}while(true);
		//}while(!tasks.isEmpty());
	}
}
