package fr.inria.convecs.sbpmn.visualisation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import fr.inria.convecs.sbpmn.calcul.CalculExecutionTimeByInstances;
import fr.inria.convecs.sbpmn.calcul.CalculTotalCost;
import fr.inria.convecs.sbpmn.common.BPMNResources;
import fr.inria.convecs.sbpmn.common.Resource;
import fr.inria.convecs.sbpmn.deploy.BPMNProcess;
import fr.inria.convecs.sbpmn.files.CSVWriter;

@SuppressWarnings("serial")
public class RealTimeResourceCostChart extends ChartPanel implements Runnable {
	private static TimeSeries timeSeries;
	// private long nbrInstances = 0;
	private BPMNProcess bpmnProcess;
	private BPMNResources bpmnResources;
	private ArrayList<Resource> resources;
	private CSVWriter writer;
	private int period;
	private long lastResult = 0L;

	public void SetProcessKey(BPMNProcess processKey) {
		this.bpmnProcess = processKey;
	}
	
	public void setCSVWrtier(CSVWriter writer) {
		this.writer = writer;
	}
	
	public RealTimeResourceCostChart(String chartContent, String title, String yaxisName, ArrayList<Resource> resources, int period) {
		super(createChart(chartContent, title, yaxisName));
		
		this.resources = new ArrayList<>();
		this.resources.addAll(resources);
		this.period = period;
	}

	private static JFreeChart createChart(String chartContent, String title, String yaxisName) {
		// 创建时序图对象
		timeSeries = new TimeSeries(chartContent);
		TimeSeriesCollection timeseriescollection = new TimeSeriesCollection(timeSeries);
		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(title, "RealTime", yaxisName, timeseriescollection,
				true, true, false);
		XYPlot xyplot = jfreechart.getXYPlot();
		// 纵坐标设定
		ValueAxis valueaxis = xyplot.getDomainAxis();
		// 自动设置数据轴数据范围
		valueaxis.setAutoRange(true);
		// 数据轴固定数据范围 30s
		// valueaxis.setFixedAutoRange(30000D);

		valueaxis = xyplot.getRangeAxis();
		// valueaxis.setRange(0.0D,200D);

		xyplot.setBackgroundPaint(Color.WHITE);

		return jfreechart;
	}

	public void run() {
		while (true) {
			try {
				timeSeries.add(new Millisecond(), randomNum());
				
				try {
					this.writer.write(new String[] {new Timestamp(System.currentTimeMillis()).toString() , Long.toString(randomNum())});
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Thread.sleep((this.period / 6) * 1000);
			} catch (InterruptedException e) {
			}
		}
	}

	private long randomNum() {

		CalculTotalCost calculTotalCost = new CalculTotalCost(this.bpmnProcess, this.resources);

		return calculTotalCost.getTotalCost();

	}

}
