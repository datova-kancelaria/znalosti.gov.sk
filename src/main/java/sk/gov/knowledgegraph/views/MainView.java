package sk.gov.knowledgegraph.views;

import java.util.Optional;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Inline;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

import sk.gov.knowledgegraph.views.MainView;
import sk.gov.idsk4j.IDSKHeaderWeb;
import sk.gov.idsk4j.IDSKHeaderWebMain;
import sk.gov.idsk4j.IDSKHeaderWebNav;
import org.springframework.beans.factory.annotation.Value;


/**
 * The main view is a top-level placeholder for other views.
 */

@CssImport("./styles/idsk-frontend-2.8.0.min.css")
@JsModule("./styles/all.js")

public class MainView extends  AppLayout {

//    @Value("${server.url}")
//    private String serverUrl
//    private final Tabs menu;
    
    public MainView() {

    	//menu = null; //createMenuTabs();  
    	
    	// Menu
    	
      	 UnorderedList ulMenu = new UnorderedList();
      	 ulMenu.addClassName("idsk-header-web__nav-list");
	     
	     ListItem li1 = new ListItem();
	     li1.addClassName("idsk-header-web__nav-list-item");
	     li1.add(new Html("<a class=\"govuk-link idsk-header-web__nav-list-item-link\" href=\"/search\" title=\"Vyhľadávanie\">\n"
	     		+ "                    Vyhľadávanie\n"
	     		+ "                  </a>"));
	     
	     ListItem li2 = new ListItem();
	     li2.addClassName("idsk-header-web__nav-list-item");
	     li2.add(new Html("<a class=\"govuk-link idsk-header-web__nav-list-item-link\" href=\"/metadata\" title=\"Ontológie\">\n"
	     		+ "                    Ontológie\n"
	     		+ "                  </a>"));
	    
	     
	     ListItem li3 = new ListItem();
	     li3.addClassName("idsk-header-web__nav-list-item");
	     li3.add(new Html("<a class=\"govuk-link idsk-header-web__nav-list-item-link\" href=\"/categories\" title=\"Kategórie\">\n"
	     		+ "                    Kategórie\n"
	     		+ "                  </a>"));
	  
	        
	     ListItem li4 = new ListItem();
	     li4.addClassName("idsk-header-web__nav-list-item");
	     li4.add(new Html("<a class=\"govuk-link idsk-header-web__nav-list-item-link\" href=\"/data\" title=\"Príklady dát\">\n"
	     		+ "                    Príklady dát\n"
	     		+ "                  </a>"));
	  
	     ListItem li5 = new ListItem();
	     li5.addClassName("idsk-header-web__nav-list-item");
	     li5.add(new Html("<a class=\"govuk-link idsk-header-web__nav-list-item-link\" href=\"/sparqlView\" title=\"SPARQL Endpoint\">\n"
	     		+ "                    SPARQL\n"
	     		+ "                  </a>"));
	 
	     ListItem li6 = new ListItem();
	     li6.addClassName("idsk-header-web__nav-list-item");
	     li6.add(new Html("<a class=\"govuk-link idsk-header-web__nav-list-item-link\" href=\"/api\" title=\"API\">\n"
	     		+ "                    API\n"
	     		+ "                  </a>"));
	 
	     ListItem li7 = new ListItem();
	     li7.addClassName("idsk-header-web__nav-list-item");
	     li7.add(new Html("<a class=\"govuk-link idsk-header-web__nav-list-item-link\" href=\"/about\" title=\"O portáli\">\n"
	     		+ "                    O portáli\n"
	     		+ "                  </a>"));
	  	     
	     
	     ulMenu.add(li1, li2, li3, li4, li5, li6, li7);
	     //ulMenu.add(li1, li2, li4, li5);
	     

     	IDSKHeaderWebNav headerWebNav = new IDSKHeaderWebNav(ulMenu);
       	IDSKHeaderWebMain headerWebMain = new IDSKHeaderWebMain("images/znalosti-logo3.svg", "znalosti.gov.sk", "Znalostný graf údajov verejnej správy");
    	IDSKHeaderWeb headerWeb = new IDSKHeaderWeb();

        addToNavbar((headerWeb.create(headerWebMain.create(), headerWebNav.create())));
                  
    }

}
