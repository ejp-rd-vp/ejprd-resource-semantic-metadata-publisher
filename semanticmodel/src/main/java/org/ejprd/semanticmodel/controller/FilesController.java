package org.ejprd.semanticmodel.controller;

import org.apache.commons.io.FilenameUtils;
import org.ejprd.semanticmodel.message.ResponseMessage;
import org.ejprd.semanticmodel.models.converter.FileInfo;
import org.ejprd.semanticmodel.models.converter.JsonLDWriter;
import org.ejprd.semanticmodel.models.converter.RDFWriter;
import org.ejprd.semanticmodel.models.validator.Report;
import org.ejprd.semanticmodel.models.validator.ShexValidation;
import org.ejprd.semanticmodel.models.validator.Validate;
import org.ejprd.semanticmodel.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RestController
@CrossOrigin("http://localhost:8081")
public class FilesController {

    private static final long serialVersionUID = 1L;
    Logger logger = Logger.getLogger(FilesController.class.getName());
    private String currentDirectory = System.getProperty("user.dir");
    private String userUploadedfile = null;

    @Autowired
    FilesStorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("multipartFile") MultipartFile file) {
        String message = "";
        try {
            storageService.save(file);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @PostMapping("/semerd")
    public Report getHome(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String userDir = currentDirectory;
        String myPath = userDir + "/WEB-INF";
        Part filePart = request.getPart("multipartFile"); // an address of the file location
        String fileMaps = request.getParameter("mappingFile");
        String fileExtension = FilenameUtils.getExtension(filePart.getSubmittedFileName());

        System.out.println("The file Uploaded: " + fileMaps);
        System.out.println("The file Uploaded: " + fileExtension);
//        logger.info("The file Uploaded " + fileMaps);
//        logger.info("This is the file extension" + fileExtension);

        Report report = new Report();
        if (fileExtension.equals("json")) {
            try {
                System.out.println("Checked if file extension is " + fileExtension);
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

            System.out.println("Upload a file in json or rdf format please");
        }
        return report;
    }
    public Report mapJsonFile(String mappingFile2, Part filePart, String myPath) throws Exception {
        String userID = UUID.randomUUID().toString(); //generates a global identifier
        String despath = myPath + "/data_schema_" + userID + ".json"; //Creates file on the user's home directory based on the UserID
        File fileToSave = new File(despath);
        InputStream fileInputStream = filePart.getInputStream();
        Files.copy(fileInputStream, fileToSave.toPath());
        String fileLineData = fileReader(despath);
        //Operation to read and replace the name of the input file in the mappingFile
        String mappingFile = "/mapping" + mappingFile2 + ".ttl";
        String fileMapContent = fileReader(myPath + mappingFile);
        String fileMapUpdated = fileMapContent.replace("\"A\"", "\"" + despath + "\"");
        String userFileMapPath = myPath + "/data_map_" + userID + ".ttl";
        InputStream updatedDataInputStream = new ByteArrayInputStream(fileMapUpdated.getBytes());
        File userMapFile = new File(userFileMapPath);
        Files.copy(updatedDataInputStream, userMapFile.toPath());

        String userMappingOutput = myPath + "/outdata_" + userID + ".ttl";
        userMappingOutput = myPath + "/person.ttl";
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

        Path schemaPath = Paths.get(myPath + "/datatypes.json");
        Path dataPath = Paths.get(myPath + "/datatypes-data.ttl");

        Validate.validateGaphAgainstSchema("","","","");

//        Path schemaPath = Paths.get(myPath + "/validateShex/" + mappingFile2.toLowerCase() + ".json"); //schema??
//        Path dataPath = Paths.get(userMappingOutput);



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


    public String fileReader(String filePath) {
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

    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();

            return new FileInfo(filename, url);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}