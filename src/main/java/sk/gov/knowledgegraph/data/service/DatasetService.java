package sk.gov.knowledgegraph.data.service;

import sk.gov.knowledgegraph.data.entity.Dataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class DatasetService extends CrudService<Dataset, Integer> {

    private DatasetRepository repository;

    public DatasetService(@Autowired DatasetRepository repository) {
        this.repository = repository;
    }

    @Override
    protected DatasetRepository getRepository() {
        return repository;
    }

}
