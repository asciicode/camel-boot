package com.allen.camelboot;

import java.util.UUID;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvokerController {
    private final CamelContext camelContext;
    private final ProducerTemplate template;
    public InvokerController(CamelContext camelContext) {
        this.camelContext = camelContext;
        this.template = camelContext.createProducerTemplate();
    }

    @RequestMapping(value = { "/api/fire-from-producer" }, method = { RequestMethod.GET, RequestMethod.POST })
    public String fireFromProducer() {
        String uid = UUID.randomUUID().toString();
        PojoDto pojoDto = new PojoDto();
        pojoDto.setFirstnames("allen wacky");
        Exchange ex = WorkflowUtil.createStartExchange(camelContext, pojoDto, uid);
        System.out.println(pojoDto);
        template.asyncSend("direct:from-producer", ex);
        return "fire from producer";
    }
}
