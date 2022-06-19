package sk.gov.knowledgegraph.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
public class ResourceController {

    private static Logger logger = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private Repository repository;
    @Autowired
    private transient HttpServletResponse response;
    private final List<RDFFormat> supportedRDFFormates = List.of(RDFFormat.JSONLD, RDFFormat.RDFXML);

    @GetMapping(value = "/api/resource")
    public StreamingResponseBody resource(@RequestParam(value = "uri") String uri, @RequestParam(value = "content-type", required = false) String contentType,
            @RequestHeader(value = HttpHeaders.ACCEPT, required = false) String acceptHeader) throws IOException {
        return getResourceStream(uri, contentType, acceptHeader);
    }


    @PostMapping(value = "/api/resource", consumes = { "application/ld+json", "application/rdf+xml" })
    public StreamingResponseBody resourceByPost(@RequestParam(value = "uri") String uri,
            @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = true) String contentType) throws IOException {
        return getResourceStream(uri, contentType, null);
    }


    private StreamingResponseBody getResourceStream(String uri, String contentType, String acceptHeader) {
        RDFFormat format = null;
        if (contentType != null) {
            Optional<RDFFormat> f = RDFFormat.matchMIMEType(contentType, supportedRDFFormates);
            if (f.isPresent()) {
                format = f.get();
            }
        }
        if (format == null && acceptHeader != null) {
            Optional<RDFFormat> f = RDFFormat.matchMIMEType(acceptHeader, supportedRDFFormates);
            if (f.isPresent()) {
                format = f.get();
            }
        }
        if (format == null) {
            throw new IllegalArgumentException("You have to specify Content-Type or Accept header!");
        }

        try (RepositoryConnection connection = repository.getConnection()) {
            String queryString = "construct {" + "" + "  <" + uri + "> ?p ?o .\n" + "  ?s2 ?p2 <" + uri + "> } where \n" + "{{  <" + uri + "> ?p ?o } union\n"
                    + "{ ?s2 ?p2 <" + uri + "> }}";
            return getStreamingResponseBody(format, connection.prepareGraphQuery(QueryLanguage.SPARQL, queryString),
                    "output." + format.getDefaultFileExtension(), response);
        }
    }


    private StreamingResponseBody getStreamingResponseBody(RDFFormat format, GraphQuery preparedQuery, String fileName, HttpServletResponse response) {
        return out -> {
            if (preparedQuery != null) {
                response.addHeader("Content-disposition", "inline;filename=" + fileName);
                response.setContentType(format.getDefaultMIMEType());
                preparedQuery.evaluate(Rio.createWriter(format, response.getOutputStream()));
            } else {
                logger.info("Document content is empty.");
            }
        };
    }
}
