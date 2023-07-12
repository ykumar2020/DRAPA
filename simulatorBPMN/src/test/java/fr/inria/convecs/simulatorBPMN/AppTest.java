package fr.inria.convecs.simulatorBPMN;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import com.alibaba.fastjson.JSON;

import fr.inria.convecs.sbpmn.calcul.CalculExecutionTimeByInstances;
import fr.inria.convecs.sbpmn.deploy.BPMNProcess;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
    
    @Test
    public void test01() {
    	CalculExecutionTimeByInstances executionTime = new CalculExecutionTimeByInstances(new BPMNProcess("Shipment", "Shipment"));
    	System.out.println("Average:"+ executionTime.getAverageExecutionTime());
    	System.out.println("Total:"+ executionTime.getTotalExecutionTime());
    	
    }
    
    @Test
    public void testPredictionModel() {
    	
    	System.out.println("Start：" + System.currentTimeMillis());
        long strTime = System.currentTimeMillis();
    	
    	Session session = SavedModelBundle.load(System.getProperty("user.dir") + "\\python\\res04model",
                "serve").session();
    	
    	//float[][][] input = {{{0.33f, 0.48f, 0.79f, 0.54f, 0.56f}}};
    	
    	float[][][] input = {{{0.33f, 0.48f, 0.79f, 0.54f, 0.65f}}, {{0.80f, 0.92f, 0.73f, 0.92f, 0.90f}}};
    	
    	System.out.println("input: \n" + JSON.toJSONString(input));
        Tensor inputTensor = Tensor.create(input);
//        Tensor resultTensor = session.runner()
//                .feed("serving_default_inputs:0", inputTensor)
//                .fetch("StatefulPartitionedCall:0")
//                .run().get(0);
        Tensor resultTensor = session.runner()
                .feed("serving_default_input_x_input:0", inputTensor)
                .fetch("StatefulPartitionedCall:0")
                .run().get(0);

        float[][] result = new float[input.length][1];
        resultTensor.copyTo(result);
        
        System.out.println("result: \n" + JSON.toJSONString(result));
        session.close();
        
        long endTime = System.currentTimeMillis();
//        System.out.println("end：" + System.currentTimeMillis());
        long time = endTime - strTime;
        System.out.println("TotalTime：" + time);
    }
    
    
    @Test
    public void testInputData() {
    	
    	
    	//float[][][] input = {{{0.33f, 0.48f, 0.79f, 0.54f, 0.65f}}};
    	
    	float[][][] input = new float[1][1][5];
    	//System.out.println(input[0][0][0]);
    	
    	System.out.println("result: \n" + JSON.toJSONString(input));
    	
    	input[0][0][0] = 0.25f;
    	
    	System.out.println("result: \n" + JSON.toJSONString(input));
    }
    
    
    @Test
    public void testPredictionModel1() {
    	
    	System.out.println("Start：" + System.currentTimeMillis());
        long strTime = System.currentTimeMillis();
    	
    	Session session = SavedModelBundle.load(System.getProperty("user.dir") + "\\python\\res04model",
                "serve").session();
    	
    	//float[][][] input = {{{0.33f, 0.48f, 0.79f, 0.54f, 0.56f}}};
    	
    	float[][][] input = {{{0.33f, 0.48f, 0.79f, 0.54f, 0.65f}}, {{0.80f, 0.92f, 0.73f, 0.92f, 0.90f}}};
    	
    	System.out.println("input: \n" + JSON.toJSONString(input));
        Tensor inputTensor = Tensor.create(input);
//        Tensor resultTensor = session.runner()
//                .feed("serving_default_inputs:0", inputTensor)
//                .fetch("StatefulPartitionedCall:0")
//                .run().get(0);
        Tensor resultTensor = session.runner()
                .feed("serving_default_lstm_input:0", inputTensor)
                .fetch("StatefulPartitionedCall:0")
                .run().get(0);

        float[][] result = new float[input.length][1];
        resultTensor.copyTo(result);
        
        System.out.println("result: \n" + JSON.toJSONString(result));
        session.close();
        
        long endTime = System.currentTimeMillis();
//        System.out.println("end：" + System.currentTimeMillis());
        long time = endTime - strTime;
        System.out.println("TotalTime：" + time);
        
        System.out.println(result[0][0]);
    }
    
    @Test
    public void getInputData() {
    	
    	ArrayList<Float> arrayList = new ArrayList<Float>();
    	arrayList.add(0.3f);
    	arrayList.add(0.2f);
//    	arrayList.add(0.4f);
//    	arrayList.add(0.5f);
//    	arrayList.add(0.5f);
//    	arrayList.add(0.3f);
//    	arrayList.add(0.2f);
//    	arrayList.add(0.4f);
    	
    	System.out.println(arrayList);
    	
    	int trainDataSize = 5;
    	
    	ArrayList<Float> arrayListResult = new ArrayList<Float>();
    	
    	if(arrayList.size() - trainDataSize >=0) {
    		System.out.println(">=");
    		
    		System.out.print(arrayList.subList(arrayList.size() - trainDataSize, arrayList.size()));
    		arrayListResult.addAll(arrayList.subList(arrayList.size() - trainDataSize, arrayList.size()));
    	}else {
    		
    		System.out.println("<");
    		System.out.print(arrayList.subList(0, arrayList.size()));
    		for(int i= 0; i< trainDataSize - arrayList.size(); i++) {
    			arrayListResult.add(0.0f);
    		}
    		arrayListResult.addAll(arrayList.subList(0, arrayList.size()));
    	}
    	System.out.println(arrayListResult);
    	
    	float[][][] input = new float[1][1][trainDataSize];
    	
    	for(int i =0; i<trainDataSize; i++) {
    		input[0][0][i] = arrayListResult.get(i);
    	}
    	
    	System.out.println("result: \n" + JSON.toJSONString(input));
    	
    	
    	float[][][] output = new float[1][1][trainDataSize];
    	for(int i =0; i<trainDataSize; i++) {
    		output[0][0][i] = input[0][0][i];
    	}
    	
    	
    	System.out.println("result: \n" + JSON.toJSONString(output));
    	
    	output[0][0][1] = 0.3f;
    	
    	System.out.println("result: \n" + JSON.toJSONString(input));
    	System.out.println("result: \n" + JSON.toJSONString(output));
    	
    }
}
