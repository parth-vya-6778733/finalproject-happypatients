package com.sjsu.cs249.services;

import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.InitialContext;
import java.util.List;
import java.util.ArrayList;

public class MessageBroker {
    private static final Logger logger = Logger.getLogger(MessageBroker.class);

    public void sendMessage(String text) {
        try {
            InitialContext initCtx = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) initCtx.lookup("java:comp/env/jms/ConnectionFactory");
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer((Destination) initCtx.lookup("java:comp/env/jms/queue/MyQueue"));

            TextMessage testMessage = session.createTextMessage();
            testMessage.setText(text);
            testMessage.setStringProperty("aKey", "someRandomTestValue");
            producer.send(testMessage);
            logger.debug("Successfully sent message.");
        } catch (Exception e) {
            logger.error("Sending JMS message failed: "+e.getMessage(), e);
        }
    }

    public List<String> receiveMessages() {
        List<String> text = new ArrayList<String>();
        try {
            InitialContext initCtx = new InitialContext();
            QueueConnectionFactory connectionFactory = (QueueConnectionFactory) initCtx
                    .lookup("java:comp/env/jms/ConnectionFactory");
            QueueConnection queueConnection = connectionFactory.createQueueConnection();
            QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) initCtx.lookup("java:comp/env/jms/queue/MyQueue");
            QueueReceiver receiver = queueSession.createReceiver(queue);

            queueConnection.start();
            try {
                Message m = receiver.receive(1000);
                if (m != null && m instanceof TextMessage) {
                    TextMessage tm = (TextMessage) m;
                    text.add(tm.getText());
                    logger.debug(String.format("Received TextMessage with text '%s'.", text));
                } else {
                    logger.debug(String.format("No TextMessage received: '%s'", m));
                }
            } finally {
                queueSession.close();
                queueConnection.close();
            }
        } catch (Exception e) {
            logger.error("Receiving messages failed: " + e.getMessage(), e);
        }
        return text;
    }
}
