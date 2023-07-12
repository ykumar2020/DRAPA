package fr.inria.convecs.sbpmn.calcul;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import fr.inria.convecs.sbpmn.common.Resource;

public class CalculUniqueResource {
	private Resource resource;
	
	public CalculUniqueResource(Resource resource) {
		this.resource = resource;
	}
	
	public Long getResourceUsage() {
		TreeMap<Date, Integer> traces = new TreeMap<>();
		traces.putAll(this.resource.getUsageDuplicaTraces());
		
		Long duration = 0L;
		
		
		if(traces.size() == 1) {
			Long endtime = new Date().getTime();
			Long firstTime = traces.firstKey().getTime();
			
			Integer number = traces.firstEntry().getValue();
			duration = (endtime - firstTime) * number;
			
		}else {
			traces.put(new Date(), 0);
			ArrayList<Date> traceKeys = new ArrayList<>(); 
			ArrayList<Integer> traceValues = new ArrayList<>(); 
			
			traceKeys.addAll(traces.keySet());
			traceValues.addAll(traces.values());
			
			Long currentTime = traceKeys.get(0).getTime();
			
			for(int i = 1; i < traceKeys.size(); i++) {
				Long tempTime = traceKeys.get(i).getTime();
				
				duration += (tempTime - currentTime) * traceValues.get(i - 1);
				
				currentTime = tempTime;
			}
			
		}
		
		return duration;
		
	}
	
	
	public Long getResourceUsage(Date afterDate) {
		TreeMap<Date, Integer> traces = new TreeMap<>();
		
		
		for(Date key : this.resource.getUsageDuplicaTraces().keySet()) {
			if(key.getTime() >= afterDate.getTime()) {
				traces.put(key, this.resource.getUsageDuplicaTraces().get(key));
			}
		}
		
		traces.putAll(this.resource.getUsageDuplicaTraces());
		
		Long duration = 0L;
		
		
		if(traces.size() == 1) {
			Long endtime = new Date().getTime();
			Long firstTime = traces.firstKey().getTime();
			
			Integer number = traces.firstEntry().getValue();
			duration = (endtime - firstTime) * number;
			
		}else {
			traces.put(new Date(), 0);
			ArrayList<Date> traceKeys = new ArrayList<>(); 
			ArrayList<Integer> traceValues = new ArrayList<>(); 
			
			traceKeys.addAll(traces.keySet());
			traceValues.addAll(traces.values());
			
			Long currentTime = traceKeys.get(0).getTime();
			
			for(int i = 1; i < traceKeys.size(); i++) {
				Long tempTime = traceKeys.get(i).getTime();
				
				duration += (tempTime - currentTime) * traceValues.get(i - 1);
				
				currentTime = tempTime;
			}
			
		}
		
		return duration;
		
	}
}
