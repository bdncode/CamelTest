package com.cameltest.data_structures.map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.json.JSONObject;
import org.json.XML;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HashMapWithCamel {
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

                                    Map<String, Integer> hashMap = new HashMap<>();
                                    for (String attribute : attributes) {
                                        if (inboxJsonObject.has(attribute)) {
                                            int value = Integer.parseInt(inboxJsonObject.get(attribute).toString());
                                            hashMap.put(attribute, value);
                                        }
                                    }

                                    // Print keys
                                    for (String i : hashMap.keySet()) {
                                        System.out.println(i);
                                    }
                                    // Print values
                                    for (int i : hashMap.values()) {
                                        System.out.println(i);
                                    }
                                    // Print keys and values
                                    for (String i : hashMap.keySet()) {
                                        System.out.println("key: " + i + " value: " + hashMap.get(i));
                                    }

                                    Optional<String> optionalFindValue = hashMap.entrySet().stream()
                                            .filter(e -> 1 == (e.getValue()))
                                            .map(Map.Entry::getKey)
                                            .findFirst();

                                    List<String> listFindValue = hashMap.entrySet().stream()
                                            .filter(e -> e.getKey().startsWith("b"))
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
        String consumerBody1 = consumerTemplate.receiveBody("seda:end", String.class);

        System.out.println(consumerBody1);
    }
}


