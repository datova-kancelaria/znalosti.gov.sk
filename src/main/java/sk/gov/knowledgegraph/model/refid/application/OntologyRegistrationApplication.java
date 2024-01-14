package sk.gov.knowledgegraph.model.refid.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Žiadosť o registráciu ontológie
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class OntologyRegistrationApplication extends SemanticResourceRegistrationApplication {

    @NotNull
    @Valid
    private OntologyConcept subject;

}