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

    public List<Dataset> listData() {
        List<Dataset> list = new ArrayList<>();

        try (RepositoryConnection conn = repository.getConnection()) {
        	String queryString = ""
                    + "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix dcat: <http://www.w3.org/ns/dcat#>"
                    + "prefix dct: <http://purl.org/dc/terms/> prefix skos: <http://www.w3.org/2004/02/skos/core#> prefix prov: <http://www.w3.org/ns/prov#>"
                    + "select distinct ?dataset ?datasetTitle ?datasetType ?datasetTypeLabel ?wasDerivedFrom ?wasDerivedFromTitle ?publisher ?publisherName ?theme ?themeLabel ?version where {"
                    + "?dataset rdf:type dcat:Dataset ."
                    + "?dataset dct:type ?datasetType ."
                    + "?datasetType skos:prefLabel ?datasetTypeLabel . "
                    + "?dataset dct:title ?datasetTitle ."
                    + "?dataset prov:wasDerivedFrom ?wasDerivedFrom ."
                    + "?wasDerivedFrom dct:title ?wasDerivedFromTitle . "
                    + "?dataset dcat:version ?version ."
                    + "?dataset dct:publisher ?publisher ."
                    + "?publisher skos:prefLabel ?publisherName . "
                    + "?dataset dcat:theme ?theme ."
                    + "?theme skos:prefLabel ?themeLabel .  "
                    + "FILTER ( ?datasetType != <http://publications.europa.eu/resource/authority/dataset-type/ONTOLOGY>"
                    + "  && ?datasetType != <http://publications.europa.eu/resource/authority/dataset-type/TAXONOMY>"
                    + "  && ?datasetType != <http://publications.europa.eu/resource/authority/dataset-type/CODE_LIST>"
                    + "  && ?datasetType != <http://publications.europa.eu/resource/authority/dataset-type/DOMAIN_MODEL>"
                    + "  && ?datasetType != <http://publications.europa.eu/resource/authority/dataset-type/GLOSSARY>"
                    + "  && ?datasetType != <http://publications.europa.eu/resource/authority/dataset-type/THESAURUS>"
                    + "  && ?datasetType != <http://publications.europa.eu/resource/authority/dataset-type/SCHEMA>)  "
                    + "FILTER langMatches( lang(?datasetTypeLabel), \"sk\")  "
                    + "FILTER langMatches( lang(?datasetTitle), \"sk\")"
                    + "FILTER langMatches( lang(?publisherName), \"sk\" )"
                    + "FILTER langMatches( lang(?themeLabel), \"sk\" )"
                    + "} order by desc(?publisherName)";

         //   String queryString = ""
            
            log.info(queryString);
            
            TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                while (result.hasNext()) { // iterate over the result
                    BindingSet bindingSet = result.next();
                    Dataset dataset = new Dataset();
                    dataset.setPublisher(bindingSet.getValue("publisher").stringValue());
                    dataset.setPublisherName(bindingSet.getValue("publisherName").stringValue());
                    dataset.setDataset(bindingSet.getValue("dataset").stringValue());
                    dataset.setDatasetTitle(bindingSet.getValue("datasetTitle").stringValue());
                    dataset.setDatasetType(bindingSet.getValue("datasetType").stringValue());
                    dataset.setDatasetTypeLabel(bindingSet.getValue("datasetTypeLabel").stringValue());
                    dataset.setWasDerivedFrom(bindingSet.getValue("wasDerivedFrom").stringValue());
                    dataset.setWasDerivedFromTitle(bindingSet.getValue("wasDerivedFromTitle").stringValue());
         
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
    
    public List<Dataset> listMetaData() {
        List<Dataset> list = new ArrayList<>();

        try (RepositoryConnection conn = repository.getConnection()) {
            String queryString = ""
            	    + "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix dcat: <http://www.w3.org/ns/dcat#>"
                    + "prefix dct: <http://purl.org/dc/terms/> prefix skos: <http://www.w3.org/2004/02/skos/core#> prefix prov: <http://www.w3.org/ns/prov#>"
                    + "select distinct ?dataset ?datasetTitle ?datasetType ?datasetTypeLabel ?wasDerivedFrom ?wasDerivedFromTitle ?publisher ?publisherName ?theme ?themeLabel ?version where {"
                    + "?dataset rdf:type dcat:Dataset ."
                    + "?dataset dct:type ?datasetType ."
                    + "?datasetType skos:prefLabel ?datasetTypeLabel . "
                    + "?dataset dct:title ?datasetTitle ."
                    + "?dataset prov:wasDerivedFrom ?wasDerivedFrom ."
                    + "?wasDerivedFrom dct:title ?wasDerivedFromTitle . "
                    + "?dataset dcat:version ?version ."
                    + "?dataset dct:publisher ?publisher ."
                    + "?publisher skos:prefLabel ?publisherName . "
                    + "?dataset dcat:theme ?theme ."
                    + "?theme skos:prefLabel ?themeLabel .  "
                    + "FILTER ( ?datasetType = <http://publications.europa.eu/resource/authority/dataset-type/ONTOLOGY>  "
                    + "  || ?datasetType = <http://publications.europa.eu/resource/authority/dataset-type/DOMAIN_MODEL> )"                    
                    + "FILTER langMatches( lang(?datasetTypeLabel), \"sk\")  "
                    + "FILTER langMatches( lang(?datasetTitle), \"sk\")"
                    + "FILTER langMatches( lang(?publisherName), \"sk\" )"
                    + "FILTER langMatches( lang(?themeLabel), \"sk\" )"
                    + "} order by desc(?publisherName)";
            
            log.info(queryString);
            
            TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                while (result.hasNext()) { // iterate over the result
                    BindingSet bindingSet = result.next();
                    Dataset dataset = new Dataset();
                    dataset.setPublisher(bindingSet.getValue("publisher").stringValue());
                    dataset.setPublisherName(bindingSet.getValue("publisherName").stringValue());
                    dataset.setDataset(bindingSet.getValue("dataset").stringValue());
                    dataset.setDatasetTitle(bindingSet.getValue("datasetTitle").stringValue());
                  
                    dataset.setDatasetType(bindingSet.getValue("datasetType").stringValue());
                    dataset.setDatasetTypeLabel(bindingSet.getValue("datasetTypeLabel").stringValue());
                    
                    dataset.setWasDerivedFrom(bindingSet.getValue("wasDerivedFrom").stringValue());
                    dataset.setWasDerivedFromTitle(bindingSet.getValue("wasDerivedFromTitle").stringValue());
                    
                    
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
    
    public List<Dataset> listCategories() {
        List<Dataset> list = new ArrayList<>();

        try (RepositoryConnection conn = repository.getConnection()) {
            String queryString = ""
            	    + "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix dcat: <http://www.w3.org/ns/dcat#>"
                    + "prefix dct: <http://purl.org/dc/terms/> prefix skos: <http://www.w3.org/2004/02/skos/core#> prefix prov: <http://www.w3.org/ns/prov#>"
                    + "select distinct ?dataset ?datasetTitle ?datasetType ?datasetTypeLabel ?wasDerivedFrom ?wasDerivedFromTitle ?publisher ?publisherName ?theme ?themeLabel ?version where {"
                    + "?dataset rdf:type dcat:Dataset ."
                    + "?dataset dct:type ?datasetType ."
                    + "?datasetType skos:prefLabel ?datasetTypeLabel . "
                    + "?dataset dct:title ?datasetTitle ."
                    + "?dataset prov:wasDerivedFrom ?wasDerivedFrom ."
                    + "?wasDerivedFrom dct:title ?wasDerivedFromTitle . "
                    + "?dataset dcat:version ?version ."
                    + "?dataset dct:publisher ?publisher ."
                    + "?publisher skos:prefLabel ?publisherName . "
                    + "?dataset dcat:theme ?theme ."
                    + "?theme skos:prefLabel ?themeLabel .  "
                    + "FILTER ( ?datasetType = <http://publications.europa.eu/resource/authority/dataset-type/TAXONOMY>"
                    + "  || ?datasetType = <http://publications.europa.eu/resource/authority/dataset-type/CODE_LIST>"
                    + "  || ?datasetType = <http://publications.europa.eu/resource/authority/dataset-type/GLOSSARY>"
                    + "  || ?datasetType = <http://publications.europa.eu/resource/authority/dataset-type/THESAURUS>"
                    + "  || ?datasetType = <http://publications.europa.eu/resource/authority/dataset-type/SCHEMA>)  "
                    + "FILTER langMatches( lang(?datasetTypeLabel), \"sk\")  "
                    + "FILTER langMatches( lang(?datasetTitle), \"sk\")"
                    + "FILTER langMatches( lang(?publisherName), \"sk\" )"
                    + "FILTER langMatches( lang(?themeLabel), \"sk\" )"
                    + "} order by desc(?publisherName)";
            
            log.info(queryString);
            
            TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                while (result.hasNext()) { // iterate over the result
                    BindingSet bindingSet = result.next();
                    Dataset dataset = new Dataset();
                    dataset.setPublisher(bindingSet.getValue("publisher").stringValue());
                    dataset.setPublisherName(bindingSet.getValue("publisherName").stringValue());
                    dataset.setDataset(bindingSet.getValue("dataset").stringValue());
                    dataset.setDatasetTitle(bindingSet.getValue("datasetTitle").stringValue());
                  
                    dataset.setDatasetType(bindingSet.getValue("datasetType").stringValue());
                    dataset.setDatasetTypeLabel(bindingSet.getValue("datasetTypeLabel").stringValue());
                    
                    dataset.setWasDerivedFrom(bindingSet.getValue("wasDerivedFrom").stringValue());
                    dataset.setWasDerivedFromTitle(bindingSet.getValue("wasDerivedFromTitle").stringValue());
                    
                    
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
