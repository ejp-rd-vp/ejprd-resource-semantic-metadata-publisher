package org.ejprd.convert;


import be.ugent.rml.Utils;
import be.ugent.rml.conformer.MappingConformer;
import be.ugent.rml.store.RDF4JStore;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.*;
import java.util.stream.Collectors;


public class JsonToRDFMapper {
    // This maps JSON to RDF
    private static final Logger logger = LoggerFactory.getLogger(JsonToRDF.class);
    private static final Marker fatal = MarkerFactory.getMarker("FATAL");


    public void jsonToRDFMapper(String[] mOptionValue) throws Exception {

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


    }


}
