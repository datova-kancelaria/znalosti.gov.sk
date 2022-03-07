package sk.gov.knowledgegraph.views.data;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import sk.gov.knowledgegraph.data.entity.Dataset;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import sk.gov.knowledgegraph.rest.DataAPI;
import sk.gov.knowledgegraph.views.main.MainView;
import org.springframework.beans.factory.annotation.Value;

@Route(value = "data", layout = MainView.class)
@PageTitle("Datasety")
@CssImport("./styles/idsk-frontend-2.8.0.min.css")
@CssImport("./styles/views/data/data-view.css")
//@NpmPackage(value="@id-sk/frontend", version = "2.8.0")
//@JsModule("/home/liskam/eclipse-workspace/znalosti.gov.sk/node_modules/@id-sk/frontend/idsk/all.js")

public class DataView extends Div {

    private Grid<Dataset> grid2 = new Grid<>(Dataset.class, false);
  //  private Dataset dataset;

    public DataView(@Autowired DataAPI dataviewService) {
        setId("data-view");

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

        
        /*
      
        int getAllTriplesCount = 0;
        int datasetsCount = 0;
        int catalogCount = 0;
        
        getAllTriplesCount = searchService.getAllTriplesCount();
        datasetsCount = searchService.getDatasetsCount();
        catalogCount = searchService.getCatalogsCount();
        
        add(new Html("<div align=center>"+datasetsCount+" datasetov, "+catalogCount+" katalógy, <b>"+getAllTriplesCount+"</b> tripletov (znalostí)</div>"));
        
         */
      
        //add(new Html("<h3>&nbsp;&nbsp;SKKnowledgeGraph Data</h3>"));

        
        H1 h1 = new H1();
        h1.addClassName("idsk-search-results__title");
        h1.add(new Html("<div>\n"
        		+ "    Datasety\n"
        		+ "  </div>"));
        
//        main.add(h1);
        
        whiteSpace.add(h1);
   
        
        grid2.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid2.setHeightFull();
        grid2.setSelectionMode(Grid.SelectionMode.NONE);
        grid2.setVerticalScrollingEnabled(false);
        grid2.removeAllColumns(); 

        try {

        grid2.setItems(dataviewService.dcat()); 
        grid2.setHeightByRows(true);        

        /*	
        grid2.addColumn(TemplateRenderer
                .<Dataset>of("<a href=http://localhost:8080/resource?uri=[[item.catalog]]>[[item.catalogTitle]]</a>")
                .withProperty("catalogTitle", Dataset::getCatalogTitle)
                .withProperty("catalog", Dataset::getCatalog)
       ).setWidth("20%")
        .setHeader(new Html("<b>Katalóg</b>"));
        
        */
        
        
        grid2.addColumn(TemplateRenderer
                .<Dataset>of("<a href=resource?uri=[[item.dataset]]>[[item.datasetTitle]]</a>")
                .withProperty("datasetTitle", Dataset::getDatasetTitle)
                .withProperty("dataset", Dataset::getDataset)
    ).setWidth("25%")
        .setHeader(new Html("<b>Dataset</b>"));

        
        /*
        grid2.addColumn(TemplateRenderer
                .<Dataset>of("[[item.version]]")
                .withProperty("version", Dataset::getVersion)
    ).setWidth("10%")
        .setHeader(new Html("<b>Verzia</b>"));
        
        */
        
        /*
        grid2.addColumn(TemplateRenderer
                .<Dataset>of("<a href=http://localhost:8080/resource?uri=[[item.publisher]]>[[item.publisherName]]</a>")
                .withProperty("publisherName", Dataset::getPublisherName)
                .withProperty("publisher", Dataset::getPublisher)
        	    ).setWidth("30%")
        .setHeader(new Html("<b>Vydavateľ</b>"));
        
        
        grid2.addColumn(TemplateRenderer
                .<Dataset>of("<a href=http://localhost:8080/resource?uri=[[item.theme]]>[[item.themeLabel]]</a>")
                .withProperty("theme", Dataset::getTheme)
                .withProperty("themeLabel", Dataset::getThemeLabel)

       ).setWidth("15%")
        .setHeader(new Html("<b>Dátová téma</b>"));
        
        */
        
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }

        add(grid2);

    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid2-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid2);
    }

    private void refreshGrid() {
        grid2.select(null);
        grid2.getDataProvider().refreshAll();
    }
}
