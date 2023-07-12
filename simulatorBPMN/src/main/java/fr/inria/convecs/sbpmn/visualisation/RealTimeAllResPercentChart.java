package fr.inria.convecs.sbpmn.visualisation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.google.protobuf.Duration;

import fr.inria.convecs.sbpmn.calcul.CalculExecutionTimeByTasks;
import fr.inria.convecs.sbpmn.calcul.CalculResourcesUsageByTasks;
import fr.inria.convecs.sbpmn.common.Resource;
import fr.inria.convecs.sbpmn.deploy.BPMNProcess;
import fr.inria.convecs.sbpmn.files.CSVWriter;
import fr.inria.convecs.sbpmn.prediction.ResourceUsagePredict;

@SuppressWarnings("serial")
public class RealTimeAllResPercentChart extends ChartPanel implements Runnable {
	//private static TimeSeries timeSeries;
	private static HashMap<Resource, TimeSeries> timeSeriesList;
	//private long nbrInstances = 0;
	private BPMNProcess bpmnProcess;
	private CSVWriter writer;
	private int period;
	
	public  void setProcessKey(BPMNProcess bpmnProces) {
		this.bpmnProcess  = bpmnProces;
	}
	
	public void setCSVWrtier(CSVWriter writer) {
		this.writer = writer;
	}

	public RealTimeAllResPercentChart(String chartContent, String title, String yaxisName, ArrayList<Resource> resources, int period) {
		
		super(createChart(chartContent, title, yaxisName, resources));
		this.period = period;
	}

	private static JFreeChart createChart(String chartContent, String title, String yaxisName, ArrayList<Resource> resources) {
		//创建时序图对象
		timeSeriesList = new HashMap<>();
		for (Resource keyRes: resources) {
			timeSeriesList.put(keyRes, new TimeSeries(keyRes.getName()));
		}
		
		TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
		for(Resource resKey : timeSeriesList.keySet()) {
			timeseriescollection.addSeries(timeSeriesList.get(resKey));
		}
		
		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(title, "Realtime", yaxisName, timeseriescollection,
				true, true, false);
		XYPlot xyplot = jfreechart.getXYPlot();
		//纵坐标设定
		ValueAxis valueaxis = xyplot.getDomainAxis();
		//自动设置数据轴数据范围
		valueaxis.setAutoRange(true);
		//数据轴固定数据范围 30s
		//valueaxis.setFixedAutoRange(30000D);

		valueaxis = xyplot.getRangeAxis();
		//valueaxis.setRange(0.0D,200D); 
		
		NumberAxis rangeAxis = (NumberAxis) xyplot.getRangeAxis();
		DecimalFormat pctFormat = new DecimalFormat("#%");
		rangeAxis.setNumberFormatOverride(pctFormat);

		xyplot.setBackgroundPaint(Color.WHITE);
		
		
		xyplot.getRenderer().setSeriesPaint(0, Color.BLUE);
		xyplot.getRenderer().setSeriesPaint(1, Color.MAGENTA);
		xyplot.getRenderer().setSeriesPaint(2, Color.GREEN);
		xyplot.getRenderer().setSeriesPaint(3, Color.RED);
		xyplot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {10.0f, 6.0f}, 0.0f));
		xyplot.getRenderer().setSeriesStroke(
	            1, new BasicStroke(
	                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
	                1.0f, new float[] {5.0f, 6.0f}, 0.0f
	            )
	        );
		xyplot.getRenderer().setSeriesStroke(
	            2, new BasicStroke(
	                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
	                1.0f, new float[] {1.0f, 6.0f}, 0.0f
	            )
	        );
		
		
		
		return jfreechart;
	}

	public void run() {
		while (true) {
			try {
				for(Resource resKey : timeSeriesList.keySet()) {
					//timeSeriesList.get(resKey).add(new Millisecond(), randomNum(resKey));
					
					//without prediction
					timeSeriesList.get(resKey).add(new Millisecond(), randomNum_test(resKey));
					
					//with prediction
					//timeSeriesList.get(resKey).add(new Millisecond(), randomNum_with_prediction(resKey));
					
					
					try {
						//this.writer.write(new String[] {resKey.getId(), resKey.getName(), new Timestamp(System.currentTimeMillis()).toString() , Double.toString(randomNum(resKey))});
						
						//without prediction
						this.writer.write(new String[] {resKey.getId(), resKey.getName(), new Timestamp(System.currentTimeMillis()).toString() , Double.toString(randomNum_test(resKey))});
						
						//with prediction
						//this.writer.write(new String[] {resKey.getId(), resKey.getName(), new Timestamp(System.currentTimeMillis()).toString() , Double.toString(randomNum_with_prediction(resKey))});
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//timeSeries.add(new Millisecond(), randomNum());
				Thread.sleep((this.period / 6) * 1000);
			} catch (InterruptedException e) {
			}
		}
	}

	private double randomNum(Resource keyRes) {
		//CalculExecTime calExecTime = new CalculExecTime(this.processKey);
		keyRes.getId();
		
		int old_period = 2;
		
		Long duation  = keyRes.getDuration(1);
		CalculResourcesUsageByTasks usageRes = new CalculResourcesUsageByTasks(this.bpmnProcess);
		
		if (usageRes.calculTotalRes().containsKey(keyRes.getId())) {
			
			double result = usageRes.calculTotalRes(1).get(keyRes.getId()).doubleValue() * 1000 / duation.doubleValue();
			
			if(result > keyRes.getMaxUsage()) {
				keyRes.addResource();
			}else if (result < keyRes.getMinUsage()) {
				keyRes.reduceResource();
			}
			
			return usageRes.calculTotalRes(1).get(keyRes.getId()).doubleValue() * 1000 / duation.doubleValue();
			
		}else {
			return 0.0;
		}
		
	}
	
	//TODO test
	private double randomNum_test (Resource keyRes) {
		
		double result = keyRes.getResourceUsagePercent(this.period);
		if(result > keyRes.getMaxUsage()) {
			keyRes.addResource();
		}else if (result < keyRes.getMinUsage()) {
			keyRes.reduceResource();
		}
		//return keyRes.getResourceUsagePercent(this.period);
		return result;
	}
	
	
	private double randomNum_with_prediction (Resource keyRes) {
		
		double result = keyRes.getResourceUsagePercent(this.period);
		
		// trainDateNumber is number of the data to training the prediction model
		int trainDataNumber = 200;
		
		if(keyRes.getResourceUsageTrace().size() < trainDataNumber) {
			
			if(result > keyRes.getMaxUsage()) {
				keyRes.addResource();
			}else if (result < keyRes.getMinUsage()) {
				keyRes.reduceResource();
			}
			
		}else {
			
			ResourceUsagePredict predict = new ResourceUsagePredict(keyRes);
			double predictResult = predict.getNextResourceUsage();
			
			System.out.println("*Predicted results: ( " + predictResult + " )*");
			
			if(result > keyRes.getMaxUsage()) {
				keyRes.addResource();
			}else if (result < keyRes.getMinUsage()) {
				keyRes.reduceResource();
			}else {
				
				if(predictResult > keyRes.getMaxUsage()) {
					keyRes.addResource();
				}else if (predictResult < keyRes.getMinUsage()) {
					keyRes.reduceResource();
				}
			}
			
			
		}
		
		//return keyRes.getResourceUsagePercent(this.period);
		return result;
	}
	
	
	
	
}
