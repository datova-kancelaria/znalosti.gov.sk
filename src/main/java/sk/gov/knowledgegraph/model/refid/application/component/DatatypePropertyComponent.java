package sk.gov.knowledgegraph.model.refid.application.component;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import sk.gov.knowledgegraph.model.refid.application.ValueType;

/**
 * Referencovateľný identifikátor typu dátová vlastnosť v ontológii
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DatatypePropertyComponent extends OntologyComponent {

    @NotNull
    private ValueType range;

}