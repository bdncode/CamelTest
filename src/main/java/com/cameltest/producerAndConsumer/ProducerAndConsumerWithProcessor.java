package com.cameltest.producerAndConsumer;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class ProducerAndConsumerWithProcessor {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext contextWithProcess = new DefaultCamelContext();

        // Create new RouteBuilder and
        // add our route to the CamelContext
        // add the Processor between from and to
        contextWithProcess.addRoutes(new RouteBuilder() {
            public void configure() {
            from("direct:start").process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    String body = exchange.getIn().getBody(String.class);
                    body = body + " with process";
                    exchange.getOut().setBody(body);
                }
            }).to("seda:end");
            }
        });

        contextWithProcess.start();

        // Create producer, consumer and body
        String body = "String from Camel producer";

        ProducerTemplate producerTemplate = contextWithProcess.createProducerTemplate();
        producerTemplate.sendBody("direct:start", body);

        ConsumerTemplate consumerTemplate = contextWithProcess.createConsumerTemplate();
        String consumerBody = consumerTemplate.receiveBody("seda:end", String.class);

        System.out.println(consumerBody);
    }
}
