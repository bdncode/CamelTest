package com.cameltest.activemq_test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import javax.jms.ConnectionFactory;

public class ActiveMqToActiveMq {

    public static void main(String[] args) throws Exception {

        // Create context and ConnectionFactory
        CamelContext context = new DefaultCamelContext();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        String queueName1 = "queue_name1";
        String queueName2 = "queue_name2";

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("activemq:queue:"+queueName1).process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String body = exchange.getIn().getBody(String.class);
                        body = body + " is a message from " + queueName1;
                        exchange.getOut().setBody(body);
                    }
                }).to("activemq:queue:"+queueName2);
            }
        });

        while (true) {
            context.start();
        }
    }
}

