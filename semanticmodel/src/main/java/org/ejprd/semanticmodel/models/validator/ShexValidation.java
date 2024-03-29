package org.ejprd.semanticmodel.models.validator;

import fr.inria.lille.shexjava.GlobalFactory;
import fr.inria.lille.shexjava.schema.Label;
import fr.inria.lille.shexjava.schema.ShexSchema;
import fr.inria.lille.shexjava.schema.parsing.GenParser;
import fr.inria.lille.shexjava.validation.RecursiveValidation;
import fr.inria.lille.shexjava.validation.RecursiveValidationWithMemorization;
import fr.inria.lille.shexjava.validation.RefineValidation;
import fr.inria.lille.shexjava.validation.ValidationAlgorithm;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class ShexValidation {
    public ShexValidation() {
    }

    public static String doShexValidation(Path schemaFile, Path dataFile)  {

        final Logger logger = Logger.getLogger(ShexValidation.class.getName());
        List<Path> importDirectories = Collections.emptyList();

        RDF4J factory = new RDF4J();

        GlobalFactory.RDFFactory = factory; //set the global factory used in shexjava

        // load and create the shex schema
        logger.info("Reading schema");

        ShexSchema schema = null;
        try {
            schema = GenParser.parseSchema(schemaFile,importDirectories);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Label label:schema.getRules().keySet())
            logger.info(label+": "+schema.getRules().get(label));

        // load the model
        logger.info("Reading data");

        String baseIRI = "http://a.example.shex/";
        Model data = null;
        try {
            data = Rio.parse(new FileInputStream(dataFile.toFile()), baseIRI, RDFFormat.TURTLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Statement datum : data) logger.info(String.valueOf(datum));

        // create the graph
        Graph dataGraph = factory.asGraph(data);
        // choose focus node and shapelabel
        IRI focusNode = factory.createIRI("http://a.example/boolean-true"); //to change with what you want
        Label shapeLabel = new Label(factory.createIRI("http://a.example/S-boolean")); //to change with what you want
        logger.info("Refine validation:");
        // create the validation algorithm
        ValidationAlgorithm validation = new RefineValidation(schema, dataGraph);
        //validate
        validation.validate(focusNode, shapeLabel);
        //check the result
        boolean result = validation.getTyping().isConformant(focusNode, shapeLabel);
        logger.info("Recursive validation:");
        validation = new RecursiveValidation(schema, dataGraph);
        validation.validate(focusNode, shapeLabel);
        //check the result
        result = validation.getTyping().isConformant(focusNode, shapeLabel);
        logger.info("Does "+focusNode+" has shape "+shapeLabel+"? "+result);
        logger.info("Recursive validation with memorization:");
        validation = new RecursiveValidationWithMemorization(schema, dataGraph);
        validation.validate(focusNode, shapeLabel);
        //check the result
        result = validation.getTyping().isConformant(focusNode, shapeLabel);

        String outputValidation = ("Validation is successful, the  " + focusNode+" has shape "+shapeLabel+" "+ result );

        if (result == true) {
            System.out.println("Validation is successful, the  " +focusNode + " " + " Validation succeeded");
        }
        else {
            System.out.println("Validation is successful, the  " +focusNode + " " + "Validation unsuccessful");
        }
        return outputValidation ;
    }
}
