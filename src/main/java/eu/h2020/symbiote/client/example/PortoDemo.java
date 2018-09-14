package eu.h2020.symbiote.client.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.h2020.symbiote.client.AbstractSymbIoTeClientFactory;
import eu.h2020.symbiote.client.interfaces.PRClient;
import eu.h2020.symbiote.client.interfaces.RAPClient;
import eu.h2020.symbiote.client.interfaces.RHClient;
import eu.h2020.symbiote.client.interfaces.SMClient;
import eu.h2020.symbiote.cloud.model.internal.*;
import eu.h2020.symbiote.model.cim.*;
import eu.h2020.symbiote.security.accesspolicies.common.AccessPolicyType;
import eu.h2020.symbiote.security.accesspolicies.common.singletoken.SingleTokenAccessPolicySpecifier;
import eu.h2020.symbiote.security.commons.exceptions.custom.InvalidArgumentsException;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import static eu.h2020.symbiote.client.AbstractSymbIoTeClientFactory.*;

public class PortoDemo {

    public static void main(String[] args) {

        /*
        Get the factory and the component clients
         */

        // FILL ME
        String coreAAMAddress = "https://symbiote-dev.man.poznan.pl";
        String keystorePath = "testKeystore.jks";
        String keystorePassword = "testKeystore";
        Type type = Type.FEIGN;
        String mobaasId = "mobaas4";
        String icomPlatformId = "icom-platform";
        String icomPlatformII = "https://intracom.symbiote-h2020.eu";
        String fedId1 = "fed1";

        // Printing output
        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);

        // Get the configuration
        Config config = new Config(coreAAMAddress, keystorePath, keystorePassword, type);

        // Get the factory
        AbstractSymbIoTeClientFactory factory;
        try {
            factory = getFactory(config);

            // OPTIONAL section... needs to be run only once
            // - per new platform
            // and/or after revoking client certificate in an already initialized platform


            // ATTENTION: This MUST be an interactive procedure to avoid persisting credentials (password)
            // Here, you can add credentials FOR MORE THAN 1 platforms
            Set<HomePlatformCredentials> platformCredentials = new HashSet<>();

            // Sign in to mobaas4
            String username = "user";
            String password = "user";
            String clientId = "portoDemoClientId";
            HomePlatformCredentials exampleHomePlatformCredentials = new HomePlatformCredentials(
                    mobaasId,
                    username,
                    password,
                    clientId);
            platformCredentials.add(exampleHomePlatformCredentials);


            // Get Certificates for the specified platforms
            factory.initializeInHomePlatforms(platformCredentials);

            // end of optional section..
            // After running it the first time and creating the client keystore you should comment out this section.
        } catch (SecurityHandlerException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }

        // The set of platforms from which we are going to request credentials for our requests
        Set<String> platformIds = new HashSet<>(Collections.singletonList(mobaasId));

        // Get the necessary component clients
        SMClient mobaasSMClient = factory.getSMClient(mobaasId);
        RHClient icomRHClient = factory.getRHClient(icomPlatformId);
        PRClient mobaasSearchClient = factory.getPRClient(mobaasId);
        RAPClient rapClient = factory.getRapClient();

        /*
        Mobaas4 will only subscribe to sensors
         */
        System.out.println("INFO: Platform \"mobaas4\" is subscribing only to Sensors");
        Subscription subscription = new Subscription();
        subscription.setPlatformId(mobaasId);
        subscription.getResourceType().put("sensor", true);
        subscription.getResourceType().put("actuator", false);
        subscription.getResourceType().put("service", false);
        mobaasSMClient.subscribe(subscription);
        pressAnyKey();

        /*
        Register resources in icom-platform
         */
        removeAllL2resources(icomRHClient);

        System.out.println("INFO: Registering a Sensor and an Actuator in platform \"icom-platform\"");
        icomRHClient.addL2Resources(createRegistrationMessage(icomPlatformII, fedId1));
        pressAnyKey();

        /*
        Search for resources in the Platform Registry of the mobaas4 platform
         */

        // Create the request
        // Here, we specify just one search criteria, which is the platform id. You can add more criteria, such as
        // platform name, location, etc. You can check what the PlatformRegistryQuery.Builder supports.
        // If you specify no criteria, all the L2 resources will be returned
        PlatformRegistryQuery registryQuery = new PlatformRegistryQuery.Builder()
                .names(Arrays.asList("defaultSensor", "defaultActuator"))
                .build();

        System.out.println("INFO: Searching the Platform Registry of platform: " + mobaasId);
        FederationSearchResult result = mobaasSearchClient.search(registryQuery,false, platformIds);

        // Print result
        showSearchResults(result);
        pressAnyKey();

        if (result.getResources().size() == 0) {
            System.out.println("ERROR: Could not find any resources");
        } else {
            FederatedResource resource = result.getResources().iterator().next();
            FederatedResourceInfo resourceInfo = resource.getFederatedResourceInfoMap().get(fedId1);

            if (resourceInfo == null) {
                System.out.println("\nERROR: The resource is not shared in federation " + fedId1);
            } else {
                System.out.println("\nINFO: Trying to access resource \"" + resource.getAggregationId() +
                        "\" in federation \"" + fedId1 + "\"");

                try {
                    Observation observation = rapClient.getLatestObservation(resourceInfo.getoDataUrl(), true, platformIds);
                    System.out.println(om.writeValueAsString(observation));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Remove L2 resource
        removeAllL2resources(icomRHClient);

        System.out.println("END");
    }

    private static CloudResource createSensorResource(String iiUrl, String fedId) {
        String internalId = "isen1";

        CloudResource cloudResource = new CloudResource();
        cloudResource.setInternalId(internalId);
        cloudResource.setPluginId("platform_01");

        try {
            cloudResource.setAccessPolicy(new SingleTokenAccessPolicySpecifier(AccessPolicyType.PUBLIC, null));
            cloudResource.setFilteringPolicy(new SingleTokenAccessPolicySpecifier(AccessPolicyType.PUBLIC, null));
        } catch (InvalidArgumentsException e) {
            e.printStackTrace();
        }

        StationarySensor sensor = new StationarySensor();
        cloudResource.setResource(sensor);
        sensor.setName("defaultSensor");
        sensor.setDescription(Collections.singletonList("This is default sensor with internal id: " + internalId));

        FeatureOfInterest featureOfInterest = new FeatureOfInterest();
        sensor.setFeatureOfInterest(featureOfInterest);
        featureOfInterest.setName("outside air");
        featureOfInterest.setDescription(Collections.singletonList("outside air quality"));
        featureOfInterest.setHasProperty(Arrays.asList("temperature,humidity".split(",")));

        sensor.setObservesProperty(Arrays.asList("temperature,humidity".split(",")));
        sensor.setLocatedAt(new WGS84Location(
                52.513681, 13.363782, 15,
                "Berlin",
                Collections.singletonList("Grosser Tiergarten")));
        sensor.setInterworkingServiceURL(iiUrl);

        Map<String, ResourceSharingInformation> resourceSharingInformationMap = new HashMap<>();
        ResourceSharingInformation sharingInformationSensor1 = new ResourceSharingInformation();
        sharingInformationSensor1.setBartering(false);
        resourceSharingInformationMap.put(fedId, sharingInformationSensor1);

        FederationInfoBean federationInfoBeanSensor = new FederationInfoBean();
        federationInfoBeanSensor.setSharingInformation(resourceSharingInformationMap);

        cloudResource.setFederationInfo(federationInfoBeanSensor);
        cloudResource.getResource().setInterworkingServiceURL(iiUrl);

        return cloudResource;
    }

    private static CloudResource createActuatorResource(String iiUrl, String fedId) {
        String internalId = "actuatorInternalId";

        CloudResource cloudResource = new CloudResource();
        cloudResource.setInternalId(internalId);
        cloudResource.setPluginId("platform_01");

        try {
            cloudResource.setAccessPolicy(new SingleTokenAccessPolicySpecifier(AccessPolicyType.PUBLIC, null));
            cloudResource.setFilteringPolicy(new SingleTokenAccessPolicySpecifier(AccessPolicyType.PUBLIC, null));
        } catch (InvalidArgumentsException e) {
            e.printStackTrace();
        }

        Actuator actuator = new Actuator();
        cloudResource.setResource(actuator);
        actuator.setName("defaultActuator");
        actuator.setDescription(Collections.singletonList("This is default actuator with internal id: " + internalId));

        // capabilities
        Capability capability = new Capability();
        actuator.setCapabilities(Collections.singletonList(capability));

        capability.setName("OnOffCapabililty");

        // parameters
        Parameter parameter = new Parameter();
        capability.setParameters(Collections.singletonList(parameter));
        parameter.setName("on");
        parameter.setMandatory(true);
        PrimitiveDatatype datatype = new PrimitiveDatatype();
        parameter.setDatatype(datatype);
        datatype.setBaseDatatype("boolean");

        actuator.setInterworkingServiceURL(iiUrl);

        Map<String, ResourceSharingInformation> resourceSharingInformationMap = new HashMap<>();
        ResourceSharingInformation sharingInformationSensor1 = new ResourceSharingInformation();
        sharingInformationSensor1.setBartering(false);
        resourceSharingInformationMap.put(fedId, sharingInformationSensor1);

        FederationInfoBean federationInfoBeanSensor = new FederationInfoBean();
        federationInfoBeanSensor.setSharingInformation(resourceSharingInformationMap);

        cloudResource.setFederationInfo(federationInfoBeanSensor);
        cloudResource.getResource().setInterworkingServiceURL(iiUrl);

        return cloudResource;
    }

    private static List<CloudResource> createRegistrationMessage(String iiUrl, String fedId) {
        return new LinkedList<>(Arrays.asList(
                createSensorResource(iiUrl, fedId),
                createActuatorResource(iiUrl, fedId)
        ));
    }

    private static void showSearchResults(FederationSearchResult result) {
        List<FederatedResource> resources = result.getResources();

        if (resources.size() == 0) {
            System.out.println("No resource found");
        } else {
            System.out.println("The following resources were found:\n");
            System.out.printf("%-20s%s%n", "Name", "Type");

            for (int i = 0; i < 36; i++) {
                System.out.print("=");
            }
            System.out.println();

            for (FederatedResource r : resources) {
                System.out.printf("%-20s%s%n", r.getCloudResource().getResource().getName(), r.getResourceType());
            }
        }
    }

    private static void removeAllL2resources(RHClient rhClient) {
        List<CloudResource> resources = rhClient.getResources();

        if (resources.size() > 0) {
            rhClient.removeL2Resources(
                    resources.stream()
                            .map(CloudResource::getInternalId)
                            .collect(Collectors.toList()));
        }
    }

    private static void pressAnyKey() {
        System.out.println("\nPress any key to continue:\n");
        try {
            System.in.read();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
