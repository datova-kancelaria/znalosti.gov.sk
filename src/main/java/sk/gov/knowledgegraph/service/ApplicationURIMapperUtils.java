package sk.gov.knowledgegraph.service;

import static org.eclipse.rdf4j.model.util.Statements.statement;
import static org.eclipse.rdf4j.model.util.Values.iri;
import static org.eclipse.rdf4j.model.util.Values.bnode;
import static org.eclipse.rdf4j.model.util.Values.literal;
import static sk.gov.knowledgegraph.service.ReferenceIdentifierApplicationManagementService.SRRA_ONTOLOGY_PREFIX;
import static sk.gov.knowledgegraph.service.ReferenceIdentifierApplicationManagementService.URI_METAIS_USER_PREFIX;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import sk.gov.knowledgegraph.model.refid.application.Ontology;
import sk.gov.knowledgegraph.model.refid.application.OntologyComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.OntologyConcept;
import sk.gov.knowledgegraph.model.refid.application.OntologyRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.OntologyVersionRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.Organization;
import sk.gov.knowledgegraph.model.refid.application.SemanticResourceRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.URITemplate;
import sk.gov.knowledgegraph.model.refid.application.URITemplateQueryParam;
import sk.gov.knowledgegraph.model.refid.application.URITemplateRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.User;
import sk.gov.knowledgegraph.model.refid.application.component.DatatypePropertyComponent;
import sk.gov.knowledgegraph.model.refid.application.component.ObjectPropertyComponent;
import sk.gov.knowledgegraph.model.refid.application.component.OntologyComponent;

public class ApplicationURIMapperUtils {

    public static List<Statement> convertOrganization(IRI applicationIri, String type, Organization organization) {
        if (organization == null) {
            return List.of();
        }
        List<Statement> statements = new ArrayList<>();

        IRI nodeIri = iri(URI_METAIS_USER_PREFIX, organization.getOrganizationId());
        statements.add(statement(applicationIri, iri(SRRA_ONTOLOGY_PREFIX, type), nodeIri, null));
        statements.add(statement(nodeIri, RDF.TYPE, iri(SRRA_ONTOLOGY_PREFIX, "Organization"), null));
        statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "organizationId"), literal(organization.getOrganizationId()), null));

        return statements;
    }


    public static List<Statement> convertUser(IRI applicationIri, String type, User user) {
        if (user == null) {
            return List.of();
        }
        List<Statement> statements = new ArrayList<>();

        IRI nodeIri = iri(URI_METAIS_USER_PREFIX, user.getUsername());
        statements.add(statement(applicationIri, iri(SRRA_ONTOLOGY_PREFIX, type), nodeIri, null));
        statements.add(statement(nodeIri, RDF.TYPE, iri(SRRA_ONTOLOGY_PREFIX, "User"), null));
        statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "username"), literal(user.getUsername()), null));
        statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "institutionId"), literal(user.getInstitutionId()), null));

        return statements;
    }


    public static List<Statement> convertApplicationToStatements(SemanticResourceRegistrationApplication application) {
        List<Statement> statements = new ArrayList<>();
        IRI applicationIri = iri(application.getUri());

        statements.addAll(convertUser(applicationIri, "createdBy", application.getCreatedBy()));
        if (application.getCreatedAt() != null) {
            statements.add(statement(applicationIri, iri(SRRA_ONTOLOGY_PREFIX, "createdAt"), literal(application.getCreatedAt()), null));
        }

        statements.addAll(convertUser(applicationIri, "lastModifiedBy", application.getLastModifiedBy()));
        if (application.getLastModifiedAt() != null) {
            statements.add(statement(applicationIri, iri(SRRA_ONTOLOGY_PREFIX, "lastModifiedAt"), literal(application.getCreatedAt()), null));
        }

        statements.addAll(convertUser(applicationIri, "appliedBy", application.getAppliedBy()));
        if (application.getAppliedAt() != null) {
            statements.add(statement(applicationIri, iri(SRRA_ONTOLOGY_PREFIX, "appliedAt"), literal(application.getCreatedAt()), null));
        }

        statements.addAll(convertUser(applicationIri, "rejectedBy", application.getRejectedBy()));
        if (application.getRejectedAt() != null) {
            statements.add(statement(applicationIri, iri(SRRA_ONTOLOGY_PREFIX, "rejectedAt"), literal(application.getCreatedAt()), null));
        }

        statements.addAll(convertUser(applicationIri, "approvedBy", application.getApprovedBy()));
        if (application.getApprovedAt() != null) {
            statements.add(statement(applicationIri, iri(SRRA_ONTOLOGY_PREFIX, "approvedAt"), literal(application.getCreatedAt()), null));
        }

        if (ObjectUtils.isNotEmpty(application.getDescription())) {
            statements.add(statement(applicationIri, iri(SRRA_ONTOLOGY_PREFIX, "description"), literal(application.getDescription(), "sk"), null));
        }

        statements.addAll(convertOrganization(applicationIri, "applicant", application.getApplicant()));
        statements.add(statement(applicationIri, iri(SRRA_ONTOLOGY_PREFIX, "state"), literal(application.getState().name()), null));

        if (application instanceof OntologyComponentRegistrationApplication app) {

            OntologyComponent subject = app.getSubject();
            IRI nodeIri = iri(subject.getUri());
            statements.add(statement(applicationIri, iri(SRRA_ONTOLOGY_PREFIX, "subject"), nodeIri, null));
            statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "name"), literal(subject.getName(), "sk"), null));
            statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "name"), literal(subject.getNameEng(), "en"), null));
            statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "ontologyComponentCode"), literal(subject.getCode()), null));
            statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "description"), literal(subject.getDescription()), null));
            statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "ontology"), iri(subject.getOntology().getUri()), null));

            if (subject instanceof ObjectPropertyComponent s) {
                statements.add(statement(applicationIri, RDF.TYPE, iri(SRRA_ONTOLOGY_PREFIX, "ObjectPropertyComponentRegistrationApplication"), null));
                statements.add(statement(nodeIri, RDF.TYPE, iri(SRRA_ONTOLOGY_PREFIX, "ObjectPropertyComponent"), null));
                statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "range"), iri(s.getRangeUri()), null));
            } else if (subject instanceof DatatypePropertyComponent s) {
                statements.add(statement(applicationIri, RDF.TYPE, iri(SRRA_ONTOLOGY_PREFIX, "DatatypePropertyComponentRegistrationApplication"), null));
                statements.add(statement(nodeIri, RDF.TYPE, iri(SRRA_ONTOLOGY_PREFIX, "DatatypePropertyComponent"), null));
                statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "range"), literal(s.getRange()), null));
            } else {
                statements.add(statement(applicationIri, RDF.TYPE, iri(SRRA_ONTOLOGY_PREFIX, "ClassComponentRegistrationApplication"), null));
                statements.add(statement(nodeIri, RDF.TYPE, iri(SRRA_ONTOLOGY_PREFIX, "ClassComponent"), null));
            }
        } else if (application instanceof OntologyRegistrationApplication app) {
            statements.add(statement(applicationIri, RDF.TYPE, iri(SRRA_ONTOLOGY_PREFIX, "OntologyRegistrationApplication"), null));
            OntologyConcept subject = app.getSubject();
            IRI nodeIri = iri(subject.getUri());
            statements.add(statement(applicationIri, iri(SRRA_ONTOLOGY_PREFIX, "subject"), nodeIri, null));
            statements.add(statement(nodeIri, RDF.TYPE, iri(SRRA_ONTOLOGY_PREFIX, "OntologyConcept"), null));

            statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "name"), literal(subject.getName(), "sk"), null));
            statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "name"), literal(subject.getNameEng(), "en"), null));
            statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "description"), literal(subject.getDescription()), null));
            statements.addAll(convertOrganization(applicationIri, "manager", subject.getManager()));
        } else if (application instanceof OntologyVersionRegistrationApplication app) {
            statements.add(statement(applicationIri, RDF.TYPE, iri(SRRA_ONTOLOGY_PREFIX, "OntologyVersionRegistrationApplication"), null));
            Ontology subject = app.getSubject();
            IRI nodeIri = iri(subject.getUri());
            statements.add(statement(applicationIri, iri(SRRA_ONTOLOGY_PREFIX, "subject"), nodeIri, null));
            statements.add(statement(nodeIri, RDF.TYPE, iri(SRRA_ONTOLOGY_PREFIX, "Ontology"), null));

            statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "version"), literal(subject.getVersion()), null));

            if (subject.getValidFrom() != null) {
                statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "validFrom"), literal(subject.getValidFrom()), null));
            }
            if (subject.getValidTo() != null) {
                statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "validTo"), literal(subject.getValidTo()), null));
            }

            IRI conceptIri = iri(subject.getConcept().getUri());
            statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "concept"), conceptIri, null));
        } else if (application instanceof URITemplateRegistrationApplication app) {
            statements.add(statement(applicationIri, RDF.TYPE, iri(SRRA_ONTOLOGY_PREFIX, "URITemplateRegistrationApplication"), null));

            URITemplate subject = app.getSubject();
            BNode nodeIri = bnode();
            statements.add(statement(applicationIri, iri(SRRA_ONTOLOGY_PREFIX, "subject"), nodeIri, null));
            statements.add(statement(nodeIri, RDF.TYPE, iri(SRRA_ONTOLOGY_PREFIX, "URITemplate"), null));

            if (subject.getDescription() != null) {
                statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "description"), literal(subject.getDescription()), null));
            }
            if (subject.getValidFrom() != null) {
                statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "validFrom"), literal(subject.getValidFrom()), null));
            }
            if (subject.getValidTo() != null) {
                statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "validTo"), literal(subject.getValidTo()), null));
            }

            if (subject.getRedirectTemplateUrl() != null) {
                statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "redirectTemplateUrl"), literal(subject.getRedirectTemplateUrl()), null));
            }

            if (subject.getName() != null) {
                statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "name"), literal(subject.getName(), "sk"), null));
            }
            statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "hasVersion"), literal(subject.isHasVersion()), null));

            if (subject.getUriNamespace() != null) {
                statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "uriNamespace"), literal(subject.getUriNamespace()), null));
            }

            if (subject.getIdRegexp() != null) {
                statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "idRegexp"), literal(subject.getIdRegexp()), null));
            }

            if (subject.getType() != null) {
                statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "type"), iri(subject.getType().getUri()), null));
            }

            if (subject.getQueryParams() != null) {
                for (URITemplateQueryParam q : subject.getQueryParams()) {
                    BNode qUri = bnode();
                    statements.add(statement(nodeIri, iri(SRRA_ONTOLOGY_PREFIX, "queryParam"), qUri, null));
                    statements.add(statement(qUri, RDF.TYPE, iri(SRRA_ONTOLOGY_PREFIX, "URITemplateQueryParam"), null));
                    statements.add(statement(qUri, iri(SRRA_ONTOLOGY_PREFIX, "name"), literal(subject.getName()), null));
                    if (subject.getDescription() != null && !subject.getDescription().isBlank()) {
                        statements.add(statement(qUri, iri(SRRA_ONTOLOGY_PREFIX, "description"), literal(subject.getDescription(), "sk"), null));
                    }
                }
            }

        }

        return statements;
    }
}
