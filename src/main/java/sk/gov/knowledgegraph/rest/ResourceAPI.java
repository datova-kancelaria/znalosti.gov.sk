package sk.gov.knowledgegraph.rest;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.gov.knowledgegraph.data.entity.Dataset;
import sk.gov.knowledgegraph.data.entity.Resource;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

@RestController
public class ResourceAPI {

    @Autowired
    private Repository repository;
    
	private final TreeMap<Integer, Resource> resourceTreeMap = new TreeMap<>();

	@GetMapping("/api/describeResource")
	public List<Resource> uri(String uri) throws IOException, UnsupportedRDFormatException, FileNotFoundException, ParserConfigurationException, TransformerException {

		resourceTreeMap.clear();

		Model model = new TreeModel();

		int i=0;

		try (RepositoryConnection conn = repository.getConnection()) {
			
			String queryString = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix dcat: <http://www.w3.org/ns/dcat#>\n"
					+ "prefix dct: <http://purl.org/dc/terms/> prefix skos: <http://www.w3.org/2004/02/skos/core#>\n"
					+ "select distinct ?subject ?predicate ?object ?isInverse ?graph ?graphName where\n"
					+ "{ graph ?graph { {\n"
					+ "    <"+uri+"> ?predicate2 ?object .\n"
					+ "      bind (?predicate2 as ?predicate)\n"
					+ "      bind (<"+uri+"> as ?subject)     \n"
					+ "      bind (xsd:boolean(false) as ?isInverse)     \n"
					+ "    } union{\n"
					+ "      ?subject ?predicate3 <"+uri+">\n"
					+ "      bind (?predicate3 as ?predicate)\n"
					+ "      bind (<"+uri+"> as ?object) \n"
					+ "      bind (xsd:boolean(true) as ?isInverse)     \n"
					
					+ "      filter (?predicate3 !=skos:broader)     \n"
					+ "      filter (?predicate3 !=skos:narrower)     \n"
					+ "      filter (?predicate3 !=skos:inScheme)     \n"
					+ "      filter (?predicate3 !=dcat:landingPage)     \n"
					
					
					+ "    } }\n"
					+ "      ?graph dct:title ?graphName .\n"
					+ "     filter langMatches( lang(?graphName), \"sk\" ) }";
			
			
			TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				while (result.hasNext()) {  // iterate over the result
					BindingSet bindingSet = result.next();

					Value subject   = bindingSet.getValue("subject");
					Value predicate = bindingSet.getValue("predicate");
					Value object    = bindingSet.getValue("object");
					Value isInverse = bindingSet.getValue("isInverse");
					Value graph     = bindingSet.getValue("graph");
					Value graphName = bindingSet.getValue("graphName");
					
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
					
					resourceTreeMap.put(i, resource);

					i++;
				}
			}
		}


		final List<Resource> list = new ArrayList<>(resourceTreeMap.values());
		return list;
	}


	@GetMapping("/api/getBaseProperties")
	public Resource getBaseProperties(String uri) throws IOException, UnsupportedRDFormatException, FileNotFoundException, ParserConfigurationException, TransformerException {

	//	resourceTreeMap.clear();

		Model model = new TreeModel();
		Resource resource = new Resource();

		int i=0;

		try (RepositoryConnection conn = repository.getConnection()) {
					
			String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
					+ "prefix dcat: <http://www.w3.org/ns/dcat#> prefix dct: <http://purl.org/dc/terms/>\n"
					+ "prefix skos: <http://www.w3.org/2004/02/skos/core#> select distinct ?score ?type ?prefLabel ?title ?label ?typeLabel \n"
					+ "where "
					
					+ "{  <"+uri+"> rdf:type ?type .   \n"
					+ "       graph ?q {\n"
					+ "       ?type rdfs:label ?typeLabel .\n"
					+ "       filter langMatches( lang(?typeLabel), \"sk\" ) }\n"
					+ " graph ?g { {\n"
					+ "    <"+uri+"> skos:prefLabel ?prefLabel .\n"
					+ "     bind (1 as ?score)\n"
					+ "     filter langMatches( lang(?prefLabel), \"sk\" ) \n"
					+ "    } union {\n"
					+ "     <"+uri+"> dct:title ?title .\n"
					+ "     bind (2 as ?score)     \n"
					+ "     filter langMatches( lang(?title), \"sk\" ) \n"
					+ "    } union {\n"
					+ "     <"+uri+"> rdfs:label ?label .\n"
					+ "     bind (3 as ?score)           \n"
					+ "     filter langMatches( lang(?label), \"sk\" ) \n"
					+ "    } } } order by desc(?score) limit 1";
			
			
			TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				while (result.hasNext()) {  // iterate over the result
					BindingSet bindingSet = result.next();

					Value type   = bindingSet.getValue("type");
					Value typeLabel = bindingSet.getValue("typeLabel");
					Value prefLabel    = bindingSet.getValue("prefLabel");
					Value title = bindingSet.getValue("title");
					Value label     = bindingSet.getValue("label");
					
					resource.setUri(uri);
					
					if (prefLabel!=null)
						resource.setPrefLabel(prefLabel.stringValue());
					else
						if(title!=null)
							resource.setPrefLabel(title.stringValue());
						else
							if (label!=null)
								resource.setPrefLabel(label.stringValue());
								
					if (type !=null)
						resource.setType(type.stringValue());
					
					if (typeLabel !=null)
						resource.setTypeLabel(typeLabel.stringValue());
					
					i++;
				}
			}
		}

		return resource;
	}


	
}
