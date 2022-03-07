package sk.gov.idsk4j;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.grid.Grid;

public class IDSKSearchResultsContent {

	private Div searchResultsContent = new Div();
	
	public Div create()
	{
		
		this.searchResultsContent.addClassName("idsk-search-results__content");
//		this.searchResultsContent.addClassName("govuk-grid-column-three-quarters");

		this.searchResultsContent.addClassName("govuk-grid-column-full");

		return this.searchResultsContent;
	}
	
	public Div create(Grid grid)
	{
		
		this.searchResultsContent.addClassName("idsk-search-results__content");
//		this.searchResultsContent.addClassName("govuk-grid-column-three-quarters");
		
		this.searchResultsContent.addClassName("govuk-grid-column-full");
		this.searchResultsContent.add(grid);
		
		return this.searchResultsContent;
	}

}
