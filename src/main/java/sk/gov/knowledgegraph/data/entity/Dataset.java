package sk.gov.knowledgegraph.data.entity;

import javax.persistence.Entity;

import sk.gov.knowledgegraph.data.AbstractEntity;

@Entity
public class Dataset extends AbstractEntity {

    private String publisher;
    private String publisherName;
    private String dataset;
    private String datasetTitle;
    private String catalog;
    private String catalogTitle;
    private String theme;
    private String themeLabel;
    
    public String getThemeLabel() {
		return themeLabel;
	}

	public void setThemeLabel(String themeLabel) {
		this.themeLabel = themeLabel;
	}
	
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getDatasetTitle() {
        return datasetTitle;
    }

    public void setDatasetTitle(String datasetTitle) {
        this.datasetTitle = datasetTitle;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getCatalogTitle() {
        return catalogTitle;
    }

    public void setCatalogTitle(String catalogTitle) {
        this.catalogTitle = catalogTitle;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }
    
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

    private String version;
    
    

}
