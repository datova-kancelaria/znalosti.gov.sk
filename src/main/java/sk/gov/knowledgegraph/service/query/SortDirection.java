
package sk.gov.knowledgegraph.service.query;

public enum SortDirection {

    ASC,
    DESC;

    public String value() {
        return name();
    }

    public static SortDirection fromValue(String v) {
        return valueOf(v);
    }

}
