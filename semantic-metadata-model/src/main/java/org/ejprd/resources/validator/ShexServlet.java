package org.ejprd.resources.validator;



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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@WebServlet(name = "ShexServlet" , urlPatterns="/validator")
public class ShexServlet extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 /*
 The
  */
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        final Logger logger = Logger.getLogger(ShexServlet.class.getName());
        PrintWriter out = response.getWriter();
        out.println("<form action=\"/SemanticModel_war/validator\" method=\"post\" enctype=\"multipart/form-data\">");
        out.println("<input type=\"file\" name=\"schemaFilePath\">");
        out.println("<input type=\"file\" name=\"dataFile\">");
        out.println("<br/><br/>");
        out.println("<input type=\"submit\" value=\"Submit\">");
        out.println("</form>");

        Part filePartSchemaTTL = request.getPart("schemaFilePath"); // an address of the file location
       // Part filePartJson = request.getPart("schemaFilePath");
        InputStream fileInputStream = filePartSchemaTTL.getInputStream();
        //InputStream fileInputJsonStream = filePartJson.getInputStream();

        logger.info("Reading schema");

        Path schemaFile = Paths.get(request.getParameter("schemaFilePath"));
        Path dataFile = Paths.get(request.getParameter("dataFile"));
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
        Model data = Rio.parse(new FileInputStream(dataFile.toFile()), baseIRI, RDFFormat.TURTLE);
        Iterator<Statement> ite = data.iterator();
        while (ite.hasNext())
            logger.log((LogRecord) ite.next());

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
        logger.info("Does "+focusNode+" has shape "+shapeLabel+"? "+result);


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
        logger.info("Does "+focusNode+" has shape "+shapeLabel+"? "+result);

    }

}

