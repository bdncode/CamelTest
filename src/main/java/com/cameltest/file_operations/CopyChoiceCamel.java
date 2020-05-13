package com.cameltest.file_operations;


import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class CopyChoiceCamel {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            public void configure() {
            from("file:data/inbox?noop=true")
                    .wireTap("file:data/initialFolder")
                .choice()
                    .when(header("CamelFileName").endsWith(".xml"))
                        .to("file:data/outbox_xml")
                    .when(header("CamelFileName").endsWith(".txt"))
                        .to("file:data/outbox_txt")
                    .otherwise()
                        .to("file:data/outbox_other").stop()
                    .end()
                        .to("file:data/continuedProcessing");
            }
        });

        // start the route and let it do its work
        context.start();
        Thread.sleep(2000);

        // stop the CamelContext
        context.stop();
    }
}
