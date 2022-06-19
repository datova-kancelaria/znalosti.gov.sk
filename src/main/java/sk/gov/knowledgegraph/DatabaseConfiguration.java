package sk.gov.knowledgegraph;

import javax.annotation.PostConstruct;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {

    @Value("${database.url}")
    private String dbUrl;

    @Value("${database.repository}")
    private String dbRepository;

    private Repository repository = null;

    @PostConstruct
    public void initRepository() {
        RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager(dbUrl);
        //   repositoryManager.setUsernameAndPassword(dbUser, dbPassword );
        repositoryManager.init();
        repository = repositoryManager.getRepository(dbRepository);
    }


    @Bean
    public Repository getRepository() {
        return repository;
    }
}
