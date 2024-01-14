package sk.gov.knowledgegraph.model.refid.application;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {

    @NotBlank
    private String username;
    /**
     * Identifikátor/Kód inštitúcie používateľa
     */
    @NotBlank
    private String institutionId;

}