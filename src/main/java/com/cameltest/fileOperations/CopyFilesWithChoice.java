package com.cameltest.fileOperations;


import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.logging.Logger;

public class CopyFilesWithChoice {
    private static final Logger logger = Logger.getLogger(CopyFilesWithChoice.class.getName());

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();
        final String originFolder = "data/inbox";
        final String job = "copying files";

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            public void configure() {
            from("file:" + originFolder + "?noop=true")
                    .wireTap("file:data/inbox original")
                .choice()
                    .when(header("CamelFileName").endsWith(".xml"))
                        .to("file:data/outbox xml")
                    .when(header("CamelFileName").endsWith(".txt"))
                        .to("file:data/outbox txt")
                    .otherwise()
                        .to("file:data/outbox other").stop()
                    .end()
                        .to("file:data/continued processing");
            }
        });

        // start the route and let it do its work
        context.start();
        logger.info("Camel " + job + " from "+ originFolder);
        Thread.sleep(2000);

        // stop the CamelContext
        context.stop();
    }
}
