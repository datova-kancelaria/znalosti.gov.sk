package sk.gov.knowledgegraph.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    @Autowired
    private Repository repository;

    public int getAllTriplesCount()
            throws IOException, UnsupportedRDFormatException, FileNotFoundException, ParserConfigurationException, TransformerException {
        try (RepositoryConnection conn = repository.getConnection()) {
            String queryString = "SELECT (COUNT(*) as ?count) WHERE { ?s ?p ?o}";

            TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                while (result.hasNext()) { // iterate over the result
                    BindingSet bindingSet = result.next();
                    return Integer.parseInt(bindingSet.getValue("count").stringValue());
                }
            }
        }
        return 0;
    }


    public int getAllNamedGraphsCount()
            throws IOException, UnsupportedRDFormatException, FileNotFoundException, ParserConfigurationException, TransformerException {

        try (RepositoryConnection conn = repository.getConnection()) {
            String queryString = "SELECT (count (distinct ?g) as ?count) WHERE { GRAPH ?g { ?s ?p ?o } }";

            TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                while (result.hasNext()) { // iterate over the result
                    BindingSet bindingSet = result.next();
                    return Integer.parseInt(bindingSet.getValue("count").stringValue());
                }
            }
        }

        return 0;
    }


    public int getDatasetsCount() throws IOException, UnsupportedRDFormatException, FileNotFoundException, ParserConfigurationException, TransformerException {

        try (RepositoryConnection conn = repository.getConnection()) {
            String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix dcat: <http://www.w3.org/ns/dcat#>\n"
                    + "prefix dct: <http://purl.org/dc/terms/> prefix skos: <http://www.w3.org/2004/02/skos/core#>\n"
                    + "select distinct (COUNT(?dataset) as ?count)" + "WHERE {\n" + "?catalog rdf:type dcat:Catalog .\n"
                    + "?catalog dct:title ?catalogTitle .\n" + "?catalog dcat:dataset ?dataset .\n" + "?dataset rdf:type dcat:Dataset .\n"
                    + "?dataset dct:title ?datasetTitle .\n" + "?dataset dcat:version ?version .\n" + "?dataset dct:publisher ?publisher .\n"
                    + "?publisher skos:prefLabel ?publisherName . \n" + "?dataset dcat:theme ?theme .\n" + "?theme skos:prefLabel ?themeLabel .  \n"
                    + "FILTER langMatches( lang(?catalogTitle), \"sk\" )\n" + "FILTER langMatches( lang(?datasetTitle), \"sk\")\n"
                    + "FILTER langMatches( lang(?publisherName), \"sk\" )\n" + "FILTER langMatches( lang(?themeLabel), \"sk\" )\n" + "}";

            TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                while (result.hasNext()) { // iterate over the result
                    BindingSet bindingSet = result.next();
                    return Integer.parseInt(bindingSet.getValue("count").stringValue());
                }
            }
        }

        return 0;
    }


    public int getCatalogsCount() throws IOException, UnsupportedRDFormatException, FileNotFoundException, ParserConfigurationException, TransformerException {
        try (RepositoryConnection conn = repository.getConnection()) {
            String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix dcat: <http://www.w3.org/ns/dcat#>\n"
                    + "SELECT (COUNT(*) as ?count) WHERE { ?s rdf:type dcat:Catalog}";
            TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                while (result.hasNext()) { // iterate over the result
                    BindingSet bindingSet = result.next();
                    return Integer.parseInt(bindingSet.getValue("count").stringValue());
                }
            }
        }

        return 0;
    }
}
