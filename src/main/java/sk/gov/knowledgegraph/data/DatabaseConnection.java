package sk.gov.knowledgegraph.data;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class DatabaseConnection {
    @Value("${database.url}")
    private String dbUrl;

    @Value("${database.repository}")
    private String dbRepository;

    private RemoteRepositoryManager repositoryManager = null;
    private Repository repository = null;

    @PostConstruct
    public void initRepository() {
        repositoryManager = new RemoteRepositoryManager(dbUrl);
     //   repositoryManager.setUsernameAndPassword(dbUser, dbPassword );
        repositoryManager.init();
        repository = repositoryManager.getRepository(dbRepository);
    }

    @Bean
    public Repository getRepository() {
        return repository;
    }
}
