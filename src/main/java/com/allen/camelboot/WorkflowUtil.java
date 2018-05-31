package com.allen.camelboot;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;

public class WorkflowUtil {

    public static void createOutputExchange(CamelContext context, Object body, Exchange exchange) {
        Message msg = exchange.getIn();
        DefaultMessage outMsg = new DefaultMessage(context);
        outMsg.setHeaders(msg.getHeaders());
        outMsg.setBody(body);
        exchange.setOut(outMsg);
    }

    public static Exchange createStartExchange(CamelContext context, Object body, String uid) {
        DefaultExchange ex = new DefaultExchange(context);
        DefaultMessage inMsg = new DefaultMessage(context);
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("headerId", uid);
        inMsg.setHeaders(headers);
        inMsg.setBody(body);
        ex.setIn(inMsg);
        return ex;
    }

    private WorkflowUtil() {
    }
}
