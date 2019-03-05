package eu.h2020.symbiote.client.example;

import eu.h2020.symbiote.client.AbstractSymbIoTeClientFactory;
import eu.h2020.symbiote.client.interfaces.RHClient;
import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.cloud.model.internal.RdfCloudResourceList;
import eu.h2020.symbiote.core.internal.RDFFormat;
import eu.h2020.symbiote.core.internal.RDFInfo;
import eu.h2020.symbiote.security.accesspolicies.common.AccessPolicyType;
import eu.h2020.symbiote.security.accesspolicies.common.singletoken.SingleTokenAccessPolicySpecifier;
import eu.h2020.symbiote.security.commons.exceptions.custom.InvalidArgumentsException;
import eu.h2020.symbiote.security.commons.exceptions.custom.SecurityHandlerException;

import java.security.NoSuchAlgorithmException;

import static eu.h2020.symbiote.client.AbstractSymbIoTeClientFactory.*;

public class RegisterRDFResources {

    public static void main(String[] args) {

        /*
        Get the factory and the component clients
         */

        // FILL ME
        String coreAddress = "https://symbiote-dev.man.poznan.pl";
        String keystorePath = "testKeystore.jks";
        String keystorePassword = "testKeystore";
        String platform = "icom-platform-pim";
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
        RHClient rhClient = factory.getRHClient(platform);



        // Add resources
        rhClient.addL1RdfResources(getRdfList());

        // Remove resources
        // rhClient.deleteL1Resources(resources.stream().map(CloudResource::getInternalId).collect(Collectors.toList()));

        System.out.println("OK");
    }

    private static RdfCloudResourceList getRdfList() {
        RdfCloudResourceList list = new RdfCloudResourceList();
        CloudResource cloudResource = new CloudResource();
        cloudResource.setInternalId("isen1");
        cloudResource.setPluginId("RapPluginExample");

        try {
            cloudResource.setAccessPolicy(new SingleTokenAccessPolicySpecifier(AccessPolicyType.PUBLIC, null));
            cloudResource.setFilteringPolicy(new SingleTokenAccessPolicySpecifier(AccessPolicyType.PUBLIC, null));
        } catch (InvalidArgumentsException e) {
            e.printStackTrace();
        }

        cloudResource.setResource(null);
        list.getIdMappings().put("http://mapTest1.eu/platform1/mapTestRes1", cloudResource);
        RDFInfo rdfInfo = new RDFInfo();
        rdfInfo.setRdf("{\n" +
                "\n" +
                "  \"@graph\" : [ {\n" +
                "\n" +
                "    \"@id\" : \"_:b0\",\n" +
                "\n" +
                "    \"@type\" : \"core:FeatureOfInterest\",\n" +
                "\n" +
                "    \"hasProperty\" : \"test1:temp1\",\n" +
                "\n" +
                "    \"description\" : \"This is room 1\",\n" +
                "\n" +
                "    \"name\" : \"Room1\"\n" +
                "\n" +
                "  }, {\n" +
                "\n" +
                "    \"@id\" : \"http://mapTest1.eu/platform1/loc1\",\n" +
                "\n" +
                "    \"@type\" : \"core:WGS84Location\",\n" +
                "\n" +
                "    \"description\" : \"This is paris\",\n" +
                "\n" +
                "    \"name\" : \"Paris\",\n" +
                "\n" +
                "    \"alt\" : \"15.0\",\n" +
                "\n" +
                "    \"lat\" : \"48.864716\",\n" +
                "\n" +
                "    \"long\" : \"2.349014\"\n" +
                "\n" +
                "  }, {\n" +
                "\n" +
                "    \"@id\" : \"http://mapTest1.eu/platform1/mapTestRes1\",\n" +
                "\n" +
                "    \"@type\" : \"core:StationarySensor\",\n" +
                "\n" +
                "    \"hasFeatureOfInterest\" : \"_:b0\",\n" +
                "\n" +
                "    \"locatedAt\" : \"http://mapTest1.eu/platform1/loc1\",\n" +
                "\n" +
                "    \"observesProperty\" : [ \"test1:temp1\" ],\n" +
                "\n" +
                "    \"description\" : \"mapTestRes1\",\n" +
                "\n" +
                "    \"name\" : \"mapTestRes1\"\n" +
                "\n" +
                "  } ],\n" +
                "\n" +
                "  \"@context\" : {\n" +
                "\n" +
                "    \"hasProperty\" : {\n" +
                "\n" +
                "      \"@id\" : \"http://www.symbiote-h2020.eu/ontology/core#hasProperty\",\n" +
                "\n" +
                "      \"@type\" : \"@id\"\n" +
                "\n" +
                "    },\n" +
                "\n" +
                "    \"description\" : {\n" +
                "\n" +
                "      \"@id\" : \"http://www.symbiote-h2020.eu/ontology/core#description\"\n" +
                "\n" +
                "    },\n" +
                "\n" +
                "    \"name\" : {\n" +
                "\n" +
                "      \"@id\" : \"http://www.symbiote-h2020.eu/ontology/core#name\"\n" +
                "\n" +
                "    },\n" +
                "\n" +
                "    \"hasFeatureOfInterest\" : {\n" +
                "\n" +
                "      \"@id\" : \"http://www.symbiote-h2020.eu/ontology/core#hasFeatureOfInterest\",\n" +
                "\n" +
                "      \"@type\" : \"@id\"\n" +
                "\n" +
                "    },\n" +
                "\n" +
                "    \"locatedAt\" : {\n" +
                "\n" +
                "      \"@id\" : \"http://www.symbiote-h2020.eu/ontology/core#locatedAt\",\n" +
                "\n" +
                "      \"@type\" : \"@id\"\n" +
                "\n" +
                "    },\n" +
                "\n" +
                "    \"observesProperty\" : {\n" +
                "\n" +
                "      \"@id\" : \"http://www.symbiote-h2020.eu/ontology/core#observesProperty\",\n" +
                "\n" +
                "      \"@type\" : \"@id\"\n" +
                "\n" +
                "    },\n" +
                "\n" +
                "    \"id\" : {\n" +
                "\n" +
                "      \"@id\" : \"http://www.symbiote-h2020.eu/ontology/core#id\"\n" +
                "\n" +
                "    },\n" +
                "\n" +
                "    \"alt\" : {\n" +
                "\n" +
                "      \"@id\" : \"http://www.w3.org/2003/01/geo/wgs84_pos#alt\"\n" +
                "\n" +
                "    },\n" +
                "\n" +
                "    \"long\" : {\n" +
                "\n" +
                "      \"@id\" : \"http://www.w3.org/2003/01/geo/wgs84_pos#long\"\n" +
                "\n" +
                "    },\n" +
                "\n" +
                "    \"lat\" : {\n" +
                "\n" +
                "      \"@id\" : \"http://www.w3.org/2003/01/geo/wgs84_pos#lat\"\n" +
                "\n" +
                "    },\n" +
                "\n" +
                "    \"geo\" : \"http://www.w3.org/2003/01/geo/wgs84_pos#\",\n" +
                "\n" +
                "    \"core\" : \"http://www.symbiote-h2020.eu/ontology/core#\",\n" +
                "\n" +
                "    \"qu\" : \"http://purl.oclc.org/NET/ssnx/qu/quantity#\",\n" +
                "\n" +
                "    \"test1\" : \"http://www.symbiote-h2020.eu/ontology/mapTest1#\",\n" +
                "\n" +
                "    \"rdf\" : \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\",\n" +
                "\n" +
                "    \"owl\" : \"http://www.w3.org/2002/07/owl#\",\n" +
                "\n" +
                "    \"meta\" : \"http://www.symbiote-h2020.eu/ontology/meta#\",\n" +
                "\n" +
                "    \"xsd\" : \"http://www.w3.org/2001/XMLSchema#\",\n" +
                "\n" +
                "    \"rdfs\" : \"http://www.w3.org/2000/01/rdf-schema#\"\n" +
                "\n" +
                "  }\n" +
                "\n" +
                "}");

        rdfInfo.setRdfFormat(RDFFormat.JSONLD);
        list.setRdfInfo(rdfInfo);

        return list;
    }
}
