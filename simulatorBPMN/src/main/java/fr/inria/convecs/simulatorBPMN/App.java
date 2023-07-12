package fr.inria.convecs.simulatorBPMN;

import java.util.Random;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
//        System.out.println( "Hello World!" );
//        Random random = new Random(50);
//        for(int i =0; i< 8; i++) {
//        	System.out.print("" + random.nextInt(10) + ", ");
//        }
//        
//        System.out.println();
//        System.out.println("------------------------------");
//        Random random2 = new Random(50);
//        for(int i =0; i< 8; i++) {
//        	System.out.print("" +  random2.nextInt(10) + ", ");
//        }
//        System.out.println();
//        System.out.println("------------------------------");
//        new App().testApp(random);
//        
//        System.out.println();
//        new App().testApp(random);
        
    		Random random = new Random(50);
    		double lambda = -0.5;
    		Random random2 = new Random(50);
    		Random random3 = new Random(50);
    		Random random4 = new Random(50);
    		new App().getNext(random, random3,100);
    		System.out.println(" ==== ");
    		new App().getNext(random2, random4,100);
    	
    }
    
    public void testApp(Random random) {
    	for(int i =0; i< 8; i++) {
        	System.out.print("" +  random.nextInt(10) + ", ");
        }
    }
    
    public void getNext(Random rand, Random randNumber, int number) {
    	
    	for(int i=0; i< number; i++) {
    		System.out.print(1+ rand.nextInt(10) + ",");
    	}
    	System.out.println(" ==== ");
    	for(int i=0; i< number; i++) {
    		System.out.print(1+ randNumber.nextInt(3) + ",");
    	}
    	System.out.println();
    }
}
