package com.stupica.queue;


import com.stupica.ConstGlobal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.MapMessage;

import static org.junit.Assert.assertEquals;


public class TestClientProducer {

    public JmsClient    objClient = null;


    @Before
    public void setUp() throws Exception {
        objClient = new JmsClient();
    }

    @After
    public void tearDown() throws Exception {
        // Local variables
        int             iResult;

        if (objClient != null) {
            iResult = objClient.disconnect();
            assertEquals(ConstGlobal.RETURN_OK, iResult);
        }
    }


    @Test
    public void testProducer11() throws Exception {
        // Local variables
        int             iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        System.out.println("--");
        System.out.println("Test: " + this.getClass().toString() + " - 11");

        iResult = objClient.initialize("tcp://localhost:61616", "unitTest.queue",
                objClient.iTypeProducer, "unitTest-" + this.getClass().toString());
        assertEquals(ConstGlobal.RETURN_OK, iResult);
    }

    @Test
    public void testProducer12() throws Exception {
        // Local variables
        int             iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        System.out.println("--");
        System.out.println("Test: " + this.getClass().toString() + " - 12");

        iResult = objClient.initialize("tcp://localhost:61616", "unitTest.queue",
                objClient.iTypeProducer, "unitTest-" + this.getClass().toString());
        assertEquals(ConstGlobal.RETURN_OK, iResult);
        objClient.setMsgTTL(1000 * 60 * 12); // = 12 min;

        MapMessage objMsg = objClient.getSession().createMapMessage();
        objMsg.setString("msg", "Hello");
        objMsg.setJMSType("Map");
        objClient.getProducer().send(objMsg);
    }
}