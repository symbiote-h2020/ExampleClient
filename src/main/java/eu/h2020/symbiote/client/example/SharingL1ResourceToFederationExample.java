package eu.h2020.symbiote.client.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.h2020.symbiote.client.AbstractSymbIoTeClientFactory;
import eu.h2020.symbiote.client.interfaces.RHClient;
import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.security.commons.enums.AccountStatus;
import eu.h2020.symbiote.security.commons.enums.OperationType;
import eu.h2020.symbiote.security.commons.enums.UserRole;
import eu.h2020.symbiote.security.commons.exceptions.custom.AAMException;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;
import eu.h2020.symbiote.security.communication.IAAMClient;
import eu.h2020.symbiote.security.communication.payloads.Credentials;
import eu.h2020.symbiote.security.communication.payloads.UserDetails;
import eu.h2020.symbiote.security.communication.payloads.UserManagementRequest;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static eu.h2020.symbiote.client.AbstractSymbIoTeClientFactory.*;

public class SharingL1ResourceToFederationExample {


    public static void main(String[] args) {

        /*
        Get the factory and the component clients
         */

        // FILL ME
        String coreAAMAddress = "https://symbiote-open.man.poznan.pl";
        String keystorePath = "testKeystore.jks";
        String keystorePassword = "testKeystore";
        Type type = Type.FEIGN;
        String platformId = "examplePlatformId";
        String federationId = "exampleFederationId";
        String l1ResourceInternalIdToShare = "l1ResourceInternalIdToShare"; // Look below in the comments for an example on how to share all the L1 resources


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

        RHClient rhClient = factory.getRHClient(platformId);


        Map<String, Map<String, Boolean>> toShare = new HashMap<>();

        // You can uncomment the following if you want to share all your registered resources to the federation
        // Map<String, Boolean> resourceMap = rhClient.getResources().stream()
        //        .collect(Collectors.toMap(CloudResource::getInternalId, r -> false));

        Map<String, Boolean> resourceMap = new HashMap<>();
        resourceMap.put(l1ResourceInternalIdToShare, false);
        toShare.put(federationId, resourceMap);
        Map<String, List<CloudResource>> result = rhClient.shareL2Resources(toShare);
        System.out.println(result);
        System.out.println("END");
    }
}
