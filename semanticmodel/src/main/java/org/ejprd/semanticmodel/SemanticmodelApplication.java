package org.ejprd.semanticmodel;


import org.ejprd.semanticmodel.service.FilesStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;


@SpringBootApplication
public class SemanticmodelApplication implements CommandLineRunner {
    static Logger logger = LoggerFactory.getLogger(SemanticmodelApplication.class);

    @Resource
    FilesStorageService storageService;

    public static void main(String[] args) {
        SpringApplication.run(SemanticmodelApplication.class, args);
        logger.info("Semantic Metadata Model App Starts");
    }








    @Override
    public void run(String... arg) throws Exception {
        storageService.deleteAll();
        logger.info("All existing files has been deleted");
        storageService.init();
    }
}



