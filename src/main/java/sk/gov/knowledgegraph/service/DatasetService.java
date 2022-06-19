package sk.gov.knowledgegraph.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import lombok.extern.slf4j.Slf4j;
import sk.gov.knowledgegraph.model.entity.Dataset;

@Service
@Slf4j
public class DatasetService {

    @Autowired
    private Repository repository;

    public List<Dataset> listDcatDatasets() {
        List<Dataset> list = new ArrayList<>();

        try (RepositoryConnection conn = repository.getConnection()) {
            String queryString = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix dcat: <http://www.w3.org/ns/dcat#>\n"
                    + "prefix dct: <http://purl.org/dc/terms/> prefix skos: <http://www.w3.org/2004/02/skos/core#>\n"
                    + "select distinct ?catalog ?catalogTitle ?dataset ?datasetTitle ?publisher ?publisherName ?theme ?themeLabel ?version where {\n"
                    + "?catalog rdf:type dcat:Catalog .\n" + "?catalog dct:title ?catalogTitle .\n" + "?catalog dcat:dataset ?dataset .\n"
                    + "?dataset rdf:type dcat:Dataset .\n" + "?dataset dct:title ?datasetTitle .\n" + "?dataset dcat:version ?version .\n"
                    + "?dataset dct:publisher ?publisher .\n" + "?publisher skos:prefLabel ?publisherName . \n" + "?dataset dcat:theme ?theme .\n"
                    + "?theme skos:prefLabel ?themeLabel .  \n" + "FILTER langMatches( lang(?catalogTitle), \"sk\" )\n"
                    + "FILTER langMatches( lang(?datasetTitle), \"sk\")\n" + "FILTER langMatches( lang(?publisherName), \"sk\" )\n"
                    + "FILTER langMatches( lang(?themeLabel), \"sk\" )\n" + "} order by asc(?catalogTitle)  asc(?datasetTitle)";

            TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                while (result.hasNext()) { // iterate over the result
                    BindingSet bindingSet = result.next();
                    Dataset dataset = new Dataset();
                    dataset.setPublisher(bindingSet.getValue("publisher").stringValue());
                    dataset.setPublisherName(bindingSet.getValue("publisherName").stringValue());
                    dataset.setDataset(bindingSet.getValue("dataset").stringValue());
                    dataset.setDatasetTitle(bindingSet.getValue("datasetTitle").stringValue());
                    dataset.setCatalog(bindingSet.getValue("catalog").stringValue());
                    dataset.setCatalogTitle(bindingSet.getValue("catalogTitle").stringValue());
                    dataset.setTheme(bindingSet.getValue("theme").stringValue());
                    dataset.setThemeLabel(bindingSet.getValue("themeLabel").stringValue());
                    dataset.setVersion(bindingSet.getValue("version").stringValue());
                    list.add(dataset);
                }
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        return list;
    }
}
