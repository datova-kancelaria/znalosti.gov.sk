package sk.gov.knowledgegraph.views.data;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import sk.gov.knowledgegraph.data.entity.Dataset;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import sk.gov.knowledgegraph.rest.DataAPI;
import sk.gov.knowledgegraph.views.main.MainView;

@Route(value = "data", layout = MainView.class)
@PageTitle("Data")
@CssImport("./styles/views/data/data-view.css")
public class DataView extends Div {

    private Grid<Dataset> grid2 = new Grid<>(Dataset.class, false);
  //  private Dataset dataset;

    public DataView(@Autowired DataAPI dataviewService) {
        setId("data-view");


        add(new Html("<h3>&nbsp;&nbsp;SKKnowledgeGraph Data</h3>"));

        grid2.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid2.setHeightFull();
        grid2.setSelectionMode(Grid.SelectionMode.NONE);

        try {

        grid2.setItems(dataviewService.dcat());
        
        grid2.addColumn(TemplateRenderer
                .<Dataset>of("<a href=http://localhost:8080/resource?uri=[[item.catalog]]>[[item.catalogTitle]]</a>")
                .withProperty("catalogTitle", Dataset::getCatalogTitle)
                .withProperty("catalog", Dataset::getCatalog)
       ).setWidth("20%")
        .setHeader(new Html("<b>Katalóg</b>"));
        
        
        grid2.addColumn(TemplateRenderer
                .<Dataset>of("<a href=http://localhost:8080/resource?uri=[[item.dataset]]>[[item.datasetTitle]]</a>")
                .withProperty("datasetTitle", Dataset::getDatasetTitle)
                .withProperty("dataset", Dataset::getDataset)
    ).setWidth("25%")
        .setHeader(new Html("<b>Dataset</b>"));

        
        grid2.addColumn(TemplateRenderer
                .<Dataset>of("[[item.version]]")
                .withProperty("version", Dataset::getVersion)
    ).setWidth("10%")
        .setHeader(new Html("<b>Verzia</b>"));
        
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
