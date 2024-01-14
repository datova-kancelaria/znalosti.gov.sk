package sk.gov.knowledgegraph.model.refid.application.search;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import sk.gov.knowledgegraph.model.refid.application.ApplicationState;

@Data
@Accessors(chain = true)
public class SemanticResourceRegistrationApplicationSearchData {

    @NotBlank
    private String uri;
    @NotBlank
    private String name;
    @NotNull
    private ApplicationState state;
    @NotNull
    private SemanticResourceRegistrationApplicationType type;
}
