package sk.gov.knowledgegraph.model.refid.application;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import sk.gov.knowledgegraph.model.refid.application.component.ClassComponent;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ClassComponentRegistrationApplication extends OntologyComponentRegistrationApplication<ClassComponent> {

}
