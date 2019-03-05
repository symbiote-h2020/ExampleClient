package eu.h2020.symbiote.client.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.h2020.symbiote.client.AbstractSymbIoTeClientFactory;
import eu.h2020.symbiote.client.interfaces.SMClient;
import eu.h2020.symbiote.cloud.model.internal.Subscription;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;

import java.security.NoSuchAlgorithmException;

import static eu.h2020.symbiote.client.AbstractSymbIoTeClientFactory.*;

public class L2SubscribeExample {


    public static void main(String[] args) {

        /*
        Get the factory and the component clients
         */

        // FILL ME
        String coreAAMAddress = "https://symbiote-open.man.poznan.pl";
        String keystorePath = "testKeystore.jks";
        String keystorePassword = "testKeystore";
        Type type = Type.FEIGN;
        // Your own platform. The subscription will be forwarded from your own platform to the other members of the federation
        String platformId = "examplePlatformId";

        // Printing output
        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);

        // Get the configuration
        Config config = new Config(coreAAMAddress, keystorePath, keystorePassword, type);

        // Get the factory
        AbstractSymbIoTeClientFactory factory;
        try {
            factory = getFactory(config);

            // end of optional section..
            // After running it the first time and creating the client keystore you should comment out this section.
        } catch (SecurityHandlerException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }

        SMClient smClient = factory.getSMClient(platformId);

        // Here you create the desired subscription

        Subscription subscription = new Subscription();
        subscription.setPlatformId(platformId);
        subscription.getResourceType().put("sensor", true);
        subscription.getResourceType().put("actuator", true);
        subscription.getResourceType().put("service", true);
        subscription.getResourceType().put("device", true);
        smClient.subscribe(subscription);

        System.out.println("END");
    }
}
