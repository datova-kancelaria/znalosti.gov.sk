package sk.gov.knowledgegraph.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import sk.gov.knowledgegraph.model.AbstractEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public class Dataset extends AbstractEntity {

    private String publisher;
    private String publisherName;
    private String dataset;
    private String datasetTitle;
    private String catalog;
    private String catalogTitle;
    private String theme;
    private String themeLabel;
    private String version;

}
