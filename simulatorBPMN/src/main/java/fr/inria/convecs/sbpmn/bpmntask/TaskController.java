package fr.inria.convecs.sbpmn.bpmntask;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;

import fr.inria.convecs.sbpmn.common.Resource;
import fr.inria.convecs.sbpmn.common.BPMNResources;
import fr.inria.convecs.sbpmn.bpmntask.BPMNTask;

public class TaskController {

	@Override
	public String toString() {
		return "TaskController [resources=" + resources + ", task]";
	}

	private BPMNResources resources;
	// private BPMNTask task;

	public TaskController(BPMNResources resources) {
		this.resources = resources;
	}

	
	/**
	 * take resources
	 * 
	 */
	public boolean takeResource(BPMNTask task) {

		boolean flag = false;

		Map<String, Integer> taskResources = new ConcurrentHashMap<String, Integer>();
		taskResources.putAll(task.getTaskResources());

		for (String taskResKey : taskResources.keySet()) {
			for (Resource res : resources.getAllResource()) {
				if (res.getId().equals(taskResKey)) {
					int currentResource = res.getNumber();
					int requiredResource = taskResources.get(taskResKey);
					if (currentResource < requiredResource) {
						flag = true;
					}
				}
			}
		}

		if (flag) {
			return false;
		}

		for (String taskResKey : taskResources.keySet()) {
			for (Resource res : resources.getAllResource()) {

				if (res.getId().equals(taskResKey)) {
					System.out.println("\u001b[36;1m Current Resources : Key:" + taskResKey + ", value:"
							+ res.getNumber() + "\u001b[0m");
					int currentResource = res.getNumber();
					int requiredResource = taskResources.get(taskResKey);
					int resNewNumber = currentResource - requiredResource;
					res.setNumber(resNewNumber);
					
					res.addStartTime(task.getTask().getId());
					
					System.out.println("\u001b[31m " + task.getTask().getId() + " After assigning Resources -: Key:"
							+ taskResKey + ",value:" + res.getNumber() + "\u001b[0m");
				}

			}
		}
		return true;

	}

	/**
	 * 
	 * execute task
	 */
	public void executeCourrentTask(BPMNTask task) {

		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		TaskService taskService = engine.getTaskService();

		System.out.println("\u001b[35;1m" + task.getId() + " Executing duration:" + task.getDuration() + "\u001b[0m");

		System.out.println("task duration: " + task.getDuration() * 1000);

		taskService.complete(task.getId());

		System.out.println("----------------------");

	}
	
	/**
	 * Release resources 
	 * 
	 * 
	 */
	public void putResource(BPMNTask task) {

		for (String taskResKey : task.getTaskResources().keySet()) {
			for (Resource res : resources.getAllResource()) {

				if (res.getId().equals(taskResKey)) {
					System.out.println("\u001b[36;1m Current Resources : Key:" + taskResKey + ",value:"
							+ res.getNumber() + "\u001b[0m");
					int currentResource = res.getNumber();
					int requiredResource = task.getTaskResources().get(taskResKey);
					int resNewNumber = currentResource + requiredResource;
					res.setNumber(resNewNumber);
					
					res.addEndTime(task.getTask().getId());
					
					System.out.println("\u001b[31m " + task.getTask().getId() + " Release resources -: Key:"
							+ taskResKey + ",value:" + res.getNumber() + "\u001b[0m");
				}

			}
		}
	}

}
