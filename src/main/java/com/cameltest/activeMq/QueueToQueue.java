package com.cameltest.activeMq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import javax.jms.ConnectionFactory;
import java.util.logging.Logger;

public class QueueToQueue {
    private static final Logger logger = Logger.getLogger(QueueToQueue.class.getName());

    public static void main(String[] args) {

        // Create context and ConnectionFactory
        CamelContext context = new DefaultCamelContext();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        final String queueName1 = "queue_name1";
        final String queueName2 = "queue_name2";
        final String activeMqUrl = "http://localhost:8161/admin/queues.jsp";

        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {
                    from("activemq:queue:" + queueName1).process(new Processor() {
                        @Override
                        public void process(Exchange exchange) {
                            String body = exchange.getIn().getBody(String.class);
                            body = body + " is a message from " + queueName1;
                            exchange.getOut().setBody(body);
                        }
                    }).to("activemq:queue:" + queueName2);
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

