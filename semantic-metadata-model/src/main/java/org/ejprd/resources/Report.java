package org.ejprd.resources;

public class Report {

    private String inputFile;
    private String outputFileInTTl;

    public Report(String inputFile, String outputFileInTTl) {
        this.inputFile = inputFile;
        this.outputFileInTTl = outputFileInTTl;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getOutputFileInTTl() {
        return outputFileInTTl;
    }

    public void setOutputFileInTTl(String outputFileInTTl) {
        this.outputFileInTTl = outputFileInTTl;
    }
}
