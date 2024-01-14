package sk.gov.knowledgegraph;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = { Application.class })
public class DatabaseConfiguration {

    @Value("${database.url}")
    private String dbUrl;

    @Value("${database.repository.znalosti}")
    private String dbZnalostiRepository;

    @Value("${database.repository.refid}")
    private String dbRefIdRepository;

    @Bean("znalostiRepository")
    public Repository getZnalostiRepository() {
        RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager(dbUrl);
        //   repositoryManager.setUsernameAndPassword(dbUser, dbPassword );
        repositoryManager.init();
        return repositoryManager.getRepository(dbZnalostiRepository);
    }


    @Bean("refidRepository")
    public Repository getRefIdRepository() {
        RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager(dbUrl);
        repositoryManager.init();
        return repositoryManager.getRepository(dbRefIdRepository);
    }

}
