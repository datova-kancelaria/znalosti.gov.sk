package sk.gov.knowledgegraph.controller;

import static org.springframework.http.HttpHeaders.ACCEPT;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultWriter;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultWriterFactory;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultWriterRegistry;
import org.eclipse.rdf4j.query.resultio.QueryResultIO;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import sk.gov.knowledgegraph.model.entity.Result;
import sk.gov.knowledgegraph.model.exception.ErrorCode;
import sk.gov.knowledgegraph.model.exception.KnowledgeGraphException;
import sk.gov.knowledgegraph.service.SearchService;
import sk.gov.knowledgegraph.service.SparqlQueryService;

@Slf4j
@RestController
@Validated
@RequestMapping("/api")
public class ResourceController {

    @Autowired
    private Repository repository;
    @Autowired
    private SearchService searchService;
    @Autowired
    private SparqlQueryService sparqlQueryService;
    @Autowired
    private transient HttpServletResponse response;
    private final List<RDFFormat> supportedGraphRDFFormates = List.of(RDFFormat.JSONLD, RDFFormat.RDFXML, RDFFormat.TURTLE, RDFFormat.NTRIPLES);
    private final List<TupleQueryResultFormat> supportedTupleRDFFormates = List.of(TupleQueryResultFormat.JSON, TupleQueryResultFormat.CSV,
            TupleQueryResultFormat.TSV);
    private final List<BooleanQueryResultFormat> supportedBooleanRDFFormates = List.of(BooleanQueryResultFormat.JSON, BooleanQueryResultFormat.TEXT);

    @CrossOrigin
    @GetMapping(value = "/resource")
    public StreamingResponseBody resource(@RequestParam(value = "uri") String uri, @RequestParam(value = "content-type", required = false) String contentType,
            @RequestHeader(value = HttpHeaders.ACCEPT, required = false) String acceptHeader) throws KnowledgeGraphException {
        return getResourceStreamForURI(uri, contentType, acceptHeader);
    }

    @CrossOrigin
    @PostMapping(value = "/resource", produces = { "application/ld+json", "application/rdf+xml" })
    public StreamingResponseBody resourceByPost(@RequestParam(value = "uri") String uri,
            @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = true) String contentType) throws KnowledgeGraphException {
        return getResourceStreamForURI(uri, contentType, null);
    }


    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Result> search(@RequestParam(value = "q") String searchString) throws KnowledgeGraphException {
        return searchService.search(searchString);
    }


    @PostMapping(value = "/sparql")
    public StreamingResponseBody sparqlPostURLencoded(@RequestParam(value = "default-graph-uri", required = false) String defaultGraphUri,
            @RequestParam(value = "named-graph-uri", required = false) String namedGraphUri, @NotBlank @RequestParam(value = "q") String query,
            @RequestHeader(ACCEPT) String acceptHeader, @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType)
            throws KnowledgeGraphException {

        try (RepositoryConnection connection = repository.getConnection()) {
            Query preparedQuery = connection.prepareQuery(QueryLanguage.SPARQL, query);
            sparqlQueryService.setQueryDataSet(preparedQuery, defaultGraphUri, namedGraphUri, connection);
            if (preparedQuery instanceof GraphQuery) {
                return getResourceStreamForSPARQL(preparedQuery, contentType, acceptHeader);
            }
        }
        return null;

    }


    private StreamingResponseBody getResourceStreamForSPARQL(Query preparedQuery, String contentType, String acceptHeader) throws KnowledgeGraphException {
        if (preparedQuery instanceof BooleanQuery) {
            BooleanQueryResultFormat format = null;

            if (contentType != null) {
                Optional<BooleanQueryResultFormat> f = BooleanQueryResultFormat.matchMIMEType(contentType, supportedBooleanRDFFormates);
                if (f.isPresent()) {
                    format = f.get();
                }
            }
            if (format == null && acceptHeader != null) {
                Optional<BooleanQueryResultFormat> f = BooleanQueryResultFormat.matchMIMEType(acceptHeader, supportedBooleanRDFFormates);
                if (f.isPresent()) {
                    format = f.get();
                }
            }
            if (format == null) {
                throw new KnowledgeGraphException(ErrorCode.OUTPUT_FORMAT_FORMAT_MISSING, Map.of("uri", preparedQuery.toString()));
            }
            try (RepositoryConnection connection = repository.getConnection()) {
                return getStreamingResponseBody(format, (BooleanQuery) preparedQuery, "output." + format.getDefaultFileExtension(), response);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        } else if (preparedQuery instanceof TupleQuery) {
            TupleQueryResultFormat format = null;

            if (contentType != null) {
                Optional<TupleQueryResultFormat> f = TupleQueryResultFormat.matchMIMEType(contentType, supportedTupleRDFFormates);
                if (f.isPresent()) {
                    format = f.get();
                }
            }
            if (format == null && acceptHeader != null) {
                Optional<TupleQueryResultFormat> f = RDFFormat.matchMIMEType(acceptHeader, supportedTupleRDFFormates);
                if (f.isPresent()) {
                    format = f.get();
                }
            }
            if (format == null) {
                throw new KnowledgeGraphException(ErrorCode.OUTPUT_FORMAT_FORMAT_MISSING, Map.of("uri", preparedQuery.toString()));
            }
            try (RepositoryConnection connection = repository.getConnection()) {
                return getStreamingResponseBody(format, (TupleQuery) preparedQuery, "output." + format.getDefaultFileExtension(), response);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        } else if (preparedQuery instanceof GraphQuery) {
            RDFFormat format = null;
            if (contentType != null) {
                Optional<RDFFormat> f = RDFFormat.matchMIMEType(contentType, supportedGraphRDFFormates);
                if (f.isPresent()) {
                    format = f.get();
                }
            }
            if (format == null && acceptHeader != null) {
                Optional<RDFFormat> f = RDFFormat.matchMIMEType(acceptHeader, supportedGraphRDFFormates);
                if (f.isPresent()) {
                    format = f.get();
                }
            }
            if (format == null) {
                throw new KnowledgeGraphException(ErrorCode.OUTPUT_FORMAT_FORMAT_MISSING, Map.of("uri", preparedQuery.toString()));
            }
            try (RepositoryConnection connection = repository.getConnection()) {
                return getStreamingResponseBody(format, (GraphQuery) preparedQuery, "output." + format.getDefaultFileExtension(), response);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }

        return null;
    }


    private StreamingResponseBody getResourceStreamForURI(String uri, String contentType, String acceptHeader) throws KnowledgeGraphException {
        RDFFormat format = null;
        if (contentType != null) {
            Optional<RDFFormat> f = RDFFormat.matchMIMEType(contentType, supportedGraphRDFFormates);
            if (f.isPresent()) {
                format = f.get();
            }
        }
        if (format == null && acceptHeader != null) {
            Optional<RDFFormat> f = RDFFormat.matchMIMEType(acceptHeader, supportedGraphRDFFormates);
            if (f.isPresent()) {
                format = f.get();
            }
        }
        if (format == null) {
            throw new KnowledgeGraphException(ErrorCode.OUTPUT_FORMAT_FORMAT_MISSING, Map.of("uri", uri));
        }

        try (RepositoryConnection connection = repository.getConnection()) {
            String queryString = "construct {" + "" + "  <" + uri + "> ?p ?o .\n" + "  ?s2 ?p2 <" + uri + "> } where \n" + "{{  <" + uri + "> ?p ?o } union\n"
                    + "{ ?s2 ?p2 <" + uri + "> }}";
            return getStreamingResponseBody(format, connection.prepareGraphQuery(QueryLanguage.SPARQL, queryString),
                    "output." + format.getDefaultFileExtension(), response);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }


    private StreamingResponseBody getStreamingResponseBody(BooleanQueryResultFormat format, BooleanQuery preparedQuery, String fileName,
            HttpServletResponse response) {
        return out -> {
            if (preparedQuery != null) {
                response.addHeader("Content-disposition", "inline;filename=" + fileName);
                response.setContentType(format.getDefaultMIMEType());
                final Optional<BooleanQueryResultWriterFactory> optional = BooleanQueryResultWriterRegistry.getInstance().get(format);
                if (optional.isPresent()) {
                    BooleanQueryResultWriter writer = optional.get().getWriter(response.getOutputStream());
                    writer.handleBoolean(preparedQuery.evaluate());
                }
            } else {
                log.info("Document content is empty.");
            }
        };
    }


    private StreamingResponseBody getStreamingResponseBody(TupleQueryResultFormat format, TupleQuery preparedQuery, String fileName,
            HttpServletResponse response) {
        return out -> {
            if (preparedQuery != null) {
                response.addHeader("Content-disposition", "inline;filename=" + fileName);
                response.setContentType(format.getDefaultMIMEType());
                preparedQuery.evaluate(QueryResultIO.createTupleWriter(format, response.getOutputStream()));
            } else {
                log.info("Document content is empty.");
            }
        };
    }


    private StreamingResponseBody getStreamingResponseBody(RDFFormat format, GraphQuery preparedQuery, String fileName, HttpServletResponse response) {
        return out -> {
            if (preparedQuery != null) {
                response.addHeader("Content-disposition", "inline;filename=" + fileName);
                response.setContentType(format.getDefaultMIMEType());
                preparedQuery.evaluate(Rio.createWriter(format, response.getOutputStream()));
            } else {
                log.info("Document content is empty.");
            }
        };
    }
}
