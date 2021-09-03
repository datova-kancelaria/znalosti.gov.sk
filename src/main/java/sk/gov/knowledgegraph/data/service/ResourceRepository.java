package sk.gov.knowledgegraph.data.service;

import sk.gov.knowledgegraph.data.entity.Resource;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Integer> {

}
