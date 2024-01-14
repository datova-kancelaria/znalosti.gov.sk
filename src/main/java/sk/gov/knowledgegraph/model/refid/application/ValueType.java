package sk.gov.knowledgegraph.model.refid.application;

public enum ValueType {
    /**
     * http://www.w3.org/2001/XMLSchema#string
     */
    STRING,
    /**
     * http://www.w3.org/2001/XMLSchema#boolean
     */
    BOOLEAN,
    /**
     * http://www.w3.org/2001/XMLSchema#integer
     */
    INTEGER,
    /**
     * http://www.w3.org/2001/XMLSchema#positiveInteger
     */
    POSITIVE_INTEGER,
    /**
     * http://www.w3.org/2001/XMLSchema#nonNegativeInteger
     */
    NON_NEGATIVE_INTEGER, UNKNONW,
    /**
     * http://www.w3.org/2001/XMLSchema#date
     */
    DATE,
    /**
     * http://www.w3.org/2001/XMLSchema#dateTime
     */
    DATETIME,
    /**
     * http://www.w3.org/2001/XMLSchema#gYear
     */
    YEAR,
    /**
     * http://www.w3.org/2001/XMLSchema#decimal
     */
    DECIMAL,
    /**
     * http://www.w3.org/2001/XMLSchema#long
     */
    LONG,
    /**
     * http://www.w3.org/2001/XMLSchema#double
     */
    DOUBLE,
    /**
     * http://www.w3.org/2001/XMLSchema#float
     */
    FLOAT
}