package sk.gov.knowledgegraph.controller.integration;

import java.io.IOException;

import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import sk.gov.knowledgegraph.model.exception.KnowledgeGraphException;
import sk.gov.knowledgegraph.model.refid.application.SemanticResourceRegistrationApplication;
import sk.gov.knowledgegraph.model.refid.application.dto.ApproveApplicationRequestDTO;
import sk.gov.knowledgegraph.model.refid.application.dto.RejectApplicationRequestDTO;
import sk.gov.knowledgegraph.model.refid.application.search.SearchResult;
import sk.gov.knowledgegraph.model.refid.application.search.SemanticResourceRegistrationApplicationSearchCriteria;
import sk.gov.knowledgegraph.model.refid.application.search.SemanticResourceRegistrationApplicationSearchData;
import sk.gov.knowledgegraph.service.ReferenceIdentifierApplicationManagementService;

/**
 * Endpoint na integračné rozhranie na správu referencovateľných identifikátorov
 */
@Slf4j
@RestController
@Validated
@RequestMapping("/integration/api/refid/application")
public class ReferenceIdentifierManagementController {

    @Autowired
    private ReferenceIdentifierApplicationManagementService referenceIdentifierApplicationManagementService;

    /**
     * Operácia na získanie detailu žiadosti o registráciu referencovateľného identifikátora (ontológie, verzie ontológie, triedy, dátového prvku alebo URI
     * šablóny).
     * 
     * @param  uri                     - URI identifikátor žiadosti
     * @return
     * @throws KnowledgeGraphException
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public SemanticResourceRegistrationApplication getApplication(@RequestParam(value = "uri") String uri) throws KnowledgeGraphException {
        log.info("Get application {}", uri);
        return referenceIdentifierApplicationManagementService.getApplication(uri);
    }


    /**
     * Operácia na vytvorenie detailu žiadosti o registráciu referencovateľného identifikátora (ontológie, verzie ontológie, triedy, dátového prvku alebo URI
     * šablóny).
     * 
     * @param  application
     * @return
     * @throws KnowledgeGraphException
     */
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SemanticResourceRegistrationApplication createApplication(@RequestBody SemanticResourceRegistrationApplication application)
            throws KnowledgeGraphException {
        log.info("Create application {}", application);
        return referenceIdentifierApplicationManagementService.createApplication(application);
    }


    /**
     * Operácia na aktualizáciu rozpracovanej žiadosti
     * 
     * @param  application
     * @return
     * @throws KnowledgeGraphException
     */
    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SemanticResourceRegistrationApplication updateDraftApplication(@Valid @RequestBody SemanticResourceRegistrationApplication application)
            throws KnowledgeGraphException {
        log.info("Update draft application {}", application);
        return referenceIdentifierApplicationManagementService.updateDraftApplication(application);
    }


    /**
     * Operácia na výmaz žiadosti
     * 
     * @param  uri
     * @throws KnowledgeGraphException
     */
    @DeleteMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteApplication(@RequestParam(value = "uri") String uri) throws KnowledgeGraphException {
        log.info("Delete application {}", uri);
        referenceIdentifierApplicationManagementService.deleteApplication(uri);
    }


    /**
     * Operácia podanie žiadosti na schválenie
     * 
     * @param  application
     * @return
     * @throws KnowledgeGraphException
     */
    @PutMapping(value = "/apply", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SemanticResourceRegistrationApplication applyApplication(@RequestBody SemanticResourceRegistrationApplication application)
            throws KnowledgeGraphException {
        log.info("Apply application {}", application);
        return referenceIdentifierApplicationManagementService.applyApplication(application);
    }


    /**
     * Operácia na odmietnutie žiadosti, ktorá je na schválenie
     * 
     * @param  request
     * @return
     * @throws KnowledgeGraphException
     */
    @PutMapping(value = "/reject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SemanticResourceRegistrationApplication rejectApplication(@RequestBody RejectApplicationRequestDTO request) throws KnowledgeGraphException {
        log.info("Reject application {}", request);
        return referenceIdentifierApplicationManagementService.rejectApplication(request);
    }


    /**
     * Operácia na schválenie žiadosti
     * 
     * @param  request
     * @return
     * @throws KnowledgeGraphException
     */
    @PutMapping(value = "/approve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SemanticResourceRegistrationApplication approveApplication(@RequestBody ApproveApplicationRequestDTO request) throws KnowledgeGraphException {
        log.info("Approve application {}", request);
        return referenceIdentifierApplicationManagementService.approveApplication(request);
    }

    /**
     * Operácia na zresetovanie stavu databázy do pôvodného nastavanie. Táto operácia je navrhnutá výhradne na testovacie účely. Operácia nie je prístupná na produkčnom prostredí, iba na testovacom.
     * @throws KnowledgeGraphException
     * @throws RDFParseException
     * @throws RepositoryException
     * @throws IllegalArgumentException
     * @throws IOException
     */
    @DeleteMapping(value = "/reset-db", produces = MediaType.APPLICATION_JSON_VALUE)
    public void resetDb() throws KnowledgeGraphException, RDFParseException, RepositoryException, IllegalArgumentException, IOException {
        log.info("Reseting db");
        referenceIdentifierApplicationManagementService.resetDb();
    }

    /**
     * Operácia na vyhľadávanie žiadostí
     * @param  criteria
     * @return
     * @throws KnowledgeGraphException
     */
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public SearchResult<SemanticResourceRegistrationApplicationSearchData> search(SemanticResourceRegistrationApplicationSearchCriteria criteria)
            throws KnowledgeGraphException {
        log.info("Search applications {}", criteria);
        return referenceIdentifierApplicationManagementService.search(criteria);
    }

}
