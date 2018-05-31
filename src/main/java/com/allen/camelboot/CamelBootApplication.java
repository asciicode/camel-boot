package com.allen.camelboot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.jms.ConnectionFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class CamelBootApplication {
    private final CamelContext camelContext;

    public CamelBootApplication(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Component
    static class DefaultJmsComponentCustom implements ComponentCustomizer<JmsComponent> {
        private final ConnectionFactory connectionFactory;

        DefaultJmsComponentCustom(ConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
        }

        @Override
        public void customize(JmsComponent component) {
            component.setConnectionFactory(this.connectionFactory);
        }
    }

    @Bean
    RoutesBuilder routes() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                from("file://{{user.home}}/Desktop/in?noop=true").routeId("in-file-out").to("file://{{user.home}}/Desktop/out");

                from("file://{{user.home}}/Desktop/to-jms").routeId("file-to-jms").transform().body(GenericFile.class, gf -> {
                    File actualFile = File.class.cast(gf.getFile());
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(actualFile)))) {
                        return in.lines().collect(Collectors.joining());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).to("jms:queue:files");

                from("jms:queue:files").routeId("jms-to-file").setHeader("CamelFilename", () -> UUID.randomUUID().toString() + ".txt")
                        .to("file://{{user.home}}/Desktop/from-jms");
                
                from("direct:from-producer").routeId("direct-from-producer").tracing().log(">>> ${body.firstnames}")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                PojoDto bodyIn = (PojoDto) exchange.getIn().getBody();
                                System.out.println(bodyIn);
                                exchange.getIn().setBody(bodyIn);
                            }
                        });
                // .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
            }
        };
    }

	public static void main(String[] args) {
		SpringApplication.run(CamelBootApplication.class, args);
	}
}
