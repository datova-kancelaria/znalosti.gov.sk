package sk.gov.knowledgegraph.data.service;

import sk.gov.knowledgegraph.data.entity.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class ResourceService extends CrudService<Resource, Integer> {

    private ResourceRepository repository;

    public ResourceService(@Autowired ResourceRepository repository) {
        this.repository = repository;
    }

    @Override
    protected ResourceRepository getRepository() {
        return repository;
    }

}
