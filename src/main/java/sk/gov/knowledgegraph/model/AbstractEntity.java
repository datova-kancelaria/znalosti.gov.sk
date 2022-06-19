package sk.gov.knowledgegraph.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "id" })
public abstract class AbstractEntity {

    private Integer id;

}
