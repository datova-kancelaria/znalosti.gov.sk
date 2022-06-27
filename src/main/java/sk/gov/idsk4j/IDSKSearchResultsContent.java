package sk.gov.idsk4j;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;

public class IDSKSearchResultsContent {


    public Div create() {
        Div searchResultsContent = new Div();
        searchResultsContent.addClassName("idsk-search-results__content");
        //		this.searchResultsContent.addClassName("govuk-grid-column-three-quarters");

        searchResultsContent.addClassName("govuk-grid-column-full");

        return searchResultsContent;
    }


    public Div create(Grid grid) {
        Div searchResultsContent = new Div();
        searchResultsContent.addClassName("idsk-search-results__content");
        searchResultsContent.addClassName("govuk-grid-column-full");
        searchResultsContent.add(grid);

        return searchResultsContent;
    }

}
