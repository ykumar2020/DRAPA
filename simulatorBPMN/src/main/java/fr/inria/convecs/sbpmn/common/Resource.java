package fr.inria.convecs.sbpmn.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Resource {

	private String id;
	private String name;
	private volatile int number;
	private int nbrDuplica;
	private ConcurrentSkipListMap<Date, Integer> usageDuplicaTraces;
	private ConcurrentSkipListMap<Date, Integer> usageNbrTraces; // 记录资源使用变化
	private Long duration;
	private Date addReduceTimestamps;
	// private Date reduceTimestamps;
	private int controleDuration;

	private ArrayList<Boolean> resUageArrayList;
	
	private ArrayList<Float> resUsageTraces;

	private double minUsage;
	private double maxUsage;

	private double lastUsageValue = 0.0;
	
	private ConcurrentSkipListMap<String, Date> taskStartTime;
	private ConcurrentSkipListMap<String, Date> taskEndTime; // 记录资源使用变化
	
	private int passiveCost;
	private int activeCost;
	
	public void addStartTime(String taskID) {
		this.taskStartTime.put(taskID, new Date());
	}
	
	public void addEndTime(String taskID) {
		this.taskEndTime.put(taskID, new Date());
	}
	
	public ConcurrentSkipListMap<String, Date> getTaskStartTime(){
		return this.taskStartTime;
	}
	
	public ConcurrentSkipListMap<String, Date> getTaskEndTime(){
		return this.taskEndTime;
	}

	public Resource(String id, int number) {
		this.id = id;
		this.number = number;
		this.nbrDuplica = number;
		this.duration = 0L;

		this.addReduceTimestamps = new Date();
		// this.reduceTimestamps = new Date();
		this.controleDuration = 1;

		usageDuplicaTraces = new ConcurrentSkipListMap<>();
		usageDuplicaTraces.put(new Date(), number);

		resUageArrayList = new ArrayList<>(Collections.nCopies(number, true));
		resUsageTraces  = new ArrayList<>();

		this.setMinUsage(0.5);
		this.setMaxUsage(0.7);
		
		this.taskStartTime = new ConcurrentSkipListMap<>();
		this.taskEndTime = new ConcurrentSkipListMap<>(); 

	}

	public Resource(String id, String name, int number) {
		this.id = id;
		this.name = name;
		this.number = number;
		this.duration = 0L;
		this.nbrDuplica = number;

		this.addReduceTimestamps = new Date();
		// this.reduceTimestamps = new Date();
		this.controleDuration = 1;

		usageDuplicaTraces = new ConcurrentSkipListMap<>();
		usageDuplicaTraces.put(new Date(), number);

		resUageArrayList = new ArrayList<>(Collections.nCopies(number, true));
		resUsageTraces  = new ArrayList<>();

		this.setMinUsage(0.5);
		this.setMaxUsage(0.7);
		
		this.taskStartTime = new ConcurrentSkipListMap<>();
		this.taskEndTime = new ConcurrentSkipListMap<>(); 
	}

	public Resource(String id, String name, int number, double minUsage, double maxUsage, int period) {
		this.id = id;
		this.name = name;
		this.number = number;
		this.duration = 0L;
		this.nbrDuplica = number;

		this.addReduceTimestamps = new Date();
		// this.reduceTimestamps = new Date();
		// this.controleDuration = 1;
		this.controleDuration = period;
		
		Date currentTime = new Date();
		usageDuplicaTraces = new ConcurrentSkipListMap<>();
		usageDuplicaTraces.put(currentTime, number);

		this.usageNbrTraces = new ConcurrentSkipListMap<>();
		this.usageNbrTraces.put(currentTime, number);

		resUageArrayList = new ArrayList<>(Collections.nCopies(number, true));
		resUsageTraces  = new ArrayList<>();

		this.setMinUsage(minUsage);
		this.setMaxUsage(maxUsage);
		
		this.taskStartTime = new ConcurrentSkipListMap<>();
		this.taskEndTime = new ConcurrentSkipListMap<>(); 
	}
	
	
	public Resource(String id, String name, int number, double minUsage, double maxUsage, int activeCost, int passiveCost,int period) {
		this.id = id;
		this.name = name;
		this.number = number;
		this.duration = 0L;
		this.nbrDuplica = number;
		
		this.activeCost = activeCost;
		this.passiveCost = passiveCost;

		this.addReduceTimestamps = new Date();
		// this.reduceTimestamps = new Date();
		// this.controleDuration = 1;
		this.controleDuration = period;
		
		Date currentTime = new Date();
		usageDuplicaTraces = new ConcurrentSkipListMap<>();
		usageDuplicaTraces.put(currentTime, number);

		this.usageNbrTraces = new ConcurrentSkipListMap<>();
		this.usageNbrTraces.put(currentTime, number);

		resUageArrayList = new ArrayList<>(Collections.nCopies(number, true));
		resUsageTraces  = new ArrayList<>();

		this.setMinUsage(minUsage);
		this.setMaxUsage(maxUsage);
		
		this.taskStartTime = new ConcurrentSkipListMap<>();
		this.taskEndTime = new ConcurrentSkipListMap<>(); 
	}

	public ConcurrentSkipListMap<Date, Integer> getUsageDuplicaTraces() {
		return this.usageDuplicaTraces;
	}

	public ConcurrentSkipListMap<Date, Integer> getUsageNbrTraces() {
		return this.usageNbrTraces;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public synchronized int getNumber() {
		return this.number;
	}

	public void setNumber(int number) {
		synchronized(this) {
				this.number = number;
				this.usageNbrTraces.put(new Date(), number);
		}
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	//public synchronized boolean addResource() {
	public boolean addResource() {


		if (this.controleLimite()) {

			//int addNumber = this.getNumber() + 1;
			//this.setNumber(addNumber);

			this.number += 1;

			this.setNbrDuplica(this.nbrDuplica + 1);
			usageDuplicaTraces.put(new Date(), this.nbrDuplica);

			this.addReduceTimestamps = new Date();
			return true;
		} else {
			return false;
		}

	}

	//public synchronized boolean reduceResource() {
	public boolean reduceResource() {

		if (this.controleLimite()) {
			int currentNbr = this.getNumber();

			if (currentNbr - 1 > 0) {
				currentNbr -= 1;
				//this.setNumber(currentNbr);
				this.number = currentNbr;

				this.setNbrDuplica(this.getNbrDuplica() - 1);
				usageDuplicaTraces.put(new Date(), this.getNbrDuplica());

				this.addReduceTimestamps = new Date();
				return true;
			}
			return false;
		} else {
			return false;
		}

	}

	@Override
	public String toString() {
		return "Resource [id=" + id + ", name=" + name + ", number=" + number + "]";
	}

	public Long getDuration() {
		return calculResourceUsage();
	}

	public Long getDuration(int minutes) {
		return calculResourceUsage(minutes);
	}

	private Long calculResourceUsage() {
		ConcurrentSkipListMap<Date, Integer> traces = new ConcurrentSkipListMap<>();
		traces.putAll(this.getUsageDuplicaTraces());

		// Long duration = 0L;

		if (traces.size() == 1) {

			Long endtime = new Date().getTime();
			Long firstTime = traces.firstKey().getTime();

			Integer number = traces.firstEntry().getValue();
			duration = (endtime - firstTime) * number;

			System.out.println("duration:" + duration);

		} else {

			duration = 0L;
			Date temp = new Date();
			traces.put(new Date(), 0);
			ArrayList<Date> traceKeys = new ArrayList<>();
			ArrayList<Integer> traceValues = new ArrayList<>();

			for (Date tracekey : traces.keySet()) {
//				if (tracekey.getTime() > this.addReduceTimestamps.getTime()) {
				traceKeys.addAll(traces.keySet());
				traceValues.addAll(traces.values());
//				}
			}

			Long currentTime = traceKeys.get(0).getTime();

			for (int i = 1; i < traceKeys.size(); i++) {
				Long tempTime = traceKeys.get(i).getTime();

				duration += (tempTime - currentTime) * traceValues.get(i - 1);

				currentTime = tempTime;
			}
			traces.remove(temp);
		}

		return duration;

	}
	
	public ArrayList<Float> getResourceUsageTrace(){
		return this.resUsageTraces;
	}
	
	public double getResourceUsagePercent(int seconds) {
		double result = calculResourceUsagePercent(seconds);
		this.resUsageTraces.add((float)result);
		return result;
	}
	
	public double calculResourceUsagePercent(int seconds) {
		
		long currentTime = new Date().getTime();
		long lastPeriod = currentTime - seconds * 1000L;
		long totalTime = seconds * 1000L;
		
		
		ConcurrentSkipListMap<Date, Integer> allNbrUsageTraces = new ConcurrentSkipListMap<>();
		allNbrUsageTraces.putAll(this.getUsageNbrTraces());
		ConcurrentSkipListMap<Date, Integer> nbrUsageTraces = new ConcurrentSkipListMap<>();
		nbrUsageTraces.putAll(allNbrUsageTraces.subMap(new Date(lastPeriod), false, new Date(currentTime), false));
		
		
		ConcurrentSkipListMap<Date, Integer> allDuplicaTraces = new ConcurrentSkipListMap<>();
		allDuplicaTraces.putAll(this.getUsageDuplicaTraces());
		
		
		
		Date temp_date = allNbrUsageTraces.lowerKey(new Date(lastPeriod));

		if (temp_date != null) {
			//nbrUsageTraces.put(new Date(lastPeriod), allNbrUsageTraces.get(temp_date));

			totalTime = seconds * 1000L;
			
			lastPeriod = currentTime - seconds * 1000L;

		} else {

			//totalTime = currentTime - nbrUsageTraces.firstKey().getTime();
			totalTime = currentTime - nbrUsageTraces.firstKey().getTime();
			
			lastPeriod = totalTime;
		}
		
		System.out.println("Totaltime: " + totalTime);
		
		ConcurrentSkipListMap<Date, Integer> duplicaTraces = new ConcurrentSkipListMap<>();
		duplicaTraces.putAll(allDuplicaTraces.subMap(new Date(lastPeriod), false, new Date(currentTime), false));

		temp_date = allDuplicaTraces.lowerKey(new Date(lastPeriod));

		if (temp_date != null) {
			duplicaTraces.put(new Date(lastPeriod), allDuplicaTraces.get(temp_date));
		}
		
		CopyOnWriteArrayList<Date> duplicaKeys = new CopyOnWriteArrayList<>();
		CopyOnWriteArrayList<Integer> duplicaValues = new CopyOnWriteArrayList<>();

		duplicaKeys.addAll(duplicaTraces.keySet());
		duplicaValues.addAll(duplicaTraces.values());
		
		System.out.println("duplicaValues" + duplicaValues);
		
		CopyOnWriteArrayList<Date> startDate = new CopyOnWriteArrayList<>();
		CopyOnWriteArrayList<Date> endDate = new CopyOnWriteArrayList<>();
		
		ConcurrentSkipListMap<String, Date> taskStartTime = new ConcurrentSkipListMap<>();
		taskStartTime.putAll(this.getTaskStartTime());
		
		ConcurrentSkipListMap<String, Date> taskEndTime = new ConcurrentSkipListMap<>();
		taskEndTime.putAll(this.getTaskEndTime());
		
//		ConcurrentSkipListMap<String, Date> tStartTime;
//		ConcurrentSkipListMap<String, Date> tEndTime;
		
		for(String taskID : taskStartTime.keySet()) {
			if(!taskEndTime.containsKey(taskID)) {
				taskEndTime.put(taskID, new Date(currentTime));
			}
		}
		
		for(String taskID : taskStartTime.keySet()) {
			if(taskStartTime.get(taskID).getTime() <= lastPeriod) {
				if(taskEndTime.get(taskID).getTime() <= currentTime && taskEndTime.get(taskID).getTime() >= lastPeriod) {
					startDate.add(new Date(lastPeriod));
					endDate.add(taskEndTime.get(taskID));
				}
			}else {
				startDate.add(taskStartTime.get(taskID));
				endDate.add(taskEndTime.get(taskID));
			}
		}
		
		System.out.println("startDate("+ startDate.size()+ "):" + startDate);
		System.out.println("endDate("+ endDate.size()+ "):" + endDate);
		
		double totalUsage = 0.0;
		for(int i = 0; i < endDate.size(); i++) {
			totalUsage  += (endDate.get(i).getTime() - startDate.get(i).getTime()) * 1.0 / 1000L;
		}
		System.out.println("total usage time:" + totalUsage);
		
		if (startDate.isEmpty()) {
			System.out.println(this.name + " is empty");
			return 0.0;
			// return this.lastUsageValue;
		} else {
			System.out.println(this.name + " is noempty");

			long cumul_result = 0L;
			
			CopyOnWriteArrayList<Date> temp_duration = new CopyOnWriteArrayList<>();
			CopyOnWriteArrayList<Integer> temp_replicas = new CopyOnWriteArrayList<>();

			// FIXME Index: 23, Size: 22
			for (int i = 0; i < startDate.size(); i++) {

				if (duplicaTraces.subMap(startDate.get(i), false, endDate.get(i), false).isEmpty()) {

					temp_duration.add(startDate.get(i));
					temp_duration.add(endDate.get(i));
					
					if (duplicaTraces.containsKey(startDate.get(i))){
						temp_replicas.add(duplicaTraces.get(startDate.get(i)));
						
						temp_replicas.add(duplicaTraces.get(startDate.get(i)));
						
					}else {
						
						//if (duplicaTraces.lowerKey(startDate.get(i)) != null) {

							temp_replicas.add(duplicaTraces.get(duplicaTraces.lowerKey(startDate.get(i))));
							
							temp_replicas.add(duplicaTraces.get(duplicaTraces.lowerKey(startDate.get(i))));
//						} else {
//							temp_replicas.add(duplicaTraces.get(duplicaTraces.higherKey(startDate.get(i))));
//							
//							temp_replicas.add(duplicaTraces.get(duplicaTraces.higherKey(startDate.get(i))));
//						}
					}
					//System.out.println("++size:" + (temp_replicas.size() *  2 == temp_duration.size()));

				} else {

					ConcurrentSkipListMap<Date, Integer> temp_treeMap = new ConcurrentSkipListMap<>();
					temp_treeMap.putAll(duplicaTraces.subMap(startDate.get(i), false, endDate.get(i), false));
					
					temp_duration.add(startDate.get(i));

					for (Date key : temp_treeMap.keySet()) {
						temp_duration.add(key);
//						temp_replicas.add(temp_treeMap.get(key));
//						temp_replicas.add(temp_treeMap.get(key));
						temp_replicas.add(duplicaTraces.get(duplicaTraces.lowerKey(key)));
						temp_replicas.add(duplicaTraces.get(duplicaTraces.lowerKey(key)));

						temp_duration.add(key);
						temp_replicas.add(temp_treeMap.get(key));
						temp_replicas.add(temp_treeMap.get(key));
						//temp_replicas.add(duplicaTraces.get(duplicaTraces.lowerKey(key)));
						//temp_replicas.add(duplicaTraces.get(duplicaTraces.lowerKey(key)));
					}
					temp_duration.add(endDate.get(i));
					
//					if (temp_treeMap.size() == 1) {
//						temp_duration.add(startDate.get(i));
//						temp_duration.add(temp_treeMap.firstKey());
//						temp_replicas.add(temp_treeMap.get(temp_treeMap.firstKey()));
//
//						temp_duration.add(temp_treeMap.firstKey());
//						temp_duration.add(endDate.get(i));
//						temp_replicas.add(temp_treeMap.get(temp_treeMap.firstKey()));
//
//					} else {
//						temp_duration.add(startDate.get(i));
//
//						for (Date key : temp_treeMap.keySet()) {
//							temp_duration.add(key);
//							temp_replicas.add(temp_treeMap.get(key));
//
//							temp_duration.add(key);
//							temp_replicas.add(temp_treeMap.get(key));
//						}
//						temp_duration.add(endDate.get(i));
//					}

				}
			}
			
//			System.out.println("Outside_size:" + (temp_replicas.size() *  2 == temp_duration.size()));
			System.out.println("temp_duration(" + temp_duration.size() + "):" + temp_duration);
			System.out.println("temp_replicas(" + temp_replicas.size() + "):" + temp_replicas);
//			
////			int decart = temp_replicas.size() - temp_duration.size()/2;
////			for(int i=0; i< decart; i++) {
////				temp_replicas.remove(temp_replicas.size() - 1);
////			}
			
			Map<Integer, ArrayList<Date>> temp_map = new HashMap<>();
			for(int i =0; i < temp_replicas.size(); i++) {
				if(temp_map.keySet().contains(temp_replicas.get(i))) {
					temp_map.get(temp_replicas.get(i)).add(temp_duration.get(i));
				}else {
					ArrayList<Date> init_date = new ArrayList<>();
					init_date.add(temp_duration.get(i));
					temp_map.put(temp_replicas.get(i), init_date);
				}
			}
			temp_duration.clear();
			temp_replicas.clear();
			
			for(Integer i: temp_map.keySet()) {
				for(Date d: temp_map.get(i)) {
					temp_duration.add(d);
					temp_replicas.add(i);
				}
			}
			
			System.out.println("temp_duration(" + temp_duration.size() + "):" + temp_duration);
			System.out.println("temp_replicas(" + temp_replicas.size() + "):" + temp_replicas);
			
			if(new HashSet<Integer>(temp_replicas).size() == 1) {
				System.out.println(totalUsage * 1000 / (totalTime * temp_replicas.get(0)));
				if(totalUsage * 1000 / (totalTime * temp_replicas.get(0)) > 1.0) {
					return 1.0;
				}
				return totalUsage * 1000 / (totalTime * temp_replicas.get(0));
			}
			

			//double percent_factor = 0.0;
			//long startTime = temp_duration.get(0).getTime();
			//long startTime = lastPeriod;
			//long cumul_time = 0L;
			//long endTime = temp_duration.get(1).getTime();
			
			CopyOnWriteArrayList<Double> final_result = new CopyOnWriteArrayList<>();
			
			CopyOnWriteArrayList<Integer> index_start = new CopyOnWriteArrayList<>();
			index_start.add(0);
			CopyOnWriteArrayList<Integer> index_end = new CopyOnWriteArrayList<>();
			
			
			CopyOnWriteArrayList<Long> cumul_time_list = new CopyOnWriteArrayList<>();
			CopyOnWriteArrayList<Long> start_time_list = new CopyOnWriteArrayList<>();
			CopyOnWriteArrayList<Long> end_time_list = new CopyOnWriteArrayList<>();
			CopyOnWriteArrayList<Integer> replica_list = new CopyOnWriteArrayList<>();
			
			for(int i = 0; i < temp_replicas.size() - 1; i++) {
				if(temp_replicas.get(i) != temp_replicas.get(i+1)) {
					index_end.add(i);
					index_start.add(i+1);
				}
			}
			index_end.add(temp_replicas.size() - 1);
			
			for(int i =0; i < index_start.size(); i++) {
				double percent_factor = 0.0;
				long cumul_time = 0L;
				long start_time = 0L;
				long end_time = 0L;
				for(int j = index_start.get(i); j < index_end.get(i); j +=2) {
					cumul_time += (temp_duration.get(j+1).getTime() - temp_duration.get(j).getTime());
				}
				
				System.out.println("cumul time:" + cumul_time);
				if(i == 0) {
					start_time = lastPeriod;
				}else {
					start_time = temp_duration.get(index_start.get(i)).getTime();
				}
				end_time = temp_duration.get(index_end.get(i)).getTime();
				percent_factor = (end_time - start_time) * 1.0 / totalTime; 
				
				//FIX Error
				percent_factor = 1.0;
				
				System.out.println("start_time:" + start_time);
				System.out.println("end_time:" + end_time);
				System.out.println("percent_factor:" + percent_factor);
				
				//final_result.add((cumul_time * percent_factor) / temp_replicas.get(index_end.get(i)));
				
				start_time_list.add(start_time);
				end_time_list.add(end_time);
				cumul_time_list.add(cumul_time);
				replica_list.add(temp_replicas.get(index_end.get(i)));
			}
			
			System.out.println("-------------Filtrage-----------------");
			
			Map<Long, ArrayList<Integer>> same_start_time_map = new HashMap<>();
			Map<Long, ArrayList<Integer>> same_end_time_map = new HashMap<>();
			for(int i = 0; i< start_time_list.size(); i++) {
				if(same_start_time_map.get(start_time_list.get(i)) != null) {
					same_start_time_map.get(start_time_list.get(i)).add(i);
				}
				
				ArrayList<Integer> temp_s_list = new ArrayList<>();
				temp_s_list.add(i);
				same_start_time_map.put(start_time_list.get(i), temp_s_list);
				
			}
			
			for(int i = 0; i< end_time_list.size(); i++) {
				if(same_end_time_map.get(end_time_list.get(i)) != null) {
					same_end_time_map.get(end_time_list.get(i)).add(i);
				}
				
				ArrayList<Integer> temp_e_list = new ArrayList<>();
				temp_e_list.add(i);
				same_end_time_map.put(end_time_list.get(i), temp_e_list);
			}
			
			System.out.println("same start:"  + same_start_time_map);
			System.out.println("same end:" + same_end_time_map);
			
			for(long start_time : same_start_time_map.keySet()) {
				long real_cumul_time = 0L;
				long real_start_time = start_time;
				long real_end_time = 0L;
				Integer real_replica = 0;
				double percent_factor = 0.0;
				
				if(same_start_time_map.get(start_time).size() > 1) {
					for(int i = 0; i < same_start_time_map.get(start_time).size(); i++) {
						
						if(end_time_list.get(i) >= real_end_time){
							real_end_time = end_time_list.get(i);
						}
						real_cumul_time += cumul_time_list.get(i);
						real_replica = replica_list.get(i);
					}
					
					System.out.println("cumul time:" + real_cumul_time);
					System.out.println("start_time:" + real_start_time);
					System.out.println("end_time:" + real_end_time);
					
					percent_factor = (real_end_time - real_start_time) * 1.0 / totalTime;
					
					// Fix error
					percent_factor = 1.0;
					
					System.out.println("percent_factor:" + percent_factor);
					
					final_result.add((real_cumul_time * percent_factor) / real_replica);
				}
			}
			
			
			
			for(long end_time : same_end_time_map.keySet()) {
				long real_cumul_time = 0L;
				long real_start_time = Long.MAX_VALUE;
				long real_end_time = end_time;
				Integer real_replica = 0;
				double percent_factor = 0.0;
				
				if(same_end_time_map.get(end_time).size() > 1) {
					for(int i = 0; i < same_end_time_map.get(end_time).size(); i++) {
						
						if(start_time_list.get(i) < real_start_time){
							real_start_time = start_time_list.get(i);
						}
						real_cumul_time += cumul_time_list.get(i);
						real_replica = replica_list.get(i);
					}
					
					System.out.println("cumul time:" + real_cumul_time);
					System.out.println("start_time:" + real_start_time);
					System.out.println("end_time:" + real_end_time);
					
					percent_factor = (real_end_time - real_start_time) * 1.0 / totalTime;
					
					//Fix error
					percent_factor = 1.0;
					
					System.out.println("percent_factor:" + percent_factor);
					
					final_result.add((real_cumul_time * percent_factor) / real_replica);

				}
			}
			
			if(final_result.size() == 0) {
				for(int i = 0; i< start_time_list.size(); i++) {
					double percent_factor = (end_time_list.get(i) - start_time_list.get(i)) * 1.0 / totalTime;
					System.out.println("cumul time:" + cumul_time_list.get(i));
					System.out.println("start_time:" + start_time_list.get(i));
					System.out.println("end_time:" + end_time_list.get(i));
					
					// Fix error
					percent_factor  = 1.0;
					
					System.out.println("percent_factor:" + percent_factor);
					final_result.add((cumul_time_list.get(i) * percent_factor) / replica_list.get(i));
				}
				
			}
			
			

			double final_res = 0.0;
			for (double i : final_result) {
				final_res += i;
			}

			if ((final_res / totalTime) >= 1.0) {
				System.out.println("1.0* :" + (final_res / totalTime));
				return 1.0;
			}
			System.out.println(final_res / totalTime);
			
			
			return final_res / totalTime;
		}
		
	}
	
	
	public double calculResourceUsagePercentOld(int seconds) {

		long currentTime = new Date().getTime();
		long lastPeriod = currentTime - seconds * 1000L;
		long totalTime = seconds * 1000L;

		ConcurrentSkipListMap<Date, Integer> allNbrUsageTraces = new ConcurrentSkipListMap<>();
		allNbrUsageTraces.putAll(this.getUsageNbrTraces());
		
		ConcurrentSkipListMap<Date, Integer> allDuplicaTraces = new ConcurrentSkipListMap<>();
		allDuplicaTraces.putAll(this.getUsageDuplicaTraces());

		ConcurrentSkipListMap<Date, Integer> nbrUsageTraces = new ConcurrentSkipListMap<>();
		nbrUsageTraces.putAll(allNbrUsageTraces.subMap(new Date(lastPeriod), false, new Date(currentTime), false));

		Date temp_date = allNbrUsageTraces.lowerKey(new Date(lastPeriod));

		if (temp_date != null) {
			//nbrUsageTraces.put(new Date(lastPeriod), allNbrUsageTraces.get(temp_date));

			totalTime = seconds * 1000L;
			
			lastPeriod = currentTime - seconds * 1000L;

		} else {

			//totalTime = currentTime - nbrUsageTraces.firstKey().getTime();
			totalTime = currentTime - nbrUsageTraces.firstKey().getTime();
			
			lastPeriod = totalTime;
		}
		
		System.out.println("*****Totaltime: " + totalTime);
		
		

		ConcurrentSkipListMap<Date, Integer> duplicaTraces = new ConcurrentSkipListMap<>();
		duplicaTraces.putAll(allDuplicaTraces.subMap(new Date(lastPeriod), false, new Date(currentTime), false));

		temp_date = allDuplicaTraces.lowerKey(new Date(lastPeriod));

		if (temp_date != null) {
			duplicaTraces.put(new Date(lastPeriod), allDuplicaTraces.get(temp_date));
		}

		CopyOnWriteArrayList<Date> nbrUsageKeys = new CopyOnWriteArrayList<>();
		CopyOnWriteArrayList<Integer> nbrUsageValues = new CopyOnWriteArrayList<>();

		nbrUsageKeys.addAll(nbrUsageTraces.keySet());
		nbrUsageValues.addAll(nbrUsageTraces.values());
		
		System.out.println("nbrUsageValues" + nbrUsageValues);
		
		
		CopyOnWriteArrayList<Date> nbrUsageKeys_temp = new CopyOnWriteArrayList<>();
		nbrUsageKeys_temp.addAll(nbrUsageKeys);
		CopyOnWriteArrayList<Integer> nbrUsageValues_temp = new CopyOnWriteArrayList<>();
		nbrUsageValues_temp.addAll(nbrUsageValues);
		if(nbrUsageValues.size() > 0) {
			int value_temp = nbrUsageValues.get(0);
			for(int i=1; i< nbrUsageValues.size(); i++) {
				if(value_temp == nbrUsageValues.get(i)) {
					nbrUsageKeys_temp.remove(i);
					nbrUsageValues_temp.remove(i);
					
				}else {
					value_temp = nbrUsageValues.get(i);
				}
			}
		}
		
		if(nbrUsageValues.size() != nbrUsageValues_temp.size()) {
			nbrUsageKeys.clear();
			nbrUsageValues.clear();
			nbrUsageKeys.addAll(nbrUsageKeys_temp);
			nbrUsageValues.addAll(nbrUsageValues_temp);
			
		}
		
		
		CopyOnWriteArrayList<Date> duplicaKeys = new CopyOnWriteArrayList<>();
		CopyOnWriteArrayList<Integer> duplicaValues = new CopyOnWriteArrayList<>();

		duplicaKeys.addAll(duplicaTraces.keySet());
		duplicaValues.addAll(duplicaTraces.values());
		
		System.out.println("duplicaValues" + duplicaValues);
		//
		//
		CopyOnWriteArrayList<Date> startDate = new CopyOnWriteArrayList<>();
		CopyOnWriteArrayList<Date> endDate = new CopyOnWriteArrayList<>();
		
		//FIXME
		int usingNbr = 0;
		if(nbrUsageValues.size() > 0) {
			usingNbr = duplicaValues.get(0) - nbrUsageValues.get(0);
		}
		//FIXME (21/09)
//		else {
//			usingNbr = duplicaValues.get(0);
//			//nbrUsageValues.add(duplicaValues.get(0));
//		}
				
		for (int i = 0; i < usingNbr; i++) {
			if(nbrUsageKeys.size() != 0) {
				startDate.add(nbrUsageKeys.get(0));
			}else {
				startDate.add(new Date(lastPeriod));
			}
			
		}
		
		System.out.println("list:" + nbrUsageValues);
		
		int a =0;
		if(nbrUsageValues.size()>0) {
			a = nbrUsageValues.get(0);
		}
		
		for (int i = 1; i < nbrUsageValues.size(); i++) {
			if (a == nbrUsageValues.get(i) + 1) {
				startDate.add(nbrUsageKeys.get(i));
			} else if (a == nbrUsageValues.get(i) - 1) {
				endDate.add(nbrUsageKeys.get(i));
			}else if(a > nbrUsageValues.get(i) + 1) {
				
				for(int j = 0; j < a - nbrUsageValues.get(i) - 1; j++) {
					endDate.add(nbrUsageKeys.get(i));
				}
				
			}else if (a < nbrUsageValues.get(i) - 1) {
				
				for(int j = 0; j < nbrUsageValues.get(i) - 1 - a; j++) {
					startDate.add(nbrUsageKeys.get(i));
				}
				
				
			}
			else if  (a == nbrUsageValues.get(i)) {
				//startDate.add(nbrUsageKeys.get(i));
				//endDate.add(nbrUsageKeys.get(i));
				continue;
			}
			a = nbrUsageValues.get(i);
		}

		System.out.println("Before:***************************");
		System.out.println("startDate("+ startDate.size() + "):" + startDate);
		System.out.println("endDate("+ endDate.size() + "):" + endDate);

		int addNumber = startDate.size() - endDate.size();
		for (int i = 0; i < addNumber; i++) {
			endDate.add(new Date(currentTime));
		}
		
		System.out.println("After:***************************");
		System.out.println("startDate("+ startDate.size() + "):" + startDate);
		System.out.println("endDate("+ endDate.size() + "):" + endDate);
		
		
		
		CopyOnWriteArrayList<Date> temp_list_start = new CopyOnWriteArrayList<>();
		CopyOnWriteArrayList<Date> temp_list = new CopyOnWriteArrayList<>();

		for (int i = 0; i < startDate.size(); i++) {
			if (startDate.get(i).getTime() < endDate.get(i).getTime()) {
				temp_list_start.add(startDate.get(i));
				temp_list.add(endDate.get(i));
			}
		}

		if (temp_list_start.size() == temp_list.size()) {
			endDate.clear();
			endDate.addAll(temp_list);
			startDate.clear();
			startDate.addAll(temp_list_start);

		}

		addNumber = startDate.size() - endDate.size();

		for (int i = 0; i < addNumber; i++) {
			endDate.add(new Date(currentTime));
		}
		
		addNumber = endDate.size() - startDate.size();
		for (int i = 0; i < addNumber; i++) {
			startDate.add(endDate.get(startDate.size() - 1));
		}

		System.out.println("final:***************************");
		System.out.println("startDate("+ startDate.size() + "):" + startDate);
		System.out.println("endDate("+ endDate.size() + "):" + endDate);
		Collections.sort(startDate);
		Collections.sort(endDate);
		
		double totalUsage = 0.0;
		for(int i = 0; i < endDate.size(); i++) {
			totalUsage  += (endDate.get(i).getTime() - startDate.get(i).getTime()) * 1.0 / 1000L;
		}
		System.out.println("total usage time:" + totalUsage);

		if (startDate.isEmpty()) {
			System.out.println(this.name + " is empty");
			return 0.0;
			// return this.lastUsageValue;
		} else {
			System.out.println(this.name + " is noempty");

			long cumul_result = 0L;
//			if (duplicaTraces.size() == 1) {
//				System.out.println("duplicaTrace size:" + duplicaTraces.size());
//				
//				for (int i = 0; i < startDate.size(); i++) {
//					cumul_result += (endDate.get(i).getTime() - startDate.get(i).getTime());
//				}
//				System.out.println(cumul_result * 1.0 / (totalTime * duplicaTraces.get(duplicaTraces.firstKey())));
////				if (cumul_result * 1.0 / (totalTime * duplicaTraces.get(duplicaTraces.firstKey())) > 1.0) {
////					return 1.0;
////				}
//				return cumul_result * 1.0 / (totalTime * duplicaTraces.get(duplicaTraces.firstKey()));
//			}

			// changer the replica

			CopyOnWriteArrayList<Date> temp_duration = new CopyOnWriteArrayList<>();
			CopyOnWriteArrayList<Integer> temp_replicas = new CopyOnWriteArrayList<>();

			// FIXME Index: 23, Size: 22
			for (int i = 0; i < startDate.size(); i++) {

				if (duplicaTraces.subMap(startDate.get(i), false, endDate.get(i), false).isEmpty()) {

					temp_duration.add(startDate.get(i));
					temp_duration.add(endDate.get(i));
					
					if (duplicaTraces.containsKey(startDate.get(i))){
						temp_replicas.add(duplicaTraces.get(startDate.get(i)));
						
						temp_replicas.add(duplicaTraces.get(startDate.get(i)));
						
					}else {
						
						//if (duplicaTraces.lowerKey(startDate.get(i)) != null) {

							temp_replicas.add(duplicaTraces.get(duplicaTraces.lowerKey(startDate.get(i))));
							
							temp_replicas.add(duplicaTraces.get(duplicaTraces.lowerKey(startDate.get(i))));
//						} else {
//							temp_replicas.add(duplicaTraces.get(duplicaTraces.higherKey(startDate.get(i))));
//							
//							temp_replicas.add(duplicaTraces.get(duplicaTraces.higherKey(startDate.get(i))));
//						}
					}
					//System.out.println("++size:" + (temp_replicas.size() *  2 == temp_duration.size()));

				} else {

					ConcurrentSkipListMap<Date, Integer> temp_treeMap = new ConcurrentSkipListMap<>();
					temp_treeMap.putAll(duplicaTraces.subMap(startDate.get(i), false, endDate.get(i), false));
					
					temp_duration.add(startDate.get(i));

					for (Date key : temp_treeMap.keySet()) {
						temp_duration.add(key);
//						temp_replicas.add(temp_treeMap.get(key));
//						temp_replicas.add(temp_treeMap.get(key));
						temp_replicas.add(duplicaTraces.get(duplicaTraces.lowerKey(key)));
						temp_replicas.add(duplicaTraces.get(duplicaTraces.lowerKey(key)));

						temp_duration.add(key);
						temp_replicas.add(temp_treeMap.get(key));
						temp_replicas.add(temp_treeMap.get(key));
						//temp_replicas.add(duplicaTraces.get(duplicaTraces.lowerKey(key)));
						//temp_replicas.add(duplicaTraces.get(duplicaTraces.lowerKey(key)));
					}
					temp_duration.add(endDate.get(i));
					
//					if (temp_treeMap.size() == 1) {
//						temp_duration.add(startDate.get(i));
//						temp_duration.add(temp_treeMap.firstKey());
//						temp_replicas.add(temp_treeMap.get(temp_treeMap.firstKey()));
//
//						temp_duration.add(temp_treeMap.firstKey());
//						temp_duration.add(endDate.get(i));
//						temp_replicas.add(temp_treeMap.get(temp_treeMap.firstKey()));
//
//					} else {
//						temp_duration.add(startDate.get(i));
//
//						for (Date key : temp_treeMap.keySet()) {
//							temp_duration.add(key);
//							temp_replicas.add(temp_treeMap.get(key));
//
//							temp_duration.add(key);
//							temp_replicas.add(temp_treeMap.get(key));
//						}
//						temp_duration.add(endDate.get(i));
//					}

				}
			}
			
//			System.out.println("Outside_size:" + (temp_replicas.size() *  2 == temp_duration.size()));
			System.out.println("temp_duration(" + temp_duration.size() + "):" + temp_duration);
			System.out.println("temp_replicas(" + temp_replicas.size() + "):" + temp_replicas);
//			
////			int decart = temp_replicas.size() - temp_duration.size()/2;
////			for(int i=0; i< decart; i++) {
////				temp_replicas.remove(temp_replicas.size() - 1);
////			}
			
			Map<Integer, ArrayList<Date>> temp_map = new HashMap<>();
			for(int i =0; i < temp_replicas.size(); i++) {
				if(temp_map.keySet().contains(temp_replicas.get(i))) {
					temp_map.get(temp_replicas.get(i)).add(temp_duration.get(i));
				}else {
					ArrayList<Date> init_date = new ArrayList<>();
					init_date.add(temp_duration.get(i));
					temp_map.put(temp_replicas.get(i), init_date);
				}
			}
			temp_duration.clear();
			temp_replicas.clear();
			
			for(Integer i: temp_map.keySet()) {
				for(Date d: temp_map.get(i)) {
					temp_duration.add(d);
					temp_replicas.add(i);
				}
			}
			
			System.out.println("temp_duration(" + temp_duration.size() + "):" + temp_duration);
			System.out.println("temp_replicas(" + temp_replicas.size() + "):" + temp_replicas);
			
			if(new HashSet<Integer>(temp_replicas).size() == 1) {
				System.out.println(totalUsage * 1000 / (totalTime * temp_replicas.get(0)));
				if(totalUsage * 1000 / (totalTime * temp_replicas.get(0)) > 1.0) {
					return 1.0;
				}
				return totalUsage * 1000 / (totalTime * temp_replicas.get(0));
			}
			

			//double percent_factor = 0.0;
			//long startTime = temp_duration.get(0).getTime();
			//long startTime = lastPeriod;
			//long cumul_time = 0L;
			//long endTime = temp_duration.get(1).getTime();
			
			CopyOnWriteArrayList<Double> final_result = new CopyOnWriteArrayList<>();
			
			CopyOnWriteArrayList<Integer> index_start = new CopyOnWriteArrayList<>();
			index_start.add(0);
			CopyOnWriteArrayList<Integer> index_end = new CopyOnWriteArrayList<>();
			
			
			CopyOnWriteArrayList<Long> cumul_time_list = new CopyOnWriteArrayList<>();
			CopyOnWriteArrayList<Long> start_time_list = new CopyOnWriteArrayList<>();
			CopyOnWriteArrayList<Long> end_time_list = new CopyOnWriteArrayList<>();
			CopyOnWriteArrayList<Integer> replica_list = new CopyOnWriteArrayList<>();
			
			for(int i = 0; i < temp_replicas.size() - 1; i++) {
				if(temp_replicas.get(i) != temp_replicas.get(i+1)) {
					index_end.add(i);
					index_start.add(i+1);
				}
			}
			index_end.add(temp_replicas.size() - 1);
			
			for(int i =0; i < index_start.size(); i++) {
				double percent_factor = 0.0;
				long cumul_time = 0L;
				long start_time = 0L;
				long end_time = 0L;
				for(int j = index_start.get(i); j < index_end.get(i); j +=2) {
					cumul_time += (temp_duration.get(j+1).getTime() - temp_duration.get(j).getTime());
				}
				
				System.out.println("cumul time:" + cumul_time);
				if(i == 0) {
					start_time = lastPeriod;
				}else {
					start_time = temp_duration.get(index_start.get(i)).getTime();
				}
				end_time = temp_duration.get(index_end.get(i)).getTime();
				percent_factor = (end_time - start_time) * 1.0 / totalTime; 
				
				//Fix error
				percent_factor = 1.0;
				
				System.out.println("start_time:" + start_time);
				System.out.println("end_time:" + end_time);
				System.out.println("percent_factor:" + percent_factor);
				
				//final_result.add((cumul_time * percent_factor) / temp_replicas.get(index_end.get(i)));
				
				start_time_list.add(start_time);
				end_time_list.add(end_time);
				cumul_time_list.add(cumul_time);
				replica_list.add(temp_replicas.get(index_end.get(i)));
			}
			
			System.out.println("-------------Filtrage-----------------");
			
			Map<Long, ArrayList<Integer>> same_start_time_map = new HashMap<>();
			Map<Long, ArrayList<Integer>> same_end_time_map = new HashMap<>();
			for(int i = 0; i< start_time_list.size(); i++) {
				if(same_start_time_map.get(start_time_list.get(i)) != null) {
					same_start_time_map.get(start_time_list.get(i)).add(i);
				}
				
				ArrayList<Integer> temp_s_list = new ArrayList<>();
				temp_s_list.add(i);
				same_start_time_map.put(start_time_list.get(i), temp_s_list);
				
			}
			
			for(int i = 0; i< end_time_list.size(); i++) {
				if(same_end_time_map.get(end_time_list.get(i)) != null) {
					same_end_time_map.get(end_time_list.get(i)).add(i);
				}
				
				ArrayList<Integer> temp_e_list = new ArrayList<>();
				temp_e_list.add(i);
				same_end_time_map.put(end_time_list.get(i), temp_e_list);
			}
			
			System.out.println("same start:"  + same_start_time_map);
			System.out.println("same end:" + same_end_time_map);
			
			for(long start_time : same_start_time_map.keySet()) {
				long real_cumul_time = 0L;
				long real_start_time = start_time;
				long real_end_time = 0L;
				Integer real_replica = 0;
				double percent_factor = 0.0;
				
				if(same_start_time_map.get(start_time).size() > 1) {
					for(int i = 0; i < same_start_time_map.get(start_time).size(); i++) {
						
						if(end_time_list.get(i) >= real_end_time){
							real_end_time = end_time_list.get(i);
						}
						real_cumul_time += cumul_time_list.get(i);
						real_replica = replica_list.get(i);
					}
					
					System.out.println("cumul time:" + real_cumul_time);
					System.out.println("start_time:" + real_start_time);
					System.out.println("end_time:" + real_end_time);
					
					percent_factor = (real_end_time - real_start_time) * 1.0 / totalTime;
					
					System.out.println("percent_factor:" + percent_factor);
					
					final_result.add((real_cumul_time * percent_factor) / real_replica);
				}
			}
			
			
			
			for(long end_time : same_end_time_map.keySet()) {
				long real_cumul_time = 0L;
				long real_start_time = Long.MAX_VALUE;
				long real_end_time = end_time;
				Integer real_replica = 0;
				double percent_factor = 0.0;
				
				if(same_end_time_map.get(end_time).size() > 1) {
					for(int i = 0; i < same_end_time_map.get(end_time).size(); i++) {
						
						if(start_time_list.get(i) < real_start_time){
							real_start_time = start_time_list.get(i);
						}
						real_cumul_time += cumul_time_list.get(i);
						real_replica = replica_list.get(i);
					}
					
					System.out.println("cumul time:" + real_cumul_time);
					System.out.println("start_time:" + real_start_time);
					System.out.println("end_time:" + real_end_time);
					
					percent_factor = (real_end_time - real_start_time) * 1.0 / totalTime;
					
					System.out.println("percent_factor:" + percent_factor);
					
					final_result.add((real_cumul_time * percent_factor) / real_replica);

				}
			}
			
			if(final_result.size() == 0) {
				for(int i = 0; i< start_time_list.size(); i++) {
					double percent_factor = (end_time_list.get(i) - start_time_list.get(i)) * 1.0 / totalTime;
					System.out.println("cumul time:" + cumul_time_list.get(i));
					System.out.println("start_time:" + start_time_list.get(i));
					System.out.println("end_time:" + end_time_list.get(i));
					
					System.out.println("percent_factor:" + percent_factor);
					final_result.add((cumul_time_list.get(i) * percent_factor) / replica_list.get(i));
				}
				
			}
			
			

			double final_res = 0.0;
			for (double i : final_result) {
				final_res += i;
			}

			if ((final_res / totalTime) >= 1.0) {
				System.out.println("1.0* :" + (final_res / totalTime));
				return 1.0;
			}
			System.out.println(final_res / totalTime);
			
			
			return final_res / totalTime;

//			long startTime = startDate.get(0).getTime();
//			long endTime;
//			long cumul_result = 0L;
//			int dup = 0;
//			
//			ArrayList<ArrayList<Long>> temp_duration = new ArrayList<>(); 
//			
//			ArrayList<Double> results = new ArrayList<>();
//			
//			for(int i = 0; i < startDate.size(); i++) {
//				
//				if(endDate.get(i).getTime() - startDate.get(i).getTime() < 0) {
//					continue;
//				}
//				
//				if (duplicaTraces.subMap(startDate.get(i), true, endDate.get(i), true).isEmpty()) {
//					
//					System.out.println("No change in the number of replicas!");
//					endTime = endDate.get(i).getTime();
//					
//					if(duplicaTraces.lowerKey((startDate.get(i))) != null) {
//						dup = duplicaTraces.get(duplicaTraces.lowerKey((startDate.get(i))));
//					}else {
//						dup = duplicaTraces.get(startDate.get(i));
//					}
//					
//					cumul_result += (endDate.get(i).getTime() - startDate.get(i).getTime());
//				
//					
//					double temp_result = ((endTime - startTime) * dup) * 1.0 / totalTime;
//					System.out.println("cumul_result: " + cumul_result + ", replica: " + dup + ", duration:" + temp_result);
//					results.add(temp_result * cumul_result);
//					
//				}else {
//					TreeMap<Date, Integer> temp = new ConcurrentSkipListMap<>();
//					temp.putAll(duplicaTraces.subMap(startDate.get(i), true, endDate.get(i), true));
//					for(int j = 0; j < temp.size() ; j++) {
//						
//						endTime = temp.get(j);
//						double temp_result = ((endTime - startTime) * dup) * 1.0 / totalTime;
//						results.add(temp_result * cumul_result);
//						dup = duplicaTraces.get(duplicaTraces.lowerKey(new Date(temp.get(j))));
//					}
//					
//				}
//			}
//
//			double final_results = 0.0;
//			for(double i: results) {
//				final_results += i;
//			}
//			
//			System.out.println(final_results);
//			return final_results;

		}

		// return 1.0;

//		long cumul_result = 0L;
//		
//		for (int i = 0; i < startDate.size(); i++) {
//			
//			if(endDate.get(i).getTime() - startDate.get(i).getTime() < 0) {
//				continue;
//			}
//			if (duplicaTraces.subMap(startDate.get(i), true, endDate.get(i), true).isEmpty()) {
//				
//				int dup;
//				if(duplicaTraces.lowerKey((startDate.get(i))) != null) {
//					dup = duplicaTraces.get(duplicaTraces.lowerKey((startDate.get(i))));
//				}else {
//					dup = duplicaTraces.get(startDate.get(i));
//				}
//				//int dup = duplicaTraces.get(duplicaTraces.lowerKey((startDate.get(i))));
//				//cumul_result += (endDate.get(i).getTime() - startDate.get(i).getTime()) / dup;
//				cumul_result += (endDate.get(i).getTime() - startDate.get(i).getTime()) / dup;
//
//			} else {
//
////				if(duplicaTraces.lowerKey(startDate.get(i)) != null) {
////					int dup_left = duplicaTraces.get(duplicaTraces.lowerKey(startDate.get(i)));
////					
////					if(duplicaTraces.lowerKey(startDate.get(i)).getTime() > lastPeriod) {
////						cumul_result += (startDate.get(i).getTime() - duplicaTraces.lowerKey(startDate.get(i)).getTime()) / dup_left;
////					}else {
////						cumul_result += (startDate.get(i).getTime() - lastPeriod) / dup_left;
////					}
////					
////				}else {
////					int dup_left = duplicaTraces.get(duplicaTraces.higherKey(startDate.get(i)));
////					
////					cumul_result += (duplicaTraces.higherKey(startDate.get(i)).getTime() - startDate.get(i).getTime()) / dup_left;
////				}
//
//				if (duplicaTraces.higherKey(startDate.get(i)) != null) {
//					int dup_right = duplicaTraces.get(duplicaTraces.higherKey(startDate.get(i)));
//					
////					cumul_result += (endDate.get(i).getTime() - duplicaTraces.higherKey(startDate.get(i)).getTime())
////							/ dup_right;
//					
//					double factor = (endDate.get(i).getTime() - duplicaTraces.higherKey(startDate.get(i)).getTime())* 1.0 / (endDate.get(i).getTime() - startDate.get(i).getTime());
//					cumul_result += ((endDate.get(i).getTime() - duplicaTraces.higherKey(startDate.get(i)).getTime()) * factor)
//							/ dup_right;
//					
//					
//
//					if (duplicaTraces.lowerKey(startDate.get(i)) != null) {
//						int dup_left = duplicaTraces.get(duplicaTraces.lowerKey(startDate.get(i)));
////						cumul_result += (duplicaTraces.higherKey(startDate.get(i)).getTime()
////								- startDate.get(i).getTime()) / dup_left;
//						factor = (duplicaTraces.higherKey(startDate.get(i)).getTime() - startDate.get(i).getTime())*1.0 / (endDate.get(i).getTime() - startDate.get(i).getTime());
//						cumul_result += (duplicaTraces.higherKey(startDate.get(i)).getTime()
//								- startDate.get(i).getTime()) * factor / dup_left;
//						
//					} else {
////						cumul_result += (duplicaTraces.higherKey(startDate.get(i)).getTime()
////								- startDate.get(i).getTime()) / dup_right;
//						factor = (duplicaTraces.higherKey(startDate.get(i)).getTime() - startDate.get(i).getTime())*1.0 / (endDate.get(i).getTime() - startDate.get(i).getTime());
//						cumul_result += (duplicaTraces.higherKey(startDate.get(i)).getTime()
//								- startDate.get(i).getTime()) / dup_right;
//					}
//					
//				} else {
//					//cumul_result += (endDate.get(i).getTime() - startDate.get(i).getTime()) / this.nbrDuplica;
//					
//					cumul_result += (endDate.get(i).getTime() - startDate.get(i).getTime()) / this.nbrDuplica;
//				}
//			}
//
//			// cumul_result += endDate.get(i).getTime() - startDate.get(i).getTime();
//		}
//
//		System.out.println(this.name);
//		System.out.println(cumul_result * 1.0 / totalTime);
//
//		if (cumul_result * 1.0 / totalTime > 1) {
//			this.lastUsageValue = 1.0;
//			return 1.0;
//		}
//		
//		if((cumul_result * 1.0 / totalTime) <= 0){
//			return this.lastUsageValue;
//		}
//		this.lastUsageValue = cumul_result * 1.0 / totalTime;
//		
//		return cumul_result * 1.0 / totalTime;

	}

	private Long calculResourceUsage(int minutes) {
		TreeMap<Date, Integer> traces = new TreeMap<>();
		traces.putAll(this.getUsageDuplicaTraces());

		// Long duration = 0L;

		if (traces.size() == 1) {

			Long endtime = new Date().getTime();
			Long firstTime = traces.firstKey().getTime();

			Integer number = traces.firstEntry().getValue();

			duration = (endtime - firstTime) * number;

			Long duration_temp = minutes * 60000L * number;

			if (duration < duration_temp) {
				System.out.println("duration:" + duration);
				return duration;
			} else {
				return duration_temp;
			}

		} else {

			duration = 0L;
			Date temp = new Date();

			traces.put(temp, 0);

			Long condition = temp.getTime() - minutes * 60000L;

			ArrayList<Date> traceKeys = new ArrayList<>();
			ArrayList<Integer> traceValues = new ArrayList<>();

			// Date tempDate = new Date();
			int tempValue = 0;
			for (Date tracekey : traces.keySet()) {
				if (tracekey.getTime() < condition) {
					// tempDate = tracekey;
					tempValue = traces.get(tracekey);

				} else {
					break;
				}
			}
			if (tempValue != 0) {
				traceKeys.add(new Date(condition));
				traceValues.add(tempValue);
			}

			for (Date tracekey : traces.keySet()) {
				if (tracekey.getTime() >= condition) {
					traceKeys.add(tracekey);
					traceValues.add(traces.get(tracekey));
				}
			}

			Long currentTime = traceKeys.get(0).getTime();

			Long realDuration = 0L;

			for (int i = 1; i < traceKeys.size(); i++) {
				Long tempTime = traceKeys.get(i).getTime();

				duration += (tempTime - currentTime) * traceValues.get(i - 1);
				realDuration += (tempTime - currentTime);

				currentTime = tempTime;
			}
			traces.remove(temp);

			System.out.println("realDuration: " + realDuration);
			return duration;
		}

		// System.out.println("*******:" + duration);

		// return duration;

	}

	private boolean controleLimite() {
		// minutes
		// Long limiteTime = this.controleDuration * 60000L;

		// seconds
		Long limiteTime = this.controleDuration * 1000L;
		if ((new Date().getTime() - this.addReduceTimestamps.getTime()) >= limiteTime) {
			return true;
		} else {
			return false;
		}

	}

	private boolean controleLimite(int minutes) {
		Long limiteTime = this.controleDuration * minutes * 60000L;
		if ((new Date().getTime() - this.addReduceTimestamps.getTime()) >= limiteTime) {
			return true;
		} else {
			return false;
		}

	}
	
	//TODO
	public long getResourceCost() {
		long resCost = 0L;
		long durationTime =0L;
		Date startD = this.getUsageDuplicaTraces().firstKey();
		Date endD = new Date();
		durationTime = endD.getTime() - startD.getTime();
		
		ConcurrentSkipListMap<String, Date> taskStartTime = new ConcurrentSkipListMap<String, Date>();
		taskStartTime.putAll(this.taskStartTime);
		ConcurrentSkipListMap<String, Date> taskEndTime = new ConcurrentSkipListMap<String, Date>(); 
		taskEndTime.putAll(this.taskEndTime);
		for(String taskID: taskStartTime.keySet()) {
			if(!taskEndTime.containsKey(taskID)) {
				taskEndTime.put(taskID, new Date());
			}
		}
		//long 
		
		long realTime = 0L;
		
		for(String taskID: taskStartTime.keySet()) {
			realTime += taskEndTime.get(taskID).getTime() - taskStartTime.get(taskID).getTime();
		}
		
		long passive_cost = ((durationTime - realTime) / 1000) * this.passiveCost;
		long active_cost =  (realTime * this.activeCost)/1000;
		
		resCost = passive_cost + active_cost;
		
		return resCost;
	}

	public int getNbrDuplica() {
		return nbrDuplica;
	}

	public void setNbrDuplica(int nbrDuplica) {
		this.nbrDuplica = nbrDuplica;
	}

	public double getMinUsage() {
		return minUsage;
	}

	public void setMinUsage(double minUsage) {
		this.minUsage = minUsage;
	}

	public double getMaxUsage() {
		return maxUsage;
	}

	public void setMaxUsage(double maxUsage) {
		this.maxUsage = maxUsage;
	}

}