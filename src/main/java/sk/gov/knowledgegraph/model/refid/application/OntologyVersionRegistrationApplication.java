package sk.gov.knowledgegraph.model.refid.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Žiadosť o registráciu verzie ontológie
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class OntologyVersionRegistrationApplication extends SemanticResourceRegistrationApplication {

    @NotNull
    @Valid
    public Ontology subject;

}