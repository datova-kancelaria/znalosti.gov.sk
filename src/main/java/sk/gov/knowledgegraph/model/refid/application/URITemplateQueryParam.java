package sk.gov.knowledgegraph.model.refid.application;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Trieda reprezentuje query parametre, ktoré sú povolené za referencovateľným identifikátorom a ktoré je zároveň možné používať v redirectTemplateUrl
 */
@Data
@Accessors(chain = true)
public class URITemplateQueryParam {

    /**
     * Názov parametra
     */
    @NotBlank
    private String name;
    /**
     * Popis
     */
    private String description;

}