package sk.gov.knowledgegraph.data.service;

import sk.gov.knowledgegraph.data.entity.Dataset;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DatasetRepository extends JpaRepository<Dataset, Integer> {

}
