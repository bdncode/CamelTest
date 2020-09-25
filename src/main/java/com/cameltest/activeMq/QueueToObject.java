package com.cameltest.activeMq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import javax.jms.ConnectionFactory;
import java.util.logging.Logger;

public class QueueToObject {
    private static final Logger logger = Logger.getLogger(QueueToObject.class.getName());

    public static void main(String[] args) throws Exception {

        // Create context and ConnectionFactory
        CamelContext context = new DefaultCamelContext();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        final String queueName = "queue_name";
        final String activeMqUrl = "http://localhost:8161/admin/queues.jsp";

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("activemq:queue:" + queueName)
                    .to("seda:end");
            }
        });

        logger.info("ActiveMQ on " + activeMqUrl);
        context.start();

        ConsumerTemplate consumerTemplate = context.createConsumerTemplate();
        String consumerBody = consumerTemplate.receiveBody("seda:end", String.class);

        logger.info(consumerBody);
    }
}
