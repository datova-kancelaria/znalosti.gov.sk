package sk.gov.knowledgegraph.model.refid.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import sk.gov.knowledgegraph.model.refid.application.component.OntologyComponent;

/**
 * Žiadosť o registráciu ontológického prvku (dátovej vlastnosti, objektovej vlastnosti alebo triedy)
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public abstract class OntologyComponentRegistrationApplication<T extends OntologyComponent> extends SemanticResourceRegistrationApplication {

    @NotNull
    @Valid
    private T subject;

}