package org.ejpraredisease.resources;

import org.ejpraredisease.resources.converter.JsonToRDF;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Files;
import java.util.UUID;


@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10,   // 10 MB
        maxFileSize = 1024 * 1024 * 50,          // 50 MB
        maxRequestSize = 1024 * 1024 * 100)      // 100 MB

@WebServlet(name = "LMSServlet", urlPatterns = {"/LMSServlet"})
public class LMSServlet extends HttpServlet {


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
        doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String userID = UUID.randomUUID().toString(); //generates a global identifier
        PrintWriter out = response.getWriter();
        String docType = "<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n";
        out.println(docType);
        out.println("<form action=\"/Semerd_war/LMSServlet\" method=\"post\" enctype=\"multipart/form-data\">");
        out.println("<input type=\"file\" name=\"fileToUpload\">");
        out.println("<br/><br/>");
        out.println("<input type=\"submit\" value=\"Submit\">");
        out.println("</form>");
        Part filePart = request.getPart("fileToUpload"); // an address of the file location
        out.println(filePart.getSubmittedFileName()); // prints the name of the file uploaded
        InputStream fileInputStream = filePart.getInputStream();
        String myPath = getServletContext().getRealPath("/WEB-INF"); //contain client uploaded files
        String despath = myPath + "/data_schema_" + userID + ".json"; //Creates file on the user's home directory based on the UserID
        File fileToSave = new File(despath);
        out.println("<br>" + "<br>" + "<br>");
        out.println(myPath);
        out.println(despath);
        Files.copy(fileInputStream, fileToSave.toPath());
        //Files.copy(fileInputStream, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
        String fileUrl = ("http://localhost:8080/uploaded-files/" + filePart.getSubmittedFileName());
        String fileLineData = fileReader(despath);
        out.println(fileLineData);
        out.println("<fieldset><legend><em>Direct Input</em></legend>");
        out.println("<textarea  style=\"margin: 0px; width: 490px; height: 300px;\" " +
                "name=\"\\&quot;fileInJson\\&quot;\">");
        out.println(fileLineData);
        out.println("</textarea>");
        out.println("</fieldset>");

        out.println("<p><input id=\"submit2\" name=\"submit2\" type=\"button\" value=\"Convert to RDF\" /></p>");
        String fileMapContent = fileReader(myPath + "/mappingOrganisation.ttl");
        String fileMapUpdated = fileMapContent.replace("\"A\"", "\"" + despath + "\"");

        String userFileMapPath = myPath + "/data_map_" + userID + ".ttl";
        String userMappingOutput = myPath + "/outdata_" + userID + ".ttl";
        InputStream updatedDataInputStream = new ByteArrayInputStream(fileMapUpdated.getBytes());
        File userMapFile = new File(userFileMapPath);
        Files.copy(updatedDataInputStream, userMapFile.toPath());

        converter(userFileMapPath,userMappingOutput);
        String  userOutputTTL = fileReader(userMappingOutput);
        out.println("<fieldset><legend><em>Output In Turtle</em></legend>");
        out.println("<textarea  style=\"margin: 0px; width: 490px; height: 300px;\" " +
                "name=\"\\&quot;fileInJson\\&quot;\">");
        out.println(userOutputTTL);
        out.println("</textarea>");
        out.println("</fieldset>");

        out.println("<p><input id=\"submit2\" name=\"submit2\" type=\"button\" value=\"Convert to RDF\" /></p>");
        //out.println("<pre>" + fileMapUpdated + "</pre>");


    }

    public String converter(String userFileMapPath, String userMappingOutput) {
        String[] args =new String[4];
        args[0] = "-m";
        args[1] = userFileMapPath;
        args[2] = "-o";
        args[3] = userMappingOutput;

        JsonToRDF jsonToRDF = new JsonToRDF();
        jsonToRDF.jsonToRDF(args);

        return "successful";
    }

}

