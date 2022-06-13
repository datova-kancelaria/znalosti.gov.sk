package sk.gov.knowledgegraph.views.about;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import sk.gov.knowledgegraph.data.entity.Result;
import sk.gov.knowledgegraph.rest.SearchAPI;
import sk.gov.knowledgegraph.views.main.MainView;

@Route(value = "about", layout = MainView.class)
@PageTitle("O portáli")
@CssImport("./styles/idsk-frontend-2.8.0.min.css")
//@NpmPackage(value="@id-sk/frontend", version = "2.8.0")
//@JsModule("/home/liskam/eclipse-workspace/znalosti.gov.sk/node_modules/@id-sk/frontend/idsk/all.js")

public class AboutView extends Div {

    private Grid<Result> grid4 = new Grid<>(Result.class, false);

    public AboutView(@Autowired SearchAPI searchService) {
        setId("about-view");

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
        h1.add(new Html("<div>\n"
        		+ "    Informácie o portáli\n"
        		+ "  </div>"));
        
//        main.add(h1);
        
        whiteSpace.add(h1);
        
        //add(new Html("<div><h3>&nbsp;&nbsp;Informácie</h3></div>"));
        
        add(new Html("<div>Projekt znalosti.gov.sk poskytuje elektronické služby dátovej interoperability pre Informačné systémy verejnej správy vo forme prepojených údajov (LinkedData). "
        		+ "Sústreďuje sa reprezentáciu štrukturálnych metadát do formy prepojených údajov, akými je napr. Centrálny model údajov verejnej správy, tj. množina ontológií opisujúcich údaje (objekty evidencie) v základných registroch ako <a href=resource?uri=https://data.gov.sk/def/ontology/physical-person/PhysicalPerson target=\"_blank\">Fyzická osoba</a>, <a href=resource?uri=https://data.gov.sk/def/ontology/legal-subject/LegalSubject target=\"_blank\">Právny subjekt</a>, <a href=resource?uri=https://data.gov.sk/def/ontology/location/PhysicalAddress target=\"_blank\">Fyzická adresa</a>, <a href=resource?uri=http://www.w3.org/ns/org#OrganizationalUnit target=\"_blank\">Organizačná jednotka</a>, <a href=resource?uri=http://www.w3.org/ns/dcat%23Dataset target=\"_blank\">Dataset</a> a ostatné ... spolu so všetkými číselníkmi reprezentovanými ako sémantickými hierarchickými taxonómiami. Uvedená množina prepojených metadát predstavuje Znalostný graf (Knowledge Graph) údajov verejnej správy."
        		+ "<br><br>Projekt je vyvíjaný od roku 2021 v Dátovej kancelárii <a href=https://mirri.gov.sk target=\"_blank\">Ministerstva investícií, regionálneho rozvoja a informatizácie Slovenskej republiky</a>. Vaše pripomienky posielajte prosím na <a href=mailto:miroslav.liska@mirri.gov.sk>miroslav.liska@mirri.gov.sk</a>, resp. <a href=mailto:viktoria.sunderlikova@mirri.gov.sk>viktoria.sunderlikova@mirri.gov.sk</a> ."
        		+ "<br><br>Zdrojový kód portálu je dostupný na adrese: <a href=https://github.com/datova-kancelaria/znalosti.gov.sk target=\"_blank\">https://github.com/datova-kancelaria/znalosti.gov.sk</a> pod licenciou <a href=https://eur-lex.europa.eu/legal-content/SK/TXT/HTML/?uri=CELEX:32017D0863&from=SK target=\"_blank\">EUPL</a>."
   		        + " Nájdené chyby, pripomienky alebo návrhy na zlepšenie môžete zadať priamo tu: <a href=https://github.com/datova-kancelaria/znalosti.gov.sk/issues target=\"_blank\">https://github.com/datova-kancelaria/znalosti.gov.sk/issues</a>.</div>"));
   	    
    
      //  https://github.com/datova-kancelaria/znalosti.gov.sk/issues
    }
    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid4-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid4);
    }

    private void refreshGrid() {
        grid4.select(null);
        grid4.getDataProvider().refreshAll();
    }
}
