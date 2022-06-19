package sk.gov.idsk4j;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;

public class IDSKHeaderWebMain {

    private Div main = new Div();
    private String siteSVGBanner;
    private String siteName;
    private String siteTitle;

    public String getSiteSVGBanner() {
        return siteSVGBanner;
    }


    public void setSiteSVGBanner(String siteSVGBanner) {
        this.siteSVGBanner = siteSVGBanner;
    }


    public String getSiteName() {
        return siteName;
    }


    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }


    public String getSiteTitle() {
        return siteTitle;
    }


    public void setSiteTitle(String siteTitle) {
        this.siteTitle = siteTitle;
    }


    public IDSKHeaderWebMain(String siteSVGBanner, String siteName, String siteTitle) {
        this.siteSVGBanner = siteSVGBanner;
        this.siteName = siteName;
        this.siteTitle = siteTitle;
    }


    public Div create() {
        this.main.addClassName("idsk-header-web__main");

        Div container = new Div();
        container.addClassName("govuk-width-container");
        this.main.add(container);

        Div gridRow = new Div();
        gridRow.addClassName("govuk-grid-row");

        container.add(gridRow);

        Div columnOneThird = new Div();
        //columnOneThird.addClassName("govuk-grid-column-one-third-from-desktop"); 

        columnOneThird.addClassName("govuk-grid-column-full");

        gridRow.add(columnOneThird);

        Div mainHeadline = new Div();
        mainHeadline.addClassName("idsk-header-web__main-headline");

        /*
         * 
         * mainHeadline.add(new Html("<a href=\"/\" title=\"Odkaz na úvodnú stránku\">\n"
         * + "                <img src=\"images/znalosti-logo1.png\" alt=\"ID-SK Frontend\" class=\"idsk-header-web__main-headline-logo\">\n"
         * + "          </a>"));
         * 
         * 
         * mainHeadline.add(new Html("<a href=\"/\" title=\"Odkaz na úvodnú stránku\">\n"
         * +
         * "                <img src=\"https://idsk.gov.sk/public/assets/images/idsk-logo.svg\" alt=\"ID-SK Frontend\" class=\"idsk-header-web__main-headline-logo\">\n"
         * + "          </a>"));
         * 
         * 
         * mainHeadline.add(new Html("<a href=\"/\" title=\"Odkaz na úvodnú stránku\">"
         * + "                <div class=\"idsk-header-web__banner-title\">znalosti.gov.sk</div>"
         * + "          </a>"));
         * 
         * 
         * mainHeadline.add(new Html("<a href=\"/\" title=\"Odkaz na úvodnú stránku\">\n"
         * + "                <font \"font-family:'Courier New'\">znalosti.gov.sk</font>"
         * + "          </a>"));
         * 
         * 
         * mainHeadline.add(new Html("<a href=\"/\" title=\"Odkaz na úvodnú stránku\">"
         * + "                <div class=\"govuk-heading-xl\">znalosti.gov.sk</div>"
         * + "          </a>"));
         * 
         * 
         * mainHeadline.add(new Html("<a href=\"/\" title=\"Odkaz na úvodnú stránku\">\n"
         * + "                <img src=\"images/znalosti-logo1.svg\" alt=\"ID-SK Frontend\" class=\"idsk-header-web__main-headline-logo\">\n"
         * + "          </a>"));
         * 
         */

        mainHeadline.add(new Html("<a href=\"/\" title=\"Odkaz na úvodnú stránku\">\n" + "          <img src=\"" + getSiteSVGBanner()
                + "\" alt=\"ID-SK Frontend\" class=\"idsk-header-web__main-headline-logo\"> <h2 class=\"govuk-heading-m\">" + getSiteTitle() + "</h2> "
                + "          </a>"));

        columnOneThird.add(mainHeadline);

        return this.main;
    }
}
