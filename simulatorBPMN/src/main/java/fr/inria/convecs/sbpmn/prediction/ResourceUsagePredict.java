package fr.inria.convecs.sbpmn.prediction;

import java.util.ArrayList;

import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import com.alibaba.fastjson.JSON;

import fr.inria.convecs.sbpmn.common.Resource;

public class ResourceUsagePredict {
	
	private Resource resouce;
	private int stepSize;
	
	public ResourceUsagePredict(Resource resource) {
		this.resouce = resource;
		this.stepSize = 5;
	}
	
	public ResourceUsagePredict(Resource resource, int stepSize) {
		this.resouce = resource;
		this.stepSize = stepSize;
	}
	
	private float[][][] getInputData(){
		
		ArrayList<Float> arrayList = new ArrayList<Float>();
		
		arrayList.addAll(this.resouce.getResourceUsageTrace());
		
		int trainDataSize = this.stepSize;
    	
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
    	
    	
    	float[][][] inputData = new float[1][1][trainDataSize];
    	
    	for(int i =0; i<trainDataSize; i++) {
    		inputData[0][0][i] = arrayListResult.get(i);
    	}
		
		return inputData;
	}
	
	public double getNextResourceUsage() {
		
		return this.calculNextResourceUsage();
	}
	
	private float calculNextResourceUsage() {
		
//		System.out.println("Start：" + System.currentTimeMillis());
//        long strTime = System.currentTimeMillis();
    	
    	Session session = SavedModelBundle.load(System.getProperty("user.dir") + "\\python\\"+ this.resouce.getId() + "model",
                "serve").session();
    	
    	//float[][][] input = {{{0.33f, 0.48f, 0.79f, 0.54f, 0.56f}}};
    	
    	//float[][][] input = {{{0.33f, 0.48f, 0.79f, 0.54f, 0.65f}}, {{0.80f, 0.92f, 0.73f, 0.92f, 0.90f}}};
    	
    	float[][][] input = new float[1][1][this.stepSize];
    	
    	for(int i = 0; i < stepSize; i++) {
    		input[0][0][i] = this.getInputData()[0][0][i];
    	}
    	
    	System.out.println("input: \n" + JSON.toJSONString(input));
        Tensor inputTensor = Tensor.create(input);
//        Tensor resultTensor = session.runner()
//                .feed("serving_default_inputs:0", inputTensor)
//                .fetch("StatefulPartitionedCall:0")
//                .run().get(0);
        
        /*How  to get the input and the output of the models ? 
         * .\res04model is File path of the prediction model
        Command: saved_model_cli show --dir .\res04model --all 
        */
        Tensor resultTensor = session.runner()
                .feed("serving_default_lstm_input:0", inputTensor)
                .fetch("StatefulPartitionedCall:0")
                .run().get(0);

        float[][] result = new float[input.length][1];
        resultTensor.copyTo(result);
        
        System.out.println("result: \n" + JSON.toJSONString(result));
        session.close();
        
//        long endTime = System.currentTimeMillis();
////        System.out.println("end：" + System.currentTimeMillis());
//        long time = endTime - strTime;
//        System.out.println("TotalTime：" + time);
        
        float singleResult =  result[0][0];
        
		return singleResult;
	}
}
