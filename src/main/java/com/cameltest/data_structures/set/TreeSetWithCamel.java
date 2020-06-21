package com.cameltest.data_structures.set;

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


public class TreeSetWithCamel {
    private static Logger logger = Logger.getLogger(TreeSetWithCamel.class.getName());

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
                                List<Integer> outboxList = new ArrayList<>();
                                for (String attribute : attributes) {
                                    if (inboxJsonObject.has(attribute)) {
                                        int value = Integer.parseInt(inboxJsonObject.get(attribute).toString());
                                        outboxList.add(value);
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
        consumerBody = consumerBody.replaceAll("[^-?0-9]+", " ");
        logger.info(consumerBody);

        Set<Integer> treeSet = new TreeSet<>();
        List<Integer> integerList = new ArrayList<>();
        List<Integer> listFindValue = new LinkedList();

        for (String s : Arrays.asList(consumerBody.trim().split(" "))) {
            integerList.add(Integer.parseInt(s));
            treeSet.addAll(integerList);
        }

        for (Iterator<Integer> it = treeSet.iterator(); it.hasNext(); ) {
            Integer i = it.next();
            if (i > 2) {
                listFindValue.add(i);
            }
            if (i > 3) {
                it.remove();
            }
        }

        treeSet.removeAll(listFindValue);

        for (Iterator<Integer> it = treeSet.iterator(); it.hasNext(); ) {
            Integer i = it.next();
            logger.info(String.valueOf(i));
        }

        System.out.println(listFindValue);
    }
}


