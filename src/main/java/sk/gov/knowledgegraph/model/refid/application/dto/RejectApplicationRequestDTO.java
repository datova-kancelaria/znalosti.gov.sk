package sk.gov.knowledgegraph.model.refid.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import sk.gov.knowledgegraph.model.refid.application.User;

@Data
public class RejectApplicationRequestDTO {

    @NotBlank
    private String uri;
    @NotNull
    @Valid
    private User user;
}
