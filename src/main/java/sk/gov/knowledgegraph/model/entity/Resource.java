package sk.gov.knowledgegraph.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import sk.gov.knowledgegraph.model.AbstractEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public class Resource extends AbstractEntity {

    private String prefLabel;
    private String type;
    private String typeLabel;
    private String uri;
    private String subject;
    private String predicate;
    private String object;
    private String isInverse;
    private String graph;
    private String graphName;
    private String language;
    private String uriShort;
    private String predicateShort;
    private String objectShort;

    private String subjectShort;

    public String shortenUri(String uri) {
        // CORE Ontologie

        if (uri.contains("http://www.w3.org/1999/02/22-rdf-syntax-ns#")) {
            return uri.replaceAll("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:");
        } else if (uri.contains("http://www.w3.org/2000/01/rdf-schema#")) {
            return uri.replaceAll("http://www.w3.org/2000/01/rdf-schema#", "rdfs:");
        } else if (uri.contains("http://www.w3.org/2002/07/owl#")) {
            return uri.replaceAll("http://www.w3.org/2002/07/owl#", "owl:");
        } else if (uri.contains("http://www.w3.org/2001/XMLSchema#")) {
            return uri.replaceAll("http://www.w3.org/2001/XMLSchema#", "xsd:");
        } else if (uri.contains("http://www.w3.org/2004/02/skos/core#")) {
            return uri.replaceAll("http://www.w3.org/2004/02/skos/core#", "skos:");
        } else if (uri.contains("http://www.w3.org/ns/dcat#")) {
            return uri.replaceAll("http://www.w3.org/ns/dcat#", "dcat:");
        } else if (uri.contains("http://schema.org/")) {
            return uri.replaceAll("http://schema.org/", "schema:");
        } else if (uri.contains("http://purl.org/dc/terms/")) {
            return uri.replaceAll("http://purl.org/dc/terms/", "dct:");
        } else if (uri.contains("http://www.w3.org/ns/adms#")) {
            return uri.replaceAll("http://www.w3.org/ns/adms#", "adms:");
        } else if (uri.contains("http://www.w3.org/ns/org#")) {
            return uri.replaceAll("http://www.w3.org/ns/org#", "org:");
        } else if (uri.contains("http://www.w3.org/ns/regorg#")) {
            return uri.replaceAll("http://www.w3.org/ns/regorg# ", "rov:");
        } else if (uri.contains("http://www.w3.org/ns/shacl#")) {
            return uri.replaceAll("http://www.w3.org/ns/shacl#", "sh:");
        } else if (uri.contains("http://xmlns.com/foaf/0.1/")) {
            return uri.replaceAll("http://xmlns.com/foaf/0.1/", "foaf:");
        }

        // E?? Ontologie

        else if (uri.contains("http://data.europa.eu/m8g/")) {
            return uri.replaceAll("http://data.europa.eu/m8g/", "cpov:");
        } else if (uri.contains("http://www.w3.org/ns/locn#")) {
            return uri.replaceAll("http://www.w3.org/ns/locn#", "locn:");
        } else if (uri.contains("http://purl.org/vocab/cpsv#")) {
            return uri.replaceAll("http://purl.org/vocab/cpsv#", "cpsv:");
        }

        // N??rodn?? ontol??gie

        else if (uri.contains("https://data.gov.sk/def/ontology/physical-person/"))
            return uri.replaceAll("https://data.gov.sk/def/ontology/physical-person/", "pper:");

        else if (uri.contains("https://data.gov.sk/def/ontology/legal-subject/"))
            return uri.replaceAll("https://data.gov.sk/def/ontology/legal-subject/", "lsub:");

        else if (uri.contains("https://data.gov.sk/def/ontology/location/"))
            return uri.replaceAll("https://data.gov.sk/def/ontology/location/", "loca:");

        else if (uri.contains("https://data.gov.sk/def/ontology/finance/"))
            return uri.replaceAll("https://data.gov.sk/def/ontology/finance/", "fin:");

        else if (uri.contains("https://data.gov.sk/def/ontology/egov/"))
            return uri.replaceAll("https://data.gov.sk/def/ontology/egov/", "egov:");

        else
            return uri;
    }


    public String getResourceIcon(String uri) {

        if (uri.contains("skos:narrower"))
            return "&nbsp;<img src=/images/level-down.png height=20 width=20>";
        else if (uri.contains("skos:broader"))
            return "&nbsp;<img src=/images/level-up.png height=20 width=20>";
        else if (uri.contains("skos:hasTopConcept"))
            return "&nbsp;<img src=/images/top.png height=20 width=20>";

        else if (uri.contains("dcat:landingPage"))
            return "&nbsp;<img src=/images/exit.png height=20 width=20>";
        else if (uri.contains("dcat:accessUrl"))
            return "&nbsp;<img src=/images/exit.png height=20 width=20>";
        else if (uri.contains("dcat:downloadURL"))
            return "&nbsp;<img src=/images/exit.png height=20 width=20>";
        else if (uri.contains("foaf:homepage"))
            return "&nbsp;<img src=/images/exit.png height=20 width=20>";
        else if (uri.contains("skos:hasTopConcept"))
            return "&nbsp;<img src=/images/exit.png height=20 width=20>";

        else
            return "";
    }


    public String getLinkBaseUrl(String linkBase, String uri) {
        if (uri.contains("dcat:landingPage"))
            return "";
        else if (uri.contains("dcat:accessUrl"))
            return "";
        else if (uri.contains("dcat:downloadURL"))
            return "";
        else if (uri.contains("foaf:homepage"))
            return "";
        else if (uri.contains("foaf:page"))
            return "";

        else
            return linkBase;
    }


    public String getLanguageIcon(String uri) {

        if (uri.contains("sk"))
            return "&nbsp;<img src=/icons/sk.png height=20 width=20>";
        else if (uri.contains("en"))
            return "&nbsp;<img src=/icons/en.png height=20 width=20>";
        else if (uri.contains("de"))
            return "&nbsp;<img src=/icons/de.png height=20 width=20>";
        else if (uri.contains("cs"))
            return "&nbsp;<img src=/icons/cs.png height=20 width=20>";
        else if (uri.contains("it"))
            return "&nbsp;<img src=/icons/it.png height=20 width=20>";
        else if (uri.contains("pl"))
            return "&nbsp;<img src=/icons/pl.png height=20 width=20>";
        else if (uri.contains("hu"))
            return "&nbsp;<img src=/icons/hu.png height=20 width=20>";
        else if (uri.contains("fi"))
            return "&nbsp;<img src=/icons/fi.png height=20 width=20>";
        else if (uri.contains("ua"))
            return "&nbsp;<img src=/icons/ua.png height=20 width=20>";
        else if (uri.contains("es"))
            return "&nbsp;<img src=/icons/es.png height=20 width=20>";
        else if (uri.contains("el"))
            return "&nbsp;<img src=/icons/el.png height=20 width=20>";
        else if (uri.contains("fr"))
            return "&nbsp;<img src=/icons/fr.png height=20 width=20>";
        else if (uri.contains("nl"))
            return "&nbsp;<img src=/icons/nl.png height=20 width=20>";
        else if (uri.contains("da"))
            return "&nbsp;<img src=/icons/da.png height=20 width=20>";

        else
            return "";
    }
}
