package sk.gov.knowledgegraph.model.entity;

import lombok.Data;

@Data
public class Result {

    private String subject;
    private String property;
    private String object;
    private String graph;
    private String searchString;
    private String graphName;
    private int startIndex;
    private int endIndex;

}
