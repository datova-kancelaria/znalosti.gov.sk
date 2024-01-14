package sk.gov.knowledgegraph.model.refid.application.search;

public enum SemanticResourceRegistrationApplicationType {
    /**
     * Ontológia
     */
    ONTOLOGY,
    /**
     * Verzia ontológie
     */
    ONTOLOGY_VERSION,
    /**
     * Trieda
     */
    ONTOLOGY_COMPONENT_CLASS,
    /**
     * Dátová vlastnosť
     */
    ONTOLOGY_COMPONENT_DATA_TYPE,
    /**
     * Objektová vlastnosť
     */
    ONTOLOGY_COMPONENT_OBJECT_TYPE,
    /**
     * Šablóna URI
     */
    URI_TEMPLATE;
}
