package sk.gov.knowledgegraph.views.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sk.gov.knowledgegraph.data.entity.Resource;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;

import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.helpers.CrudServiceDataProvider;
import sk.gov.knowledgegraph.rest.ResourceAPI;
import sk.gov.knowledgegraph.views.main.MainView;
import com.vaadin.flow.component.textfield.TextField;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Route(value = "resource", layout = MainView.class)
@PageTitle("Resource")
@CssImport("./styles/views/resource/resource-view.css")

public class ResourceView extends Div
{

    private Grid<Resource> grid = new Grid<>(Resource.class, false);

    public ResourceView(@Autowired ResourceAPI resourceService)  {
        setId("resource-view");
        
    	String uriString = VaadinService.getCurrent().getCurrentRequest().getParameter("uri");      
        String prefLabel = "";
        String type = "";
        String typeLabel = "";
    	
    	Resource res = new Resource();        

        
        try {
            res = resourceService.getBaseProperties(uriString);

                     
           if(res.getPrefLabel()!=null)
        	   prefLabel=res.getPrefLabel();
           
           if(res.getType()!=null)
        	   type=res.getType();
           
           if(res.getTypeLabel()!=null)
        	   typeLabel="("+res.getTypeLabel()+")";
          
           String imageString ="";

           if(prefLabel!="")
           {	   
        	   add(new Html("<div></br><table><tr><td><font color=DarkBlue size=5><b>&nbsp;&nbsp;znalosti o:&nbsp;</b></font></td><td><font size=5><b>"+prefLabel+"</b></font>&nbsp;<a href=http://localhost:8080/resource?uri="+type+"><font size=5 color=gray>"+typeLabel+"</font></a></td></tr><tr><td></td><td><font size=4><b>"+uriString+"</b></font></td></tr></table></div>"));
           
           }
           else
           {        	
        	   if (type!="")
        	     	   add(new Html("<div><font size=5 color=DarkBlue><b>&nbsp;&nbsp;znalosti o:&nbsp;</font>"+uriString+"</div>"));
        	   else
    	     	   		add(new Html("<div><font size=5 color=DarkBlue><b>&nbsp;&nbsp;znalosti o:&nbsp;</font>"+uriString+"&nbsp;<a href=http://localhost:8080/resource?uri="+type+"><font size=5 color=gray>"+typeLabel+"</font></a></div>"));
           }
        
        }
        catch (Exception e)
        {
        	add(e.toString());
        }        
        
        try {
        	
        	
        	add(new Html("<br>"));
        	
        	grid.setItems(resourceService.uri(uriString));
            grid.setHeightFull();
            grid.setSelectionMode(Grid.SelectionMode.NONE);
          //  grid.setWidth("80%");
            
            // grid.getColumns().forEach(col -> col.setAutoWidth(true));           
            
  	
             grid.addColumn(new ComponentRenderer<>(resource -> {
            	
            	
                if(resource.getIsInverse().contains("false"))
                {
                	return new Html("<div><b><a href=http://localhost:8080/resource?uri="+resource.getPredicate().replace("#","%23")+">"+resource.getPredicateShort()+"</b></a>"+resource.getResourceIcon(resource.getPredicateShort()));
                
                    //Image logo = new Image("images/logo.png", "SKKnowledgeGraph logo");
                	//	return new Image("dede.jpg", "resource");
                
                }
                else
                    return new Html("<div><b>is <a href=http://localhost:8080/resource?uri="+resource.getPredicate().replace("#","%23")+">"+resource.getPredicateShort()+"</a> of</b>"+resource.getResourceIcon(resource.getPredicateShort())+"</div>");
            })).setWidth("15%")
               .setHeader(new Html("<b>Vlastnosť</b>"));
                

            
            grid.addColumn(new ComponentRenderer<>(resource -> {
            	
            	String linkBase = "";
            	String targetWindowString = "";

            	linkBase = resource.getLinkBaseUrl("http://localhost:8080/resource?uri=" , resource.getPredicateShort());
            	
            	if(linkBase=="")
            		targetWindowString = "target=new";
            		
            	// resource is URI
            	
                if(resource.getIsInverse().contains("false") && resource.getObject().contains("http"))
                {	
                	 if(resource.getPredicate().contains("logo") || resource.getPredicate().contains("img"))
                    
                		 return new Html("<div height=\10px\"><img  style=\"display:block;\"  height=\"100px\"  src="+resource.getObject()+">");
                	//	 return new Image(resource.getObject(), resource.getObject());
                	 else
                	     
                		 if (resource.getObjectShort()!=null)
                    		 return new Html("<div><a "+targetWindowString+" href="+linkBase+resource.getObject().replace("#","%23")+">"+resource.getObjectShort()+"</a>");
                		 else
                			 return new Html("<div><a "+targetWindowString+" href="+linkBase+resource.getObject().replace("#","%23")+">"+resource.getObject()+"</a>");
                }

                
             // resource is value
                
                else if (resource.getIsInverse().contains("false") && !resource.getObject().contains("http"))
                    return new Label(resource.getObject());

             
             // inverse resource
                
                
                else if(resource.getIsInverse().contains("true") && resource.getObject().contains("http"))
                   
                	 if (resource.getSubjectShort()!=null)
                		 return new Html("<div><a href=http://localhost:8080/resource?uri="+resource.getSubject().replace("#","%23")+">"+resource.getSubjectShort()+"</a>");
                	 else
                		 return new Html("<div><a href=http://localhost:8080/resource?uri="+resource.getSubject().replace("#","%23")+">"+resource.getSubject()+"</a>");

                // inverse value

            
                else if (resource.getIsInverse().contains("true") && !resource.getObject().contains("http"))
                    return new Label(resource.getSubject());
                
                else
                    return new Html("");
            })).setWidth("45%")
               .setHeader(new Html("<b>Hodnota</b>"));
                
            
            
      
            
            
            grid.addColumn(TemplateRenderer
                    .<Resource>of("<a href=http://localhost:8080/resource?uri=[[item.graph]]>[[item.graphName]]</a>")
                    .withProperty("graph", Resource::getGraph)
                    .withProperty("graphName", Resource::getGraphName)
            ).setWidth("20%")
             .setHeader(new Html("<b>Graf</b>"));
                     
            
            
          //  add(grid);
          //  dataLayout.setAlignItems(Alignment.CENTER);

           // add(dataLayout, grid);

            HorizontalLayout dataLayout = new HorizontalLayout();
            dataLayout.setWidth("80%");
            dataLayout.add(grid);
            dataLayout.setAlignItems(Alignment.CENTER);
            add(dataLayout, grid);
            
            //setContent(dataLayout);

            
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        	add(e.toString());

        }
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }
}