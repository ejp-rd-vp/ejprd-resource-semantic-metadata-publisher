package org.ejprd.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    private String inputFile;
    private String outputFileInTTl;
    private String userJsonLDFile;
    private String sheVal;

}
