package org.ejprd.convertRdfJson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class SerializePattern {

    private SerializePattern() {
    }

    /**
     * Converts a SkolemIRI to a BNode.
     *
     * @param input the SkolemIRI to convert.
     * @return a BNode.
     */
    public static String convertSkolem(final String input) {
        final Pattern p = Pattern.compile("trellis:bnode/([0-9a-f]{8}-([0-9a-f]{4}-){3}[0-9a-f]{44})");
        final Matcher m = p.matcher(input);
        final StringBuffer sb = new StringBuffer(input.length());
        while (m.find()) {
            final String id = m.group(1);
            final String bnode = "_:b" + id;
            m.appendReplacement(sb, Matcher.quoteReplacement(bnode));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Converts a trellis:data node to a concrete hostname.
     *
     * @param input the dataset to convert.
     * @param hostname the hostname.
     * @return a BNode.
     */
    static String convertHostname(final String input, final String hostname) {
        final Pattern p = Pattern.compile("(http://ex.com/)");
        //final Pattern p = Pattern.compile("(trellis:data/)(.*)");
        final Matcher m = p.matcher(input);
        final StringBuffer sb = new StringBuffer(input.length());
        while (m.find()) {
            final String path = m.group(2);
            final String node = hostname + "/" + path;
            m.appendReplacement(sb, Matcher.quoteReplacement(node));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * @param input input
     * @return boolean
     */
    public static boolean isNotEmpty(final String input) {
        final Pattern p = Pattern.compile("^<");
        final Matcher m = p.matcher(input);
        return m.find();
    }
}

