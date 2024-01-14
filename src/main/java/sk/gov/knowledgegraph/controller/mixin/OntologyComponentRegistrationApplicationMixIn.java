package sk.gov.knowledgegraph.controller.mixin;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import sk.gov.knowledgegraph.model.refid.application.ClassComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.DatatypePropertyComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.ObjectPropertyComponentRegistrationApplication;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "class")
@JsonSubTypes({ @JsonSubTypes.Type(value = DatatypePropertyComponentRegistrationApplication.class),
        @JsonSubTypes.Type(value = ObjectPropertyComponentRegistrationApplication.class),
        @JsonSubTypes.Type(value = ClassComponentRegistrationApplication.class) })
public interface OntologyComponentRegistrationApplicationMixIn {
}
