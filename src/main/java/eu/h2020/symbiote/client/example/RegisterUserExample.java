package eu.h2020.symbiote.client.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.h2020.symbiote.client.AbstractSymbIoTeClientFactory;
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

import static eu.h2020.symbiote.client.AbstractSymbIoTeClientFactory.*;

public class RegisterUserExample {


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
        String paamOwnerUsername = "paamOwnerUsername";
        String paamOwnerPassword = "paamOwnerPassword";
        String email = "example@example.com";
        String username = "userNameInHomePlatform"; // Username of the registering user
        String password = "passwordInHomePlatform"; // Password of the registering user

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

        IAAMClient iaamClient = factory.getAAMClient(platformId);

        UserManagementRequest userManagementRequest = new UserManagementRequest(
                new Credentials(paamOwnerUsername, paamOwnerPassword), new Credentials(username, password),
                new UserDetails(new Credentials(username, password), email, UserRole.USER,
                        AccountStatus.ACTIVE, new HashMap<>(), new HashMap<>(), true, false),
                OperationType.CREATE);

        try {
            iaamClient.manageUser(userManagementRequest);
            System.out.println("User registration done");
        } catch (AAMException e) {
            throw new RuntimeException(e);
        }

        System.out.println("END");
    }
}
