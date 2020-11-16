package org.ejpraredisease.resources.publisher;

public class Main {
    public Main() {
    }
    public static void main(String[] args) {
        JsonToRDF jsonToRDF = new JsonToRDF();
        jsonToRDF.jsonToRDF(args);
    }
}
