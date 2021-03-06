package com.cameltest.fileOperations;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.json.JSONObject;
import org.json.XML;

import java.util.logging.Logger;

public class JsonToXmlConverter {
    private static final Logger logger = Logger.getLogger(JsonToXmlConverter.class.getName());

    public static void main(String args[]){
        // create CamelContext
        CamelContext context = new DefaultCamelContext();
        final String originFolder = "data/inbox";
        final String job = "converting files";

        // add our route to the CamelContext
        try {
            context.addRoutes(new RouteBuilder() {
                public void configure() {
                    from("file:" + originFolder + "?noop=true")
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            context.start();
            logger.info("Camel " + job + " from "+ originFolder);
            Thread.sleep(2000);

            // stop the CamelContext
            context.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


