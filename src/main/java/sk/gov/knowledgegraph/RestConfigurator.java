package sk.gov.knowledgegraph;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;
import sk.gov.knowledgegraph.controller.mixin.ClassComponentRegistrationApplicationMixIn;
import sk.gov.knowledgegraph.controller.mixin.DatatypePropertyComponentRegistrationApplicationMixIn;
import sk.gov.knowledgegraph.controller.mixin.ObjectPropertyComponentRegistrationApplicationMixIn;
import sk.gov.knowledgegraph.controller.mixin.OntologyComponentRegistrationApplicationMixIn;
import sk.gov.knowledgegraph.controller.mixin.OntologyRegistrationApplicationMixIn;
import sk.gov.knowledgegraph.controller.mixin.OntologyVersionRegistrationApplicationMixIn;
import sk.gov.knowledgegraph.controller.mixin.SemanticResourceRegistrationApplicationMixIn;
import sk.gov.knowledgegraph.controller.mixin.URITemplateRegistrationApplicationMixIn;
import sk.gov.knowledgegraph.model.refid.application.ClassComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.DatatypePropertyComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.ObjectPropertyComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.OntologyComponentRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.OntologyRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.OntologyVersionRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.SemanticResourceRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.URITemplateRegistrationApplication;

@Slf4j
@Configuration
public class RestConfigurator implements Jackson2ObjectMapperBuilderCustomizer, WebMvcConfigurer {

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        log.debug("Adding RestConfigurator mixins");
        builder.featuresToDisable(SerializationFeature.WRAP_ROOT_VALUE, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                DeserializationFeature.UNWRAP_ROOT_VALUE).serializationInclusion(JsonInclude.Include.NON_EMPTY)
                .mixIn(SemanticResourceRegistrationApplication.class, SemanticResourceRegistrationApplicationMixIn.class)
                .mixIn(URITemplateRegistrationApplication.class, URITemplateRegistrationApplicationMixIn.class)
                .mixIn(OntologyVersionRegistrationApplication.class, OntologyVersionRegistrationApplicationMixIn.class)
                .mixIn(OntologyRegistrationApplication.class, OntologyRegistrationApplicationMixIn.class)
                .mixIn(OntologyComponentRegistrationApplication.class, OntologyComponentRegistrationApplicationMixIn.class)
                .mixIn(ClassComponentRegistrationApplication.class, ClassComponentRegistrationApplicationMixIn.class)
                .mixIn(DatatypePropertyComponentRegistrationApplication.class, DatatypePropertyComponentRegistrationApplicationMixIn.class)
                .mixIn(ObjectPropertyComponentRegistrationApplication.class, ObjectPropertyComponentRegistrationApplicationMixIn.class);
    }

}