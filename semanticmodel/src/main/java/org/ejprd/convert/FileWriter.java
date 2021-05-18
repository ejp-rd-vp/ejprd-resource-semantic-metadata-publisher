package org.ejprd.convert;

import be.ugent.rml.Utils;
import be.ugent.rml.store.QuadStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

//Why look at the hdt format?

public class FileWriter {
    private static final Logger logger = LoggerFactory.getLogger(FileWriter.class);
    public static void writeOutput(QuadStore store, String outputFile, String format) {
        boolean hdt = format != null && format.equals("hdt");
        //
        if (hdt) {
            try {
                format = "nquads";
                File tmpFile = File.createTempFile("file", ".nt");
                tmpFile.deleteOnExit();
                String uncompressedOutputFile = tmpFile.getAbsolutePath();
                File nquadsFile = writeOutputUncompressed(store, uncompressedOutputFile, format);
                Utils.ntriples2hdt(uncompressedOutputFile, outputFile);
                nquadsFile.deleteOnExit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else{
            if (format != null) {
                format = format.toLowerCase();
            } else {
                format = "nquads";
            }
            writeOutputUncompressed(store, outputFile, format);
        }
    }

    public static File writeOutputUncompressed(QuadStore store, String outputFile, String format) {
        File targetFile = null;

        if (store.size() > 1) {
            logger.info(store.size() + " quads were generated");
        } else {
            logger.info(store.size() + " quad was generated");
        }

        try {
            Writer out;
            String doneMessage = null;

            //if output file provided, write to triples output file
            if (outputFile != null) {
                targetFile = new File(outputFile);
                logger.info("Writing quads to " + targetFile.getPath() + "...");

                if (!targetFile.isAbsolute()) {
                    targetFile = new File(System.getProperty("user.dir") + "/" + outputFile);
                }

                doneMessage = "Writing to " + targetFile.getPath() + " is done.";

                out = new BufferedWriter(new java.io.FileWriter(targetFile));

            } else {
                out = new BufferedWriter(new OutputStreamWriter(System.out));
            }

            store.write(out, format);
            out.close();

            if (doneMessage != null) {
                logger.info(doneMessage);
            }
        } catch (Exception e) {
            System.err.println("Writing output failed. Reason: " + e.getMessage());
        }

        return targetFile;
    }

}
