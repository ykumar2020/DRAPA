package fr.inria.convecs.sbpmn.deploy;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class BPMNControllerTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        //assertTrue( true );
    	BPMNController bpmn = new BPMNController("Shipment", "Shipment");
    	assertTrue(bpmn.bpmnDeployment());
    	assertTrue(bpmn.generateInstances(1));
    }
}
