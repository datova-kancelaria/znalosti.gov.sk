/*******************************************************************************
 * Copyright (c) 2021 Eclipse RDF4J contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/
package sk.gov.knowledgegraph.service;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.common.lang.FileFormat;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.impl.SimpleDataset;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultWriter;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultWriterFactory;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultWriterRegistry;
import org.eclipse.rdf4j.query.resultio.QueryResultFormat;
import org.eclipse.rdf4j.query.resultio.QueryResultIO;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class SparqlQueryService {

    private static Logger logger = LoggerFactory.getLogger(SparqlQueryService.class);

    /**
     * The repository that is being served.
     */
    @Autowired
    private Repository repository;

    @RequestMapping(value = "/sparql", method = RequestMethod.POST, consumes = APPLICATION_FORM_URLENCODED_VALUE)
    public void sparqlPostURLencoded(@RequestParam(value = "default-graph-uri", required = false) String defaultGraphUri,
            @RequestParam(value = "named-graph-uri", required = false) String namedGraphUri, @RequestParam(value = "query") String query,
            @RequestHeader(ACCEPT) String acceptHeader, HttpServletRequest request, HttpServletResponse response) throws IOException {
        doSparql(request, query, acceptHeader, defaultGraphUri, namedGraphUri, response);
    }


    @RequestMapping(value = "/sparql", method = RequestMethod.GET)
    public void sparqlGet(@RequestParam(value = "default-graph-uri", required = false) String defaultGraphUri,
            @RequestParam(value = "named-graph-uri", required = false) String namedGraphUri, @RequestParam(value = "query") String query,
            @RequestHeader(ACCEPT) String acceptHeader, HttpServletRequest request, HttpServletResponse response) throws IOException {
        doSparql(request, query, acceptHeader, defaultGraphUri, namedGraphUri, response);
    }


    private void doSparql(HttpServletRequest request, String query, String acceptHeader, String defaultGraphUri, String namedGraphUri,
            HttpServletResponse response) throws IOException {

        // logger.debug("Debug log message");
        logger.info("myAcceptHeader" + acceptHeader);
        logger.info("query" + query);
        logger.info(response.toString());

        //logger.error("Error log message");

        try (RepositoryConnection connection = repository.getConnection()) {
            Query preparedQuery = connection.prepareQuery(QueryLanguage.SPARQL, query);
            setQueryDataSet(preparedQuery, defaultGraphUri, namedGraphUri, connection);
            for (QueryTypes qt : QueryTypes.values()) {
                if (qt.accepts(preparedQuery, acceptHeader)) {
                    qt.evaluate(preparedQuery, acceptHeader, response, defaultGraphUri, namedGraphUri);
                }
            }
        } catch (MalformedQueryException | MismatchingAcceptHeaderException mqe) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad IRI for default or namedGraphIri");
        }
    }


    public String getTupleQueryResultHtml(String query) {
        logger.info("setTupleQueryResultHtml");

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
        logger.info("setGraphQueryResultHtml");

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
        logger.info("setBooleanQueryResultHtml");

        String resultHtml = "<table class=\"govuk-table\"><thead class=\"govuk-table__head\"><tr class=\"govuk-table__row\"><th scope=\"col\" class=\"govuk-table__header\">Je to pravda?</th></tr></thead>";
        resultHtml = resultHtml + "<tbody class=\"govuk-table__body\">";

        try (RepositoryConnection conn = repository.getConnection()) {

            BooleanQuery bq = conn.prepareBooleanQuery(QueryLanguage.SPARQL, query);

            resultHtml = resultHtml + "<tr class=\"govuk-table__row\">";
            resultHtml = resultHtml + "<td  class=\"govuk-table__cell\">" + bq.evaluate() + "</td>";
            resultHtml = resultHtml + "</tr>";

            resultHtml = resultHtml + "</tbody></table>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
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
    private void setQueryDataSet(Query q, String defaultGraphUri, String namedGraphUri, RepositoryConnection connection) {
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

    private enum QueryTypes {

        CONSTRUCT_OR_DESCRIBE(q -> q instanceof GraphQuery, RDFFormat.TURTLE, RDFFormat.NTRIPLES, RDFFormat.JSONLD, RDFFormat.RDFXML) {

            @Override
            protected void evaluate(Query q, String acceptHeader, HttpServletResponse response, String defaultGraphUri, String namedGraphUri)
                    throws QueryEvaluationException, RDFHandlerException, UnsupportedRDFormatException, IOException {
                GraphQuery gq = (GraphQuery) q;

                //RDFFormat format = (RDFFormat) bestFormat(acceptHeader);

                RDFFormat format = (RDFFormat) RDFFormat.JSONLD;

                response.setContentType(format.getDefaultMIMEType());
                gq.evaluate(Rio.createWriter(format, response.getOutputStream()));
            }
        },
        SELECT(q -> q instanceof TupleQuery, TupleQueryResultFormat.JSON, TupleQueryResultFormat.SPARQL, TupleQueryResultFormat.CSV,
                TupleQueryResultFormat.TSV) {

            @Override
            protected void evaluate(Query q, String acceptHeader, HttpServletResponse response, String defaultGraphUri, String namedGraphUri)
                    throws QueryEvaluationException, RDFHandlerException, UnsupportedRDFormatException, IOException {

                TupleQuery tq = (TupleQuery) q;
                //QueryResultFormat format = (QueryResultFormat) bestFormat(acceptHeader);
                //	
                TupleQueryResultFormat format = (TupleQueryResultFormat) TupleQueryResultFormat.CSV;
                //	TupleQueryResultFormat format = (TupleQueryResultFormat) TupleQueryResultFormat.SPARQL;

                response.setContentType(format.getDefaultMIMEType());
                //tq.evaluate(QueryResultIO.createTupleWriter(format, response.getOutputStream()));

                tq.evaluate(QueryResultIO.createTupleWriter(format, response.getOutputStream()));

            }
        },

        ASK(q -> q instanceof BooleanQuery, BooleanQueryResultFormat.TEXT, BooleanQueryResultFormat.JSON, BooleanQueryResultFormat.SPARQL) {

            @Override
            protected void evaluate(Query q, String acceptHeader, HttpServletResponse response, String defaultGraphUri, String namedGraphUri)
                    throws QueryEvaluationException, RDFHandlerException, UnsupportedRDFormatException, IOException {
                BooleanQuery bq = (BooleanQuery) q;

                // QueryResultFormat format = (QueryResultFormat) bestFormat(acceptHeader);
                QueryResultFormat format = (QueryResultFormat) BooleanQueryResultFormat.TEXT;

                response.setContentType(format.getDefaultMIMEType());
                final Optional<BooleanQueryResultWriterFactory> optional = BooleanQueryResultWriterRegistry.getInstance().get(format);
                if (optional.isPresent()) {

                    BooleanQueryResultWriter writer = optional.get().getWriter(response.getOutputStream());
                    writer.handleBoolean(bq.evaluate());
                }

            }
        };

        private final FileFormat[] formats;
        private final Predicate<Query> typeChecker;

        QueryTypes(Predicate<Query> typeChecker, FileFormat... formats) {
            this.typeChecker = typeChecker;
            this.formats = formats;
        }


        /**
         * Test if the query is of a type that can be answered. And that the accept headers allow for the response to be
         * send.
         * 
         * @param  preparedQuery
         * @param  acceptHeader
         * @return                                  true if the query is of the right type and acceptHeaders are acceptable.
         * @throws MismatchingAcceptHeaderException
         */
        protected boolean accepts(Query preparedQuery, String acceptHeader) throws MismatchingAcceptHeaderException {
            if (accepts(preparedQuery)) {
                if (acceptHeader == null || acceptHeader.isEmpty()) {
                    return true;
                } else {
                    for (FileFormat format : formats) {
                        for (String mimeType : format.getMIMETypes()) {
                            if (acceptHeader.contains(mimeType))
                                return true;
                        }
                    }
                }
                throw new MismatchingAcceptHeaderException();
            }
            return false;
        }


        protected abstract void evaluate(Query q, String acceptHeader, HttpServletResponse response, String defaultGraphUri, String namedGraphUri)
                throws QueryEvaluationException, RDFHandlerException, UnsupportedRDFormatException, IOException;


        protected boolean accepts(Query q) {
            return typeChecker.test(q);
        };


        protected FileFormat bestFormat(String acceptHeader) {
            if (acceptHeader == null || acceptHeader.isEmpty()) {
                return formats[0];

            } else {
                for (FileFormat format : formats) {
                    for (String mimeType : format.getMIMETypes()) {
                        if (acceptHeader.contains(mimeType))
                            return format;
                    }
                }
            }
            return formats[0];
        }
    }


    private static class MismatchingAcceptHeaderException extends Exception {

        private static final long serialVersionUID = 1L;

    }
}
