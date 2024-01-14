package sk.gov.knowledgegraph.model.refid.application;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Organization {

    @NotBlank
    private String organizationId;
    @NotBlank
    private String uri;

}