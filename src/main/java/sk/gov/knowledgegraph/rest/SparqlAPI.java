package sk.gov.knowledgegraph.rest;

import org.apache.commons.lang3.text.WordUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.resultio.QueryResultIO;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultWriter;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
    
	private final TreeMap<Integer, Resource> resourceTreeMap = new TreeMap<>();

	private TupleQueryResult result;
    
    @GetMapping("/api/sparqlView")
	public TupleQueryResult sparqlView(String queryString) throws IOException, UnsupportedRDFormatException, FileNotFoundException, ParserConfigurationException, TransformerException {


    	resourceTreeMap.clear();

		Model model = new TreeModel();

		int i=0;

		try (RepositoryConnection conn = repository.getConnection()) {

			TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
			result = tupleQuery.evaluate();
			
			}
    
    return result;
		
	}

	
}