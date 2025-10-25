package sk.gov.knowledgegraph.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.util.Statements;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.sail.nativerdf.config.NativeStoreConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import sk.gov.knowledgegraph.model.exception.ErrorCode;
import sk.gov.knowledgegraph.model.exception.KnowledgeGraphException;

@Data
@Slf4j
public class RepositoryPool {

    private final String defaultRepositoryId;
    private final String dbUrl;
    private final String githubRepositoryUrl;
    private final RestTemplate restTemplate;

    private final Map<String, Repository> repositories = new HashMap<>();

    public RepositoryPool(String defaultRepositoryId, String dbUrl, String githubRepositoryUrl, RestTemplate restTemplate) {
        this.defaultRepositoryId = defaultRepositoryId;
        this.dbUrl = dbUrl;
        this.githubRepositoryUrl = githubRepositoryUrl;
        this.restTemplate = restTemplate;
        RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager(dbUrl);
        repositoryManager.init();
        Repository repo = repositoryManager.getRepository(this.defaultRepositoryId);
        if (repo != null) {
            this.repositories.put(this.defaultRepositoryId, repo);
        } else {
            log.error("No database with id: {} on url: {}", this.defaultRepositoryId, dbUrl);
        }
    }


    public Repository getDefaultRepository() {
        return this.repositories.get(this.defaultRepositoryId);
    }


    public Repository getRepositoryOrDefault(String repositoryId) {
        if (this.repositories.containsKey(repositoryId)) {
            return this.repositories.get(repositoryId);
        }

        return this.repositories.get(this.defaultRepositoryId);
    }


    public Repository getRepository(String repositoryId) {
        return this.repositories.get(repositoryId);
    }


    public Set<String> reloadDbFromBranch(String branchId) throws KnowledgeGraphException {
        Set<String> branchIds = getGithubBranches();

        Set<String> branchesIdToReload = new HashSet<>();

        if (branchId != null && !branchIds.contains(branchId)) {
            throw new KnowledgeGraphException(ErrorCode.BRANCH_TO_RELOAD_DOES_NOT_EXIST, Map.of("branchId", branchId));
        }
        if (branchId != null) {
            branchesIdToReload.add(branchId);
        } else {
            branchesIdToReload.addAll(branchIds);
        }

        Set<String> skipBranches = Set.of("master", "main", "development", "Refactoring", "refid", "refid-test");

        for (String id : branchesIdToReload) {
            if (id == null || id.isBlank() || skipBranches.contains(id)) {
                log.debug("Skipping reloading repository with ID {}", id);
                continue;
            }

            RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager(dbUrl);
            repositoryManager.init();
            Set<String> existingRepositories = repositoryManager.getInitializedRepositoryIDs();

            //clear existing repository before reloading data
            String newDbId = "znalosti-" + id;

            if (repositories.containsKey(newDbId) || existingRepositories.contains(id)) {
                try (RepositoryConnection conn = repositories.get(newDbId).getConnection()) {
                    conn.clear();
                }
            }

            try {
                repositories.put(newDbId, createRepository(newDbId, repositoryManager));
                loadFromGithubBranch(id, newDbId);
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }

        }

        return repositories.keySet();
    }


    private Set<String> getGithubBranches() {
        Set<String> branchIds = new HashSet<>();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(githubRepositoryUrl + "/branches", String.class);
        String branchesJsonStr = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        GithubBranch[] jsonObj = null;
        try {
            jsonObj = mapper.readValue(branchesJsonStr, GithubBranch[].class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }

        for (int i = 0; i < jsonObj.length; i++) {
            branchIds.add(jsonObj[i].getName());
        }
        return branchIds;
    }


    private void loadFromGithubBranch(String branchId, String dbId) throws IOException {
        log.debug("Trying to get content of github repository from branch {} and load it to database {}", branchId, dbId);
        URL url = URI.create(githubRepositoryUrl + "/zipball/" + branchId).toURL();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        try (InputStream in = connection.getInputStream();
                ZipInputStream zipIn = new ZipInputStream(in);
                RepositoryConnection conn = repositories.get(dbId).getConnection()) {
            ZipEntry entry = null;
            while ((entry = zipIn.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    log.info("Skipping directory {}", entry.getName());
                    continue;
                }
                log.info("Trying to load file {} to {} database.", entry.getName(), dbId);
                RDFFormat format = RDFFormat
                        .matchFileName(entry.getName(), List.of(RDFFormat.JSONLD, RDFFormat.NTRIPLES, RDFFormat.RDFXML, RDFFormat.TRIG, RDFFormat.TURTLE))
                        .orElse(null);
                if (format == null && entry.getName().toLowerCase().endsWith(".json")) {
                    format = RDFFormat.JSONLD;
                }
                if (format == null) {
                    log.info("Unrecognized RDFFormat for file {}. Unable to load to {} database.", entry.getName(), dbId);
                    continue;
                }
                Resource context = resolveContext(entry.getName());
                byte[] data = IOUtils.toByteArray(zipIn);
                try (InputStream stream = new ByteArrayInputStream(data)) {
                    //we need to verify validity of loaded data to make sure repository wont be broken
                    RDFParser parser = Rio.createParser(format);
                    parser.getParserConfig().set(BasicParserSettings.VERIFY_URI_SYNTAX, true);
                    parser.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, true);
                    parser.setRDFHandler(new StatementCollector()); 
                    parser.parse(stream, "http://example.org/base/");

                    conn.begin();
                    conn.add(new ByteArrayInputStream(data), format, context);//needs to load data in memory as conn.add() closes underlying stream
                    conn.commit();
                    log.info("File {} loaded to {} database.", entry.getName(), dbId);
                } catch (Exception e) {
                    log.error("Unable to load file {} to database for following error: {}", entry.getName(), dbId, e.getMessage(), e);
                    continue;
                } finally {
                    if (conn.isActive()) {
                        conn.commit();
                    }
                }

            }
            //FIXME - toto tu je preto ze kontexty nemaju na Githube lokalizaciu co nasledne rozbije search()
            conn.begin();
            conn.add(Statements.statement(Values.iri("https://data.gov.sk/set/catalog/nkod"), Values.iri("http://purl.org/dc/terms/title"),
                    Values.literal("Národný katalóg otvorených dát", "sk"), Values.iri("https://data.gov.sk/set/catalog/nkod")));
            conn.add(Statements.statement(Values.iri("https://data.gov.sk/set/catalog/knowledgegraph"), Values.iri("http://purl.org/dc/terms/title"),
                    Values.literal("Znalostný graf SK", "sk"), Values.iri("https://data.gov.sk/set/catalog/knowledgegraph")));
            conn.add(Statements.statement(Values.iri("https://data.gov.sk/id/egov/isvs/63"), Values.iri("http://purl.org/dc/terms/title"),
                    Values.literal("Centrálny metainformačný systém verejnej správy", "sk"), Values.iri("https://data.gov.sk/id/egov/isvs/63")));
            conn.commit();
        }
        log.info("Finished loading data from branch {} to database {}", branchId, dbId);
    }


    private Resource resolveContext(String name) {
        if (name != null && (name.contains("abox") || name.contains("tbox"))) {
            return Values.iri("https://data.slovensko.sk/datasety/catalog/knowledgegraph");
        }
        return null;
    }


    private Repository createRepository(String repositoryId, RepositoryManager manager) throws ClientProtocolException, IOException {
        // Create repository configuration
        Model configModel = new LinkedHashModel();
        NativeStoreConfig nativeConfig = new NativeStoreConfig("spoc,posc");
        SailRepositoryConfig sailConfig = new SailRepositoryConfig(nativeConfig);
        RepositoryConfig repoConfig = new RepositoryConfig(repositoryId, sailConfig);

        repoConfig.export(configModel);

        // Step 2: Serialize to Turtle
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Rio.write(configModel, out, RDFFormat.TURTLE);
        byte[] turtleBytes = out.toByteArray();
        HttpPut post = new HttpPut(this.dbUrl + "/repositories/" + repositoryId);
        post.setEntity(new ByteArrayEntity(turtleBytes, ContentType.create("text/turtle")));

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            client.execute(post, response -> {
                log.info("Response from RDF4J server for creating repository request: " + response.getStatusLine());
                return null;
            });
        }

        return manager.getRepository(repositoryId);
    }
}
