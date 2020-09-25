package com.cameltest.fileOperations;


import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.logging.Logger;

public class CopyFilesCamel {
    private static final Logger logger = Logger.getLogger(CopyFilesCamel.class.getName());

    public static void main(String args[]) {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();
        final String activeMqUrl = "http://localhost:8161/admin/queues.jsp";

        // add the route to the CamelContext
        try {
            context.addRoutes(new RouteBuilder() {
                public void configure() {
                    from("file:data/inbox?noop=true").to("file:data/outbox");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // start the route and let it do its work
        try {
            context.start();
            logger.info("ActiveMQ on " + activeMqUrl);
            Thread.sleep(2000);

            // stop the CamelContext
            context.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
