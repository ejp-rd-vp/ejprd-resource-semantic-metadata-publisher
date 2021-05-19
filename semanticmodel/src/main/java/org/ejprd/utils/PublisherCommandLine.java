package org.ejprd.utils;


import ch.qos.logback.classic.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.ejprd.converter.ParserOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class PublisherCommandLine {

    public static ParserOptions getJsonToRDFOptions(){

        Options options = new Options();

        Option mappingdocOption = Option.builder("m")
                .longOpt("mappingfile")
                .hasArg()
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .desc("one or more mapping file paths and/or strings " +
                        "(multiple values are concatenated). " +
                        "r2rml is converted to rml if needed using" +
                        " the r2rml arguments.")
                .build();

        Option outputfileOption = Option.builder("o")
                .longOpt("outputfile")
                .hasArg()
                .desc("path to output file (default: stdout)")
                .build();

        Option functionfileOption = Option.builder("f")
                .longOpt("functionfile")
                .hasArg()
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .desc("one or more function file paths (dynamic functions with relative paths are found relative to the cwd)")
                .build();

        Option triplesmapsOption = Option.builder("t")
                .longOpt("triplesmaps")
                .hasArg()
                .desc("IRIs of the triplesmaps that should be executed in order, split by ',' (default is all triplesmaps)")
                .build();

        Option removeduplicatesOption = Option.builder("d")
                .longOpt("duplicates")
                .desc("remove duplicates in the output")
                .build();

        Option configfileOption = Option.builder("c")
                .longOpt("configfile")
                .hasArg()
                .desc("path to configuration file")
                .build();

        Option helpOption = Option.builder("h")
                .longOpt("help")
                .desc("show help info")
                .build();

        Option verboseOption = Option.builder("v")
                .longOpt("verbose")
                .desc("show more details in debugging output")
                .build();
        Option metadataOption = Option.builder("e")
                .longOpt("metadatafile")
                .hasArg()
                .desc("path to output metadata file")
                .build();

        Option metadataDetailLevelOption = Option.builder("l")
                .longOpt("metadataDetailLevel")
                .hasArg()
                .desc("generate metadata on given detail level (dataset - triple - term)")
                .build();

        Option serializationFormatOption = Option.builder("s")
                .longOpt("serialization")
                .desc("serialization format (nquads (default), turtle, trig, trix, jsonld, hdt)")
                .hasArg()
                .build();

        options.addOption(mappingdocOption);
        options.addOption(outputfileOption);
        options.addOption(functionfileOption);
        options.addOption(removeduplicatesOption);
        options.addOption(configfileOption);
        options.addOption(helpOption);
        options.addOption(verboseOption);
        options.addOption(metadataOption);
        options.addOption(serializationFormatOption);
        options.addOption(metadataDetailLevelOption);
        options.addOption(triplesmapsOption);

        //Aggregates command line Options in a JSONToRDFOptions Object
        ParserOptions parserOptions = new ParserOptions();
        parserOptions.setMappingDocOption(mappingdocOption);
        parserOptions.setOutputFileOption(outputfileOption);
        parserOptions.setFunctionFileOption(functionfileOption);
        parserOptions.setRemoveDuplicatesOption(removeduplicatesOption);
        parserOptions.setConfigFileOption(configfileOption);
        parserOptions.setHelpOption(helpOption);
        parserOptions.setVerboseOption(verboseOption);
        parserOptions.setMetadataOption(metadataOption);
        parserOptions.setMetadataDetailLevelOption(metadataDetailLevelOption);
        parserOptions.setOptions(options);
        parserOptions.setSerializationFormatOption(serializationFormatOption);
        parserOptions.setTriplesmapsOption(triplesmapsOption);

        return parserOptions;
    }

    public static boolean checkOptionPresence(Option option, CommandLine lineArgs, Properties properties) {
        return lineArgs.hasOption(option.getOpt()) || (properties != null
                && properties.getProperty(option.getLongOpt()) != null
                && !properties.getProperty(option.getLongOpt()).equals("false"));  // ex: 'help = false' in the config file shouldn't return the help text
    }

    public static String getPriorityOptionValue(Option option, CommandLine lineArgs, Properties properties) {
        if (lineArgs.hasOption(option.getOpt())) {
            return lineArgs.getOptionValue(option.getOpt());
        } else if (properties != null && properties.getProperty(option.getLongOpt()) != null) {
            return properties.getProperty(option.getLongOpt());
        } else {
            return null;
        }
    }

    public static String[] getOptionValues(Option option, CommandLine lineArgs, Properties properties) {
        if (lineArgs.hasOption(option.getOpt())) {
            return lineArgs.getOptionValues(option.getOpt());
        } else if (properties != null && properties.getProperty(option.getLongOpt()) != null) {
            return properties.getProperty(option.getLongOpt()).split(" ");
        } else {
            return null;
        }
    }


    public static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar mapper.jar <options>\noptions:", options);
    }

    public static void setLoggerLevel(Level level) {
        Logger root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        ((ch.qos.logback.classic.Logger) root).setLevel(level);
    }

}
