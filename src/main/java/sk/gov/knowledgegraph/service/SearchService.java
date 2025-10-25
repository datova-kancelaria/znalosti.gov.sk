package sk.gov.knowledgegraph.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sk.gov.knowledgegraph.model.RepositoryPool;
import sk.gov.knowledgegraph.model.entity.Result;
import sk.gov.knowledgegraph.model.exception.ErrorCode;
import sk.gov.knowledgegraph.model.exception.KnowledgeGraphException;

@Service
@Slf4j
public class SearchService {

    @Autowired
    @Qualifier("znalostiRepository")
    private RepositoryPool repositoryPool;

    public List<Result> search(String searchString) throws KnowledgeGraphException {
        return search(searchString, repositoryPool.getDefaultRepositoryId());
    }


    public List<Result> search(String searchString, String dbId) throws KnowledgeGraphException {

        List<Result> list = new ArrayList<>();

        if (!repositoryPool.getRepositories().containsKey(dbId)) {
            throw new KnowledgeGraphException(ErrorCode.UNKNOWN_REPOSITORY, Map.of("dbId", dbId));
        }

        try (RepositoryConnection conn = repositoryPool.getRepositoryOrDefault(dbId).getConnection()) {
            String queryString = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n       " + "prefix dcat: <http://www.w3.org/ns/dcat#>\n"
                    + "prefix dct: <http://purl.org/dc/terms/>\n" + "prefix skos: <http://www.w3.org/2004/02/skos/core#>\n"
                    + "select distinct ?subject ?object ?graph ?graphName where\n" + "{ graph ?graph {\n" + "    ?subject ?predicate ?object .\n"
                    + "    filter not exists {?subject dcat:hasCurrentVersion ?currentDataset}\n" + "    filter regex(?object,\"" + searchString
                    + "\", \"i\")\n" + "    filter (lang(?object)= \"sk\")}\n" + "    ?graph dct:title ?graphName \n" + "    filter (lang(?graphName)= \"sk\")}"
                    + " limit 100";
            log.info(queryString);
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
                    searchResult.setGraphName(valueOfGraphName.stringValue());

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
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        return list;
    }
}
