package sk.gov.knowledgegraph.model.refid.application.component;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Referencovateľný identifikátor typu objektová vlastnosť v ontológii
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ObjectPropertyComponent extends OntologyComponent {

    @NotBlank
    private String rangeUri;

}