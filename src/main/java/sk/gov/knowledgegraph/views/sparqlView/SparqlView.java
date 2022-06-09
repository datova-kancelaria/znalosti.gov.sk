package sk.gov.knowledgegraph.views.sparqlView;

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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.impl.TupleQueryResultBuilder;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriUtils;

import sk.gov.idsk4j.IDSKSearchResultsContent;
import sk.gov.idsk4j.IDSKSearchResultsFilter;
import sk.gov.knowledgegraph.common.URIView;
import sk.gov.knowledgegraph.data.entity.Result;
import sk.gov.knowledgegraph.rest.QueryResponder;
import sk.gov.knowledgegraph.rest.SearchAPI;
import sk.gov.knowledgegraph.rest.SparqlAPI;
import sk.gov.knowledgegraph.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;

// import com.vaadin.event.ShortcutListener;

@Route(value = "sparqlView", layout = MainView.class)
@PageTitle("SPARQL Endpoint")
@CssImport("./styles/idsk-frontend-2.8.0.min.css")
//@CssImport("./styles/views/search/search-view.css")
//@NpmPackage(value="@id-sk/frontend", version = "2.8.0")
//@JsModule("/home/liskam/eclipse-workspace/znalosti.gov.sk/node_modules/@id-sk/frontend/idsk/all.js")

public class SparqlView extends Div {

 	 private String resultHtml;

	 public HttpServletRequest request;
	 public HttpServletResponse response;
	 
//	 private SparqlAPI sparqlApi = new SparqlAPI();
	 
	 public void setServletRequest(HttpServletRequest request) {     this.request = request; }
     public void setServletResponse(HttpServletResponse response) {      this.response = response;   }   
     
     public HttpServletRequest getServletRequest()
     {
    	 return request;
     }
     
     public HttpServletResponse getServletResponse()
     {
    	 return response;
     }
     
     public String acceptHeader = "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9";
    
     @Autowired
     private Repository repository;
     
	private static Logger logger = LoggerFactory.getLogger(SparqlView.class);

    //private Grid<Result> grid5 = new Grid<>(Result.class, false);
    
    public URIView uriView = new URIView();

    
    public SparqlView(@Autowired QueryResponder sparqlService) throws UnsupportedRDFormatException, FileNotFoundException, IOException, ParserConfigurationException, TransformerException {
        
    	    	
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
        h1.add(new Html("<div>\n"
        		+ "    Zadaj SPARQL dotaz\n"
        		+ "  </div>"));
               
        searchResults.add(h1);

        Div searchBar = new Div();
        searchBar.addClassName("govuk-grid-column-full");
        searchBar.addClassName("idsk-search-results__search-bar");
        
        Div searchComponent = new Div();
        searchComponent.addClassName("idsk-search-component");
        searchBar.add(searchComponent);
            
        TextArea textarea= new TextArea();
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
      
                if (textarea.getValue().toUpperCase().contains("SELECT"))
                	setTupleQueryResultHtml(textarea.getValue());
                else if (textarea.getValue().toUpperCase().contains("CONSTRUCT"))
                	setGraphQueryResultHtml(textarea.getValue());
                else if (textarea.getValue().toUpperCase().contains("ASK"))
                	setBooleanQueryResultHtml(textarea.getValue());
                            	
                resultHtmlDiv.add(new Html(getResultHtml()));     
            	resultsDiv.add(resultHtmlDiv);
            	  		
        }
        catch (Exception ee)
        {
            System.out.println(ee.toString());
        }
        });
        
        
        btnExport.addClickListener(e -> {
        	   try {
        	
              	String encodedQuery = UriUtils.encodePath(textarea.getValue(), "UTF-8");		
            	UI.getCurrent().getPage().executeJs("window.open('/sparql?query="+encodedQuery+"', '_blank')");

        	   }
        	   catch (Exception ee)
               {
        		   logger.info(ee.getMessage().toString());               
               }
        	
        });
            
    }
    
    public void setTupleQueryResultHtml(String query)
    {
    	logger.info("setTupleQueryResultHtml");
    	
	 	this.resultHtml = "<table class=\"govuk-table\"><thead class=\"govuk-table__head\">";

    	
    	try (RepositoryConnection conn = repository.getConnection()) {	
    		
    		TupleQuery tupleQuery = conn.prepareTupleQuery(query);
			TupleQueryResult result = tupleQuery.evaluate();
			        			
			List<String> bindingNames = result.getBindingNames();
			
	          this.resultHtml = this.resultHtml + "<tr class=\"govuk-table__row\">";

	          for (int i=0; i<bindingNames.size();i++)
	        	  this.resultHtml = this.resultHtml + "<th scope=\"col\" class=\"govuk-table__header\">?" + bindingNames.get(i) + "</th>";
      
	          this.resultHtml = this.resultHtml + "</tr></thead><tbody class=\"govuk-table__body\">";
			
			while (result.hasNext()) {  // iterate over the result

				BindingSet bindingSet = result.next();
         //       logger.info("bindingSet.size():"+bindingSet.size());
                
                this.resultHtml = this.resultHtml + "<tr class=\"govuk-table__row\">";
                                        
                for (int i=0; i<bindingSet.size();i++)
                	this.resultHtml = this.resultHtml + "<td  class=\"govuk-table__cell\">" + uriView.formatResultString(bindingSet.getValue(bindingNames.get(i)).stringValue()) + "</td>";
                
                this.resultHtml = this.resultHtml + "</tr>";
             }
    	
        	this.resultHtml = this.resultHtml + "</tbody></table>";

			
    	}
    	
    }
    
    public void setGraphQueryResultHtml(String query)
    {
    	logger.info("setGraphQueryResultHtml");
    	
	 	this.resultHtml = "<table class=\"govuk-table\"><thead class=\"govuk-table__head\"><tr class=\"govuk-table__row\"><th scope=\"col\" class=\"govuk-table__header\">?subject</th><th scope=\"col\" class=\"govuk-table__header\">?predicate</th><th scope=\"col\" class=\"govuk-table__header\">?object</th></tr></thead>";
        this.resultHtml = this.resultHtml + "<tbody class=\"govuk-table__body\">";

    	try (RepositoryConnection conn = repository.getConnection()) {	
    		
    		GraphQueryResult graphResult = conn.prepareGraphQuery(query).evaluate();
    		
    		for (Statement statement: graphResult) {
    			    	    	
                this.resultHtml = this.resultHtml + "<tr class=\"govuk-table__row\">";
            	this.resultHtml = this.resultHtml + "<td  class=\"govuk-table__cell\">" + uriView.formatResultString(statement.getSubject().stringValue()) + "</td>" +
            			                            "<td  class=\"govuk-table__cell\">" + uriView.formatResultString(statement.getPredicate().stringValue()) + "</td>" +
            			                            "<td  class=\"govuk-table__cell\">" + uriView.formatResultString(statement.getObject().stringValue()) + "</td>";
                this.resultHtml = this.resultHtml + "</tr>";
    			}
    		
        	this.resultHtml = this.resultHtml + "</tbody></table>";
    	}
    	
    }		
   
    public void setBooleanQueryResultHtml(String query)
    {
    	logger.info("setBooleanQueryResultHtml");
    	
    	this.resultHtml = "<table class=\"govuk-table\"><thead class=\"govuk-table__head\"><tr class=\"govuk-table__row\"><th scope=\"col\" class=\"govuk-table__header\">Je to pravda?</th></tr></thead>";
        this.resultHtml = this.resultHtml + "<tbody class=\"govuk-table__body\">";

    	
    	try (RepositoryConnection conn = repository.getConnection()) {	
    		
			BooleanQuery bq = conn.prepareBooleanQuery(QueryLanguage.SPARQL, query);

			
    	    	
                this.resultHtml = this.resultHtml + "<tr class=\"govuk-table__row\">";
            	this.resultHtml = this.resultHtml + "<td  class=\"govuk-table__cell\">" + bq.evaluate() + "</td>";
                this.resultHtml = this.resultHtml + "</tr>";
    		
        	this.resultHtml = this.resultHtml + "</tbody></table>";
    	}
    	
    }	
    
    public String getResultHtml()
    {
    	return this.resultHtml;
    }
    
    
    public String getGraphQueryHtmlResult()
    {
    	return null;
    }

}
