package org.ejprd;

import com.google.gson.Gson;
import org.apache.commons.io.FilenameUtils;

import org.ejprd.converter.JsonLDWriter;
import org.ejprd.converter.RDFWriter;
import org.ejprd.exception.CyclicReferencesException;
import org.ejprd.exception.NotStratifiedException;
import org.ejprd.exception.UndefinedReferenceException;
import org.ejprd.validator.Report;
import org.ejprd.validator.ShexValidation;



import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import java.util.logging.Logger;

@WebServlet(name = "SemanticERDServlet", urlPatterns = {"/semerd"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10,   // 10 MB
        maxFileSize = 1024 * 1024 * 50,          // 50 MB
        maxRequestSize = 1024 * 1024 * 100)      // 100 MB

public class SemanticERDServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    Logger logger = Logger.getLogger(SemanticERDServlet.class.getName());

    public String fileReader(String filePath) {
        String resultOutput = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                resultOutput += string + "\n";
            }
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            logger.info("The file you are trying to upload" + filePath + "does not exist!");

        } catch (IOException ioException) {
            ioException.printStackTrace();
            logger.info("The file you are trying to upload" + filePath + "is not readable");
        }
        return resultOutput;
    }

    // Method to handle POST method request.
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String myPath = getServletContext().getRealPath("/WEB-INF"); //contain client uploaded files
        PrintWriter output = response.getWriter();
        Part filePart = request.getPart("multipartFile"); // an address of the file location
        String fileMaps = request.getParameter("mappingFile");
        String fileExtension = FilenameUtils.getExtension(filePart.getSubmittedFileName());

       // logger.info(fileExtension +"    "+ filePart.getSubmittedFileName());

        Report report = new Report();
        if (fileExtension.equals("json")) {
            try {
                report = mapJsonFile(fileMaps, filePart , myPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (fileExtension.equals("ttl") || fileExtension.equals("nt")){
            try {
                report = mapTTLFile(filePart , fileMaps, myPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else {

            logger.info("Upload RDF format file please");

        }

        String reportFiles = new Gson().toJson(report);
        output.println(reportFiles);
        output.flush();
        logger.info("userJsonLDFile");


    }


    public Report mapJsonFile(String mappingFile2, Part filePart, String myPath) throws Exception {
        String userID = UUID.randomUUID().toString(); //generates a global identifier
        String despath = myPath + "/data_schema_" + userID + ".json"; //Creates file on the user's home directory based on the UserID
        File fileToSave = new File(despath);
        InputStream fileInputStream = filePart.getInputStream();
        Files.copy(fileInputStream, fileToSave.toPath());
        String fileLineData = fileReader(despath);
        log("mappingFile is not uploaded");

        //Operation to read and replace the name of the input file in the mappingFile
        String mappingFile = "/mapping" + mappingFile2 + ".ttl";
        String fileMapContent = fileReader(myPath + mappingFile);
        String fileMapUpdated = fileMapContent.replace("\"A\"", "\"" + despath + "\"");
        String userFileMapPath = myPath + "/data_map_" + userID + ".ttl";
        InputStream updatedDataInputStream = new ByteArrayInputStream(fileMapUpdated.getBytes());
        File userMapFile = new File(userFileMapPath);
        Files.copy(updatedDataInputStream, userMapFile.toPath());

        String userMappingOutput = myPath + "/outdata_" + userID + ".ttl";
        converter(userFileMapPath, userMappingOutput);

        Report report = convertMapper(userMappingOutput, mappingFile2 ,myPath);
        report.setInputFile(fileLineData);
        return report;
    }

    public Report mapTTLFile(Part filePart,String mappingFile2, String myPath ) throws Exception {
        String userID = UUID.randomUUID().toString(); //generates a global identifier
        String despath = myPath + "/data_InTurtle_" + userID + ".ttl"; //Creates file on the user's home directory based on the UserID
        File fileToSave = new File(despath);
        InputStream fileInputStream = filePart.getInputStream();
        Files.copy(fileInputStream, fileToSave.toPath());
        logger.info("The mapping file is :" + mappingFile2);
        return convertMapper(despath, mappingFile2, myPath );
    }

    public Report convertMapper(String userMappingOutput ,String mappingFile2, String myPath) throws Exception {

        String fileOutPutInTTL = fileReader(userMappingOutput);
        String ctxPath = myPath + "/contextsFiles/"+mappingFile2.toLowerCase()+"Context.json";
        String framePath = myPath + "/framesFiles/"+mappingFile2.toLowerCase()+"Frame.json";

        JsonLDWriter jsonLDWriter = new JsonLDWriter();
        String userJsonLDFile = null;
        userJsonLDFile = jsonLDWriter.toJsonLd(userMappingOutput, ctxPath, framePath);

        ShexValidation shexValidator = new ShexValidation();
        Path schemaPath = Paths.get(myPath + "/datatypes.json"); //to change form parameter
        Path dataPath = Paths.get(myPath + "/datatypes-data.ttl"); //to change form parameter



        String sheVal = shexValidator.doShexValidation(schemaPath, dataPath);

        return Report.builder()
                .outputFileInTTl(fileOutPutInTTL)
                .userJsonLDFile(userJsonLDFile)
                .sheVal(sheVal)
                .build();
    }

    public String converter(String userFileMapPath, String userMappingOutput) {
        String[] args = new String[4];
        args[0] = "-m";
        args[1] = userFileMapPath;
        args[2] = "-o";
        args[3] = userMappingOutput;
        RDFWriter RDFWriter = new RDFWriter();
        RDFWriter.jsonToRDF(args);
        return "successful";
    }
}
