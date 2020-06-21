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

public class ListWithCamel {
    private static Logger logger = Logger.getLogger(ListWithCamel.class.getName());

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
        Set<Integer> hashSet = new HashSet<>();

        List<Integer> arrayList = new ArrayList<>();

        for (String s : Arrays.asList(consumerBody.trim().split(" "))) {
            arrayList.add(Integer.parseInt(s));
        }
        hashSet.addAll(arrayList);

        List<Integer> linkedList = new LinkedList<>(hashSet);
        linkedList.add(0,0);

        List<Integer> reverseList = new ArrayList<>(linkedList.size());

        for (ListIterator<Integer> it = linkedList.listIterator(linkedList.size()); it.hasPrevious();) {
            reverseList.add(it.previous());
        }
        for (int i : linkedList) {
            reverseList.add(i + linkedList.size());
        }
        System.out.println(linkedList);
        System.out.println(reverseList);

        reverseList.remove(0);
        reverseList.remove(Integer.valueOf(0));
        System.out.println(reverseList);


        int num = (int) linkedList.stream()
                .filter(i -> i<3).count();
        logger.info(String.valueOf(num));


        String stringMax = arrayList.stream()
                .reduce(0, Integer::max).toString().toUpperCase();
        logger.info(stringMax);


        int sumResult = linkedList.stream()
                .reduce(0, Integer::sum);
        logger.info(String.valueOf(sumResult));


        List<Integer> doubleValueList = linkedList.stream()
                .map(n -> n * 2).collect(Collectors.toList());
        System.out.println(doubleValueList);
    }
}


