package sk.gov.knowledgegraph.views;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import sk.gov.knowledgegraph.model.entity.Resource;
import sk.gov.knowledgegraph.service.ResourceService;

@Route(value = "resource", layout = MainView.class)
@PageTitle("URI Zdroj")
@CssImport("./styles/idsk-frontend-2.8.0.min.css")
@CssImport("./styles/views/resource/resource-view.css")

public class ResourceView extends Div implements HasUrlParameter<String> {

    private static Logger logger = LoggerFactory.getLogger(ResourceView.class);
    private Grid<Resource> grid = new Grid<>(Resource.class, false);
    private ResourceService resourceService;
    private Main main = new Main();  	
    private Div whiteSpace = new Div();
    
    public ResourceView(@Autowired ResourceService resourceService) {
        this.resourceService = resourceService;

    }


    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();

        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        if (parametersMap.get("uri") != null) {
            showResource(parametersMap.get("uri").get(0));
        }
    }


    private void showResource(String uriString) {
    	  
  	    setId("resource-view");
	    addClassName("govuk-width-container");
	    main.addClassName("govuk-main-wrapper");
	    main.setId("main-content");
	    add(main);
	   
	    whiteSpace.getElement().removeAllChildren();
	    whiteSpace.addClassName("app-whitespace-highlight");
	 
        String prefLabel = "";
        String type = "";
        String typeLabel = "";

        Resource res = new Resource();

        logger.info("uriString"+uriString);
        
        try {

        	String jsonLDIconString = "<a target=new href=api/resource?uri=" + uriString + "&content-type="
                    + URLEncoder.encode("application/ld+json", StandardCharsets.UTF_8.toString()) + "><img src=images/json-ld-data-32.png valign=bottom></a>";

        	res = resourceService.getBaseProperties(uriString);
            
            logger.info("res"+res);

            	
            if (res != null)	
            {
            	if (res.getPrefLabel()!= null)
                    prefLabel = res.getPrefLabel();
           	 
            	if (res.getType()!= null)
            		type = res.getType();
             
            	if (res.getTypeLabel() != null)
                       typeLabel = "(" + res.getTypeLabel() + ")";
            }
            else
            	logger.warn("res == null");
     
        	logger.info("uriString:"+uriString);
        	logger.info("prefLabel:"+prefLabel);
        	logger.info("type:"+type);
        	logger.info("typeLabel:"+typeLabel);

        	String imageString = "";
            
            
            if (prefLabel != "") {
            	
            	logger.info("type2"+type);
            	
            	whiteSpace.add(new Html("<div></br><table><tr><td><font color=DarkBlue size=5><b>znalosti o:</b></font><br><font size=5><b>" + prefLabel
                        + "</b></font>&nbsp;<a href=resource?uri=" + type + "><font size=5 color=gray>" + typeLabel
                        + "</font></a></td></tr><tr><td><font size=4 color=DarkBlue><b>" + uriString + " " + jsonLDIconString
                        + "</b></font>&nbsp;<img src=></td></tr></table></div>"));
            }            
            else {
            	  	           
            	whiteSpace.add(new Html("<div></br><table><tr><td><font color=DarkBlue size=5><b>znalosti o:</b></font><br</tr><tr><td><font size=4 color=DarkBlue><b>" + uriString + " " + jsonLDIconString
                        + "</b></font>&nbsp;<img src=></td></tr></table></div>"));
            }
     

        } catch (Exception e) {
            add(e.toString());
        }

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setColumnReorderingAllowed(true);
        grid.setAllRowsVisible(true);
        grid.setSelectionMode(Grid.SelectionMode.NONE);       
        grid.removeAllColumns();

        try {


            grid.setItems(resourceService.describeUriBySelect(uriString));
            grid.setAllRowsVisible(true);
            grid.addColumn(new ComponentRenderer<>(resource -> {

                if (resource.getIsInverse().contains("false")) {
                    return new Html("<div><b><a href=resource?uri=" + resource.getPredicate().replace("#", "%23") + ">" + resource.getPredicateShort()
                            + "</b></a>" + resource.getResourceIcon(resource.getPredicateShort()));

                } else
                    return new Html("<div><b>is <a href=resource?uri=" + resource.getPredicate().replace("#", "%23") + ">" + resource.getPredicateShort()
                            + "</a> of</b>" + resource.getResourceIcon(resource.getPredicateShort()) + "</div>");
            })).setWidth("15%").setHeader(new Html("<b>Vlastnosť</b>"));

            grid.addColumn(new ComponentRenderer<>(resource -> {

                String linkBase = "";
                String targetWindowString = "";

                linkBase = resource.getLinkBaseUrl("resource?uri=", resource.getPredicateShort());

                if (linkBase == "")
                    targetWindowString = "target=new";

                // resource is URI

                if (resource.getIsInverse().contains("false") && resource.getObject().contains("http")) {
                    if (resource.getPredicate().contains("logo") || resource.getPredicate().contains("img"))

                        return new Html("<div height=\10px\"><img  style=\"display:block;\"  height=\"100px\"  src=" + resource.getObject() + ">");
                    //	 return new Image(resource.getObject(), resource.getObject());
                    else

                    if (resource.getObjectShort() != null)
                        return new Html("<div><a " + targetWindowString + " href=" + linkBase + resource.getObject().replace("#", "%23") + ">"
                                + resource.getObjectShort() + "</a>");
                    else
                        return new Html("<div><a " + targetWindowString + " href=" + linkBase + resource.getObject().replace("#", "%23") + ">"
                                + resource.getObject() + "</a>");
                }

                // resource is value

                else if (resource.getIsInverse().contains("false") && !resource.getObject().contains("http"))
                    // return new Label(resource.getObject());

                    if (resource.getLanguage() != null)
                        return new Html("<div>" + resource.getLanguageIcon(resource.getLanguage()) + " " + resource.getObject().toString());
                    else
                        return new Label(resource.getObject());

                // inverse resource

                else if (resource.getIsInverse().contains("true") && resource.getObject().contains("http"))

                    if (resource.getSubjectShort() != null)
                        return new Html("<div><a href=resource?uri=" + resource.getSubject().replace("#", "%23") + ">" + resource.getSubjectShort() + "</a>");
                    else
                        return new Html("<div><a href=resource?uri=" + resource.getSubject().replace("#", "%23") + ">" + resource.getSubject() + "</a>");

                // inverse value

                else if (resource.getIsInverse().contains("true") && !resource.getObject().contains("http"))
                    return new Label(resource.getSubject());

                else
                    return new Html("");
            })).setWidth("45%").setHeader(new Html("<b>Hodnota</b>"));

            grid.addColumn(LitRenderer.<Resource> of("<a href=resource?uri=${item.graph}>${item.graphName}</a>")
                    .withProperty("graph", Resource::getGraph).withProperty("graphName", Resource::getGraphName)).setWidth("20%")
                    .setHeader(new Html("<b>Graf</b>"));

    
           // HorizontalLayout dataLayout = new HorizontalLayout();
           // dataLayout.setWidth("80%");
           //  dataLayout.add(grid);
           //  dataLayout.setAlignItems(Alignment.CENTER);
           // add(dataLayout, grid);

            whiteSpace.add(grid);
            main.add(whiteSpace);
            
            //setContent(dataLayout);

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            add(e.toString());
        }
    }

}
