package sk.gov.knowledgegraph.model.refid.application.component;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Referencovateľný identifikátor typu trieda v ontológii
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ClassComponent extends OntologyComponent {

}