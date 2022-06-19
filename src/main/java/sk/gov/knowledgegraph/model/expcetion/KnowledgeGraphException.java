package sk.gov.knowledgegraph.model.expcetion;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Setter(value = AccessLevel.PROTECTED)
@Getter
@ToString
public class KnowledgeGraphException extends Exception {
    private static final long serialVersionUID = 6674973974476193568L;

    private final Error errorCode;
    private final Map<String, String> details;

    public KnowledgeGraphException(final Error errorCode) {
        this(errorCode, Map.of());
    }

    public KnowledgeGraphException(final Error errorCode, final Map<String, String> details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
    }
}
