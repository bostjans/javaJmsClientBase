package com.stupica.queue;


import com.stupica.ConstGlobal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import javax.jms.MapMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayName("test11Producer")
public class TestClientProducer {

    String  qAddr = "tcp://mq01:61616";
    public  JmsClientBase objClient = null;


    @BeforeEach
    public void setUp() throws Exception {
        objClient = new JmsClientBase();
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Local variables
        int             iResult;

        if (objClient != null) {
            iResult = objClient.disconnect();
            assertEquals(ConstGlobal.RETURN_OK, iResult);
        }
    }


    @DisplayName("testProducer11")
    @Test
    public void testProducer11() throws Exception {
        // Local variables
        int             iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        System.out.println("--");
        System.out.println("Test: " + this.getClass().toString() + " - 11");

        iResult = objClient.initialize(qAddr, "unitTest.queue",
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

        iResult = objClient.initialize(qAddr, "unitTest.queue",
                objClient.iTypeProducer, "unitTest-" + this.getClass().toString());
        assertEquals(ConstGlobal.RETURN_OK, iResult);
        objClient.setMsgTTL(1000 * 60 * 12); // = 12 min;
        {
            MapMessage objMsg = objClient.getSession().createMapMessage();
            objMsg.setString("msg", "Hello");
            objMsg.setJMSType("Map");
            objClient.getProducer().send(objMsg);
            assertEquals(ConstGlobal.RETURN_OK, iResult);
        }
    }
}
