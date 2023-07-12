package fr.inria.convecs.sbpmn.deploy;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import fr.inria.convecs.sbpmn.deploy.BPMNController;

public class Workload extends TimerTask{
	
	
	private BPMNController bpmn;
	private int nbrOfInstances;
	private Map<String, Object> variables;
	
	private static int instCount = 0;
	//private double PERIOD;
	
	
	public Workload(BPMNController bpmn, int nbrOfInstances, Map<String, Object> variables) {
		
		this.bpmn = bpmn;
		this.nbrOfInstances = nbrOfInstances;
		this.variables = new HashMap<>();
		this.variables.putAll(variables);
		//this.PERIOD = period;
	}
	
	
      @Override
      public void run()
        {
    	  if(instCount < nbrOfInstances) {
    		  
    		  //this.bpmn.generateInstances(1, variables);
    		  // instCount += 1;
    		// random nbr of instances
    		  int nbr = new Random().nextInt(2) + 1;
    		  this.bpmn.generateInstances(nbr, variables);
    		  instCount += nbr;
    	  }else {
    		  cancel();
    	  }
             
        }
    }
	

