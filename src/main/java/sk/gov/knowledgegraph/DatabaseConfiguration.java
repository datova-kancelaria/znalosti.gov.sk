package sk.gov.knowledgegraph;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import sk.gov.knowledgegraph.model.RepositoryPool;

@Slf4j
@Configuration
@ComponentScan(basePackageClasses = { Application.class })
public class DatabaseConfiguration {

    @Value("${database.url}")
    private String dbUrl;

    @Value("${database.repository.znalosti}")
    private String dbZnalostiRepository;

    @Value("${database.source-data.github-url}")
    private String githubSourceDataUrl;

    @Value("${database.repository.refid}")
    private String dbRefIdRepository;

    @Bean("znalostiRepository")
    public RepositoryPool getZnalostiRepository() {
        return new RepositoryPool(dbZnalostiRepository, dbUrl, githubSourceDataUrl, new RestTemplate());
    }


    @Bean("refidRepository")
    public Repository getRefIdRepository() {
        RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager(dbUrl);
        repositoryManager.init();
        Repository repo = repositoryManager.getRepository(dbRefIdRepository);
        if (repo == null) {
            log.error("No dababase with id: {} on url: {}", dbRefIdRepository, dbUrl);
        }
        return repo;
    }

}
