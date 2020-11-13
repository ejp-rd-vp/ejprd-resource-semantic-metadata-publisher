package uk.ac.ebi.spot.publisher;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class JsonToRDFOptions {

    private Option mappingdocOption;
    private Option outputfileOption;
    private Option functionfileOption;
    private Option removeduplicatesOption;
    private Option configfileOption;
    private Option helpOption;
    private Option verboseOption;
    private Option metadataOption;
    private Option metadataDetailLevelOption;
    private Options options;
    private Option serializationFormatOption;
    private Option triplesmapsOption;

    public JsonToRDFOptions() {

    }

    public Option getMappingdocOption() {
        return mappingdocOption;
    }

    public void setMappingdocOption(Option mappingdocOption) {
        this.mappingdocOption = mappingdocOption;
    }

    public Option getOutputfileOption() {
        return outputfileOption;
    }

    public void setOutputfileOption(Option outputfileOption) {
        this.outputfileOption = outputfileOption;
    }

    public Option getFunctionfileOption() {
        return functionfileOption;
    }

    public void setFunctionfileOption(Option functionfileOption) {
        this.functionfileOption = functionfileOption;
    }

    public Option getRemoveduplicatesOption() {
        return removeduplicatesOption;
    }

    public void setRemoveduplicatesOption(Option removeduplicatesOption) {
        this.removeduplicatesOption = removeduplicatesOption;
    }

    public Option getConfigfileOption() {
        return configfileOption;
    }

    public void setConfigfileOption(Option configfileOption) {
        this.configfileOption = configfileOption;
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
