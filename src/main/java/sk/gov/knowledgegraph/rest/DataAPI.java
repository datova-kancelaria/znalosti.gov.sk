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
public class DataAPI {

    @Autowired
    private Repository repository;
    
	private final TreeMap<Integer, Dataset> datasetTreeMap = new TreeMap<>();


	@GetMapping("/api/dcat")
	public List<Dataset> dcat() throws IOException, UnsupportedRDFormatException, FileNotFoundException, ParserConfigurationException, TransformerException {


		datasetTreeMap.clear();

		Model model = new TreeModel();

		int i=0;

		try (RepositoryConnection conn = repository.getConnection()) {
			String queryString =
							  "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix dcat: <http://www.w3.org/ns/dcat#>\n"
							  + "prefix dct: <http://purl.org/dc/terms/> prefix skos: <http://www.w3.org/2004/02/skos/core#>\n"
							  + "select distinct ?catalog ?catalogTitle ?dataset ?datasetTitle ?publisher ?publisherName ?theme ?themeLabel ?version where {\n"
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
							  + "} order by asc(?catalogTitle)  asc(?datasetTitle)"; 
					
			TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
			try (TupleQueryResult result = tupleQuery.evaluate()) {
				while (result.hasNext()) {  // iterate over the result
					BindingSet bindingSet = result.next();

					Value publisher = bindingSet.getValue("publisher");
					Value publisherName = bindingSet.getValue("publisherName");
					Value datasetUri = bindingSet.getValue("dataset");
					Value datasetTitle = bindingSet.getValue("datasetTitle");
					Value catalog = bindingSet.getValue("catalog");
					Value catalogTitle = bindingSet.getValue("catalogTitle");
					Value theme = bindingSet.getValue("theme");
					Value themeLabel = bindingSet.getValue("themeLabel");
					
					Value currCatalog = bindingSet.getValue("currentCatalog");
					Value currDataset = bindingSet.getValue("currentDataset");
					Value version = bindingSet.getValue("version");
					
					Dataset dataset = new Dataset();
					dataset.setPublisher(publisher.stringValue());
					dataset.setPublisherName(publisherName.stringValue());
					dataset.setDataset(datasetUri.stringValue());
					dataset.setDatasetTitle(datasetTitle.stringValue());
					dataset.setCatalog(catalog.stringValue());
					dataset.setCatalogTitle(catalogTitle.stringValue());
					dataset.setTheme(theme.stringValue());
					dataset.setThemeLabel(themeLabel.stringValue());
					dataset.setVersion(version.stringValue());
					
					datasetTreeMap.put(i, dataset);

					i++;
				}
			}
		}


		final List<Dataset> list = new ArrayList<>(datasetTreeMap.values());
		return list;
	}


}
