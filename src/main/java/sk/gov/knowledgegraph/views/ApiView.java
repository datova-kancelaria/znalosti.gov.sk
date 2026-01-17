package sk.gov.knowledgegraph.views;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "api", layout = MainView.class)
@PageTitle("API")
@CssImport("./styles/idsk-frontend-2.8.0.min.css")
public class ApiView extends Div {

    public ApiView() {
        setId("api-view");

        addClassName("govuk-width-container");

        Main main = new Main();
        main.addClassName("govuk-main-wrapper");
        main.setId("main-content");
        add(main);

        Div whiteSpace = new Div();
        whiteSpace.addClassName("app-whitespace-highlight");
        main.add(whiteSpace);

        Div dataResults = new Div();
        dataResults.addClassName("govuk-grid-column-full");
        whiteSpace.add(dataResults);

        H1 h1 = new H1();
        h1.addClassName("idsk-search-results__title");
        h1.add(new Html("<div align=\"center\">Znalosti.sk API</div>"));

        whiteSpace.add(h1);

        add(new Html("<div class=\"govuk-heading-m\">Dopyty nad SPARQL Endpointom</div>"));
        add(new Html(
                "<div><b>Popis: </b><br />Operácia na dopytovanie na SPARQL endpointom zverejnenom na znalosti.gov.sk. Výstupom operácie výsledok SPARQL dopytu vo formáte súboru na základe hlavičky Accept.</div>"));
        add(new Html(
                "<div class=\"govuk-inset-text\"><div class=govuk-link>POST /api/sparql?q={query}&default-graph-uri={default-graph-uri}&db-id={db-id}</div></div>"));
        add(new Html("<div><b>URL parametre: </b><ul>"
                + "<li><span style=\"text-decoration: underline;\">q</span> - URL Encoded SPARQL query. Podporované sú SELECT, ASK a CONSTRUCT dopyty. <b>Required</b></li>"
                + "<li><span style=\"text-decoration: underline;\">named-graph-uri</span> - IRI podgrafu v databáze, voči ktorému má byť query spustené. <b>Optional</b></li>"
                + "<li><span style=\"text-decoration: underline;\">db-id</span> - Identifikátor databázy voči ktorej má byť dopyt spustený. Ak nie je uvedený tak sa dopytujeme poslednej platnej verzii. Zoznam možných hodnôť je možné získať z \"Zoznam databáz znalosti.sk\". <b>Optional</b></li>"
                + "</ul></div>"));
        add(new Html("<div><b>Povolené hodnoty hlavičky \"Accept\" pre ASK query: </b><ul>"
                + "<li><span style=\"text-decoration: underline;\">application/sparql-results+json</span></li>"
                + "<li><span style=\"text-decoration: underline;\">application/json</span></li>"
                + "<li><span style=\"text-decoration: underline;\">application/x-sparqlstar-results+json</span></li>"
                + "<li><span style=\"text-decoration: underline;\">text/boolean</span></li>" + "</ul></div>"));
        add(new Html("<div><b>Povolené hodnoty hlavičky \"Accept\" pre SELECT query: </b><ul>"
                + "<li><span style=\"text-decoration: underline;\">application/xml</span></li>"
                + "<li><span style=\"text-decoration: underline;\">application/sparql-results+xml</span></li>"
                + "<li><span style=\"text-decoration: underline;\">text/csv</span></li>"
                + "<li><span style=\"text-decoration: underline;\">text/tab-separated-values</span></li>" + "</ul></div>"));
        add(new Html("<div><b>Povolené hodnoty hlavičky \"Accept\" pre CONSTRUCT query: </b><ul>"
                + "<li><span style=\"text-decoration: underline;\">application/ld+json</span></li>"
                + "<li><span style=\"text-decoration: underline;\">application/rdf+xml</span></li>"
                + "<li><span style=\"text-decoration: underline;\">application/xml</span></li>"
                + "<li><span style=\"text-decoration: underline;\">text/xml</span></li>"
                + "<li><span style=\"text-decoration: underline;\">text/turtle</span></li>"
                + "<li><span style=\"text-decoration: underline;\">application/x-turtle</span></li>"
                + "<li><span style=\"text-decoration: underline;\">application/n-triples</span></li>"
                + "<li><span style=\"text-decoration: underline;\">text/plain</span></li>" + "</ul></div>"));
        add(new Html("<div><b>Príklady volania: </b></div>"));
        add(new Html(
                "<div class=\"govuk-inset-text\"><p>curl --location --request POST 'https://znalosti.gov.sk/api/sparql?Accept=application%2Fxml&q=SELECT%20%2A%20WHERE%20%7B%20%3FS%20%3Fp%20%3Fo%20%7D'</p><p>curl --location --request POST 'https://znalosti.gov.sk/api/sparql?Accept=application%2Fxml&q=CONSTRUCT%20%7B%20%3Fs%20%3Fp%20%3Fo%20%7D%20WHERE%20%7B%20%3Fs%20%3Fp%20%3Fo%20%7D%20%20'</p></div>"));

        add(new Html("<div><br></div>"));
        add(new Html("<div><br></div>"));
        add(new Html("<div class=\"govuk-heading-m\">Zoznam databáz znalosti.sk</div>"));
        add(new Html(
                "<div><b>Popis: </b><br />Zoznam databáz znalosti.sk. Znalosti.sk podporuje viacero verzií databáz pre zabezpečenie kompatibility medzi integrujúcimi sa subjektami. Rôzne verzie databáz znalosti.sk reprezentujú rôzne verzie Centrálneho modelu údajov, ktoré vznikajú v priebehu času.</div>"));
        add(new Html("<div class=\"govuk-inset-text\"><div class=\"govuk-link\">GET /api/list-dbs</div></div>"));

        add(new Html("<div class=govuk-heading-m>Vráť všetky znalosti o URI</div>"));
        add(new Html("<div class=govuk-link>GET /api/resource?uri={URI}&Accept={Accept}</div>"));
        add(new Html("<div class=govuk-link>POST /api/resource?uri={URI}</div>"));

        add(new Html("<div class=govuk-link><br></div>"));
        add(new Html("<div class=govuk-link><br></div>"));

        add(new Html("<div class=govuk-heading-m>Vyhľadávaj v znalostiach</div>"));
        add(new Html("<div class=govuk-link>/api/search?q={searchString}</div>"));

        add(new Html("<div class=govuk-link><br></div>"));
        add(new Html("<div class=govuk-link><br></div>"));

    }

}
