package org.ejprd.semanticmodel.models.validator;

import fr.inria.lille.shexjava.schema.Label;
import fr.inria.lille.shexjava.schema.ShexSchema;
import fr.inria.lille.shexjava.schema.parsing.GenParser;
import fr.inria.lille.shexjava.util.Pair;
import fr.inria.lille.shexjava.validation.RecursiveValidation;
import fr.inria.lille.shexjava.validation.Status;
import fr.inria.lille.shexjava.validation.ValidationAlgorithmAbstract;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.apache.commons.rdf.simple.SimpleRDF;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Validate {

    private final static RDF rdfFactory = new SimpleRDF();

    public static List<Object> validateGaphAgainstSchema(String dataFileName,
                                                 String schemaFileName,
                                                 String focusNodeIri,
                                                 String shapeLabelIri) throws Exception {

        Graph dataModel = new RDF4J().asGraph(getData(dataFileName));
        if (dataModel == null) {
            System.err.println("Was unable to parse the data. Aborting.");
            System.exit(1);
        }

        ShexSchema schema = getSchema(schemaFileName);
        if (schema == null) {
            System.err.println("Was unable to parse the schema. Aborting.");
            System.exit(1);
        }

        RDFTerm focusNode = null;
        Label shapeLabel = null;

        try {
            focusNode = rdfFactory.createIRI(focusNodeIri);
            shapeLabel = new Label(rdfFactory.createIRI(shapeLabelIri));
        } catch (Exception e) {
            System.err.println("Incorrect format for focus node or shape label");
            System.err.println("Caused by:");
            System.err.println(e.getMessage());
        }

        // Always recursive
        ValidationAlgorithmAbstract val = new RecursiveValidation(schema, dataModel);
        System.out.println("Validating graph " + dataFileName + " against schema " + schemaFileName + ".");

        boolean result = val.validate(focusNode, shapeLabel);
        System.out.println(String.format("%s %s %s", focusNode, result ? "SATISFIES" : "DOES NOT SATISFY", shapeLabel));

        List<Object> associations = new ArrayList<>();
        try {
            associations = generateStatusFile(val.getTyping().getStatusMap());
        } catch (Exception e) {
            System.err.println("Error writing the result.");
            System.err.println("Caused by: ");
            System.err.println(e.getMessage());
        }

        System.out.println("Validation complete.");

        return associations;
    }

    private static ShexSchema getSchema(String schemaFileName) {
        ShexSchema schema;
        try {
            schema = GenParser.parseSchema(rdfFactory, Paths.get(schemaFileName));
        } catch (IOException e) {
            System.err.println("Error reading the schema file.");
            System.err.println("Caused by: ");
            System.err.println(e.getMessage());
            ;
            return null;
        } catch (Exception e) {
            System.err.println("Error while parsing the schema file. Caused by: " + e.getMessage());
            e.printStackTrace(System.err);
            return null;
        }
        return schema;
    }

    private static Model getData(String dataFileName) {

        Model dataModel;

        try {
            InputStream inputStream = new FileInputStream(new File(dataFileName));
            dataModel = Rio.parse(inputStream, dataFileName, Rio.getParserFormatForFileName(dataFileName).orElse(RDFFormat.RDFXML));
        } catch (Exception e) {
            System.err.println("Error while reading the data file.");
            System.err.println("Caused by: ");
            System.err.println(e.getMessage());
            return null;
        }
        return dataModel;
    }

    private static List<Object> generateStatusFile(Map<Pair<RDFTerm, Label>, Status> statusMap) {
        List<Object> associations = new ArrayList<>();
        for (Map.Entry<Pair<RDFTerm, Label>, Status> e : statusMap.entrySet()) {
            RDFTerm node = e.getKey().one;
            Label label = e.getKey().two;
            Status status = e.getValue();
            Map<String, String> associationMap = new LinkedHashMap<>(3);
            associationMap.put("nodeSelector", node.ntriplesString());
            associationMap.put("shapeLabel", label.stringValue());
            associationMap.put("status", status.name().toLowerCase());
            associations.add(associationMap);
        }
        return associations;
    }

}

