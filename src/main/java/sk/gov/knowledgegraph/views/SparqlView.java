package sk.gov.knowledgegraph.views;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriUtils;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import lombok.extern.slf4j.Slf4j;
import sk.gov.idsk4j.IDSKSearchResultsContent;
import sk.gov.idsk4j.IDSKSearchResultsFilter;
import sk.gov.knowledgegraph.service.SparqlQueryService;

@Slf4j
@Route(value = "sparqlView", layout = MainView.class)
@PageTitle("SPARQL Endpoint")
@CssImport("./styles/idsk-frontend-2.8.0.min.css")
public class SparqlView extends Div {

    public String acceptHeader = "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9";

    public SparqlView(@Autowired SparqlQueryService sparqlService)
            throws UnsupportedRDFormatException, FileNotFoundException, IOException, ParserConfigurationException, TransformerException {

        setId("sparql-view");

        addClassName("govuk-width-container");

        Main main = new Main();
        main.addClassName("govuk-main-wrapper");
        main.setId("main-content");
        add(main);

        Div whiteSpace = new Div();
        whiteSpace.addClassName("app-whitespace-highlight");
        main.add(whiteSpace);

        Div idskSearchResultsDiv = new Div();

        idskSearchResultsDiv.addClassName("idsk-search-results");
        whiteSpace.add(idskSearchResultsDiv);

        Div searchResults = new Div();
        searchResults.addClassName("govuk-grid-column-full");
        searchResults.addClassName("idsk-search-results__title");

        idskSearchResultsDiv.add(searchResults);

        H1 h1 = new H1();
        h1.addClassName("idsk-search-results__title");
        h1.add(new Html("<div>\n" + "    Zadaj SPARQL dotaz\n" + "  </div>"));

        searchResults.add(h1);

        Div searchBar = new Div();
        searchBar.addClassName("govuk-grid-column-full");
        searchBar.addClassName("idsk-search-results__search-bar");

        Div searchComponent = new Div();
        searchComponent.addClassName("idsk-search-component");
        searchBar.add(searchComponent);

        TextArea textarea = new TextArea();
        textarea.setId("query");
        textarea.addClassName("govuk-textarea");
        textarea.setHeight("500px");
        textarea.setHeightFull();
        textarea.setValue("select * where {?s ?p ?o} limit 100");

        searchComponent.add(textarea);

        Button btnSearch2 = new Button();
        btnSearch2.addClassName("idsk-button");
        btnSearch2.setText("Vykonaj");
        btnSearch2.setWidth("150px");

        Button btnExport = new Button();
        btnExport.addClassName("idsk-button");
        btnExport.addClassName("idsk-button--secondary");
        btnExport.setText("Export");
        btnExport.setWidth("100px");

        searchComponent.add(btnSearch2);
        searchComponent.add(" ");
        searchComponent.add(btnExport);

        idskSearchResultsDiv.add(searchBar);

        IDSKSearchResultsFilter idskSearchResultsFilter = new IDSKSearchResultsFilter();
        IDSKSearchResultsContent idskSearchResultsContent = new IDSKSearchResultsContent();

        Div resultsDiv = new Div();
        idskSearchResultsDiv.add(resultsDiv);

        btnSearch2.addClickListener(e -> {

            try {

                resultsDiv.removeAll();
                Div resultHtmlDiv = new Div();

                if (textarea.getValue().toUpperCase().contains("SELECT")) {
                    resultHtmlDiv.add(new Html(sparqlService.getTupleQueryResultHtml(textarea.getValue())));
                } else if (textarea.getValue().toUpperCase().contains("CONSTRUCT")) {
                    resultHtmlDiv.add(new Html(sparqlService.getGraphQueryResultHtml(textarea.getValue())));
                } else if (textarea.getValue().toUpperCase().contains("ASK")) {
                    resultHtmlDiv.add(new Html(sparqlService.getBooleanQueryResultHtml(textarea.getValue())));
                }
                resultsDiv.add(resultHtmlDiv);

            } catch (Exception ee) {
                log.warn(ee.getMessage(), ee);
            }
        });

        btnExport.addClickListener(e -> {
            try {

                String encodedQuery = UriUtils.encodePath(textarea.getValue(), "UTF-8");
                UI.getCurrent().getPage().executeJs("window.open('/sparql?query=" + encodedQuery + "', '_blank')");

            } catch (Exception ee) {
                log.warn(ee.getMessage().toString());
            }

        });

    }

}
