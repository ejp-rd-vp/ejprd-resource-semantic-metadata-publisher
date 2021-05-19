package org.ejprd.converter;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class ParserOptions {

    //The command line options

    private Option mappingDocOption;
    private Option outputFileOption;
    private Option functionFileOption;
    private Option removeDuplicatesOption;
    private Option configFileOption;
    private Option helpOption;
    private Option verboseOption;
    private Option metadataOption;
    private Option metadataDetailLevelOption;
    private Option serializationFormatOption;
    private Option triplesmapsOption;
    private Options options;

    public ParserOptions() {

    }

    public Option getMappingDocOption() {
        return mappingDocOption;
    }

    public void setMappingDocOption(Option mappingDocOption) {
        this.mappingDocOption = mappingDocOption;
    }

    public Option getOutputFileOption() {
        return outputFileOption;
    }

    public void setOutputFileOption(Option outputFileOption) {
        this.outputFileOption = outputFileOption;
    }

    public Option getFunctionFileOption() {
        return functionFileOption;
    }

    public void setFunctionFileOption(Option functionFileOption) {
        this.functionFileOption = functionFileOption;
    }

    public Option getRemoveDuplicatesOption() {
        return removeDuplicatesOption;
    }

    public void setRemoveDuplicatesOption(Option removeDuplicatesOption) {
        this.removeDuplicatesOption = removeDuplicatesOption;
    }

    public Option getConfigFileOption() {
        return configFileOption;
    }

    public void setConfigFileOption(Option configFileOption) {
        this.configFileOption = configFileOption;
    }

    public Option getHelpOption() {
        return helpOption;
    }

    public void setHelpOption(Option helpOption) {
        this.helpOption = helpOption;
    }

    public Option getVerboseOption() {
        return verboseOption;
    }

    public void setVerboseOption(Option verboseOption) {
        this.verboseOption = verboseOption;
    }

    public Option getMetadataOption() {
        return metadataOption;
    }

    public void setMetadataOption(Option metadataOption) {
        this.metadataOption = metadataOption;
    }

    public Option getMetadataDetailLevelOption() {
        return metadataDetailLevelOption;
    }

    public void setMetadataDetailLevelOption(Option metadataDetailLevelOption) {
        this.metadataDetailLevelOption = metadataDetailLevelOption;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public Option getSerializationFormatOption() {
        return serializationFormatOption;
    }

    public void setSerializationFormatOption(Option serializationFormatOption) {
        this.serializationFormatOption = serializationFormatOption;
    }

    public Option getTriplesmapsOption() {
        return triplesmapsOption;
    }

    public void setTriplesmapsOption(Option triplesmapsOption) {
        this.triplesmapsOption = triplesmapsOption;
    }

}
