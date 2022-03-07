package sk.gov.knowledgegraph.rest;

import org.apache.commons.lang3.text.WordUtils;
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
import sk.gov.knowledgegraph.data.entity.Result;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class SearchAPI {

    @Autowired
    private Repository repository;
    
	private final TreeMap<Integer, Result> resultTreeMap = new TreeMap<>();

	@GetMapping("/api/search")
	public List<Result> search(String searchString) throws IOException, UnsupportedRDFormatException, FileNotFoundException, ParserConfigurationException, TransformerException {

		resultTreeMap.clear();

		Model model = new TreeModel();

		int i=0;

		try (RepositoryConnection conn = repository.getConnection()) {
			
			
			String queryString = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n	" +
					"prefix dcat: <http://www.w3.org/ns/dcat#>\n" +
					"prefix dct: <http://purl.org/dc/terms/>\n" +
					"prefix skos: <http://www.w3.org/2004/02/skos/core#>\n" 
					+ "select distinct ?subject ?object ?graph ?graphName where\n"
					+ "{ graph ?graph {\n"
					+ "    ?subject ?predicate ?object .\n"
					+ "    filter not exists {?subject dcat:hasCurrentVersion ?currentDataset}\n"					
					+ "    filter regex(?object,\""+searchString+"\", \"i\")\n"
					+ "    filter (lang(?object)= \"sk\")}\n"
					+ "    ?graph dct:title ?graphName \n"
					+ "    filter (lang(?graphName)= \"sk\")}"
					+ " limit 100";
			
					TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
								
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				while (result.hasNext()) {  // iterate over the result
					BindingSet bindingSet = result.next();

					Value valueOfSubject = bindingSet.getValue("subject");
				//	Value valueOfProperty = bindingSet.getValue("property");
					Value valueOfObject = bindingSet.getValue("object");
					Value valueOfGraph = bindingSet.getValue("graph");
				 	Value valueOfGraphName = bindingSet.getValue("graphName");

					Result searchResult = new Result();

					searchResult.setSubject(valueOfSubject.stringValue());
				//	searchResult.setProperty(valueOfProperty.stringValue());
					searchResult.setObject(valueOfObject.stringValue());
					searchResult.setGraph(valueOfGraph.stringValue());
					searchResult.setSearchString(searchString);
					searchResult.setGrapNameh(valueOfGraphName.stringValue());
				
					
					/* priprava na matching vysledku
					
					Pattern pattern = Pattern.compile(searchString);
					Matcher matcher = pattern.matcher(valueOfObject.stringValue());
					
					while (matcher.find()) {
						searchResult.setStartIndex(matcher.start());
						searchResult.setEndIndex(matcher.start());
				    }
				   */
					resultTreeMap.put(i, searchResult);

					i++;
				}
			}
		}

		final List<Result> list = new ArrayList<>(resultTreeMap.values());
		return list;
	}

	@GetMapping("/api/stat/getAllTriplesCount")
	public int getAllTriplesCount() throws IOException, UnsupportedRDFormatException, FileNotFoundException, ParserConfigurationException, TransformerException {

		int count = 0;
		
		int i=0;

		try (RepositoryConnection conn = repository.getConnection()) {
			String queryString = "SELECT (COUNT(*) as ?count) WHERE { ?s ?p ?o}";

			TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				while (result.hasNext()) {  // iterate over the result
					BindingSet bindingSet = result.next();

					Value countBind = bindingSet.getValue("count");
					count = Integer.parseInt(countBind.stringValue());
				}
			}
		}

		return count;
	}
	
	@GetMapping("/api/stat/getDatasetsCount")
	public int getDatasetsCount() throws IOException, UnsupportedRDFormatException, FileNotFoundException, ParserConfigurationException, TransformerException {

		int count = 0;
		
		int i=0;

		try (RepositoryConnection conn = repository.getConnection()) {
			String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix dcat: <http://www.w3.org/ns/dcat#>\n"
					+ "prefix dct: <http://purl.org/dc/terms/> prefix skos: <http://www.w3.org/2004/02/skos/core#>\n"
					+ "select distinct (COUNT(?dataset) as ?count)"
					+ "WHERE {\n" 
					+ "?catalog rdf:type dcat:Catalog .\n"
					+ "?catalog dct:title ?catalogTitle .\n"
					+ "?catalog dcat:dataset ?dataset .\n"
					+ "?dataset rdf:type dcat:Dataset .\n"
					+ "?dataset dct:title ?datasetTitle .\n"
					+ "?dataset dcat:version ?version .\n"
					+ "?dataset dct:publisher ?publisher .\n"
					+ "?publisher skos:prefLabel ?publisherName . \n"
					+ "?dataset dcat:theme ?theme .\n"
					+ "?theme skos:prefLabel ?themeLabel .  \n"
					+ "FILTER langMatches( lang(?catalogTitle), \"sk\" )\n"
					+ "FILTER langMatches( lang(?datasetTitle), \"sk\")\n"
					+ "FILTER langMatches( lang(?publisherName), \"sk\" )\n"
					+ "FILTER langMatches( lang(?themeLabel), \"sk\" )\n"
					+ "}";

			TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				while (result.hasNext()) {  // iterate over the result
					BindingSet bindingSet = result.next();

					Value countBind = bindingSet.getValue("count");
					count = Integer.parseInt(countBind.stringValue());
				}
			}
		}

		return count;
	}
	
	@GetMapping("/api/stat/getCatalogsCount")
	public int getCatalogsCount() throws IOException, UnsupportedRDFormatException, FileNotFoundException, ParserConfigurationException, TransformerException {

		int count = 0;
		
		int i=0;

		try (RepositoryConnection conn = repository.getConnection()) {
			String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix dcat: <http://www.w3.org/ns/dcat#>\n"
					+ "SELECT (COUNT(*) as ?count) WHERE { ?s rdf:type dcat:Catalog}";

			TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				while (result.hasNext()) {  // iterate over the result
					BindingSet bindingSet = result.next();

					Value countBind = bindingSet.getValue("count");
					count = Integer.parseInt(countBind.stringValue());
				}
			}
		}

		return count;
	}
	
}
