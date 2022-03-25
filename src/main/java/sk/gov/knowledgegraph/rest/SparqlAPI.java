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
public class SparqlAPI {

    @Autowired
    private Repository repository;
    
	private final TreeMap<Integer, Result> resultTreeMap = new TreeMap<>();

	@GetMapping("/api/sparql")
	public List<Result> search(String queryString) throws IOException, UnsupportedRDFormatException, FileNotFoundException, ParserConfigurationException, TransformerException {

		resultTreeMap.clear();

		Model model = new TreeModel();

		int i=0;

		try (RepositoryConnection conn = repository.getConnection()) {
			
			TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
								
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				while (result.hasNext()) {  // iterate over the result
					BindingSet bindingSet = result.next();

					for (i=0; i<bindingSet.size(); i++)
					{}
					
					Value valueOfSubject = bindingSet.getValue("subject");
					Value valueOfProperty = bindingSet.getValue("property");
					Value valueOfObject = bindingSet.getValue("object");
					Value valueOfGraph = bindingSet.getValue("graph");
				 	Value valueOfGraphName = bindingSet.getValue("graphName");

					Result searchResult = new Result();

					searchResult.setSubject(valueOfSubject.stringValue());
					searchResult.setProperty(valueOfProperty.stringValue());
					searchResult.setObject(valueOfObject.stringValue());
					searchResult.setGraph(valueOfGraph.stringValue());
					searchResult.setSearchString(queryString);
					searchResult.setGrapNameh(valueOfGraphName.stringValue());
				
					resultTreeMap.put(i, searchResult);

					i++;
				}
			}
		}

		final List<Result> list = new ArrayList<>(resultTreeMap.values());
		return list;
	}
	
}
