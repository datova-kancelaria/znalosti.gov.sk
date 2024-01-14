package sk.gov.knowledgegraph.controller.mixin;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import sk.gov.knowledgegraph.model.refid.application.ClassComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.DatatypePropertyComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.ObjectPropertyComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.OntologyComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.OntologyRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.OntologyVersionRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.URITemplateRegistrationApplication;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DatatypePropertyComponentRegistrationApplication.class),
        @JsonSubTypes.Type(value = ObjectPropertyComponentRegistrationApplication.class),
        @JsonSubTypes.Type(value = ClassComponentRegistrationApplication.class),
        @JsonSubTypes.Type(value = OntologyComponentRegistrationApplication.class),
        @JsonSubTypes.Type(value = OntologyRegistrationApplication.class),
        @JsonSubTypes.Type(value = OntologyVersionRegistrationApplication.class),
        @JsonSubTypes.Type(value = URITemplateRegistrationApplication.class)
})
public interface SemanticResourceRegistrationApplicationMixIn {
}
