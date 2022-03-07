package sk.gov.idsk4j;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Header;

public class IDSKHeaderWeb {
	
	private String siteName = "";
	private String siteURL = "";
	private Header header = new Header();
    
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getSiteURL() {
		return siteURL;
	}
	public void setSiteURL(String siteURL) {
		this.siteURL = siteURL;
	}
	
	public IDSKHeaderWeb(String siteName, String siteURL) {
		this.siteName = siteName;
		this.siteURL = siteURL;
	}

	public Header create(Div mainDiv, Div navDiv )
	{
		this.header.addClassName("idsk-header-web");
		this.header.setWidthFull();
		this.header.setHeightFull();
		
		Div tricolor = new Div();
        tricolor.addClassName("idsk-header-web__tricolor");
        
        Div brand = new Div();
        brand.addClassName("idsk-header-web__brand");
        
        Div container = new Div();
        container.addClassName("govuk-width-container");        
       
        Div gridRow = new Div();
        gridRow.addClassName("govuk-grid-row");        
        
        container.add(gridRow);
     
        Div columnFull = new Div();
        columnFull.addClassName("govuk-grid-column-full");   
     
        gridRow.add(columnFull);   
        
        Div brandGestor = new Div();
        brandGestor.addClassName("idsk-header-web__brand-gestor");   
        
        brandGestor.add(new Html("<span class=\"govuk-body-s idsk-header-web__brand-gestor-text\">\n"
        		+ "              <b>"+this.getSiteName()+"</b>\n"
        		+ "                </span>"));
        
        Div brandSpacer = new Div();
        brandSpacer.addClassName("idsk-header-web__brand-spacer");   
        
        Div brandLanguage = new Div();
        brandLanguage.addClassName("idsk-header-web__brand-language"); 
     // brandLanguage.addClassName("idsk-header-web__brand-language--active"); 
        
        
        Html htmlButton = new Html("<div><button class=\"idsk-header-web__brand-language-button\" aria-label=\"Rozbaliť jazykové menu\" aria-expanded=\"false\" data-text-for-hide=\"Skryť jazykové menu\" data-text-for-show=\"Rozbaliť jazykové menu\">\n"
        		+ "              Slovenčina\n"
        		+ "              <div class=\"idsk-header-web__link-arrow\"></div>\n"
        		+ "            </button><ul class=\"idsk-header-web__brand-language-list\">\n"
        		+ "                <li class=\"idsk-header-web__brand-language-list-item\">\n"
        		+ "                  <a class=\"govuk-link idsk-header-web__brand-language-list-item-link \" title=\"English\" href=\"#2\">\n"
        		+ "                    English\n"
        		+ "                  </a>\n"
        		+ "                </li>\n"
        		+ "                <li class=\"idsk-header-web__brand-language-list-item\">\n"
        		+ "                  <a class=\"govuk-link idsk-header-web__brand-language-list-item-link \" title=\"German\" href=\"#3\">\n"
        		+ "                    German\n"
        		+ "                  </a>\n"
        		+ "                </li>\n"
        		+ "                <li class=\"idsk-header-web__brand-language-list-item\">\n"
        		+ "                  <a class=\"govuk-link idsk-header-web__brand-language-list-item-link idsk-header-web__brand-language-list-item-link--selected\" title=\"Slovenčina\" href=\"#1\">\n"
        		+ "                    Slovenčina\n"
        		+ "                  </a>\n"
        		+ "                </li>\n"
        		+ "            </ul></div>");
        
        brandLanguage.add(htmlButton);
        
        columnFull.add(brandGestor, brandSpacer, brandLanguage);
        brand.add(container);	
                  
        Div divider = new Div();
        divider.addClassName("idsk-header-web__nav--divider");
                
        this.header.add(tricolor, brand, mainDiv, divider, navDiv);
		
        
		return this.header;
	}
	
}
