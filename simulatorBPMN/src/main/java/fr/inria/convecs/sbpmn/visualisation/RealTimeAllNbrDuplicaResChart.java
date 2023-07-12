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
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import fr.inria.convecs.sbpmn.common.Resource;
import fr.inria.convecs.sbpmn.deploy.BPMNProcess;
import fr.inria.convecs.sbpmn.files.CSVWriter;

@SuppressWarnings("serial")
public class RealTimeAllNbrDuplicaResChart extends ChartPanel implements Runnable {
	//private static TimeSeries timeSeries;
	private static HashMap<Resource, TimeSeries> timeSeriesList;
	//private long nbrInstances = 0;
	private BPMNProcess bpmnProcess;
	private CSVWriter writer;
	private int period;
	
	public  void SetProcessKey(BPMNProcess bpmnProcess) {
		this.bpmnProcess  = bpmnProcess;
	}
	
	public void setCSVWrtier(CSVWriter writer) {
		this.writer = writer;
	}
	
	public RealTimeAllNbrDuplicaResChart(String chartContent, String title, String yaxisName, ArrayList<Resource> resources, int period) {
		
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
		//valueaxis.setAutoRange(true);
		valueaxis.setAutoRange(true);
		//数据轴固定数据范围 30s
		//valueaxis.setFixedAutoRange(30000D);
		
		valueaxis = xyplot.getRangeAxis();
		
//		NumberAxis yAxis = (NumberAxis) xyplot.getRangeAxis();
//		DecimalFormat format = new DecimalFormat("0");
//		yAxis.setNumberFormatOverride(format);
		
		//valueaxis.setRange(0D, 10D); 
		//jfreechart.setBackgroundPaint(Color.WHITE);
		// background
		xyplot.setBackgroundPaint(Color.WHITE);
//		xyplot.setRangeGridlinesVisible(true);
//		xyplot.setRangeGridlinePaint(Color.gray);
		
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
					timeSeriesList.get(resKey).add(new Millisecond(), randomNum(resKey));
					
					try {
						this.writer.write(new String[] {resKey.getId(), resKey.getName(), new Timestamp(System.currentTimeMillis()).toString() , Double.toString(randomNum(resKey))});
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

	//private long randomNum() {
	private int randomNum(Resource resource) {
		//CalculExecTime calExecTime = new CalculExecTime(this.processKey);
		return resource.getNbrDuplica();
		
	}
	
}
