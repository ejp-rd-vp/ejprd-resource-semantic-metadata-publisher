package org.ejprd.converter;


import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.slf4j.LoggerFactory.getLogger;

public class JsonLDWriter {

    public JsonLDWriter() {
    }

    private static final Logger logger = getLogger(JsonLDWriter.class);

    /**
     * @param ntriples String
     * @param contextUri String
     * @param frameUri String
     * @return String
     * @throws IOException IOException
     * @throws JsonLdError JsonLdError
     */
    public static String toJsonLd(final String ntriples, final String contextUri, final String frameUri) throws Exception {
        final Object ctxobj;
        final Object frame;
        final Object outobj;
        final Object compactobj;
        final Object frameobj;
        final JsonLdOptions opts = new JsonLdOptions("");
        opts.setUseNativeTypes(true);

        final InputStream is = new FileInputStream(contextUri);
        String ntriplesContent = fileReader(ntriples);
        ctxobj = JsonUtils.fromInputStream(is);

        if (SerializePattern.isNotEmpty(ntriplesContent)) {
            final String graph = SerializePattern.convertSkolem(ntriplesContent);
            outobj = JsonLdProcessor.fromRDF(graph, opts);
            compactobj = JsonLdProcessor.compact(outobj, ctxobj, opts);
            final InputStream fileInputStream = new FileInputStream(frameUri);
            frame = JsonUtils.fromInputStream(fileInputStream);
            frameobj = JsonLdProcessor.frame(compactobj, frame, opts);

            System.out.println(JsonUtils.toPrettyString(frameobj));

            Files.write(Paths.get("output.json"), JsonUtils.toPrettyString(compactobj).getBytes());

            return JsonUtils.toPrettyString(frameobj);
        } else {

            final String empty = "empty SPARQL result set";
            logger.error(empty);
            throw new Exception("Wrong file type uploaded");

        }


    }
    public static String fileReader(String filePath) {
        String resultOutput = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                resultOutput += string + "\n";
            }
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return resultOutput;
    }
}
