package sk.gov.knowledgegraph.model.refid.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Základné údaje o ontológii
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = "uri")
public class OntologyConcept {

    /**
     * URI identifikátor ontológie
     */
    @NotBlank
    private String uri;
    /**
     * Názov ontológie (SK)
     */
    @NotBlank
    private String name;
    /**
     * Názov ontológie (ENG)
     */
    private String nameEng;
    /**
     * Popis
     */
    private String description;
    @NotNull
    @Valid
    private Organization manager;


}