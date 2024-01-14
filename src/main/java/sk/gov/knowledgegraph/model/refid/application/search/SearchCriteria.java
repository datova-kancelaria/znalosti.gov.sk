package sk.gov.knowledgegraph.model.refid.application.search;

public abstract class SearchCriteria {

    private int start = 0;
    private Integer rows = null;

    public SearchCriteria() {
    }


    public SearchCriteria(int start, Integer rows) {
        this.start = start;
        this.rows = rows;
    }


    public int getStart() {
        return start;
    }


    public void setStart(int start) {
        this.start = start;
    }


    public Integer getRows() {
        return rows;
    }


    public void setRows(Integer rows) {
        this.rows = rows;
    }
}