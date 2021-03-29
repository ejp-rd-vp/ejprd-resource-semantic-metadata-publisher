package org.ejprd;


import com.google.gson.Gson;
import org.ejprd.convert.JsonToRDF;

import org.ejprd.convertRdfJson.FromRdf;
import org.ejprd.utils.Report;

import org.ejprd.validator.ShexVal;

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


@WebServlet(name = "SemerdServlet", urlPatterns = {"/semerd"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10,   // 10 MB
        maxFileSize = 1024 * 1024 * 50,          // 50 MB
        maxRequestSize = 1024 * 1024 * 100)      // 100 MB

public class SemerdServlet extends HttpServlet {

    //Logger log = LoggerFactory.getLogger(this.getClass());
    private static final long serialVersionUID = 1L;

    public String fileReader(String filePath) {
        String resultOutput = "";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String str;
            while ((str = br.readLine()) != null) {
                resultOutput += str + "\n";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultOutput;
    }

    // Method to handle POST method request.
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //ServletContext servletContext = getServletContext();

        doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String userID = UUID.randomUUID().toString(); //generates a global identifier
        PrintWriter out = response.getWriter();
        Part filePart = request.getPart("multipartFile"); // an address of the file location
        InputStream fileInputStream = filePart.getInputStream();

        String myPath = getServletContext().getRealPath("/WEB-INF"); //contain client uploaded files
        String despath = myPath + "/data_schema_" + userID + ".json"; //Creates file on the user's home directory based on the UserID
        File fileToSave = new File(despath);
        Files.copy(fileInputStream, fileToSave.toPath());
        String fileLineData = fileReader(despath);

        String mappingFile2 = request.getParameter("mappingFile");

        log("mappingFile is not uploaded");

        //Operation to read and replace the name of the input file in the mappingFile
        String mappingFile = "/" + mappingFile2;
        String fileMapContent = fileReader(myPath + mappingFile);
        String fileMapUpdated = fileMapContent.replace("\"A\"", "\"" + despath + "\"");

        String userFileMapPath = myPath + "/data_map_" + userID + ".ttl";
        String userMappingOutput = myPath + "/outdata_" + userID + ".ttl";


        InputStream updatedDataInputStream = new ByteArrayInputStream(fileMapUpdated.getBytes());
        File userMapFile = new File(userFileMapPath);
        Files.copy(updatedDataInputStream, userMapFile.toPath());
        converter(userFileMapPath, userMappingOutput);
        String fileOutPutInTTL = fileReader(userMappingOutput);
        Report report = new Report(fileLineData, fileOutPutInTTL);
        String reportFiles = new Gson().toJson(report);
        out.println(reportFiles);
        out.flush();

        String filePath = "/Users/dipo/Documents/GitHub/ejprd-resource-semantic-metadata-publisher/semanticmodel/src/main/resources/turtlesNdJson/n3-data.n3";
        String ctxPath = "/Users/dipo/Documents/GitHub/ejprd-resource-semantic-metadata-publisher/semanticmodel/src/main/resources/turtlesNdJson/context.json";
        String framePath = "/Users/dipo/Documents/GitHub/ejprd-resource-semantic-metadata-publisher/semanticmodel/src/main/resources/turtlesNdJson/anno-frame.json";
        FromRdf fromRdf = new FromRdf();
        String userJsonLDFile =  fromRdf.toJsonLd(filePath, ctxPath, framePath);
        //String userConJsonLD =  new Gson().toJson(userJsonLDFile);
        //out.println (userJsonLDFile );
        // out.flush();

        ShexVal shexVal = new ShexVal();
        Path schemaPath = Paths.get("/Users/dipo/Documents/GitHub/ejprd-resource-semantic-metadata-publisher/src/main/resources/shex/datatypes.json"); //to change form parameter
        Path dataPath = Paths.get("/Users/dipo/Documents/GitHub/ejprd-resource-semantic-metadata-publisher/src/main/resources/shex/datatypes-data.ttl"); //to change form parameter
        //String sheVal;
        try {
            //sheVal =  shexVal.shexVal(schemaPath, dataPath);
            shexVal.shexVal(schemaPath, dataPath);
            // out.println(sheVal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String converter(String userFileMapPath, String userMappingOutput) {
        String[] args = new String[4];
        args[0] = "-m";
        args[1] = userFileMapPath;
        args[2] = "-o";
        args[3] = userMappingOutput;
        JsonToRDF jsonToRDF = new JsonToRDF();
        jsonToRDF.jsonToRDF(args);
        return "successful";
    }
}

















