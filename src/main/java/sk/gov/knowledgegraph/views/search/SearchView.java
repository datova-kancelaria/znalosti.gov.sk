package sk.gov.knowledgegraph.views.search;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
import sk.gov.knowledgegraph.data.entity.Result;
import sk.gov.knowledgegraph.rest.SearchAPI;
import sk.gov.knowledgegraph.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "search", layout = MainView.class)
@PageTitle("Search")
@CssImport("./styles/views/search/search-view.css")
@RouteAlias(value = "", layout = MainView.class)
public class SearchView extends HorizontalLayout {

    private TextField searchText;
    private Button btnSearch;

    private Grid<Result> grid5 = new Grid<>(Result.class, false);

    public SearchView(@Autowired SearchAPI searchService) throws UnsupportedRDFormatException, FileNotFoundException, IOException, ParserConfigurationException, TransformerException {
        setId("search-view");
        
        int getAllTriplesCount = 0;
        int datasetsCount = 0;
        int catalogCount = 0;
        
        getAllTriplesCount = searchService.getAllTriplesCount();
        datasetsCount = searchService.getDatasetsCount();
        catalogCount = searchService.getCatalogsCount();
        
        add(new Html("<div align=center>"+datasetsCount+" datasetov, "+catalogCount+" katalógy, <b>"+getAllTriplesCount+"</b> tripletov (znalostí)</div>"));
        
        searchText = new TextField();
      
       
        

        
       // TextField myTextField = new TextField("A text field");
       // myTextField.setImmediate(true);
        
        //searchText.setImmediate();
        
        btnSearch = new Button("Hladaj");
        searchText.setWidth("30%"); //setWidthFull(); //.setWidth("300");
        searchText.setClearButtonVisible(true);
        
        VerticalLayout myLayout = new VerticalLayout();
     //   HorizontalLayout myLayout = new HorizontalLayout();
        
        
        myLayout.setWidthFull();
     //   myLayout.setHeightFull();
        
        myLayout.addClassName("search-layout");

        myLayout.add(searchText, btnSearch);
        myLayout.setAlignItems(Alignment.CENTER);
        
        add(myLayout);
        

        grid5.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid5.setHeightFull();
        grid5.setSelectionMode(Grid.SelectionMode.NONE);

        btnSearch.addClickListener(e -> {

            try {

            grid5.removeAllColumns();
            grid5.setItems(searchService.search(searchText.getValue()));
            grid5.setHeightByRows(true);
            
            grid5.addColumn(new ComponentRenderer<>(result -> {
            	
            	return new Html (
            			"<div><font size=3><b><a href=http://localhost:8080/resource?uri="+result.getSubject().replace("#","%23")+">"+result.getObject()+"</a></b></font></br>" +
            			"<font size=2 color=grey>"+result.getSubject()+"</font></br>" +
            			"<font size=1 color=grey>"+result.getGraphName()+"</font>" +
            			"<br /></div>"
            					);
                       }));
  
            add(grid5);

        }
        catch (Exception ee)
        {
            System.out.println(ee.toString());
        }
        });
        
        
        
        searchText.addKeyPressListener(Key.ENTER, event ->
        {     	
       
        	  try {

                  grid5.removeAllColumns();
                  grid5.setItems(searchService.search(searchText.getValue()));

                  grid5.setHeightByRows(true);
                  grid5.addColumn(new ComponentRenderer<>(result -> {
                  	
                  	return new Html (
                  			"<div><font size=3><b><a href=http://localhost:8080/resource?uri="+result.getSubject().replace("#","%23")+">"+result.getObject()+"</a></b></font></br>" +
                  			"<font size=2 color=grey>"+result.getSubject()+"</font></br>" +
                  			"<font size=1 color=grey>"+result.getGraphName()+"</font>" +
                  			"<br /></div>"
                  					);
                             }));
                     add(grid5);

              }
              catch (Exception ee)
              {
                  System.out.println(ee.toString());
              }
              });
    }

    private Component createSearchLayout() {
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.addClassName("search-layout");
        searchLayout.setWidthFull();
        searchLayout.setHeightFull();
        return searchLayout;
    }
    
    
    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid5-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid5);
    }

    private void refreshGrid() {
        grid5.select(null);
        grid5.getDataProvider().refreshAll();
    }
}
