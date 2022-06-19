package sk.gov.knowledgegraph.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import sk.gov.knowledgegraph.model.entity.Result;

@Service
public class SearchService {

    @Autowired
    private Repository repository;

    public List<Result> search(String searchString)
            throws IOException, UnsupportedRDFormatException, FileNotFoundException, ParserConfigurationException, TransformerException {

        List<Result> list = new ArrayList<>();

        try (RepositoryConnection conn = repository.getConnection()) {

            String queryString = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n       " + "prefix dcat: <http://www.w3.org/ns/dcat#>\n"
                    + "prefix dct: <http://purl.org/dc/terms/>\n" + "prefix skos: <http://www.w3.org/2004/02/skos/core#>\n"
                    + "select distinct ?subject ?object ?graph ?graphName where\n" + "{ graph ?graph {\n" + "    ?subject ?predicate ?object .\n"
                    + "    filter not exists {?subject dcat:hasCurrentVersion ?currentDataset}\n" + "    filter regex(?object,\"" + searchString
                    + "\", \"i\")\n" + "    filter (lang(?object)= \"sk\")}\n" + "    ?graph dct:title ?graphName \n" + "    filter (lang(?graphName)= \"sk\")}"
                    + " limit 100";

            TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);

            try (TupleQueryResult result = tupleQuery.evaluate()) {
                while (result.hasNext()) { // iterate over the result
                    BindingSet bindingSet = result.next();

                    Value valueOfSubject = bindingSet.getValue("subject");
                    //      Value valueOfProperty = bindingSet.getValue("property");
                    Value valueOfObject = bindingSet.getValue("object");
                    Value valueOfGraph = bindingSet.getValue("graph");
                    Value valueOfGraphName = bindingSet.getValue("graphName");

                    Result searchResult = new Result();

                    searchResult.setSubject(valueOfSubject.stringValue());
                    //      searchResult.setProperty(valueOfProperty.stringValue());
                    searchResult.setObject(valueOfObject.stringValue());
                    searchResult.setGraph(valueOfGraph.stringValue());
                    searchResult.setSearchString(searchString);
                    searchResult.setGrapNameh(valueOfGraphName.stringValue());

                    /*
                     * priprava na matching vysledku
                     * 
                     * Pattern pattern = Pattern.compile(searchString);
                     * Matcher matcher = pattern.matcher(valueOfObject.stringValue());
                     * 
                     * while (matcher.find()) {
                     * searchResult.setStartIndex(matcher.start());
                     * searchResult.setEndIndex(matcher.start());
                     * }
                     */
                    list.add(searchResult);
                }
            }
        }

        return list;
    }
}