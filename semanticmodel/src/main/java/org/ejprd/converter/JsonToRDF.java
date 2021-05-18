package org.ejprd.converter;



import be.ugent.rml.Executor;
import be.ugent.rml.Utils;
import be.ugent.rml.conformer.MappingConformer;
import be.ugent.rml.functions.FunctionLoader;
import be.ugent.rml.functions.lib.IDLabFunctions;
import be.ugent.rml.metadata.MetadataGenerator;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.store.RDF4JStore;
import be.ugent.rml.store.SimpleQuadStore;
import be.ugent.rml.term.NamedNode;
import be.ugent.rml.term.Term;
import ch.qos.logback.classic.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.ejprd.utils.PublisherCommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.ejprd.converter.FileWriter.writeOutput;

public class JsonToRDF {

    // This parses JSON to RDF
    private static final Logger logger = LoggerFactory.getLogger(JsonToRDF.class);
    private static final Marker fatal = MarkerFactory.getMarker("FATAL");

    public Boolean jsonToRDF(String[] args) {
        String basePath = System.getProperty("user.dir");
        JsonToRDFOptions jsonToRDFOptions = PublisherCommandLine.getJsonToRDFOptions();
        CommandLineParser parser = new DefaultParser();

        try {
            // parse the command line arguments
            CommandLine lineArgs = parser.parse(jsonToRDFOptions.getOptions(), args);

            // Check if config file is given
            Properties configFile = null;
            if (lineArgs.hasOption("c")) {
                configFile = new Properties();
                configFile.load(Utils.getReaderFromLocation(lineArgs.getOptionValue("c")));
            }

            if (PublisherCommandLine.checkOptionPresence(jsonToRDFOptions.getHelpOption(), lineArgs, configFile)) {
                PublisherCommandLine.printHelp(jsonToRDFOptions.getOptions());
                return false;
            }

            if (PublisherCommandLine.checkOptionPresence(jsonToRDFOptions.getVerboseOption(), lineArgs, configFile)) {
                PublisherCommandLine.setLoggerLevel(Level.DEBUG);
            } else {
                PublisherCommandLine.setLoggerLevel(Level.ERROR);
            }

            String[] mOptionValue = PublisherCommandLine.getOptionValues(jsonToRDFOptions.getMappingDocOption(), lineArgs, configFile);
            if (mOptionValue == null) {
                PublisherCommandLine.printHelp(jsonToRDFOptions.getOptions());
            } else {
                jsonToRDFMapper( mOptionValue, basePath,lineArgs,configFile, jsonToRDFOptions);

            }

            return true;
        } catch (ParseException exp) {
            // oops, something went wrong
            logger.error("Parsing failed. Reason: " + exp.getMessage());
            PublisherCommandLine.printHelp(jsonToRDFOptions.getOptions());
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }




    private static QuadStore getStoreForFormat(String outputFormat) {
        if (outputFormat == null || outputFormat.equals("nquads") || outputFormat.equals("hdt")) {
            return new SimpleQuadStore();
        } else {
            return new RDF4JStore();
        }
    }


    public void jsonToRDFMapper(String[] mOptionValue, String basePath,
                                CommandLine lineArgs, Properties configFile, JsonToRDFOptions jsonToRDFOptions) throws Exception {

        // Concatenate all mapping files
        List<InputStream> lis = Arrays.stream(mOptionValue)
                .map(Utils::getInputStreamFromFileOrContentString)
                .collect(Collectors.toList());
        InputStream is = new SequenceInputStream(Collections.enumeration(lis));

        Map<String, String> mappingOptions = new HashMap<>();

        // Read mapping file.
        RDF4JStore rmlStore = new RDF4JStore();
        rmlStore.read(is, null, RDFFormat.TURTLE);

        MappingConformer conformer = new MappingConformer
                (rmlStore, mappingOptions);

        try {
            boolean conversionNeeded = conformer.conform();

            if (conversionNeeded) {
                logger.info("Conversion to RML was needed");
            }
        } catch (Exception e) {

            logger.error(fatal, "Failed to make mapping file conformant to RML spec.", e);

        }


        RecordsFactory factory = new RecordsFactory(basePath);

        String outputFormat = PublisherCommandLine.getPriorityOptionValue(jsonToRDFOptions.getSerializationFormatOption(), lineArgs, configFile);
        QuadStore outputStore = getStoreForFormat(outputFormat);

        Executor executor;

        // Extract required information and create the MetadataGenerator
        MetadataGenerator metadataGenerator = null;
        String metadataFile = PublisherCommandLine.getPriorityOptionValue(jsonToRDFOptions.getMetadataOption(), lineArgs, configFile);
        String requestedDetailLevel = PublisherCommandLine.getPriorityOptionValue(jsonToRDFOptions.getMetadataDetailLevelOption(), lineArgs, configFile);

        if (PublisherCommandLine.checkOptionPresence(jsonToRDFOptions.getMetadataOption(), lineArgs, configFile)) {
            if (requestedDetailLevel != null) {
                MetadataGenerator.DETAIL_LEVEL detailLevel;
                switch (requestedDetailLevel) {
                    case "dataset":
                        detailLevel = MetadataGenerator.DETAIL_LEVEL.DATASET;
                        break;
                    case "triple":
                        detailLevel = MetadataGenerator.DETAIL_LEVEL.TRIPLE;
                        break;
                    case "term":
                        detailLevel = MetadataGenerator.DETAIL_LEVEL.TERM;
                        break;
                    default:
                        logger.error("Unknown metadata detail level option. Use the -h flag for more info.");
                        return;
                }

                QuadStore metadataStore = getStoreForFormat(outputFormat);

                metadataGenerator = new MetadataGenerator(
                        detailLevel,
                        PublisherCommandLine.getPriorityOptionValue(jsonToRDFOptions.getMetadataOption(), lineArgs, configFile),
                        mOptionValue,
                        rmlStore,
                        metadataStore
                );
            } else {
                logger.error("Please specify the detail level when requesting metadata generation. Use the -h flag for more info.");
            }
        }

        String[] fOptionValue = PublisherCommandLine.getOptionValues(jsonToRDFOptions.getFunctionFileOption(), lineArgs, configFile);
        FunctionLoader functionLoader;

        // Read function description files.
        if (fOptionValue == null) {
            functionLoader = new FunctionLoader();
        } else {
            logger.debug("Using custom path to functions.ttl file: " + Arrays.toString(fOptionValue));
            RDF4JStore functionDescriptionTriples = new RDF4JStore();
            functionDescriptionTriples.read(Utils.getInputStreamFromFile(Utils.getFile("functions_idlab.ttl")), null, RDFFormat.TURTLE);
            Map<String, Class> libraryMap = new HashMap<>();
            libraryMap.put("IDLabFunctions", IDLabFunctions.class);
            List<InputStream> lisF = Arrays.stream(fOptionValue)
                    .map(Utils::getInputStreamFromFileOrContentString)
                    .collect(Collectors.toList());
            for (InputStream inputStream : lisF) {
                functionDescriptionTriples.read(inputStream, null, RDFFormat.TURTLE);
            }
            functionLoader = new FunctionLoader(functionDescriptionTriples, libraryMap);
        }

        // We have to get the InputStreams of the RML documents again,
        // because we can only use an InputStream once

        lis = Arrays.stream(mOptionValue)
                .map(Utils::getInputStreamFromFileOrContentString)
                .collect(Collectors.toList());
        is = new SequenceInputStream(Collections.enumeration(lis));

        executor = new Executor(rmlStore, factory, functionLoader, outputStore, Utils.getBaseDirectiveTurtle(is));

        List<Term> triplesMaps = new ArrayList<>();

        String tOptionValue = PublisherCommandLine.getPriorityOptionValue(jsonToRDFOptions.getTriplesmapsOption(), lineArgs, configFile);
        if (tOptionValue != null) {
            List<String> triplesMapsIRI = Arrays.asList(tOptionValue.split(","));
            triplesMapsIRI.forEach(iri -> triplesMaps.add(new NamedNode(iri)));
        }

        if (metadataGenerator != null) {
            metadataGenerator.preMappingGeneration(triplesMaps.isEmpty() ?
                    executor.getTriplesMaps() : triplesMaps, rmlStore);
        }

        // Get start timestamp for post mapping metadata
        String startTimestamp = Instant.now().toString();

        try {
            QuadStore result = executor.execute(triplesMaps, PublisherCommandLine.checkOptionPresence
                            (jsonToRDFOptions.getRemoveDuplicatesOption(), lineArgs, configFile),
                    metadataGenerator);

            // Get stop timestamp for post mapping metadata
            String stopTimestamp = Instant.now().toString();

            // Generate post mapping metadata and output all metadata
            if (metadataGenerator != null) {
                metadataGenerator.postMappingGeneration(startTimestamp, stopTimestamp,
                        result);

                writeOutput(metadataGenerator.getResult(), metadataFile, outputFormat);
            }

            String outputFile = PublisherCommandLine.getPriorityOptionValue(jsonToRDFOptions.getOutputFileOption(), lineArgs, configFile);

            if (result.isEmpty()) {
                logger.info("No results!");
                // Write even if no results
            }
            result.copyNameSpaces(rmlStore);
            writeOutput(result, outputFile, outputFormat);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }


}

