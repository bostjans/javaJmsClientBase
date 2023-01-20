package com.stupica.queue;


import com.stupica.ConstGlobal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.TextMessage;

import static org.junit.Assert.*;


public class TestClientConsumer {

    String  qAddr = "tcp://mq01:61616";
    public  JmsClientBase objClient = null;


    @Before
    public void setUp() throws Exception {
        objClient = new JmsClientBase();
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
    public void testConsumer01() throws Exception {
        // Local variables
        int             iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        System.out.println("--");
        System.out.println("Test: " + this.getClass().toString() + " - 01");

        iResult = objClient.initialize(qAddr);
        assertEquals(ConstGlobal.RETURN_OK, iResult);
        assertTrue(objClient.isConnected());
    }


    @Test
    public void testConsumer11() throws Exception {
        // Local variables
        int             iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        System.out.println("--");
        System.out.println("Test: " + this.getClass().toString() + " - 11");

        iResult = objClient.initialize(qAddr, "unitTest.queue",
                                        objClient.iTypeConsumer, "unitTest-" + this.getClass().toString());
        assertEquals(ConstGlobal.RETURN_OK, iResult);
    }

    @Test
    public void testConsumer12() throws Exception {
        // Local variables
        int             iResult;
        String          sMsgId;
        Message         objMessage = null;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        System.out.println("--");
        System.out.println("Test: " + this.getClass().toString() + " - 12");

        iResult = objClient.initialize(qAddr, "unitTest.queue",
                objClient.iTypeConsumer, "unitTest-" + this.getClass().toString());
        assertEquals(ConstGlobal.RETURN_OK, iResult);
        //objClient.setSessionMode(Session.CLIENT_ACKNOWLEDGE);

        // read a message from the queue destination
        System.out.print("< Receive message ..");
        objMessage = objClient.receive(3);
        System.out.println(" - -- --- ----> Receive message end.");
        assertNotNull(objMessage);

        try {
            sMsgId = objMessage.getJMSMessageID();
            System.out.println("= Message Receive - ID: " + sMsgId);
            System.out.println("= Message Receive - type: " + objMessage.getJMSType());
        } catch (Exception ex) {
            iResult = ConstGlobal.RETURN_ERROR;
            System.err.println("method(): Error at message operation!"
                    + " Operation: getJMSType()"
                    + "; Msg.: " + ex.getMessage());
        }
        if (objMessage instanceof TextMessage) {
            System.out.println("= Message Type: TextMessage");
        } else if (objMessage instanceof MapMessage) {
            System.out.println("= Message Type: MapMessage");
        } else {
            System.out.println("= Message of unknown Type!");
        }
    }
}
