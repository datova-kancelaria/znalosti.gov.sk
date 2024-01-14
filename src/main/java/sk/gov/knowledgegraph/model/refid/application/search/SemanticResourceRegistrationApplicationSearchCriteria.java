package sk.gov.knowledgegraph.model.refid.application.search;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import sk.gov.knowledgegraph.model.refid.application.ApplicationState;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SemanticResourceRegistrationApplicationSearchCriteria extends SearchCriteria {

    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate createdAtFrom;

    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate createdAtTo;

    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate approvedAtFrom;

    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate approvedAtTo;

    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate appliedAtFrom;

    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate appliedAtTo;

    private String createdByInsitutionId;
    private String createdByUsername;
    private ApplicationState state;
    private SemanticResourceRegistrationApplicationType type;
}
