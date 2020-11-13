///*
// * Copyright (c) 2020, EJPRD. All rights reserved.
// * Use is subject to license terms.
//  */
//
//package uk.ac.ebi.spot.publisher;
//
//
//import be.ugent.rml.Utils;
//import be.ugent.rml.store.QuadStore;
//import be.ugent.rml.store.RDF4JStore;
//import be.ugent.rml.store.SimpleQuadStore;
//import ch.qos.logback.classic.Level;
//import org.apache.commons.cli.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.Marker;
//import org.slf4j.MarkerFactory;
//
//import java.io.*;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * A representation of file JSON files conversion to RDF format.
// *
// * <p> User interfaces and operating systems use system-dependent <em>pathname
// * strings</em> to name files and directories.  This class presents an
// * abstract, system-independent view of JSON inputstream file converted to RDF.
// *
// * @author  Olamidipupo Ajigboye
// * @since   1.0.0
// */
//
//
//
//public class JsonToRDFConverter {
//
//        private static final Logger logger = LoggerFactory.getLogger(JsonToRDFConverter.class);
//        private static final Marker fatal = MarkerFactory.getMarker("FATAL");
//        private static Options options = new Options();;
//        private static String[] args;
//        private static Option verboseOption;
//        String basePath = System.getProperty("user.dir");
//
//
//
//
//        public JsonToRDFConverter(Options options, String basePath, String[] args) {
//                this.basePath = basePath;
//                this.options = options;
//                this.args = args;
//        }
//
//        //Options values for commandLine
//
//        public void Options() {
//
//                Option mappingdocOption = Option.builder("m")
//                        .longOpt("mappingFile")
//                        .hasArg()
//                        .numberOfArgs(Option.UNLIMITED_VALUES)
//                        .desc("one or more mapping file paths and/or strings " +
//                                "(multiple values are concatenated). " +
//                                "r2rml is converted to rml if needed using" +
//                                " the r2rml arguments.")
//                        .build();
//
//                Option functionfileOption = Option.builder("f")
//                        .longOpt("functionfile")
//                        .hasArg()
//                        .numberOfArgs(Option.UNLIMITED_VALUES)
//                        .desc("one or more function file paths (dynamic functions with relative paths are found relative to the cwd)")
//                        .build();
//
//                Option outputfileOption = Option.builder("o")
//                        .longOpt("outputfile")
//                        .hasArg()
//                        .desc("path to output file (default: stdout)")
//                        .build();
//
//                Option triplesmapsOption = Option.builder("t")
//                        .longOpt("triplesmaps")
//                        .hasArg()
//                        .desc("IRIs of the triplesmaps that should be executed in order, split by ',' (default is all triplesmaps)")
//                        .build();
//
//                Option removeduplicatesOption = Option.builder("d")
//                        .longOpt("duplicates")
//                        .desc("remove duplicates in the output")
//                        .build();
//
//                Option configfileOption = Option.builder("c")
//                        .longOpt("configfile")
//                        .hasArg()
//                        .desc("path to configuration file")
//                        .build();
//
//                Option helpOption = Option.builder("h")
//                        .longOpt("help")
//                        .desc("show help info")
//                        .build();
//
//                Option verboseOption = Option.builder("v")
//                        .longOpt("verbose")
//                        .desc("show more details in debugging output")
//                        .build();
//
//                Option serializationFormatOption = Option.builder("s")
//                        .longOpt("serialization")
//                        .desc("serialization format (nquads (default), turtle, trig, trix, jsonld, hdt)")
//                        .hasArg()
//                        .build();
//
//                Option metadataOption = Option.builder("e")
//                        .longOpt("metadatafile")
//                        .hasArg()
//                        .desc("path to output metadata file")
//                        .build();
//
//                Option metadataDetailLevelOption = Option.builder("l")
//                        .longOpt("metadataDetailLevel")
//                        .hasArg()
//                        .desc("generate metadata on given detail level (dataset - triple - term)")
//                        .build();
//
//
//                options.addOption(mappingdocOption);
//                options.addOption(outputfileOption);
//                options.addOption(functionfileOption);
//                options.addOption(removeduplicatesOption);
//                options.addOption(configfileOption);
//                options.addOption(helpOption);
//                options.addOption(serializationFormatOption);
//                options.addOption(triplesmapsOption);
//                options.addOption(verboseOption);
//                options.addOption(metadataOption);
//                options.addOption(metadataDetailLevelOption);
//
//        }
//
//
//        private static void commandLineParser(Options options, String[] args) {
//
//                CommandLineParser parser = new DefaultParser();
//
//                try {
//                        // parse the command line arguments
//                        CommandLine lineArgs = parser.parse(options, args);
//                        // Check if config file is given
//                        Properties configFile = null;
//                        if (lineArgs.hasOption("c")) {
//                                configFile = new Properties();
//                                configFile.load(Utils.getReaderFromLocation(lineArgs.getOptionValue("c")));
//                        }
//
//                        if (checkOptionPresence(helpOption, lineArgs, configFile)) {
//
//                                printHelp(options);
//                                return;
//                        }
//
//                        if (checkOptionPresence(verboseOption, lineArgs, configFile)) {
//                                setLoggerLevel(Level.DEBUG);
//                        } else {
//                                setLoggerLevel(Level.ERROR);
//                        }
//
//                } catch (ParseException e) {
//                        e.printStackTrace();
//                } catch (IOException e) {
//                        e.printStackTrace();
//                }
//        }
//
//        private static void setLoggerLevel(Level level) {
//                Logger root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
//                ((ch.qos.logback.classic.Logger) root).setLevel(level);
//        }
//
//
//        private static boolean checkOptionPresence(Option option, CommandLine lineArgs, Properties properties) {
//                return lineArgs.hasOption(option.getOpt()) || (properties != null
//                        && properties.getProperty(option.getLongOpt()) != null
//                        && !properties.getProperty(option.getLongOpt()).equals("false"));  // ex: 'help = false' in the config file shouldn't return the help text
//        }
//
//        private static void mOptionValue(Option mappingdocOption, CommandLine lineArgs, Properties configFile){
//
//                String[] mOptionValue = getOptionValues(mappingdocOption, lineArgs, configFile);
//                if (mOptionValue == null) {
//                        printHelp(options);
//                } else {
//                        // Concatenate all mapping files
//                        List<InputStream> lis = Arrays.stream(mOptionValue)
//                                .map(Utils::getInputStreamFromFileOrContentString)
//                                .collect(Collectors.toList());
//                        InputStream is = new SequenceInputStream(Collections.enumeration(lis));
//
//                        Map<String, String> mappingOptions = new HashMap<>();
//                        }
//        }
//
//        private static String[] getOptionValues(Option option, CommandLine lineArgs, Properties properties) {
//                if (lineArgs.hasOption(option.getOpt())) {
//                        return lineArgs.getOptionValues(option.getOpt());
//                } else if (properties != null && properties.getProperty(option.getLongOpt()) != null) {
//                        return properties.getProperty(option.getLongOpt()).split(" ");
//                } else {
//                        return null;
//                }
//        }
//
//        private static void printHelp(Options options) {
//                HelpFormatter formatter = new HelpFormatter();
//                formatter.printHelp("java -jar mapper.jar <options>\noptions:", options);
//        }
//
//        private static void writeOutput(QuadStore store, String outputFile, String format) {
//                boolean hdt = format != null && format.equals("hdt");
//
//                if (hdt) {
//                        try {
//                                format = "nquads";
//                                File tmpFile = File.createTempFile("file", ".nt");
//                                tmpFile.deleteOnExit();
//                                String uncompressedOutputFile = tmpFile.getAbsolutePath();
//
//                                File nquadsFile = writeOutputUncompressed(store, uncompressedOutputFile, format);
//                                Utils.ntriples2hdt(uncompressedOutputFile, outputFile);
//                                nquadsFile.deleteOnExit();
//                        } catch (IOException e) {
//                                e.printStackTrace();
//                        }
//                } else {
//                        if (format != null) {
//                                format = format.toLowerCase();
//                        } else {
//                                format = "nquads";
//                        }
//
//                        writeOutputUncompressed(store, outputFile, format);
//                }
//        }
//
//        private static File writeOutputUncompressed(QuadStore store, String outputFile, String format) {
//                File targetFile = null;
//
//                if (store.size() > 1) {
//                        logger.info(store.size() + " quads were generated");
//                } else {
//                        logger.info(store.size() + " quad was generated");
//                }
//
//                try {
//                        Writer out;
//                        String doneMessage = null;
//
//                        //if output file provided, write to triples output file
//                        if (outputFile != null) {
//                                targetFile = new File(outputFile);
//                                logger.info("Writing quads to " + targetFile.getPath() + "...");
//
//                                if (!targetFile.isAbsolute()) {
//                                        targetFile = new File(System.getProperty("user.dir") + "/" + outputFile);
//                                }
//
//                                doneMessage = "Writing to " + targetFile.getPath() + " is done.";
//
//                                out = new BufferedWriter(new FileWriter(targetFile));
//
//                        } else {
//                                out = new BufferedWriter(new OutputStreamWriter(System.out));
//                        }
//
//                        store.write(out, format);
//                        out.close();
//
//                        if (doneMessage != null) {
//                                logger.info(doneMessage);
//                        }
//                } catch (Exception e) {
//                        System.err.println("Writing output failed. Reason: " + e.getMessage());
//                }
//
//                return targetFile;
//        }
//
//        private static QuadStore getStoreForFormat(String outputFormat) {
//                if (outputFormat == null || outputFormat.equals("nquads") || outputFormat.equals("hdt")) {
//                        return new SimpleQuadStore();
//                } else {
//                        return new RDF4JStore();
//                }
//        }
//
//
//        }
//
//
//
