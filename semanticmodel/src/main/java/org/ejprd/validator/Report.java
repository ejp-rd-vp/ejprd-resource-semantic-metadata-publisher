package org.ejprd.validator;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Report {

    private String inputFile;
    private String outputFileInTTl;
    private String userJsonLDFile;
    private String sheVal;
}
