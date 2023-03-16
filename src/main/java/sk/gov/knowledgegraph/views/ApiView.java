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
        h1.add(new Html("<div>\n" + "    API\n" + "  </div>"));

        //        main.add(h1);

        whiteSpace.add(h1);

        //add(new Html("<div><h3>&nbsp;&nbsp;Informácie</h3></div>"));

        //  add(new Html("<div class=govuk-heading-m>API systému znalosti.gov.sk</div>"));

        add(new Html("<div class=govuk-heading-m>POST SPARQL Enpoint</div>"));
        add(new Html("<div class=govuk-link>POST /api/sparql?q={query}&default-graph-uri={default-graph-uri}&named-graph-uri={named-graph-uri}</div>"));

        add(new Html("<div class=govuk-link><br></div>"));
        add(new Html("<div class=govuk-link><br></div>"));

        add(new Html("<div class=govuk-heading-m>Vráť všetky znalosti o URI</div>"));
        add(new Html("<div class=govuk-link>GET /api/resource?uri={URI}&content-type={contentType}</div>"));
        add(new Html("<div class=govuk-link>POST /api/resource?uri={URI}</div>"));

        add(new Html("<div class=govuk-link><br></div>"));
        add(new Html("<div class=govuk-link><br></div>"));

        //        add(new Html("<div class=govuk-heading-m>Vráť základné vlastnosti URI</div>"));
        //        add(new Html("<div class=govuk-link>/api/getBaseProperties?uri=URI</div>"));
        //
        //        add(new Html("<div class=govuk-link><br></div>"));
        //        add(new Html("<div class=govuk-link><br></div>"));

        add(new Html("<div class=govuk-heading-m>Vyhľadávaj v znalostiach</div>"));
        add(new Html("<div class=govuk-link>/api/search?q={searchString}</div>"));

        add(new Html("<div class=govuk-link><br></div>"));
        add(new Html("<div class=govuk-link><br></div>"));

        //        add(new Html("<div class=govuk-heading-m>Vráť zoznam nahratých RDF datasetov</div>"));
        //        add(new Html("<div class=govuk-link>/api/dcat</div>"));
        //
        //        add(new Html("<div class=govuk-link><br></div>"));
        //        add(new Html("<div class=govuk-link><br></div>"));
        //
        //        add(new Html("<div class=govuk-heading-m>Vráť celkový počet grafov</div>"));
        //        add(new Html("<div class=govuk-link>/api/stat/getAllNamedGraphsCount</div>"));
        //
        //        add(new Html("<div class=govuk-link><br></div>"));
        //        add(new Html("<div class=govuk-link><br></div>"));
        //
        //        add(new Html("<div class=govuk-heading-m>Vráť celkový počet tripletov</div>"));
        //        add(new Html("<div class=govuk-link>/api/stat/getAllTriplesCount</div>"));
        //
        //        add(new Html("<div class=govuk-link><br></div>"));
        //        add(new Html("<div class=govuk-link><br></div>"));
        //
        //        add(new Html("<div class=govuk-heading-m>Vráť celkový počet datasetov</div>"));
        //        add(new Html("<div class=govuk-link>/api/stat/getDatasetsCount</div>"));
        //
        //        add(new Html("<div class=govuk-link><br></div>"));
        //        add(new Html("<div class=govuk-link><br></div>"));
        //
        //        add(new Html("<div class=govuk-heading-m>Vráť celkový počet katalógov</div>"));
        //        add(new Html("<div class=govuk-link>/api/stat/getCatalogsCount</div>"));

    }

}
