package sk.gov.knowledgegraph.service;

import static org.eclipse.rdf4j.model.util.Values.iri;
import static sk.gov.knowledgegraph.service.ReferenceIdentifierApplicationManagementService.SRRA_ONTOLOGY_PREFIX;
import static sk.gov.knowledgegraph.service.ReferenceIdentifierApplicationManagementService.URI_NAMESPACES;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sk.gov.knowledgegraph.model.exception.ErrorCode;
import sk.gov.knowledgegraph.model.exception.KnowledgeGraphException;
import sk.gov.knowledgegraph.model.refid.application.ApplicationState;
import sk.gov.knowledgegraph.model.refid.application.ClassComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.DatatypePropertyComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.ObjectPropertyComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.Ontology;
import sk.gov.knowledgegraph.model.refid.application.OntologyConcept;
import sk.gov.knowledgegraph.model.refid.application.OntologyRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.OntologyVersionRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.Organization;
import sk.gov.knowledgegraph.model.refid.application.SemanticResourceRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.URITemplate;
import sk.gov.knowledgegraph.model.refid.application.URITemplateQueryParam;
import sk.gov.knowledgegraph.model.refid.application.URITemplateRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.User;
import sk.gov.knowledgegraph.model.refid.application.ValueType;
import sk.gov.knowledgegraph.model.refid.application.component.ClassComponent;
import sk.gov.knowledgegraph.model.refid.application.component.DatatypePropertyComponent;
import sk.gov.knowledgegraph.model.refid.application.component.ObjectPropertyComponent;
import sk.gov.knowledgegraph.service.query.Query;

@Service
@Slf4j
public class LoadReferenceIdentifierApplicationManagementService {

    @Autowired
    @Qualifier("refidRepository")
    private Repository refidRepository;

    public SemanticResourceRegistrationApplication loadApplicationFromDB(String uri) throws KnowledgeGraphException {
        String queryStr = """
                SELECT
                        ?uri
                        ?type
                        ?description
                        ?state
                        ?createdAt ?createdByUsername ?createdByInstitutionId
                        ?lastModifiedAt ?lastModifiedByUsername ?lastModifiedByInstitutionId
                        ?appliedAt ?appliedByUsername ?appliedByInstitutionId
                        ?rejectedAt ?rejectedByUsername ?rejectedByInstitutionId
                        ?approvedAt ?approvedByUsername ?approvedByInstitutionId
                        ?applicantUri ?applicantOrganizationId
                        ?subject
                WHERE
                {
                        """;
        queryStr += " GRAPH <" + uri + "> { \n";
        queryStr += """
                         ?uri rdf:type ?type .
                         ?uri srra:state ?state .
                         ?uri srra:applicant ?applicantUri . ?applicantUri srra:organizationId ?applicantOrganizationId .
                         ?uri srra:createdAt ?createdAt . ?uri srra:createdBy ?createdBy . ?createdBy srra:username ?createdByUsername . ?createdBy srra:institutionId ?createdByInstitutionId .
                         OPTIONAL { ?uri srra:lastModifiedAt ?lastModifiedAt .?uri srra:lastModifiedBy ?lastModifiedBy . ?lastModifiedBy srra:username ?lastModifiedByUsername . ?lastModifiedBy srra:institutionId ?lastModifiedByInstitutionId . }
                         OPTIONAL { ?uri srra:appliedAt ?appliedAt .?uri srra:appliedBy ?appliedBy . ?appliedBy srra:username ?appliedByUsername . ?appliedBy srra:institutionId ?appliedByInstitutionId . }
                         OPTIONAL { ?uri srra:rejectedAt ?rejectedAt .?uri srra:rejectedBy ?rejectedBy . ?rejectedBy srra:username ?rejectedByUsername . ?rejectedBy srra:institutionId ?rejectedByInstitutionId . }
                         OPTIONAL { ?uri srra:approvedAt ?approvedAt .?uri srra:approvedBy ?approvedBy . ?approvedBy srra:username ?approvedByUsername . ?approvedBy srra:institutionId ?approvedByInstitutionId . }
                         OPTIONAL { ?uri srra:description ?description . }
                         ?uri srra:subject ?subject .
                   }
                }
                 """;

        Query q = new Query(queryStr, new Locale("sk"));
        q.setLimit(2); //ak nebude presne 1 vysledok tak vyhodime exceptionu
        q.bindValueToVariable("uri", uri);
        List<SemanticResourceRegistrationApplication> mappedResults = new ArrayList<>();
        try (RepositoryConnection conn = refidRepository.getConnection()) {
            log.debug("Executing query for loadApplicationFromDB(): {}", q.getPreparedQuery(URI_NAMESPACES));
            TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, q.getPreparedQuery(URI_NAMESPACES));
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                result.forEach(c -> {
                    SemanticResourceRegistrationApplication data = null;

                    if (c.getBinding("type").getValue().stringValue().equals(iri(SRRA_ONTOLOGY_PREFIX, "OntologyRegistrationApplication").stringValue())) {
                        data = new OntologyRegistrationApplication();
                        ((OntologyRegistrationApplication) data).setSubject(loadOntologyConcept(c.getBinding("subject").getValue().stringValue()));
                    } else if (c.getBinding("type").getValue().stringValue()
                            .equals(iri(SRRA_ONTOLOGY_PREFIX, "ClassComponentRegistrationApplication").stringValue())) {
                        data = new ClassComponentRegistrationApplication();
                        ((ClassComponentRegistrationApplication) data).setSubject(loadClassPropertyComponent(c.getBinding("subject").getValue().stringValue()));
                    } else if (c.getBinding("type").getValue().stringValue()
                            .equals(iri(SRRA_ONTOLOGY_PREFIX, "DatatypePropertyComponentRegistrationApplication").stringValue())) {
                        data = new DatatypePropertyComponentRegistrationApplication();
                        ((DatatypePropertyComponentRegistrationApplication) data)
                                .setSubject(loadDataPropertyComponent(c.getBinding("subject").getValue().stringValue()));
                    } else if (c.getBinding("type").getValue().stringValue()
                            .equals(iri(SRRA_ONTOLOGY_PREFIX, "ObjectPropertyComponentRegistrationApplication").stringValue())) {
                        data = new ObjectPropertyComponentRegistrationApplication();
                        ((ObjectPropertyComponentRegistrationApplication) data)
                                .setSubject(loadObjectPropertyComponent(c.getBinding("subject").getValue().stringValue()));
                    } else if (c.getBinding("type").getValue().stringValue()
                            .equals(iri(SRRA_ONTOLOGY_PREFIX, "OntologyVersionRegistrationApplication").stringValue())) {
                        data = new OntologyVersionRegistrationApplication();
                        ((OntologyVersionRegistrationApplication) data).setSubject(loadDataOntology(c.getBinding("subject").getValue().stringValue()));
                    } else if (c.getBinding("type").getValue().stringValue()
                            .equals(iri(SRRA_ONTOLOGY_PREFIX, "URITemplateRegistrationApplication").stringValue())) {
                        data = new URITemplateRegistrationApplication();
                        ((URITemplateRegistrationApplication) data).setSubject(loadDataURITemplate(uri));
                    }
                    data.setUri(c.getBinding("uri").getValue().stringValue());
                    data.setDescription(c.hasBinding("description") ? c.getBinding("description").getValue().stringValue() : null);
                    data.setState(ApplicationState.valueOf(c.getBinding("state").getValue().stringValue()));

                    data.setApplicant(new Organization().setOrganizationId(c.getBinding("applicantOrganizationId").getValue().stringValue())
                            .setUri(c.getBinding("applicantUri").getValue().stringValue()));

                    data.setCreatedAt(LocalDate.parse(c.getBinding("createdAt").getValue().stringValue(), DateTimeFormatter.ISO_DATE));
                    data.setCreatedBy(new User().setInstitutionId(c.getBinding("createdByInstitutionId").getValue().stringValue())
                            .setUsername(c.getBinding("createdByUsername").getValue().stringValue()));

                    if (c.hasBinding("lastModifiedAt")) {
                        data.setLastModifiedAt(LocalDate.parse(c.getBinding("lastModifiedAt").getValue().stringValue(), DateTimeFormatter.ISO_DATE));
                        data.setLastModifiedBy(new User().setInstitutionId(c.getBinding("lastModifiedByInstitutionId").getValue().stringValue())
                                .setUsername(c.getBinding("lastModifiedByUsername").getValue().stringValue()));
                    }

                    if (c.hasBinding("appliedAt")) {
                        data.setAppliedAt(LocalDate.parse(c.getBinding("appliedAt").getValue().stringValue(), DateTimeFormatter.ISO_DATE));
                        data.setAppliedBy(new User().setInstitutionId(c.getBinding("appliedByInstitutionId").getValue().stringValue())
                                .setUsername(c.getBinding("appliedByUsername").getValue().stringValue()));
                    }

                    if (c.hasBinding("rejectedAt")) {
                        data.setRejectedAt(LocalDate.parse(c.getBinding("rejectedAt").getValue().stringValue(), DateTimeFormatter.ISO_DATE));
                        data.setRejectedBy(new User().setInstitutionId(c.getBinding("rejectedByInstitutionId").getValue().stringValue())
                                .setUsername(c.getBinding("rejectedByUsername").getValue().stringValue()));
                    }

                    if (c.hasBinding("approvedAt")) {
                        data.setApprovedAt(LocalDate.parse(c.getBinding("approvedAt").getValue().stringValue(), DateTimeFormatter.ISO_DATE));
                        data.setApprovedBy(new User().setInstitutionId(c.getBinding("approvedByInstitutionId").getValue().stringValue())
                                .setUsername(c.getBinding("approvedByUsername").getValue().stringValue()));
                    }

                    mappedResults.add(data);
                });
            }
        }

        if (mappedResults.isEmpty()) {
            throw new KnowledgeGraphException(ErrorCode.NO_APPLICATION_FOUND, Map.of("uri", uri));
        }
        if (mappedResults.size() > 1) {
            throw new KnowledgeGraphException(ErrorCode.INTERNAL_DATA_CORRUPTION_TOO_MANY_APPLICATIONS_FOUND_FOR_URI, Map.of("uri", uri));
        }
        return mappedResults.get(0);

    }


    public OntologyConcept loadOntologyConcept(String uri) {
        String queryStr = """
                SELECT
                        ?uri
                        ?nameSk
                        ?nameEng
                        ?description
                        ?manager ?managerOrganizationId
                WHERE
                {
                        ?uri srra:name ?nameSk . FILTER(langMatches(lang(?nameSk),"sk")) .
                        OPTIONAL { ?uri srra:name ?nameEng . FILTER(langMatches(lang(?nameEng),"en"))}
                        OPTIONAL { ?uri srra:description ?description . }
                        OPTIONAL { ?uri srra:manager ?manager . ?manager srra:organizationId ?managerOrganizationId }
                }
                 """;

        Query q = new Query(queryStr, new Locale("sk"));
        q.bindValueToVariable("uri", uri);
        q.setLimit(1);

        try (RepositoryConnection connQueryParam = refidRepository.getConnection()) {
            log.debug("Executing query for ontology: {}", q.getPreparedQuery(URI_NAMESPACES));
            TupleQuery tupleQueryQueryParam = connQueryParam.prepareTupleQuery(QueryLanguage.SPARQL, q.getPreparedQuery(URI_NAMESPACES));
            try (TupleQueryResult resultQueryParam = tupleQueryQueryParam.evaluate()) {
                while (resultQueryParam.hasNext()) {
                    final OntologyConcept ontologyConcept = new OntologyConcept();
                    BindingSet c = resultQueryParam.next();
                    ontologyConcept.setUri(c.getBinding("uri").getValue().stringValue());
                    ontologyConcept.setName(c.getBinding("nameSk").getValue().stringValue());
                    if (c.hasBinding("nameEng")) {
                        ontologyConcept.setNameEng(c.getBinding("nameEng").getValue().stringValue());
                    }
                    if (c.hasBinding("description")) {
                        ontologyConcept.setDescription(c.getBinding("description").getValue().stringValue());
                    }

                    if (c.hasBinding("manager")) {
                        ontologyConcept.setManager(new Organization());
                        ontologyConcept.getManager().setUri(c.getBinding("manager").getValue().stringValue());
                        ontologyConcept.getManager().setOrganizationId(c.getBinding("managerOrganizationId").getValue().stringValue());
                    }
                    return ontologyConcept;

                }
            }
        }
        return null;
    }


    public ClassComponent loadClassPropertyComponent(String uri) {
        String queryStr = """
                SELECT
                        ?uri
                        ?nameSk
                        ?nameEng
                        ?description
                        ?ontologyComponentCode
                        ?ontologyComponentRangeUri
                        ?ontology
                WHERE
                {
                        ?uri srra:name ?nameSk . FILTER(langMatches(lang(?nameSk),"sk")) .
                        OPTIONAL { ?uri srra:name ?nameEng . FILTER(langMatches(lang(?nameEng),"en"))}
                        OPTIONAL { ?uri srra:ontologyComponentCode ?ontologyComponentCode }
                        OPTIONAL { ?uri srra:description ?description . }
                        OPTIONAL { ?uri srra:ontology ?ontology . }
                }
                 """;

        Query q = new Query(queryStr, new Locale("sk"));
        q.setLimit(1);
        q.bindValueToVariable("uri", uri);

        try (RepositoryConnection connQueryParam = refidRepository.getConnection()) {
            log.debug("Executing query for data property component: {}", q.getPreparedQuery(URI_NAMESPACES));
            TupleQuery tupleQueryQueryParam = connQueryParam.prepareTupleQuery(QueryLanguage.SPARQL, q.getPreparedQuery(URI_NAMESPACES));
            try (TupleQueryResult resultQueryParam = tupleQueryQueryParam.evaluate()) {
                while (resultQueryParam.hasNext()) {
                    final ClassComponent classComponent = new ClassComponent();
                    BindingSet c = resultQueryParam.next();
                    classComponent.setUri(c.getBinding("uri").getValue().stringValue());

                    classComponent.setName(c.getBinding("nameSk").getValue().stringValue());
                    if (c.hasBinding("nameEng")) {
                        classComponent.setNameEng(c.getBinding("nameEng").getValue().stringValue());
                    }
                    if (c.hasBinding("description")) {
                        classComponent.setDescription(c.getBinding("description").getValue().stringValue());
                    }
                    if (c.hasBinding("ontologyComponentCode")) {
                        classComponent.setCode(c.getBinding("ontologyComponentCode").getValue().stringValue());
                    }

                    if (c.hasBinding("ontology")) {
                        classComponent.setOntology(loadDataOntology(c.getBinding("ontology").getValue().stringValue()));
                    }
                    return classComponent;

                }
            }
        }
        return null;
    }


    public DatatypePropertyComponent loadDataPropertyComponent(String uri) {
        String queryStr = """
                SELECT
                        ?uri
                        ?nameSk
                        ?nameEng
                        ?description
                        ?ontologyComponentCode
                        ?ontologyComponentRangeUri
                        ?ontology
                WHERE
                {
                        ?uri srra:name ?nameSk . FILTER(langMatches(lang(?nameSk),"sk")) .
                        OPTIONAL { ?uri srra:name ?nameEng . FILTER(langMatches(lang(?nameEng),"en"))}
                        OPTIONAL { ?uri srra:ontologyComponentCode ?ontologyComponentCode }
                        OPTIONAL { ?uri srra:range ?ontologyComponentRangeUri }
                        OPTIONAL { ?uri srra:description ?description . }
                        OPTIONAL { ?uri srra:ontology ?ontology . }
                }
                 """;

        Query q = new Query(queryStr, new Locale("sk"));
        q.bindValueToVariable("uri", uri);
        q.setLimit(1);

        try (RepositoryConnection connQueryParam = refidRepository.getConnection()) {
            log.debug("Executing query for data property component: {}", q.getPreparedQuery(URI_NAMESPACES));
            TupleQuery tupleQueryQueryParam = connQueryParam.prepareTupleQuery(QueryLanguage.SPARQL, q.getPreparedQuery(URI_NAMESPACES));
            try (TupleQueryResult resultQueryParam = tupleQueryQueryParam.evaluate()) {
                while (resultQueryParam.hasNext()) {
                    final DatatypePropertyComponent dataPropertyComponent = new DatatypePropertyComponent();

                    BindingSet c = resultQueryParam.next();
                    dataPropertyComponent.setUri(c.getBinding("uri").getValue().stringValue());

                    if (c.hasBinding("nameSk")) {
                        dataPropertyComponent.setName(c.getBinding("nameSk").getValue().stringValue());
                    }
                    if (c.hasBinding("nameEng")) {
                        dataPropertyComponent.setNameEng(c.getBinding("nameEng").getValue().stringValue());
                    }
                    if (c.hasBinding("description")) {
                        dataPropertyComponent.setDescription(c.getBinding("description").getValue().stringValue());
                    }
                    if (c.hasBinding("ontologyComponentCode")) {
                        dataPropertyComponent.setCode(c.getBinding("ontologyComponentCode").getValue().stringValue());
                    }
                    if (c.hasBinding("ontologyComponentRangeUri")) {
                        dataPropertyComponent.setRange(ValueType.valueOf(c.getBinding("ontologyComponentRangeUri").getValue().stringValue()));
                    }

                    if (c.hasBinding("ontology")) {
                        dataPropertyComponent.setOntology(loadDataOntology(c.getBinding("ontology").getValue().stringValue()));
                    }
                    return dataPropertyComponent;
                }
            }
        }
        return null;
    }


    public ObjectPropertyComponent loadObjectPropertyComponent(String uri) {
        String queryStr = """
                SELECT
                        ?uri
                        ?nameSk
                        ?nameEng
                        ?description
                        ?ontologyComponentCode
                        ?ontologyComponentRangeUri
                        ?ontology
                WHERE
                {
                        ?uri srra:name ?nameSk . FILTER(langMatches(lang(?nameSk),"sk")) .
                        OPTIONAL { ?uri srra:name ?nameEng . FILTER(langMatches(lang(?nameEng),"en"))}
                        OPTIONAL { ?uri srra:ontologyComponentCode ?ontologyComponentCode }
                        OPTIONAL { ?uri srra:range ?ontologyComponentRangeUri }
                        OPTIONAL { ?uri srra:description ?description . }
                        OPTIONAL { ?uri srra:ontology ?ontology . }
                }
                 """;

        Query q = new Query(queryStr, new Locale("sk"));
        q.bindValueToVariable("uri", uri);
        q.setLimit(1);

        try (RepositoryConnection connQueryParam = refidRepository.getConnection()) {
            log.debug("Executing query for object property component: {}", q.getPreparedQuery(URI_NAMESPACES));
            TupleQuery tupleQueryQueryParam = connQueryParam.prepareTupleQuery(QueryLanguage.SPARQL, q.getPreparedQuery(URI_NAMESPACES));
            try (TupleQueryResult resultQueryParam = tupleQueryQueryParam.evaluate()) {
                while (resultQueryParam.hasNext()) {
                    final ObjectPropertyComponent objectPropertyComponent = new ObjectPropertyComponent();

                    BindingSet c = resultQueryParam.next();
                    objectPropertyComponent.setUri(c.getBinding("uri").getValue().stringValue());

                    if (c.hasBinding("nameSk")) {
                        objectPropertyComponent.setName(c.getBinding("nameSk").getValue().stringValue());
                    }
                    if (c.hasBinding("nameEng")) {
                        objectPropertyComponent.setNameEng(c.getBinding("nameEng").getValue().stringValue());
                    }
                    if (c.hasBinding("description")) {
                        objectPropertyComponent.setDescription(c.getBinding("description").getValue().stringValue());
                    }
                    if (c.hasBinding("ontologyComponentCode")) {
                        objectPropertyComponent.setCode(c.getBinding("ontologyComponentCode").getValue().stringValue());
                    }
                    if (c.hasBinding("ontologyComponentRangeUri")) {
                        objectPropertyComponent.setRangeUri(c.getBinding("ontologyComponentRangeUri").getValue().stringValue());
                    }

                    if (c.hasBinding("ontology")) {
                        objectPropertyComponent.setOntology(loadDataOntology(c.getBinding("ontology").getValue().stringValue()));
                    }
                    return objectPropertyComponent;
                }
            }
        }
        return null;
    }


    public Ontology loadDataOntology(String uri) {
        String queryStr = """
                SELECT
                        ?uri
                        ?version
                        ?validFrom
                        ?validTo
                        ?concept ?conceptNameSk
                WHERE
                {
                         ?uri rdf:type ?type .
                         OPTIONAL { ?uri srra:validFrom ?validFrom . }
                         OPTIONAL { ?uri srra:validTo ?validTo . }
                         OPTIONAL { ?uri srra:version ?version . }
                         OPTIONAL { ?uri srra:concept ?concept . ?concept srra:name ?conceptNameSk . FILTER(langMatches(lang(?conceptNameSk), "sk")) . }
                }
                 """;

        Query q = new Query(queryStr, new Locale("sk"));
        q.bindValueToVariable("uri", uri);
        q.setLimit(1);

        try (RepositoryConnection connQueryParam = refidRepository.getConnection()) {
            log.debug("Executing query for ontology: {}", q.getPreparedQuery(URI_NAMESPACES));
            TupleQuery tupleQueryQueryParam = connQueryParam.prepareTupleQuery(QueryLanguage.SPARQL, q.getPreparedQuery(URI_NAMESPACES));
            try (TupleQueryResult resultQueryParam = tupleQueryQueryParam.evaluate()) {
                while (resultQueryParam.hasNext()) {
                    final Ontology ontology = new Ontology();
                    BindingSet c = resultQueryParam.next();
                    ontology.setUri(c.getBinding("uri").getValue().stringValue());

                    if (c.hasBinding("validFrom")) {
                        ontology.setValidFrom(LocalDate.parse(c.getBinding("validFrom").getValue().stringValue(), DateTimeFormatter.ISO_DATE));
                    }

                    if (c.hasBinding("validTo")) {
                        ontology.setValidTo(LocalDate.parse(c.getBinding("validTo").getValue().stringValue(), DateTimeFormatter.ISO_DATE));
                    }

                    if (c.hasBinding("version")) {
                        ontology.setVersion(c.getBinding("version").getValue().stringValue());
                    }
                    if (c.hasBinding("concept")) {
                        ontology.setConcept(loadOntologyConcept(c.getBinding("concept").getValue().stringValue()));
                    }
                    return ontology;
                }
            }
        }
        return null;
    }


    public URITemplate loadDataURITemplate(String appUri) {
        String queryStr = """
                SELECT
                        ?uri
                        ?redirectTemplateUrl
                        ?nameSk
                        ?hasVersion
                        ?uriNamespace
                        ?description
                        ?validFrom
                        ?validTo
                        ?idRegexp
                        ?type ?typeComponentNameSk
                WHERE
                {
                         ?appUri srra:subject ?uri .
                         ?uri srra:name ?nameSk . FILTER(langMatches(lang(?nameSk),"sk")) .
                         OPTIONAL { ?uri srra:redirectTemplateUrl ?redirectTemplateUrl }
                         OPTIONAL { ?uri srra:hasVersion ?hasVersion . }
                         OPTIONAL { ?uri srra:uriNamespace ?uriNamespace }
                         OPTIONAL { ?uri srra:description ?description . }
                         OPTIONAL { ?uri srra:validFrom ?validFrom . }
                         OPTIONAL { ?uri srra:validTo ?validTo . }
                         OPTIONAL { ?uri srra:idRegexp ?idRegexp . }
                         OPTIONAL { ?uri srra:type ?type . ?type srra:name ?typeComponentNameSk . FILTER(langMatches(lang(?typeComponentNameSk), "sk")) . }
                }
                 """;

        Query q = new Query(queryStr, new Locale("sk"));
        q.bindValueToVariable("appUri", appUri);
        q.setLimit(1);

        try (RepositoryConnection connUriTemplate = refidRepository.getConnection()) {
            log.debug("Executing query for loadApplicationFromDB () query param: {}", q.getPreparedQuery(URI_NAMESPACES));
            TupleQuery tupleUriParam = connUriTemplate.prepareTupleQuery(QueryLanguage.SPARQL, q.getPreparedQuery(URI_NAMESPACES));
            try (TupleQueryResult resultUriParam = tupleUriParam.evaluate()) {
                while (resultUriParam.hasNext()) {
                    final URITemplate uriTemplate = new URITemplate();

                    BindingSet c = resultUriParam.next();
                    if (c.hasBinding("redirectTemplateUrl")) {
                        uriTemplate.setRedirectTemplateUrl(c.getBinding("redirectTemplateUrl").getValue().stringValue());
                    }
                    if (c.hasBinding("nameSk")) {
                        uriTemplate.setName(c.getBinding("nameSk").getValue().stringValue());
                    }

                    if (c.hasBinding("hasVersion")) {
                        uriTemplate.setHasVersion(Boolean.valueOf(c.getBinding("hasVersion").getValue().stringValue()));
                    }

                    if (c.hasBinding("uriNamespace")) {
                        uriTemplate.setUriNamespace(c.getBinding("uriNamespace").getValue().stringValue());
                    }

                    if (c.hasBinding("description")) {
                        uriTemplate.setDescription(c.getBinding("description").getValue().stringValue());
                    }

                    if (c.hasBinding("validFrom")) {
                        uriTemplate.setValidFrom(LocalDate.parse(c.getBinding("validFrom").getValue().stringValue(), DateTimeFormatter.ISO_DATE));
                    }

                    if (c.hasBinding("validTo")) {
                        uriTemplate.setValidTo(LocalDate.parse(c.getBinding("validTo").getValue().stringValue(), DateTimeFormatter.ISO_DATE));
                    }

                    if (c.hasBinding("type")) {
                        uriTemplate.setType(new ClassComponent());
                        uriTemplate.getType().setUri(c.getBinding("type").getValue().stringValue());
                        uriTemplate.getType().setName(c.getBinding("typeComponentNameSk").getValue().stringValue());
                    }

                    Query queryParamsQuery = new Query("""
                            SELECT ?name ?description WHERE {
                               ?appUri srra:subject ?subjectUri .
                               ?subjectUri srra:queryParam ?queryParam .
                               ?queryParam srra:name ?name .
                               ?queryParam srra:description ?description .
                            }
                            """);
                    queryParamsQuery.bindValueToVariable("appUri", appUri);

                    try (RepositoryConnection connQueryParam = refidRepository.getConnection()) {
                        log.debug("Executing query for loadApplicationFromDB () query param: {}", queryParamsQuery.getPreparedQuery(URI_NAMESPACES));
                        TupleQuery tupleQueryQueryParam = connQueryParam.prepareTupleQuery(QueryLanguage.SPARQL,
                                queryParamsQuery.getPreparedQuery(URI_NAMESPACES));
                        try (TupleQueryResult resultQueryParam = tupleQueryQueryParam.evaluate()) {
                            while (resultQueryParam.hasNext()) {
                                BindingSet qpBinding = resultQueryParam.next();
                                if (uriTemplate.getQueryParams() == null) {
                                    uriTemplate.setQueryParams(new ArrayList<>());
                                }

                                URITemplateQueryParam qp = new URITemplateQueryParam();
                                qp.setName(qpBinding.getBinding("name").getValue().stringValue());
                                qp.setDescription(qpBinding.getBinding("description").getValue().stringValue());
                                uriTemplate.getQueryParams().add(qp);
                            }
                        }
                    }
                    return uriTemplate;
                }
            }
        }

        return null;
    }

}
