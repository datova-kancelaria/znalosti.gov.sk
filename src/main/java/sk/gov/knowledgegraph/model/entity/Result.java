package sk.gov.knowledgegraph.model.entity;

public class Result {

    private String subject;
    private String property;
    private String object;
    private String graph;
    private String searchString;
    private String graphName;

    public int getStartIndex() {
        return startIndex;
    }


    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }


    public int getEndIndex() {
        return endIndex;
    }


    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    private int startIndex;
    private int endIndex;

    public String getSubject() {
        return subject;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }


    public String getProperty() {
        return property;
    }


    public void setProperty(String property) {
        this.property = property;
    }


    public String getObject() {
        return object;
    }


    public void setObject(String object) {
        this.object = object;
    }


    public String getGraph() {
        return graph;
    }


    public void setGraph(String graph) {
        this.graph = graph;
    }


    public String getGraphName() {
        return graphName;
    }


    public void setGrapNameh(String graphName) {
        this.graphName = graphName;
    }


    public String getSearchString() {
        return searchString;
    }


    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
