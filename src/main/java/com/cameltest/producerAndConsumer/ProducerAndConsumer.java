package com.cameltest.producerAndConsumer;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class ProducerAndConsumer {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();
        CamelContext contextWithProcess = new DefaultCamelContext();

        // Create new RouteBuilder and
        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start")
                    .to("seda:end");
            }
        });

        context.start();

        // Create producer, consumer and body
        String body = "String from Camel producer";

        ProducerTemplate producerTemplate = context.createProducerTemplate();
        producerTemplate.sendBody("direct:start", body);

        ConsumerTemplate consumerTemplate = context.createConsumerTemplate();
        String consumerBody = consumerTemplate.receiveBody("seda:end", String.class);

        System.out.println(consumerBody);
    }
}
