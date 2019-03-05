package eu.h2020.symbiote.client.example;

import eu.h2020.symbiote.client.AbstractSymbIoTeClientFactory;
import eu.h2020.symbiote.client.interfaces.RAPClient;
import eu.h2020.symbiote.model.cim.Observation;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static eu.h2020.symbiote.client.AbstractSymbIoTeClientFactory.*;

public class RAPPluginStarterTests {

    public static void main(String[] args) {

        /*
        Get the factory and the component clients
         */

        // FILL ME
        String coreAddress = "https://symbiote-dev.man.poznan.pl";
        String keystorePath = "testKeystore.jks";
        String keystorePassword = "testKeystore";
        Type type = Type.FEIGN;

        // Get the configuration
        Config config = new Config(coreAddress, keystorePath, keystorePassword, type);

        // Get the factory
        AbstractSymbIoTeClientFactory factory;
        try {
            factory = getFactory(config);
        } catch (SecurityHandlerException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }

        // Get the necessary component clients
        RAPClient rapClient = factory.getRapClient();

        /*
        Issue requests to RAP
         */

        String serviceOutput = rapClient.invokeServiceAsGuest("https://46b7b974.ngrok.io/rap/Service('serv')", "[\n" +
                "\n" +
                "    {\n" +
                "\n" +
                "        \"inputParam1\" : \"on\"\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "]", false);

        System.out.println(serviceOutput);

        rapClient.actuateAsGuest("https://46b7b974.ngrok.io/rap/Actuators('aaa')", "{\n" +
                "\n" +
                "  \"OnOffCapabililty\" : [\n" +
                "\n" +
                "    {\n" +
                "\n" +
                "      \"on\" : true\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "  ]\n" +
                "\n" +
                "}", false);

        List<Observation> observations = rapClient.getTopObservationsAsGuest("https://46b7b974.ngrok.io/rap/Sensor('sss')", 10, false);
        System.out.println(observations);
    }
}
