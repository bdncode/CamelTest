package com.cameltest.fileOperations;


import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.logging.Logger;

public class CopyFiles {
    private static final Logger logger = Logger.getLogger(CopyFiles.class.getName());

    public static void main(String args[]) {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();
        final String originFolder = "data/inbox";
        final String destinyFolder = "data/outbox";
        final String job = "copying files";

        // add the route to the CamelContext
        try {
            context.addRoutes(new RouteBuilder() {
                public void configure() {
                    from("file:" + originFolder + "?noop=true").to("file:" + destinyFolder);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // start the route and let it do its work
        try {
            context.start();
            logger.info("Camel " + job + " from "+ originFolder + " to " + destinyFolder);
            Thread.sleep(2000);

            // stop the CamelContext
            context.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
