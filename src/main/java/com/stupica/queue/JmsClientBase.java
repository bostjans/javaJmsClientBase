package com.stupica.queue;


import com.stupica.ConstGlobal;
import com.stupica.GlobalVar;

import javax.jms.*;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import java.util.*;
import java.util.logging.Logger;


/**
 * Created by bostjans on 24/03/19.
 */
public class JmsClientBase {
    // Variables
    //
    public final int iTypeConsumer = 1;
    public final int iTypeProducer = 2;
    public final int iTypeConsumerAndProducer = 3;
    public int iType = iTypeConsumer;

    protected int iSessionMode = Session.AUTO_ACKNOWLEDGE;

    protected int iMsgTTL = 1000 * 60 * 60 * 24 * 365;

    protected boolean bIsConnected = false;
    public boolean bIgnoreException = false;
    public boolean bShouldSetExceptionListener = true;

    /**
     * configuration parameters
     */
    public String sQueueAddr = "tcp://localhost:61616";
    public String sQueueName = "generic.queue";
    protected String sClientId = "programId";

    private String sMsgIdLast = null;

    private Connection      objJmsConnection;
    private Queue           objQueue = null;
    private Session         objJmsSession = null;
    private Destination     objJmsDestination = null;
    private MessageConsumer messageConsumer;
    private MessageProducer messageProducer;

    protected static Logger logger = Logger.getLogger(JmsClientBase.class.getName());


    /**
     * Object constructor
     * /
    public JmsClientBase() {
    } */


    /**
     * Method: initialize
     *
     * ..
     *
     * @return int iResult	1 = AllOK;
     */
    public int initialize(String asQueueAddr, String asQueueName, int aiType, String asClientId) {
        sQueueAddr = asQueueAddr;
        sQueueName = asQueueName;
        iType = aiType;
        sClientId = asClientId;
        return initialize();
    }

    /**
     * Method: initialize
     *
     * ..
     *
     * @return int iResult	1 = AllOK;
     */
    public int initialize() {
        // Local variables
        int             iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            iResult = connect();
        }
        return iResult;
    }


    /**
     * Method: setSessionMode
     *
     * ..
     */
    public void setSessionMode(int aiMode) {
        iSessionMode = aiMode;
    }

    /**
     * Method: setMsgTTL
     *
     * ..
     */
    public void setMsgTTL(int aiValue) {
        iMsgTTL = aiValue;
    }

    /**
     * Method: setClientId
     *
     * ..
     */
    public void setClientId(String asId) {
        sClientId = asId;
    }


    /**
     * Method: getMsgIdLast
     *
     * ..
     */
    public String getMsgIdLast() {
        return sMsgIdLast;
    }


    /**
     * Method: connect
     *
     * ..
     *
     * @return int iResult	1 = AllOK;
     */
    protected int connect() {
        // Local variables
        int                 iResult;
        Context             objIc = null;
        ConnectionFactory   connectionFactory = null;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory");
            //props.setProperty(Context.PROVIDER_URL, "vm://localhost");
            //props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            props.setProperty(Context.PROVIDER_URL, sQueueAddr);
            props.setProperty("queue/" + sQueueName, sQueueName);
            try {
                objIc = new InitialContext(props);
            } catch (Exception ex) {
                iResult = ConstGlobal.RETURN_ERROR;
                logger.severe("connect(): Could not create InitialContext!"
                        + " URI: " + sQueueAddr
                        + "; Msg.: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            try {
                //Now we'll look up the connection factory from which we can create
                //connections to myhost:5445:
                connectionFactory = (ConnectionFactory)objIc.lookup("ConnectionFactory");
            } catch (Exception ex) {
                iResult = ConstGlobal.RETURN_ERROR;
                logger.severe("connect(): Could not get ConnectionFactory object: JNDI!!"
                        + " URI: " + sQueueAddr
                        + "; Msg.: " + ex.getMessage());
                //
                try {
                    //InitialContext ctx = new InitialContext();
                    NamingEnumeration<NameClassPair> list = objIc.list("");
                    while (list.hasMore()) {
                        System.err.println(list.next().getName());
                    }
                } catch (Exception ex2) { }
                ex.printStackTrace();
            }
        }
        // Check previous step
//        if (iResult == ConstGlobal.RETURN_OK) {
//            try {
//                //Now we'll look up the connection factory from which we can create
//                //connections to myhost:5445:
//                connectionFactory = (ConnectionFactory)objIc.lookup("connectionFactory");
//            } catch (Exception ex) {
//                iResult = ConstGlobal.RETURN_ERROR;
//                logger.severe("connect(): Could not get ConnectionFactory object: JNDI!!"
//                        + " URI: " + sQueueAddr
//                        + "; Msg.: " + ex.getMessage());
//                ex.printStackTrace();
//            }
//        }

        // Check previous step
//        if (iResult == ConstGlobal.RETURN_OK) {
//            try {
//                //And look up the Queue:
//                //objQueue = (Queue) objIc.lookup("queue/" + sDestinationName);
//                objQueue = (Queue) objIc.lookup(sDestinationName);
//            } catch (Exception ex) {
//                iResult = ConstGlobal.RETURN_ERROR;
//                logger.severe("runBefore(): Could not get Queue object: JNDI!!"
//                        + " Object: " + "queue/" + sDestinationName
//                        + "; Msg.: " + ex.getMessage());
//                ex.printStackTrace();
//            }
//        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            // create a Connection
            try {
                objJmsConnection = connectionFactory.createConnection();
                objJmsConnection.setClientID(sClientId);
            } catch (Exception ex) {
                iResult = ConstGlobal.RETURN_ERROR;
                logger.severe("connect(): Could not Connect!"
                        + " URI: " + sQueueAddr
                        + "; Msg.: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            // create a Session
            try {
                objJmsSession = objJmsConnection.createSession(false, iSessionMode);
            } catch (Exception ex) {
                iResult = ConstGlobal.RETURN_ERROR;
                logger.severe("connect(): Could not create Session!"
                        + " URI: " + sQueueAddr
                        + "; Msg.: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            // create a Dest
            try {
                objJmsDestination = objJmsSession.createQueue(sQueueName);
            } catch (Exception ex) {
                iResult = ConstGlobal.RETURN_ERROR;
                logger.severe("connect(): Could not create Queue!"
                        + " Queue: " + sQueueName
                        + "; Msg.: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            // create a Message Consumer for receiving messages
            if ((iType == iTypeConsumer) || (iType == iTypeConsumerAndProducer)) {
                try {
                    if (objQueue != null) {
                        messageConsumer = objJmsSession.createConsumer(objQueue);
                    } else if (objJmsDestination != null) {
                        messageConsumer = objJmsSession.createConsumer(objJmsDestination);
                    }
                } catch (Exception ex) {
                    iResult = ConstGlobal.RETURN_ERROR;
                    logger.severe("connect(): Could not create Consumer!"
                            + " Queue: " + sQueueName
                            + "; Msg.: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            if ((iType == iTypeProducer) || (iType == iTypeConsumerAndProducer)) {
                // create a Message Producer for sending messages
                try {
                    messageProducer = objJmsSession.createProducer(objJmsDestination);
                    messageProducer.setTimeToLive(iMsgTTL);
                } catch (Exception ex) {
                    iResult = ConstGlobal.RETURN_ERROR;
                    logger.severe("connect(): Could not create Producer!"
                            + " Queue: " + sQueueName
                            + "; Msg.: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            if (bShouldSetExceptionListener) {
                try {
                    objJmsConnection.setExceptionListener(new ExceptionListener() {
                        public void onException(JMSException aobjEx) {
                            exceptionReactor(aobjEx);
                        }
                    });
                } catch (JMSException ex) {
                    iResult = ConstGlobal.RETURN_ERROR;
                    logger.severe("connect(): Could not set exceptionHandler!"
                            + " URI: " + sQueueAddr
                            + "; Queue: " + sQueueName
                            + "; Msg.: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            // start the connection in order to receive messages
            try {
                objJmsConnection.start();
                bIsConnected = true;
            } catch (Exception ex) {
                iResult = ConstGlobal.RETURN_ERROR;
                logger.severe("connect(): Could not start receiving messages!"
                        + " URI: " + sQueueAddr
                        + "; Queue: " + sQueueName
                        + "; Msg.: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        return iResult;
    }

    /**
     * Method: disconnect
     *
     * ..
     *
     * @return int iResult	1 = AllOK;
     */
    public int disconnect() {
        // Local variables
        int             iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            if (objJmsConnection != null) {
                try {
                    objJmsConnection.stop();
                } catch (Exception ex) {
                    if (!bIgnoreException) {
                        iResult = ConstGlobal.RETURN_ERROR;
                        logger.severe("disconnect(): Error at stopping JMS!"
                                + " URI: " + sQueueAddr
                                + "; Queue: " + sQueueName
                                + "; Msg.: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        }
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            if (objJmsSession != null) {
                try {
                    objJmsSession.close();
                } catch (Exception ex) {
                    iResult = ConstGlobal.RETURN_ERROR;
                    logger.severe("disconnect(): Error at closing Session!"
                            + " URI: " + sQueueAddr
                            + "; Queue: " + sQueueName
                            + "; Msg.: " + ex.getMessage());
                    if (!bIgnoreException) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            if (objJmsConnection != null) {
                try {
                    objJmsConnection.close();
                } catch (Exception ex) {
                    iResult = ConstGlobal.RETURN_ERROR;
                    logger.severe("disconnect(): Error at closing JMS connection!"
                            + " URI: " + sQueueAddr
                            + "; Queue: " + sQueueName
                            + "; Msg.: " + ex.getMessage());
                    if (!bIgnoreException) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        bIsConnected = false;
        return iResult;
    }

    /**
     * Method: reconnect
     *
     * ..
     *
     * @return int iResult	1 = AllOK;
     */
    public int reconnect() {
        // Local variables
        int             iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            iResult = disconnect();
        }
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            iResult = connect();
        }
        return iResult;
    }


    /**
     * Method: isConnected
     *
     * ..
     */
    public boolean isConnected() {
        // Initialization

        // Check ..
        if (objJmsConnection == null) {
            bIsConnected = false;
            return bIsConnected;
        }
        if (!bIsConnected) {
            return bIsConnected;
        }
        try {
            if (objJmsConnection.getMetaData() == null) {
                bIsConnected = false;
            }
        } catch (Exception ex) {
            logger.severe("isConnected(): Error at retrieve JMS MetaData!"
                    + " URI: " + sQueueAddr
                    + "; Queue: " + sQueueName
                    + "; Msg.: " + ex.getMessage());
            //ex.printStackTrace();
            bIsConnected = false;
        }
        return bIsConnected;
    }

    /**
     * Method: exceptionReactor
     *
     * ..
     */
    public void exceptionReactor(JMSException aobjEx) {
        // Local variables
        int         iResult;
        String      sMsg;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;
        sMsg = aobjEx.getMessage();

        // Check ..
        if (sMsg.toLowerCase().contains("disconnect"))
            bIsConnected = false;

        if (!bIgnoreException)
            logger.warning("exceptionReactor(): .. something went wrong!"
                + " URI: " + sQueueAddr
                + "; Queue: " + sQueueName
                + "; Msg.: " + sMsg);
    }


    /**
     * Method: getSession
     *
     * ..
     *
     * @return Session objJmsSession	notNull = AllOK;
     */
    protected Session getSession() {
        return objJmsSession;
    }

    /**
     * Method: getProducer
     *
     * ..
     *
     * @return MessageProducer messageProducer	notNull = AllOK;
     */
    protected MessageProducer getProducer() {
        return messageProducer;
    }


    /**
     * Method: receive
     *
     * Read ..
     *
     * @return Message objMessage	notNull = AllOK;
     */
    public synchronized Message receive(int aiQueueWaitTime) {
        // Local variables
        int             iResult;
        Message         objMessage = null;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        // Check ..
        if (messageConsumer == null) {
            iResult = ConstGlobal.RETURN_ERROR;
            logger.severe("receive(): Message Consumer is not ready!"
                    + " URI: " + sQueueAddr
                    + "; Queue: " + sQueueName
                    + "; Msg.: /");
        }
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            // read a message from the queue destination
            try {
                objMessage = messageConsumer.receive(aiQueueWaitTime);
            } catch (Exception ex) {
                iResult = ConstGlobal.RETURN_ERROR;
                logger.severe("receive(): Error at message receive!"
                        + " URI: " + sQueueAddr
                        + "; Queue: " + sQueueName
                        + "; Msg.: " + ex.getMessage());
                //ex.printStackTrace();
            }
            if (objMessage == null) {
                iResult = ConstGlobal.RETURN_NODATA;
            }
        }
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            try {
                sMsgIdLast = objMessage.getJMSMessageID();
            } catch (Exception ex) {
                iResult = ConstGlobal.RETURN_ERROR;
                logger.severe("receive(): Error at retrieve of message_ID!"
                        + " URI: " + sQueueAddr
                        + "; Queue: " + sQueueName
                        + "; Msg.: " + ex.getMessage());
            }
        }
        return objMessage;
    }

    /**
     * Method: receiveCommon
     *
     * Read ..
     *
     * @return Message objMessage	notNull = AllOK;
     */
    public Message receiveCommon(int aiQueueWaitTime) {
        // Local variables
        int             iResult;
        Message         objMessage = null;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        objMessage = receive(aiQueueWaitTime);
        if (GlobalVar.bIsModeVerbose) {
            if (objMessage == null) {
                logger.info("receiveCommon(): No data received! Continue ..");
            } else {
                logger.info("receiveCommon(): Data received. Continue ..");
            }
        }
        if (objMessage != null) {
            if (GlobalVar.bIsModeVerbose) {
                try {
                    System.out.println("= Message Receive - type: " + objMessage.getJMSType());
                } catch (Exception ex) {
                    iResult = ConstGlobal.RETURN_ERROR;
                    logger.severe("receiveCommon(): Error at message operation!"
                            + " Operation: getJMSType()"
                            + "; Msg.: " + ex.getMessage());
                }
            }
        }
        return objMessage;
    }


    /**
     * Method: recover
     *
     * Recover ..
     */
    public int recover() {
        // Local variables
        int             iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        try {
            objJmsSession.recover();
            //objJmsSession.rollback();
        } catch (Exception ex) {
            iResult = ConstGlobal.RETURN_ERROR;
            logger.severe("recover(): Error at message rollback!"
                    + " URI: " + sQueueAddr
                    + "; MsgId: " + sMsgIdLast
                    + "; Msg.: " + ex.getMessage());
        }
        return iResult;
    }
}
