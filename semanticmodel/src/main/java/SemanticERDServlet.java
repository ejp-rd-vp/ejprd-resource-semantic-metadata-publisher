import com.google.gson.Gson;
import org.ejprd.converter.JsonLDWriter;
import org.ejprd.converter.RDFWriter;
import org.ejprd.validator.Report;
import org.ejprd.validator.ShexValidator;

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

    Logger log = Logger.getLogger(SemanticERDServlet.class.getName());
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

        //String filePath1= userFileMapPath;

        //log.info("userMappingOutput");

        String filePath = userMappingOutput;
        //String framePath = request.getParameter("dataFrame");
        //String ctxPath = request.getParameter("dataContext")
        //String filePath = myPath + "/personData.n3";

        String ctxPath =myPath + "/contextsFiles/personContext.json";
        String framePath = myPath +"/framesFiles/personFrame.json";


//        String ctxPath =myPath + "/contextsFiles/organisationContext.json";
//        String framePath = myPath +"/framesFiles/organisationFrame.json";


//        String ctxPath =myPath + "/contextsFiles/locationContext.json";
//        String framePath = myPath +"/framesFiles/locationFrame.json";

//        String ctxPath =myPath + "/contextsFiles/distributionContext.json";
//        String framePath = myPath +"/framesFiles/distributionFrame.json";
//
//        String ctxPath =myPath + "/contextsFiles/datasetContext.json";
//        String framePath = myPath +"/framesFiles/datasetFrame.json";
//
//        String ctxPath =myPath + "/contextsFiles/dataserviceContext.json";
//        String framePath = myPath +"/framesFiles/dataserviceFrame.json";

//        String ctxPath =myPath + "/contextsFiles/resourcesContext.json";
//        String framePath = myPath +"/framesFiles/resourcesFrame.json";



//        String ctxPath =myPath + "/contextsFiles/catalogContext.json";
//        String framePath = myPath +"/framesFiles/catalogFrame.json";



        JsonLDWriter jsonLDWriter = new JsonLDWriter();
        String userJsonLDFile =  jsonLDWriter.toJsonLd(filePath, ctxPath, framePath);

        ShexValidator shexValidator = new ShexValidator();
        Path schemaPath = Paths.get(myPath + "/datatypes.json"); //to change form parameter
        Path dataPath = Paths.get(myPath + "/datatypes-data.ttl"); //to change form parameter

        String sheVal  = shexValidator.doshexValidator(schemaPath, dataPath);

        Report report = new Report(fileLineData, fileOutPutInTTL,userJsonLDFile,  sheVal);
        String reportFiles = new Gson().toJson(report);
        out.println(reportFiles);
        out.flush();

        log.info("userJsonLDFile");
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
