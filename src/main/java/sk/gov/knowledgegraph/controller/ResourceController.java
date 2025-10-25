package sk.gov.knowledgegraph.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.impl.SimpleDataset;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultWriter;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultWriterFactory;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultWriterRegistry;
import org.eclipse.rdf4j.query.resultio.QueryResultIO;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import sk.gov.knowledgegraph.model.RepositoryPool;
import sk.gov.knowledgegraph.model.entity.Result;
import sk.gov.knowledgegraph.model.exception.ErrorCode;
import sk.gov.knowledgegraph.model.exception.KnowledgeGraphException;
import sk.gov.knowledgegraph.service.SearchService;

@Slf4j
@RestController
@Validated
@RequestMapping("/api")
public class ResourceController {

    @Autowired
    @Qualifier("znalostiRepository")
    private RepositoryPool repositoryPool;
    @Autowired
    private SearchService searchService;
    @Autowired
    private transient HttpServletResponse response;
    private final List<RDFFormat> supportedGraphRDFFormates = List.of(RDFFormat.JSONLD, RDFFormat.RDFXML, RDFFormat.TURTLE, RDFFormat.NTRIPLES);
    private final List<TupleQueryResultFormat> supportedTupleRDFFormates = List.of(TupleQueryResultFormat.SPARQL, TupleQueryResultFormat.CSV,
            TupleQueryResultFormat.TSV);
    private final List<BooleanQueryResultFormat> supportedBooleanRDFFormates = List.of(BooleanQueryResultFormat.JSON, BooleanQueryResultFormat.TEXT);

    @CrossOrigin
    @GetMapping(value = "/resource")
    public StreamingResponseBody resource(@RequestParam(value = "uri") String uri, @RequestParam(value = "db-id", required = false) String dbId,
            @RequestParam(value = HttpHeaders.ACCEPT, required = false) String acceptHeader) throws KnowledgeGraphException {
        return getResourceStreamForURI(uri, dbId, acceptHeader);
    }


    @CrossOrigin
    @PostMapping(value = "/resource", produces = { "application/ld+json", "application/rdf+xml" })
    public StreamingResponseBody resourceByPost(@RequestParam(value = "uri") String uri, @RequestParam(value = "db-id", required = false) String dbId,
            @RequestParam(value = HttpHeaders.ACCEPT, required = true) String acceptHeader) throws KnowledgeGraphException {
        return getResourceStreamForURI(uri, dbId, acceptHeader);
    }


    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Result> search(@RequestParam(value = "q") String searchString, @RequestParam(value = "db-id", required = false) String dbId)
            throws KnowledgeGraphException {
        return searchService.search(searchString, dbId);
    }


    @GetMapping(value = "/list-dbs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> listDbs() throws KnowledgeGraphException {
        return repositoryPool.getRepositories().keySet().stream().toList();
    }


    @GetMapping(value = "/reload-dbs", produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<String> reloadDbs() throws KnowledgeGraphException {
        return repositoryPool.reloadDbFromBranch(null);
    }

    @GetMapping(value = "/reload-db/{branch-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<String> reloadDbByBranchId(@RequestParam(value = "branchId", required = true) String branchId) throws KnowledgeGraphException {
        return repositoryPool.reloadDbFromBranch(branchId);
    }
    

    @PostMapping(value = "/sparql")
    public StreamingResponseBody sparqlPostURLencoded(@RequestParam(value = "named-graph-uri", required = false) String namedGraphUri,
            @NotBlank @RequestParam(value = "q") String query, @RequestParam(value = "db-id", required = false) String dbId,
            @RequestParam(HttpHeaders.ACCEPT) String acceptHeader) throws KnowledgeGraphException {
        return getResourceStreamForSPARQL(query, dbId, namedGraphUri, acceptHeader);
    }


    private StreamingResponseBody getResourceStreamForSPARQL(String query, String dbId, String namedGraphUri, String acceptHeader)
            throws KnowledgeGraphException {
        if (!repositoryPool.getRepositories().containsKey(dbId)) {
            throw new KnowledgeGraphException(ErrorCode.UNKNOWN_REPOSITORY, Map.of("dbId", dbId));
        }
        try (RepositoryConnection connection = repositoryPool.getRepositoryOrDefault(dbId).getConnection()) {
            Query preparedQuery = connection.prepareQuery(QueryLanguage.SPARQL, query);
            if (namedGraphUri != null) {
                SimpleDataset dataset = new SimpleDataset();

                if (namedGraphUri != null) {
                    IRI namedIri = connection.getValueFactory().createIRI(namedGraphUri);
                    dataset.addNamedGraph(namedIri);
                }
                preparedQuery.setDataset(dataset);
            }

            if (preparedQuery instanceof BooleanQuery) {
                BooleanQueryResultFormat format = null;

                if (format == null && acceptHeader != null) {
                    Optional<BooleanQueryResultFormat> f = BooleanQueryResultFormat.matchMIMEType(acceptHeader, supportedBooleanRDFFormates);
                    if (f.isPresent()) {
                        format = f.get();
                    }
                }
                if (format == null) {
                    throw new KnowledgeGraphException(ErrorCode.OUTPUT_FORMAT_FORMAT_MISSING, Map.of("uri", preparedQuery.toString()));
                }

                return getStreamingResponseBody(format, (BooleanQuery) preparedQuery, "output." + format.getDefaultFileExtension(), response);
            } else if (preparedQuery instanceof TupleQuery) {
                TupleQueryResultFormat format = null;

                if (format == null && acceptHeader != null) {
                    Optional<TupleQueryResultFormat> f = RDFFormat.matchMIMEType(acceptHeader, supportedTupleRDFFormates);
                    if (f.isPresent()) {
                        format = f.get();
                    }
                }
                if (format == null) {
                    throw new KnowledgeGraphException(ErrorCode.OUTPUT_FORMAT_FORMAT_MISSING, Map.of("uri", preparedQuery.toString()));
                }
                return getStreamingResponseBody(format, (TupleQuery) preparedQuery, "output." + format.getDefaultFileExtension(), response);
            } else if (preparedQuery instanceof GraphQuery) {
                RDFFormat format = null;
                if (format == null && acceptHeader != null) {
                    Optional<RDFFormat> f = RDFFormat.matchMIMEType(acceptHeader, supportedGraphRDFFormates);
                    if (f.isPresent()) {
                        format = f.get();
                    }
                }
                if (format == null) {
                    throw new KnowledgeGraphException(ErrorCode.OUTPUT_FORMAT_FORMAT_MISSING, Map.of("uri", preparedQuery.toString()));
                }
                return getStreamingResponseBody(format, (GraphQuery) preparedQuery, "output." + format.getDefaultFileExtension(), response);
            }
        } catch (Throwable ex) {
            log.warn(ex.getMessage(), ex);
        }
        return null;
    }


    private StreamingResponseBody getResourceStreamForURI(String uri, String dbId, String acceptHeader) throws KnowledgeGraphException {
        RDFFormat format = null;
        if (format == null && acceptHeader != null) {
            Optional<RDFFormat> f = RDFFormat.matchMIMEType(acceptHeader, supportedGraphRDFFormates);
            if (f.isPresent()) {
                format = f.get();
            }
        }
        if (format == null) {
            throw new KnowledgeGraphException(ErrorCode.OUTPUT_FORMAT_FORMAT_MISSING, Map.of("uri", uri));
        }

        if (repositoryPool.getRepositories().containsKey(dbId)) {
            throw new KnowledgeGraphException(ErrorCode.UNKNOWN_REPOSITORY, Map.of("dbId", dbId));
        }

        try (RepositoryConnection connection = repositoryPool.getRepositoryOrDefault(dbId).getConnection()) {
            String queryString = "CONSTRUCT { <" + uri + "> ?p ?o . ?s2 ?p2 <" + uri + "> } where { {  <" + uri + "> ?p ?o } UNION "
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
