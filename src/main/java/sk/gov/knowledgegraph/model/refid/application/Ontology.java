package sk.gov.knowledgegraph.model.refid.application;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Ontológia
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = "uri")
public class Ontology {

    @NotBlank
    private String uri;
    @NotBlank
    private String version;
    @NotNull
    private LocalDate validFrom;
    private LocalDate validTo;

    @NotNull
    @Valid
    private OntologyConcept concept;
}