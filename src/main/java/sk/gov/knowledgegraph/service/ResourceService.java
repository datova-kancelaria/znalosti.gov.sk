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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sk.gov.knowledgegraph.model.entity.Resource;

@Service
@Slf4j
public class ResourceService {

    @Autowired
    @Qualifier("znalostiRepository")
    private Repository repository;

    public List<Resource> describeUriBySelect(String uri)
            throws IOException, UnsupportedRDFormatException, FileNotFoundException, ParserConfigurationException, TransformerException {
        List<Resource> list = new ArrayList<>();
        try (RepositoryConnection conn = repository.getConnection()) {
            String queryString = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix dcat: <http://www.w3.org/ns/dcat#>\n"
                    + "prefix dct: <http://purl.org/dc/terms/> prefix skos: <http://www.w3.org/2004/02/skos/core#>\n"
                    + "select distinct ?subject ?predicate ?object ?isInverse ?graph ?graphName ?language where\n" + "{ graph ?graph { {\n" + "    <" + uri
                    + "> ?predicate2 ?object .\n" + "      bind (?predicate2 as ?predicate)\n" + "      bind (<" + uri + "> as ?subject)     \n"
                    + "      bind (xsd:boolean(false) as ?isInverse)     \n"

                    + "      BIND ( lang(?object) AS ?language )     \n"

                    + "    } union{\n" + "      ?subject ?predicate3 <" + uri + ">\n" + "      bind (?predicate3 as ?predicate)\n" + "      bind (<" + uri
                    + "> as ?object) \n" + "      bind (xsd:boolean(true) as ?isInverse)     \n"

                    + "      BIND ( lang(?object) AS ?language )     \n"

                    + "      filter (?predicate3 !=skos:broader)     \n" + "      filter (?predicate3 !=skos:narrower)     \n"
                    + "      filter (?predicate3 !=skos:inScheme)     \n" + "      filter (?predicate3 !=dcat:landingPage)     \n"

                    + "    } }\n" + "      ?graph dct:title ?graphName .\n" + "     filter langMatches( lang(?graphName), \"sk\" ) }";

            log.info(queryString);

            TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                while (result.hasNext()) { // iterate over the result
                    BindingSet bindingSet = result.next();

                    Value subject = bindingSet.getValue("subject");
                    Value predicate = bindingSet.getValue("predicate");
                    Value object = bindingSet.getValue("object");
                    Value isInverse = bindingSet.getValue("isInverse");
                    Value graph = bindingSet.getValue("graph");
                    Value graphName = bindingSet.getValue("graphName");
                    Value language = bindingSet.getValue("language");

                    Resource resource = new Resource();

                    resource.setUri(uri);
                    resource.setSubject(subject.stringValue());
                    resource.setSubjectShort(resource.shortenUri(subject.stringValue()));

                    resource.setPredicate(predicate.stringValue());
                    resource.setPredicateShort(resource.shortenUri(predicate.stringValue()));

                    resource.setObject(object.stringValue());
                    resource.setObjectShort(resource.shortenUri(object.stringValue()));

                    resource.setIsInverse(isInverse.stringValue());
                    resource.setGraph(graph.stringValue());
                    resource.setGraphName(graphName.stringValue());

                    if (language != null) {
                        resource.setLanguage(language.stringValue());
                    }
                    list.add(resource);
                }
            }
        }

        return list;
    }


    public Resource getBaseProperties(String uri) {

        try (RepositoryConnection conn = repository.getConnection()) {

            String queryString2 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                    + "prefix dcat: <http://www.w3.org/ns/dcat#> prefix dct: <http://purl.org/dc/terms/>\n"
                    + "prefix skos: <http://www.w3.org/2004/02/skos/core#> select distinct ?score ?type ?prefLabel ?title ?label ?typeLabel \n" + "where "

                    + "{  <" + uri + "> rdf:type ?type .   \n" + "       graph ?q {\n" + "       ?type rdfs:label ?typeLabel .\n"
                    + "       filter langMatches( lang(?typeLabel), \"sk\" ) }\n" + " graph ?g { {\n" + "    <" + uri + "> skos:prefLabel ?prefLabel .\n"
                    + "     bind (1 as ?score)\n" + "     filter langMatches( lang(?prefLabel), \"sk\" ) \n" + "    } union {\n" + "     <" + uri
                    + "> dct:title ?title .\n" + "     bind (2 as ?score)     \n" + "     filter langMatches( lang(?title), \"sk\" ) \n" + "    } union {\n"
                    + "     <" + uri + "> rdfs:label ?label .\n" + "     bind (3 as ?score)           \n" + "     filter langMatches( lang(?label), \"sk\" ) \n"
                    + "    } } } order by desc(?score) limit 1";

            String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                    + "prefix dcat: <http://www.w3.org/ns/dcat#> prefix dct: <http://purl.org/dc/terms/>"
                    + "prefix skos: <http://www.w3.org/2004/02/skos/core#> select distinct ?type ?prefLabel ?title ?label ?typeLabel "
                    + "where { "
                    + "     { graph ?q {"
                    + "       optional {"
                    + "       <" + uri + "> rdf:type ?type } }}"
              //      + "    { graph ?r {"
              //      + "       optional {?type rdfs:label ?typeLabel . "
             //       + "        filter langMatches( lang(?typeLabel), \"sk\" )} }}"
                    + "    { graph ?s {"
                    + "       optional {<" + uri + "> skos:prefLabel ?prefLabel ."
                    + "       filter langMatches( lang(?prefLabel), \"sk\" ) } }}"
                    + "    { graph ?t {"
                    + "       optional {<" + uri + "> dct:title ?title ."
                    + "       filter langMatches( lang(?title), \"sk\" ) } }}"
                    + "    { graph ?u {"
                    + "       optional {<" + uri + "> rdfs:label ?label ."
                    + "       filter langMatches( lang(?label), \"sk\" ) } }} }";
            
            
           log.info(queryString);

            
            TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                while (result.hasNext()) { // iterating over results

                    log.info("iterating over results");

                	BindingSet bindingSet = result.next();

                    Value typeLabel = bindingSet.getValue("typeLabel");
                    Value prefLabel = bindingSet.getValue("prefLabel");
                    Value title = bindingSet.getValue("title");
                    Value label = bindingSet.getValue("label");
                    Resource resource = new Resource();
                    resource.setUri(uri);
                  
                    
                    log.info("resource exists");
                    
                    if (bindingSet.hasBinding("prefLabel")) {
                        resource.setPrefLabel(prefLabel.stringValue());                        
                    } else if (bindingSet.hasBinding("title")) {
                        resource.setPrefLabel(title.stringValue());
                    } else if (bindingSet.hasBinding("label")) {
                        resource.setPrefLabel(label.stringValue());
                    }
                    
                    //} else
                    //	resource.setPrefLabel("NO LABEL");
                    
                    
                    if (bindingSet.hasBinding("type")) {
                        resource.setType(bindingSet.getValue("type").stringValue());
                    }

                    if (typeLabel != null) {
                       // resource.setTypeLabel(typeLabel.stringValue());
                    }
                    return resource;
                }
            }
            catch(Exception e)
            {
            	log.info(e.toString());
            }
        }

        return null;
    }
}
