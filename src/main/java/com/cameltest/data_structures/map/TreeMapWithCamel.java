package com.cameltest.data_structures.map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TreeMapWithCamel {
    private static Logger logger = Logger.getLogger(TreeMapWithCamel.class.getName());

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
                                    String [] attributes = {"b", "c", "d", "e", "a"};
                                    JsonObject inboxJsonObject = new JsonParser().parse(exchange.getIn().getBody(String.class)).getAsJsonObject();

                                    Map<String, Integer> treeMap = new TreeMap<>(Comparator.reverseOrder());
                                    for (String attribute : attributes) {
                                        if (inboxJsonObject.has(attribute)) {
                                            int value = Integer.parseInt(inboxJsonObject.get(attribute).toString());
                                            treeMap.put(attribute, value);
                                        }
                                    }

                                    // Print keys
                                    for (String i : treeMap.keySet()) {
                                        logger.info(i);
                                    }
                                    // Print values
                                    for (int i : treeMap.values()) {
                                        logger.info(String.valueOf(i));
                                    }
                                    // Print keys and values
                                    for (String i : treeMap.keySet()) {
                                        logger.info("key: " + i + " value: " + treeMap.get(i));
                                    }

                                    Optional<String> optionalFindValue = treeMap.entrySet().stream()
                                            .filter(e -> 1 == (e.getValue()))
                                            .map(Map.Entry::getKey)
                                            .findFirst();

                                    List<String> listFindValue = treeMap.entrySet().stream()
                                            .filter(e -> e.getKey().startsWith("d"))
                                            .map(Map.Entry::getKey)
                                            .collect(Collectors.toList());

                                    exchange.getOut().setBody(optionalFindValue.get().toString() + listFindValue.toString());
                                }
                            })
                        .to("seda:end");
            }
        });

        context.start();

        ConsumerTemplate consumerTemplate = context.createConsumerTemplate();
        String consumerBody = consumerTemplate.receiveBody("seda:end", String.class);

        logger.info(consumerBody);
    }
}


