package fr.inria.convecs.sbpmn.visualisation;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JFrame;

import fr.inria.convecs.sbpmn.common.Resource;
import fr.inria.convecs.sbpmn.deploy.BPMNProcess;
import fr.inria.convecs.sbpmn.files.CSVWriter;

public class CreateChart {
	
	private BPMNProcess bpmnProcess;
	private ArrayList<Resource> resoures;
	private CSVWriter writer;
	private CSVWriter writerCost;
	private CSVWriter writerReplica;
	private CSVWriter writerEtime;
	private int period;
	
	public CreateChart(BPMNProcess bpmnProcess, ArrayList<Resource> resoures, int period) throws UnsupportedEncodingException, FileNotFoundException{
		this.bpmnProcess = bpmnProcess;
		this.resoures = new ArrayList<Resource>();
		this.resoures.addAll(resoures);	
		this.writer = new CSVWriter("usage.csv");
		this.writerCost = new CSVWriter("cost.csv");
		this.writerReplica = new CSVWriter("replica.csv");
		this.writerEtime = new CSVWriter("etime.csv");
		this.period = period;
		
	}
	
	public void showAETChart() {
		JFrame frame = new JFrame("AET Chart");
		//RealTimeChart rtcp = new RealTimeChart("Random Data", "Random", "Value");
		RealTimeChart rtcp = new RealTimeChart(" AET Value", "AET (Average Execution Time) ", "AET (seconds)", this.period);
		rtcp.SetProcessKey(this.bpmnProcess);
		rtcp.setCSVWrtier(this.writerEtime);
		new BorderLayout();
		frame.getContentPane().add(rtcp, BorderLayout.CENTER);
		//frame.setBounds(200, 120, 10, 80);
		//frame.pack();
		frame.setSize(500, 400);
		frame.setVisible(true);
		(new Thread(rtcp)).start();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				System.exit(0);
			}

		});
	}
		
	public void showNbrDuplicaResChart() {
		JFrame frame = new JFrame("Resources Nbr Chart");
		//RealTimeChart rtcp = new RealTimeChart("Random Data", "Random", "Value");
		RealTimeAllNbrDuplicaResChart rtcp = new RealTimeAllNbrDuplicaResChart("res", "Resources", "Value", this.resoures, this.period);
		
		rtcp.setCSVWrtier(this.writerReplica);
		new BorderLayout();
		frame.getContentPane().add(rtcp, BorderLayout.CENTER);
		//frame.setBounds(200, 120, 10, 80);
		//frame.pack();
		frame.setSize(500, 400);
		frame.setVisible(true);
		(new Thread(rtcp)).start();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				System.exit(0);
			}

		});
	}
	
	public void showResPercentChart() {
		JFrame frame = new JFrame("Resources Percent Chart");
		//RealTimeChart rtcp = new RealTimeChart("Random Data", "Random", "Value");
		RealTimeAllResPercentChart rtcp = new RealTimeAllResPercentChart("res", "Resources(%)", "Value", this.resoures, this.period);
		rtcp.setProcessKey(this.bpmnProcess);
		rtcp.setCSVWrtier(this.writer);
		new BorderLayout();
		frame.getContentPane().add(rtcp, BorderLayout.CENTER);
		//frame.setBounds(200, 120, 10, 80);
		//frame.pack();
		frame.setSize(500, 400);
		frame.setVisible(true);
		(new Thread(rtcp)).start();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				System.exit(0);
			}

		});
	}
	
	public void showCostChart() {
		JFrame frame = new JFrame("Cost Chart");
		//RealTimeChart rtcp = new RealTimeChart("Random Data", "Random", "Value");
		RealTimeResourceCostChart rtcp = new RealTimeResourceCostChart("Cost", "Cost", "Value", this.resoures, this.period);
		rtcp.SetProcessKey(this.bpmnProcess);
		rtcp.setCSVWrtier(this.writerCost);
		new BorderLayout();
		frame.getContentPane().add(rtcp, BorderLayout.CENTER);
		//frame.setBounds(200, 120, 10, 80);
		//frame.pack();
		frame.setSize(500, 400);
		frame.setVisible(true);
		(new Thread(rtcp)).start();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				System.exit(0);
			}

		});
	}
}
