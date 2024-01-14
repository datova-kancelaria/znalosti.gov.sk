package sk.gov.knowledgegraph.service;

import static org.eclipse.rdf4j.model.util.Statements.statement;
import static org.eclipse.rdf4j.model.util.Values.iri;
import static org.eclipse.rdf4j.model.util.Values.literal;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.rdf4j.common.exception.RDF4JException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.base.CoreDatatype.XSD;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.util.RDFInserter;
import org.eclipse.rdf4j.rio.ParserConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.rio.helpers.XMLParserSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import sk.gov.knowledgegraph.model.exception.ErrorCode;
import sk.gov.knowledgegraph.model.exception.KnowledgeGraphException;
import sk.gov.knowledgegraph.model.refid.application.ApplicationState;
import sk.gov.knowledgegraph.model.refid.application.DatatypePropertyComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.OntologyComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.OntologyVersionRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.SemanticResourceRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.dto.ApproveApplicationRequestDTO;
import sk.gov.knowledgegraph.model.refid.application.dto.RejectApplicationRequestDTO;
import sk.gov.knowledgegraph.model.refid.application.search.SearchResult;
import sk.gov.knowledgegraph.model.refid.application.search.SemanticResourceRegistrationApplicationSearchCriteria;
import sk.gov.knowledgegraph.model.refid.application.search.SemanticResourceRegistrationApplicationSearchData;
import sk.gov.knowledgegraph.model.refid.application.search.SemanticResourceRegistrationApplicationType;
import sk.gov.knowledgegraph.service.query.LocalizedVariable;
import sk.gov.knowledgegraph.service.query.Query;
import sk.gov.knowledgegraph.service.query.SortDirection;

@Service
@Slf4j
public class ReferenceIdentifierApplicationManagementService {

    @Value("${database.reset-db-allowed:false}")
    private boolean resetDbAllowed;
    @Autowired
    protected Validator validator;
    @Autowired
    @Qualifier("znalostiRepository")
    private Repository znalostiRepository;

    @Autowired
    @Qualifier("refidRepository")
    private Repository refidRepository;
    @Autowired
    ResourceLoader resourceLoader;
    @Autowired
    LoadReferenceIdentifierApplicationManagementService loadRefIdService;

    public static final String URI_APPLICATION_PREFIX = "https://znalosti.gov.sk/id/application/";
    public static final String URI_METAIS_USER_PREFIX = "https://metais.vicepremier.gov.sk/id/user/";
    public static final String SRRA_ONTOLOGY_PREFIX = "https://data.gov.sk/def/ontology/semantic-resource-registration-application/";
    public static final List<Namespace> URI_NAMESPACES = List.of(new SimpleNamespace("srra", SRRA_ONTOLOGY_PREFIX),
            new SimpleNamespace("rdf", org.eclipse.rdf4j.model.base.CoreDatatype.RDF.NAMESPACE), new SimpleNamespace("rdfs", RDFS.NAMESPACE),
            new SimpleNamespace("xsd", XSD.NAMESPACE));
    private static final long RDF_LOAD_BATCH_SIZE = 500;
    private static final int MAX_SEARCH_RESULTS = 50;

    public SemanticResourceRegistrationApplication createApplication(SemanticResourceRegistrationApplication application) throws KnowledgeGraphException {
        if (application.getUri() != null) {
            throw new KnowledgeGraphException(ErrorCode.UNEXPECTED_FILLED_URI_ATTRIBUTE_IN_CREATION_OF_APPLICATION);
        }
        application.setUri(URI_APPLICATION_PREFIX + UUID.randomUUID().toString());
        application.setCreatedAt(LocalDate.now());
        application.setState(ApplicationState.DRAFT);
        //musime kontrolovat ze ci vobec ta ontologia/verzia existuje pred ulozenim 
        if (application instanceof OntologyComponentRegistrationApplication oa) {
            oa.getSubject().setOntology(loadRefIdService.loadDataOntology(oa.getSubject().getOntology().getUri()));
        } else if (application instanceof OntologyVersionRegistrationApplication oa) {
            oa.getSubject().setConcept(loadRefIdService.loadOntologyConcept(oa.getSubject().getConcept().getUri()));
        }
        validate(application);

        String queryStr = """
                SELECT ?uri ?subject ?state WHERE {
                    ?uri ssra:subject ?subject .
                    ?uri ssra:state ?state .
                    ?uri rdf:type ?type
                }
                """;
        List<String> allowedTypes = List.of(iri(SRRA_ONTOLOGY_PREFIX, "ClassComponentRegistrationApplication").stringValue(),
                iri(SRRA_ONTOLOGY_PREFIX, "DatatypePropertyComponentRegistrationApplication").stringValue(),
                iri(SRRA_ONTOLOGY_PREFIX, "ObjectPropertyComponentRegistrationApplication").stringValue(),
                iri(SRRA_ONTOLOGY_PREFIX, "OntologyRegistrationApplication").stringValue(),
                iri(SRRA_ONTOLOGY_PREFIX, "OntologyVersionRegistrationApplication").stringValue(),
                iri(SRRA_ONTOLOGY_PREFIX, "URITemplateRegistrationApplication").stringValue());

        Query q = new Query(queryStr);
        //musia byt unikatne podla typu
        //TODO validacie pre vsetky typy
        if (application instanceof DatatypePropertyComponentRegistrationApplication oa) {
            q.bindValueToVariable("subject", oa.getSubject().getUri());
            try (RepositoryConnection conn = refidRepository.getConnection()) {
                log.debug("Executing query: {}", q.getPreparedQuery(URI_NAMESPACES));
                TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, q.getPreparedQuery(URI_NAMESPACES));
                try (TupleQueryResult result = tupleQuery.evaluate()) {
                    if (result.hasNext()) {
                        BindingSet bs = result.next();
                        String state = bs.getBinding("state").getValue().stringValue();
                        if (ApplicationState.APPROVED.equals(state)) {
                            throw new KnowledgeGraphException(ErrorCode.DATATYPE_PROPERTY_COMPONENT_IS_ALREADY_APPROVED,
                                    Map.of("propertyURI", oa.getSubject().getUri()));
                        }
                    }
                }
            }
        }

        try (RepositoryConnection conn = refidRepository.getConnection()) {
            conn.begin();
            conn.add(ApplicationURIMapperUtils.convertApplicationToStatements(application), iri(application.getUri()));
            conn.commit();
        } catch (RDF4JException e) {
            log.warn(e.getMessage());
            throw e;
        }
        return loadRefIdService.loadApplicationFromDB(application.getUri());
    }


    public SearchResult<SemanticResourceRegistrationApplicationSearchData> search(SemanticResourceRegistrationApplicationSearchCriteria criteria)
            throws KnowledgeGraphException {
        List<SemanticResourceRegistrationApplicationSearchData> results = new ArrayList<>();
        int numFound = 0;

        List<String> allowedTypes = List.of(iri(SRRA_ONTOLOGY_PREFIX, "ClassComponentRegistrationApplication").stringValue(),
                iri(SRRA_ONTOLOGY_PREFIX, "DatatypePropertyComponentRegistrationApplication").stringValue(),
                iri(SRRA_ONTOLOGY_PREFIX, "ObjectPropertyComponentRegistrationApplication").stringValue(),
                iri(SRRA_ONTOLOGY_PREFIX, "OntologyRegistrationApplication").stringValue(),
                iri(SRRA_ONTOLOGY_PREFIX, "OntologyVersionRegistrationApplication").stringValue(),
                iri(SRRA_ONTOLOGY_PREFIX, "URITemplateRegistrationApplication").stringValue());

        StringBuilder whereClause = new StringBuilder();
        whereClause.append("?uri rdf:type ?allowedTypes . \n");
        whereClause.append("?uri srra:subject ?subject . ?subject srra:name ?name . FILTER(langMatches(lang(?name),\"sk\")) . \n");
        whereClause.append("?uri srra:state ?state . \n");
        whereClause.append("?uri rdf:type ?type . \n");

        if (criteria.getType() != null) {
            switch (criteria.getType()) {
            case ONTOLOGY:
                allowedTypes = List.of(iri(SRRA_ONTOLOGY_PREFIX, "OntologyRegistrationApplication").stringValue());
                break;
            case ONTOLOGY_COMPONENT_CLASS:
                allowedTypes = List.of(iri(SRRA_ONTOLOGY_PREFIX, "ClassComponentRegistrationApplication").stringValue());
                break;
            case ONTOLOGY_COMPONENT_DATA_TYPE:
                allowedTypes = List.of(iri(SRRA_ONTOLOGY_PREFIX, "DatatypePropertyComponentRegistrationApplication").stringValue());
                break;
            case ONTOLOGY_COMPONENT_OBJECT_TYPE:
                allowedTypes = List.of(iri(SRRA_ONTOLOGY_PREFIX, "ObjectPropertyComponentRegistrationApplication").stringValue());
                break;
            case ONTOLOGY_VERSION:
                allowedTypes = List.of(iri(SRRA_ONTOLOGY_PREFIX, "OntologyVersionRegistrationApplication").stringValue());
                break;
            case URI_TEMPLATE:
                allowedTypes = List.of(iri(SRRA_ONTOLOGY_PREFIX, "URITemplateRegistrationApplication").stringValue());
                break;
            default:
                break;
            }
        }

        if ((criteria.getCreatedByInsitutionId() != null && !criteria.getCreatedByInsitutionId().isBlank())
                || (criteria.getCreatedByUsername() != null && !criteria.getCreatedByUsername().isBlank())) {
            whereClause.append("?uri srra:createdBy ?createdBy . \n");
            if (criteria.getCreatedByInsitutionId() != null && !criteria.getCreatedByInsitutionId().isBlank()) {
                whereClause
                        .append("?createdBy srra:institutionId ?institutionId . FILTER(?institutionId = \"" + criteria.getCreatedByInsitutionId() + "\") . \n");
            }

            if (criteria.getCreatedByUsername() != null && !criteria.getCreatedByUsername().isBlank()) {
                whereClause.append("?createdBy srra:username ?username . FILTER(?username = \"" + criteria.getCreatedByUsername() + "\") . \n");
            }
        }

        whereClause.append(generateDataRangeQueryClause("createdAt", criteria.getCreatedAtFrom(), criteria.getCreatedAtTo()));
        whereClause.append(generateDataRangeQueryClause("approvedAt", criteria.getApprovedAtFrom(), criteria.getApprovedAtTo()));
        whereClause.append(generateDataRangeQueryClause("appliedAt", criteria.getAppliedAtFrom(), criteria.getAppliedAtTo()));

        Query q = new Query("SELECT ?uri ?name ?state ?type WHERE {\n" + whereClause.toString() + " \n}", new Locale("sk"), true);
        q.bindLanguageTagToVariable(new LocalizedVariable("name"));
        q.bindValuesToVariable("allowedTypes", allowedTypes);
        q.setOrderByVariable("name");
        q.setDirection(SortDirection.ASC);

        if (criteria.getRows() != null && criteria.getRows() > 0 && criteria.getRows() <= MAX_SEARCH_RESULTS) {
            q.setLimit(criteria.getRows());
        } else {
            q.setLimit(MAX_SEARCH_RESULTS);
        }
        if (criteria.getStart() > 0) {
            q.setOffset(criteria.getStart());
        }
        if (criteria.getState() != null) {
            q.bindValueToVariable("state", criteria.getState().name());
        }

        try (RepositoryConnection conn = refidRepository.getConnection()) {
            log.debug("Executing query for search(): {}", q.getPreparedQuery(URI_NAMESPACES));
            TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, q.getPreparedQuery(URI_NAMESPACES));

            try (TupleQueryResult result = tupleQuery.evaluate()) {
                result.forEach(c -> {
                    SemanticResourceRegistrationApplicationSearchData data = new SemanticResourceRegistrationApplicationSearchData();
                    data.setName(c.getBinding("name").getValue().stringValue());
                    data.setState(ApplicationState.valueOf(c.getBinding("state").getValue().stringValue()));

                    if (c.getBinding("type").getValue().stringValue().equals(iri(SRRA_ONTOLOGY_PREFIX, "OntologyRegistrationApplication").stringValue())) {
                        data.setType(SemanticResourceRegistrationApplicationType.ONTOLOGY);
                    } else if (c.getBinding("type").getValue().stringValue()
                            .equals(iri(SRRA_ONTOLOGY_PREFIX, "ClassComponentRegistrationApplication").stringValue())) {
                        data.setType(SemanticResourceRegistrationApplicationType.ONTOLOGY_COMPONENT_CLASS);
                    } else if (c.getBinding("type").getValue().stringValue()
                            .equals(iri(SRRA_ONTOLOGY_PREFIX, "DatatypePropertyComponentRegistrationApplication").stringValue())) {
                        data.setType(SemanticResourceRegistrationApplicationType.ONTOLOGY_COMPONENT_DATA_TYPE);
                    } else if (c.getBinding("type").getValue().stringValue()
                            .equals(iri(SRRA_ONTOLOGY_PREFIX, "ObjectPropertyComponentRegistrationApplication").stringValue())) {
                        data.setType(SemanticResourceRegistrationApplicationType.ONTOLOGY_COMPONENT_OBJECT_TYPE);
                    } else if (c.getBinding("type").getValue().stringValue()
                            .equals(iri(SRRA_ONTOLOGY_PREFIX, "OntologyVersionRegistrationApplication").stringValue())) {
                        data.setType(SemanticResourceRegistrationApplicationType.ONTOLOGY_VERSION);
                    } else if (c.getBinding("type").getValue().stringValue()
                            .equals(iri(SRRA_ONTOLOGY_PREFIX, "URITemplateRegistrationApplication").stringValue())) {
                        data.setType(SemanticResourceRegistrationApplicationType.URI_TEMPLATE);
                    }

                    data.setUri(c.getBinding("uri").getValue().stringValue());
                    results.add(data);
                });
            }
        }

        q = new Query("SELECT (COUNT(*) AS ?count) WHERE {\n" + whereClause.toString() + " \n}", new Locale("sk"), true);
        q.bindLanguageTagToVariable(new LocalizedVariable("name"));
        q.bindValuesToVariable("allowedTypes", allowedTypes);
        if (criteria.getState() != null) {
            q.bindValueToVariable("state", criteria.getState().name());
        }

        try (RepositoryConnection conn = refidRepository.getConnection()) {
            log.debug("Executing query for search() count: {}", q.getPreparedQuery(URI_NAMESPACES));
            TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, q.getPreparedQuery(URI_NAMESPACES));

            try (TupleQueryResult result = tupleQuery.evaluate()) {
                if (result.hasNext()) {
                    numFound = Integer.valueOf(result.next().getBinding("count").getValue().stringValue());
                }
            }
        }

        return new SearchResult<SemanticResourceRegistrationApplicationSearchData>(results, numFound);
    }


    public SemanticResourceRegistrationApplication updateDraftApplication(SemanticResourceRegistrationApplication application) throws KnowledgeGraphException {
        try (RepositoryConnection conn = refidRepository.getConnection()) {
            conn.begin();
            deleteApplicationFromDB(application.getUri(), conn);
            conn.add(ApplicationURIMapperUtils.convertApplicationToStatements(application), iri(application.getUri()));
            conn.commit();
        }
        return loadRefIdService.loadApplicationFromDB(application.getUri());
    }


    public void deleteApplication(String uri) throws KnowledgeGraphException {

        SemanticResourceRegistrationApplication loadedApp = loadRefIdService.loadApplicationFromDB(uri);
        if (!ApplicationState.DRAFT.equals(loadedApp.getState())) {
            throw new KnowledgeGraphException(ErrorCode.YOU_CAN_NOT_DELETE_APPLICATION_WHICH_IS_NOT_IN_DRAFT_STATE, Map.of("uri", loadedApp.getUri()));
        }
        //TODO kontrola ci uz nie su nejake datove prvky naviazane na dany datovy typ (napr. na ontologiu alebo verziu ontologie)
        try (RepositoryConnection conn = refidRepository.getConnection()) {
            deleteApplicationFromDB(uri, conn);
        }
    }


    public SemanticResourceRegistrationApplication applyApplication(SemanticResourceRegistrationApplication application) throws KnowledgeGraphException {
        SemanticResourceRegistrationApplication loadedApp = loadRefIdService.loadApplicationFromDB(application.getUri());

        return loadedApp;
    }


    public SemanticResourceRegistrationApplication getApplication(String uri) throws KnowledgeGraphException {
        return loadRefIdService.loadApplicationFromDB(uri);
    }


    public SemanticResourceRegistrationApplication rejectApplication(RejectApplicationRequestDTO request) throws KnowledgeGraphException {
        SemanticResourceRegistrationApplication loadedApp = loadRefIdService.loadApplicationFromDB(request.getUri());

        if (!ApplicationState.APPLIED.equals(loadedApp.getState())) {
            throw new KnowledgeGraphException(ErrorCode.YOU_CAN_NOT_REJECT_APPLICATION_WHICH_IS_NOT_IN_APPLIED_STATE, Map.of("uri", request.getUri()));
        }
        List<Statement> statements = new ArrayList<>();
        IRI applicationIri = iri(request.getUri());

        statements.addAll(ApplicationURIMapperUtils.convertUser(applicationIri, "rejectedBy", request.getUser()));
        statements.add(statement(applicationIri, iri(SRRA_ONTOLOGY_PREFIX, "rejectedAt"), literal(LocalDate.now()), null));
        try (RepositoryConnection conn = refidRepository.getConnection()) {
            conn.add(statements, applicationIri);
        } catch (RDF4JException e) {
            log.warn(e.getMessage());
            throw e;
        }
        return loadRefIdService.loadApplicationFromDB(request.getUri());
    }


    public SemanticResourceRegistrationApplication approveApplication(ApproveApplicationRequestDTO request) throws KnowledgeGraphException {
        SemanticResourceRegistrationApplication loadedApp = loadRefIdService.loadApplicationFromDB(request.getUri());

        if (!ApplicationState.DRAFT.equals(loadedApp.getState())) {
            throw new KnowledgeGraphException(ErrorCode.YOU_CAN_NOT_APPROVE_APPLICATION_WHICH_IS_NOT_IN_DRAFT_STATE, Map.of("uri", request.getUri()));
        }
        //TODO kontrola ci uz neexistuje Application na rovnaky datovy prvok alebo ontologiu

        List<Statement> statements = new ArrayList<>();
        IRI applicationIri = iri(request.getUri());

        statements.addAll(ApplicationURIMapperUtils.convertUser(applicationIri, "rejectedBy", request.getUser()));
        statements.add(statement(applicationIri, iri(SRRA_ONTOLOGY_PREFIX, "rejectedAt"), literal(LocalDate.now()), null));
        try (RepositoryConnection conn = refidRepository.getConnection()) {
            conn.add(statements, applicationIri);
        } catch (RDF4JException e) {
            log.warn(e.getMessage());
            throw e;
        }
        return loadRefIdService.loadApplicationFromDB(request.getUri());
    }


    private String generateDataRangeQueryClause(String attrBaseName, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return "";
        }
        if (from != null && to != null) {
            return "?uri srra:" + attrBaseName + " ?" + attrBaseName + " . FILTER(?" + attrBaseName + " >= \"" + from.format(DateTimeFormatter.ISO_DATE)
                    + "\"^^xsd:date AND ?" + attrBaseName + "<= \"" + to.format(DateTimeFormatter.ISO_DATE) + "\"^^xsd:date)";
        } else if (from != null) {
            return "?uri srra:" + attrBaseName + " ?" + attrBaseName + " . FILTER(?" + attrBaseName + " >= \"" + from.format(DateTimeFormatter.ISO_DATE)
                    + "\"^^xsd:date)";
        } else {
            return "?uri srra:" + attrBaseName + " ?" + attrBaseName + " . FILTER(?" + attrBaseName + "<= \"" + to.format(DateTimeFormatter.ISO_DATE)
                    + "\"^^xsd:date)";
        }
    }


    private void deleteApplicationFromDB(String uri, RepositoryConnection conn) {
        conn.clear(iri(uri));
    }


    public void validate(Object object, Class<?>... groups) throws KnowledgeGraphException {
        Set<ConstraintViolation<Object>> validate = validator.validate(object, groups);
        if (!validate.isEmpty()) {
            Map<String, String> data = new HashMap<>();
            if (object instanceof SemanticResourceRegistrationApplication app) {
                if (app.getUri() != null) {
                    data.put("actId", app.getUri());
                }
            }
            validate.forEach(s -> {
                data.put(s.getPropertyPath().toString(), s.getMessage());
            });
            throw new KnowledgeGraphException(ErrorCode.NOT_VALID, data);
        }
    }


    public void validate(Collection<?> objects) throws KnowledgeGraphException {
        for (Object object : objects) {
            validate(object);
        }
    }


    public void resetDb() throws RDFParseException, RepositoryException, IllegalArgumentException, IOException, KnowledgeGraphException {
        if (!resetDbAllowed) {
            throw new KnowledgeGraphException(ErrorCode.DB_RESET_NOT_ALLOWED);
        }

        try (RepositoryConnection conn = refidRepository.getConnection()) {
            conn.clear();
            try (InputStream is = resourceLoader
                    .getResource("classpath:META-INF/resources/db-refid-test-data/DatatypePropertyComponentRegistrationApplication-app-3.owl")
                    .getInputStream()) {
                loadFromIs(is, RDFFormat.RDFXML, iri("https://znalosti.gov.sk/id/application/00000000-0000-0000-0000-000000000003"), RDF_LOAD_BATCH_SIZE);
            }

            try (InputStream is = resourceLoader.getResource("classpath:META-INF/resources/db-refid-test-data/OntologyRegistrationApplication-app-1.owl")
                    .getInputStream()) {
                loadFromIs(is, RDFFormat.RDFXML, iri("https://znalosti.gov.sk/id/application/00000000-0000-0000-0000-000000000001"), RDF_LOAD_BATCH_SIZE);
            }

            try (InputStream is = resourceLoader.getResource("classpath:META-INF/resources/db-refid-test-data/OntologyVersionRegistrationApplication-app-2.owl")
                    .getInputStream()) {
                loadFromIs(is, RDFFormat.RDFXML, iri("https://znalosti.gov.sk/id/application/00000000-0000-0000-0000-000000000002"), RDF_LOAD_BATCH_SIZE);
            }

            try (InputStream is = resourceLoader.getResource("classpath:META-INF/resources/db-refid-test-data/URITemplateRegistrationApplication-app-4.owl")
                    .getInputStream()) {
                loadFromIs(is, RDFFormat.RDFXML, iri("https://znalosti.gov.sk/id/application/00000000-0000-0000-0000-000000000004"), RDF_LOAD_BATCH_SIZE);
            }
        }
    }


    private void loadFromIs(InputStream is, RDFFormat format, IRI context, long batchSize) {
        try (RepositoryConnection conn = refidRepository.getConnection()) {
            loadFileHandler(context, batchSize, conn, format, is);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    private void loadFileHandler(IRI context, long batchSize, RepositoryConnection conn, RDFFormat format, InputStream i)
            throws RDFParseException, RDFHandlerException, IOException, RepositoryException {
        if (batchSize < 1) {
            batchSize = Integer.MAX_VALUE;
        }

        System.setProperty("org.eclipse.rdf4j.rio.binary.format_version", "1");

        RDFParser parser = Rio.createParser(format);
        parser.set(XMLParserSettings.DISALLOW_DOCTYPE_DECL, false);
        ParserConfig config = parser.getParserConfig();
        config.set(BasicParserSettings.PRESERVE_BNODE_IDS, true);
        config.set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, false);
        config.set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
        config.set(BasicParserSettings.NORMALIZE_DATATYPE_VALUES, false);

        // set the parser configuration for our connection
        conn.setParserConfig(config);

        // add our own custom RDFHandler to the parser. This handler
        // takes care of adding
        // triples to our repository and doing intermittent commits
        parser.setRDFHandler(new ChunkCommitter(conn, context, batchSize));
        conn.begin();
        parser.parse(i, context == null ? "" : context.toString());
        conn.commit();

    }

    class ChunkCommitter implements RDFHandler {

        private long totalLoaded = 0;
        private final long chunkSize;
        private final RDFInserter inserter;
        private final RepositoryConnection conn;
        private final IRI context;
        private final ValueFactory factory;

        private long count = 0L;

        public ChunkCommitter(RepositoryConnection conn, IRI context, long chunkSize) {
            this.chunkSize = chunkSize;
            this.context = context;
            this.conn = conn;
            this.factory = conn.getValueFactory();
            inserter = new RDFInserter(conn);
        }


        public long getStatementCount() {
            return count;
        }


        @Override
        public void startRDF() throws RDFHandlerException {
            inserter.startRDF();
        }


        @Override
        public void endRDF() throws RDFHandlerException {
            inserter.endRDF();
        }


        @Override
        public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
            inserter.handleNamespace(prefix, uri);
        }


        @Override
        public void handleStatement(Statement st) throws RDFHandlerException {
            if (context != null) {
                st = factory.createStatement(st.getSubject(), st.getPredicate(), st.getObject(), context);
            }
            inserter.handleStatement(st);
            count++;
            // do an intermittent commit whenever the number of triples
            // has reached a multiple of the chunk size
            if (count % chunkSize == 0) {
                try {
                    conn.commit();
                    totalLoaded += chunkSize;
                    log.info("Loaded chunk {}. Already loaded {}", new Object[] { chunkSize, totalLoaded });
                    conn.begin();
                } catch (RepositoryException e) {
                    throw new RDFHandlerException(e);
                }
            }
        }


        @Override
        public void handleComment(String comment) throws RDFHandlerException {
            inserter.handleComment(comment);
        }
    }

}
