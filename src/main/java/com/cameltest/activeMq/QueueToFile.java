package com.cameltest.activeMq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import javax.jms.ConnectionFactory;
import java.util.logging.Logger;

public class QueueToFile {
    private static final Logger logger = Logger.getLogger(QueueToFile.class.getName());

    public static void main(String[] args) {

        // Create context and ConnectionFactory
        CamelContext context = new DefaultCamelContext();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        final String queueName = "queue_name";
        final String activeMqUrl = "http://localhost:8161/admin/queues.jsp";
        final String folderName = "data/outboxFromQueue";

        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {
                    from("activemq:queue:" + queueName)
                        .to("file:"+ folderName +"?noop=true");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            logger.info("ActiveMQ on " + activeMqUrl);
            while (true) {
                context.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
