package org.ejprd.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SerializePattern {
    private SerializePattern() {
    }

    /**
     * Converts a SkolemIRI to a BNode.
     * @param input the SkolemIRI to convert.
     * @return a BNode.
     */
    public static String convertSkolem(final String input) {
        final Pattern pattern = Pattern.compile("trellis:bnode/([0-9a-f]{8}-([0-9a-f]{4}-){3}[0-9a-f]{44})");
        final Matcher matcher = pattern.matcher(input);
        final StringBuffer stringBuffer = new StringBuffer(input.length());
        while (matcher.find()) {
            final String id = matcher.group(1);
            final String bnode = "_:b" + id;
            matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(bnode));
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    /**
     * Converts a trellis:data node to a concrete hostname.
     * @param input the dataset to convert.
     * @param hostname the hostname.
     * @return a BNode.
     */
    static String convertHostname(final String input, final String hostname) {
        final Pattern pattern = Pattern.compile("(http://ex.com/)");
//        final Pattern pattern = Pattern.compile("(trellis:data/)(.*)");
        final Matcher matcher = pattern.matcher(input);
        final StringBuffer stringBuffer = new StringBuffer(input.length());
        while (matcher.find()) {
            final String path = matcher.group(2);
            final String node = hostname + "/" + path;
            matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(node));
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    /**
     * @param input input
     * @return boolean
     */
    public static boolean isNotEmpty(final String input) {
        final Pattern pattern = Pattern.compile("^<");
        final Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }
}
