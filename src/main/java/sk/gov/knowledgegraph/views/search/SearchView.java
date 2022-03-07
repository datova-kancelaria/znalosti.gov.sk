package sk.gov.knowledgegraph.views.search;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.springframework.beans.factory.annotation.Autowired;

import sk.gov.idsk4j.IDSKSearchResultsContent;
import sk.gov.idsk4j.IDSKSearchResultsFilter;
import sk.gov.knowledgegraph.data.entity.Result;
import sk.gov.knowledgegraph.rest.SearchAPI;
import sk.gov.knowledgegraph.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "search", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Vyhľadávanie")
@CssImport("./styles/idsk-frontend-2.8.0.min.css")
//@CssImport("./styles/views/search/search-view.css")
//@NpmPackage(value="@id-sk/frontend", version = "2.8.0")
//@JsModule("/home/liskam/eclipse-workspace/znalosti.gov.sk/node_modules/@id-sk/frontend/idsk/all.js")

public class SearchView extends Div {

    //private TextField searchText;
    private Button btnSearch;
   
    //@Id("search-results-input")
    //TextField searchInput = new TextField();

    private Grid<Result> grid5 = new Grid<>(Result.class, false);

    public SearchView(@Autowired SearchAPI searchService) throws UnsupportedRDFormatException, FileNotFoundException, IOException, ParserConfigurationException, TransformerException {
        setId("search-view");
        
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
        h1.add(new Html("<div>\n"
        		+ "    Vyhľadaj v znalostiach\n"
        		+ "  </div>"));
               
        searchResults.add(h1);
       
        Div searchBar = new Div();
        searchBar.addClassName("govuk-grid-column-full");
        searchBar.addClassName("idsk-search-results__search-bar");
        
        Div searchComponent = new Div();
        searchComponent.addClassName("idsk-search-component");
        searchBar.add(searchComponent);
        
        searchComponent.add(new Html("<label class=\"govuk-visually-hidden\" for=\"search-results-input\">\n"
        		+ "    Zadajte hľadaný výraz\n"
        		+ "  </label>"));
      
        Input input = new Input();
        input.addClassName("govuk-input");
        input.addClassName("govuk-input--width-30");
        input.addClassName("idsk-search-component__input");
        input.setId("search-results-input");
        input.setType("text");
        
         // TextField searchText = new TextField();
         // searchComponent.add(new Html("<input class=\"govuk-input govuk-input--width-30 idsk-search-component__input \" id=\"search-results-input\" name=\"search\" type=\"text\">"));
     
        searchComponent.add(input);
       
        Button btnSearch2 = new Button();
        btnSearch2.addClassName("idsk-button");
        btnSearch2.addClassName("idsk-search-component__button");
        
        Image image = new Image("./icons/icon-search.png", "Search");
        btnSearch2.setIcon(image);
             
        searchComponent.add(btnSearch2);
        idskSearchResultsDiv.add(searchBar);
        
        IDSKSearchResultsFilter idskSearchResultsFilter = new IDSKSearchResultsFilter();        
        //idskSearchResultsDiv.add(idskSearchResultsFilter.create());
        IDSKSearchResultsContent idskSearchResultsContent = new IDSKSearchResultsContent();
          

        grid5.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid5.setHeightFull();
        grid5.setSelectionMode(Grid.SelectionMode.NONE);
        
            btnSearch2.addClickListener(e -> {

            try {

           grid5.removeAllColumns(); 
           grid5.setItems(searchService.search(input.getValue()));
           grid5.setHeightByRows(true);        
           grid5.addColumn(new ComponentRenderer<>(result -> {
            	
            	return new Html (
            			"<div><font size=3><b><a href=resource?uri="+result.getSubject().replace("#","%23")+">"+result.getObject()+"</a></b></font></br>" +
            			"<font size=2 color=grey>"+result.getSubject()+"</font></br>" +
            			"<font size=1 color=grey>"+result.getGraphName()+"</font>" +
            			"<br /></div>"
            					);
                       }));
  
           idskSearchResultsDiv.add(idskSearchResultsContent.create(grid5));
           
            
        }
        catch (Exception ee)
        {
            System.out.println(ee.toString());
        }
        });
        
    }

}
