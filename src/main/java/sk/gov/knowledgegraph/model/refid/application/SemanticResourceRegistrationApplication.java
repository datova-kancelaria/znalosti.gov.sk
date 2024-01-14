package sk.gov.knowledgegraph.model.refid.application;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Abstraktná trieda reprezentujúca žiadosť o registráciu sémantického dátového prvku.
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = "uri")
public abstract class SemanticResourceRegistrationApplication {

    /**
     * URI identifikátor dátového prvku
     */
    @NotBlank
    @Pattern(regexp = "https://znalosti.gov.sk/id/application/[0-9a-z\\-]{36}", message = "URI of application has invalid form.")
    private String uri;
    /**
     * Popis sémantického prvku na registráciu
     */
    @Max(value = 2000, message = "Description is too long. Maximal allowed size is 2000 characters.")
    private String description;
    /**
     * Dátum vytvorenia žiadosti na zaregistrovanie do Centrálneho modelu údajov
     */
    @NotNull
    private LocalDate createdAt;
    /**
     * Dátum poslednej úpravy žiadosti na zaregistrovanie do Centrálneho modelu údajov
     */
    private LocalDate lastModifiedAt;
    /**
     * Dátum podania žiadosti na zaregistrovanie do Centrálneho modelu údajov
     */
    public LocalDate appliedAt;
    /**
     * Dátum odmietnutia žiadosti na zaregistrovanie do Centrálneho modelu údajov
     */
    private LocalDate rejectedAt;
    /**
     * Dátum schválenia žiadosti na zaregistrovanie do Centrálneho modelu údajov
     */
    private LocalDate approvedAt;
    @NotNull
    @Valid
    private Organization applicant;
    @NotNull
    private ApplicationState state;
    @NotNull
    @Valid
    private User createdBy;
    @Valid
    private User lastModifiedBy;
    @Valid
    private User appliedBy;
    @Valid
    private User rejectedBy;
    @Valid
    private User approvedBy;

}