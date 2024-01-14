package sk.gov.knowledgegraph.model.refid.application.component;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import sk.gov.knowledgegraph.model.refid.application.Ontology;

/**
 * Ontologický komponent (Dátový prvok) = trieda, objektová alebo dátová vlastnosť
 */
@Data
@EqualsAndHashCode(of = "uri")
@Accessors(chain = true)
public abstract class OntologyComponent {

    @NotBlank
    private String uri;
    /**
     * Názov (SK)
     */
    @NotBlank
    private String name;
    /**
     * Názov (EN)
     */
    private String nameEng;
    /**
     * Kód dátového prvku
     */
    @NotBlank
    private String code;
    /**
     * Popis
     */
    private String description;
    @NotNull
    @Valid
    private Ontology ontology;

}