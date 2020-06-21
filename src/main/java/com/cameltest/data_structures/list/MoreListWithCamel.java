package com.cameltest.data_structures.list;

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

public class MoreListWithCamel {
    private static Logger logger = Logger.getLogger(MoreListWithCamel.class.getName());

    public static void main(String args[]) throws Exception {

        CamelContext context = new DefaultCamelContext();
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
                                List<String> outboxList = new ArrayList<>();
                                for (String attribute : attributes) {
                                    if (inboxJsonObject.has(attribute)) {
                                        String value = inboxJsonObject.get(attribute).getAsString().toLowerCase();
                                        outboxList.add(attribute + ": " + value);
                                    }
                                }
                                exchange.getOut().setBody(outboxList.toString());
                            }
                        })
                    .to("seda:end");
            }
        });

        context.start();

        ConsumerTemplate consumerTemplate = context.createConsumerTemplate();
        String consumerBody = consumerTemplate.receiveBody("seda:end", String.class);
        logger.info(consumerBody);

        List<String> arrayList = new ArrayList<>();
        consumerBody = consumerBody.substring(1,consumerBody.length()-1);
        for (String s : Arrays.asList(consumerBody.trim().split(", "))) {
            arrayList.add(s);
        }

        List<String> stringListToUpperCase = arrayList.stream()
                .map(String::toUpperCase).collect(Collectors.toList());
        System.out.println(stringListToUpperCase);


        int l = 2;
        List<String> stringListFilterLongerThan = arrayList.stream()
                .filter(s -> s.length() > l).collect(Collectors.toList());
        System.out.println(stringListFilterLongerThan);


        String stringConcatenated = arrayList.stream()
                .reduce("", String::concat).toUpperCase();
        logger.info(stringConcatenated);


        List<String> searchList = arrayList.stream()
                .filter(s -> s.startsWith(stringConcatenated.substring(0,1).toLowerCase()))
                .collect(Collectors.toList());
        System.out.println(searchList);
    }
}


