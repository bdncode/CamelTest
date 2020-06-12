package com.cameltest.data_structures;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.json.JSONObject;
import org.json.XML;

public class SetWithCamel {
    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("file:data/inbox?noop=true")
                    .choice()
                        .when(header("CamelFileName").endsWith(".json"))
                            .process(new Processor() {
                                @Override
                                public void process(Exchange exchange) {
                                    String body = exchange.getIn().getBody(String.class);
                                    JSONObject inboxJsonObject = new JSONObject(body);
                                    String outboxXmlObject = XML.toString(inboxJsonObject);
                                    exchange.getOut().setBody(outboxXmlObject);
                                    }
                            })
                        .to("file:data/outbox xml")
                        .when(header("CamelFileName").endsWith(".xml"))
                            .process(new Processor() {
                                @Override
                                public void process(Exchange exchange) {
                                    String body = exchange.getIn().getBody(String.class);
                                    JSONObject jsonObject = XML.toJSONObject(body);
                                    String outboxJsonObject = jsonObject.toString();
                                    exchange.getOut().setBody(outboxJsonObject);
                                }
                            })
                        .to("file:data/outbox json");
            }
        });

        // start the route and let it do its work
        context.start();
        Thread.sleep(2000);

        // stop the CamelContext
        context.stop();
    }
}


