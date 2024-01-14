package sk.gov.knowledgegraph.model.refid.application;

/**
 * Typy stavov platnosti dátových prvkov
 */
public enum ApplicationState {
    /**
     * Rozpracovaný stav, zatiaľ nedaný na schvaľovanie do pracovenej skupiny
     */
    DRAFT,
    /**
     * Daný na schvalenie do pracovnej skupiny
     */
    APPLIED,
    /**
     * Schválený
     */
    APPROVED,
    /**
     * Zamietnutý
     */
    REJECTED
}