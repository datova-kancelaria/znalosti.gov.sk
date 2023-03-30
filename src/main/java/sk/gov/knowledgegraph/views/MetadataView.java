package sk.gov.knowledgegraph.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import sk.gov.knowledgegraph.controller.ResourceController;
import sk.gov.knowledgegraph.model.entity.Dataset;
import sk.gov.knowledgegraph.service.DatasetService;

@Route(value = "metadata", layout = MainView.class)
@PageTitle("Datasety")
@CssImport("./styles/idsk-frontend-2.8.0.min.css")
@CssImport("./styles/views/data/data-view.css")
public class MetadataView extends Div {

    private static Logger logger = LoggerFactory.getLogger(DataView.class);

    public MetadataView(@Autowired DatasetService datasetService) {
        Grid<Dataset> grid2 = new Grid<>(Dataset.class, false);
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

        //add(new Html("<h3>&nbsp;&nbsp;SKKnowledgeGraph Data</h3>"));

        H1 h1 = new H1();
        h1.addClassName("idsk-search-results__title");
        h1.add(new Html("<div>\n" + "    Ontológie\n" + "  </div>"));

        //        main.add(h1);

        whiteSpace.add(h1);

        // Div idskDatasetsDiv = new Div();

        grid2.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid2.setHeightFull();
        grid2.setSelectionMode(Grid.SelectionMode.NONE);
        grid2.setVerticalScrollingEnabled(false);
        grid2.removeAllColumns();

        try {

            grid2.setItems(datasetService.listMetaData());
            grid2.setHeightByRows(true);

            grid2.addColumn(TemplateRenderer.<Dataset> of("<a href=resource?uri=[[item.dataset]]>[[item.datasetTitle]]</a>")
                    .withProperty("datasetTitle", Dataset::getDatasetTitle).withProperty("dataset", Dataset::getDataset)).setWidth("35%")
                    .setHeader(new Html("<b>Dataset</b>"));
            
            grid2.addColumn(TemplateRenderer.<Dataset> of("<a href=resource?uri=[[item.datasetType]]>[[item.datasetTypeLabel]]</a>")
                    .withProperty("datasetTypeLabel", Dataset::getDatasetTypeLabel).withProperty("datasetType", Dataset::getDatasetType)).setWidth("15%")
                    .setHeader(new Html("<b>Typ</b>"));
            
            grid2.addColumn(TemplateRenderer.<Dataset> of("<a href=resource?uri=[[item.wasDerivedFrom]]>[[item.wasDerivedFromTitle]]</a>")
                    .withProperty("wasDerivedFromTitle", Dataset::getWasDerivedFromTitle).withProperty("wasDerivedFrom", Dataset::getWasDerivedFrom)).setWidth("25%")
                    .setHeader(new Html("<b>Zdrojový ISVS</b>"));
            
            grid2.addColumn(TemplateRenderer.<Dataset> of("<a href=resource?uri=[[item.publisher]]>[[item.publisherName]]</a>")
                    .withProperty("publisherName", Dataset::getPublisherName).withProperty("publisher", Dataset::getPublisher)).setWidth("25%")
                    .setHeader(new Html("<b>Vydavateľ</b>"));



        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        whiteSpace.add(grid2);
    }

}
