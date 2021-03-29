package org.ejprd.convert;

public class Main {
    public static void main(String[] args) throws Exception {
        JsonToRDF jsonToRDF = new JsonToRDF();
        jsonToRDF.jsonToRDF(args);
    }
}

// -m Semerd/src/main/resources/mappingLocation.ttl -o Location23.ttl