/*******************************************************************************
 * Copyright (c) 2021 Eclipse RDF4J contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package sk.gov.knowledgegraph.service;

import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.impl.SimpleDataset;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SparqlQueryService {

    /**
     * The repository that is being served.
     */
    @Autowired
    private Repository repository;

    public String getTupleQueryResultHtml(String query) {
        log.info("setTupleQueryResultHtml");

        String resultHtml = "<table class=\"govuk-table\"><thead class=\"govuk-table__head\">";

        try (RepositoryConnection conn = repository.getConnection()) {
            TupleQuery tupleQuery = conn.prepareTupleQuery(query);
            TupleQueryResult result = tupleQuery.evaluate();
            List<String> bindingNames = result.getBindingNames();
            resultHtml = resultHtml + "<tr class=\"govuk-table__row\">";

            for (int i = 0; i < bindingNames.size(); i++) {
                resultHtml = resultHtml + "<th scope=\"col\" class=\"govuk-table__header\">?" + bindingNames.get(i) + "</th>";
            }
            resultHtml = resultHtml + "</tr></thead><tbody class=\"govuk-table__body\">";

            while (result.hasNext()) { // iterate over the result
                BindingSet bindingSet = result.next();
                resultHtml = resultHtml + "<tr class=\"govuk-table__row\">";

                for (int i = 0; i < bindingSet.size(); i++) {
                    resultHtml = resultHtml + "<td  class=\"govuk-table__cell\">" + formatResultString(bindingSet.getValue(bindingNames.get(i)).stringValue())
                            + "</td>";
                }
                resultHtml = resultHtml + "</tr>";
            }

            resultHtml = resultHtml + "</tbody></table>";
        }
        return resultHtml;
    }


    public String getGraphQueryResultHtml(String query) {
        log.info("setGraphQueryResultHtml");

        String resultHtml = "<table class=\"govuk-table\"><thead class=\"govuk-table__head\"><tr class=\"govuk-table__row\"><th scope=\"col\" class=\"govuk-table__header\">?subject</th><th scope=\"col\" class=\"govuk-table__header\">?predicate</th><th scope=\"col\" class=\"govuk-table__header\">?object</th></tr></thead>";
        resultHtml = resultHtml + "<tbody class=\"govuk-table__body\">";

        try (RepositoryConnection conn = repository.getConnection()) {

            GraphQueryResult graphResult = conn.prepareGraphQuery(query).evaluate();

            for (Statement statement : graphResult) {

                resultHtml = resultHtml + "<tr class=\"govuk-table__row\">";
                resultHtml = resultHtml + "<td  class=\"govuk-table__cell\">" + formatResultString(statement.getSubject().stringValue()) + "</td>"
                        + "<td  class=\"govuk-table__cell\">" + formatResultString(statement.getPredicate().stringValue()) + "</td>"
                        + "<td  class=\"govuk-table__cell\">" + formatResultString(statement.getObject().stringValue()) + "</td>";
                resultHtml = resultHtml + "</tr>";
            }

            resultHtml = resultHtml + "</tbody></table>";
        }
        return resultHtml;
    }


    public String getBooleanQueryResultHtml(String query) {
        log.info("setBooleanQueryResultHtml");

        String resultHtml = "<table class=\"govuk-table\"><thead class=\"govuk-table__head\"><tr class=\"govuk-table__row\"><th scope=\"col\" class=\"govuk-table__header\">Je to pravda?</th></tr></thead>";
        resultHtml = resultHtml + "<tbody class=\"govuk-table__body\">";

        try (RepositoryConnection conn = repository.getConnection()) {

            BooleanQuery bq = conn.prepareBooleanQuery(QueryLanguage.SPARQL, query);

            resultHtml = resultHtml + "<tr class=\"govuk-table__row\">";
            resultHtml = resultHtml + "<td  class=\"govuk-table__cell\">" + bq.evaluate() + "</td>";
            resultHtml = resultHtml + "</tr>";

            resultHtml = resultHtml + "</tbody></table>";
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return resultHtml;
    }


    private String formatResultString(String inString) {
        if (inString.contains("http")) {
            return "<<a href=resource?uri=" + inString.replace("#", "%23") + ">" + inString + "</a>>";
        } else {
            return inString;
        }
    }


    /**
     * @see                   <a href="https://www.w3.org/TR/sparql11-protocol/#dataset">protocol dataset</a>
     * @param q               the query
     * @param defaultGraphUri
     * @param namedGraphUri
     * @param connection
     */
    public void setQueryDataSet(Query q, String defaultGraphUri, String namedGraphUri, RepositoryConnection connection) {
        if (defaultGraphUri != null || namedGraphUri != null) {
            SimpleDataset dataset = new SimpleDataset();

            if (defaultGraphUri != null) {
                IRI defaultIri = connection.getValueFactory().createIRI(defaultGraphUri);
                dataset.addDefaultGraph(defaultIri);
            }

            if (namedGraphUri != null) {
                IRI namedIri = connection.getValueFactory().createIRI(namedGraphUri);
                dataset.addNamedGraph(namedIri);
            }
            q.setDataset(dataset);
        }
    }

}
