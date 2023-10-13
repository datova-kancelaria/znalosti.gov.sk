package sk.gov.knowledgegraph.controller.integration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import sk.gov.knowledgegraph.model.entity.Result;
import sk.gov.knowledgegraph.model.exception.KnowledgeGraphException;
import sk.gov.knowledgegraph.service.SearchService;

@Slf4j
@RestController
@Validated
@RequestMapping("/integration/api")
public class ExternalIntegrationController {

    @Autowired
    private SearchService searchService;

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Result> search(@RequestParam(value = "q") String searchString) throws KnowledgeGraphException {
        return searchService.search(searchString);
    }

}
