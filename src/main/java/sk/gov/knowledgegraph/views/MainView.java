package sk.gov.knowledgegraph.views;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;

import sk.gov.idsk4j.IDSKHeaderWeb;
import sk.gov.idsk4j.IDSKHeaderWebMain;
import sk.gov.idsk4j.IDSKHeaderWebNav;

/**
 * The main view is a top-level placeholder for other views.
 */

@CssImport("./styles/idsk-frontend-2.8.0.min.css")
@JsModule("./styles/all.js")
public class MainView extends AppLayout {

    public MainView() {

        // Menu

        UnorderedList ulMenu = new UnorderedList();
        ulMenu.addClassName("idsk-header-web__nav-list");

        ListItem li1 = new ListItem();
        li1.addClassName("idsk-header-web__nav-list-item");
        li1.add(new Html("<a class=\"govuk-link idsk-header-web__nav-list-item-link\" href=\"/search\" title=\"Vyhľadávanie\">\n"
                + "                    Vyhľadávanie\n" + "                  </a>"));

        ListItem li2 = new ListItem();
        li2.addClassName("idsk-header-web__nav-list-item");
        li2.add(new Html("<a class=\"govuk-link idsk-header-web__nav-list-item-link\" href=\"/data\" title=\"Data\">\n" + "                    Data\n"
                + "                  </a>"));

        ListItem li3 = new ListItem();
        li3.addClassName("idsk-header-web__nav-list-item");
        li3.add(new Html("<a class=\"govuk-link idsk-header-web__nav-list-item-link\" href=\"/sparqlView\" title=\"SPARQL Endpoint\">\n"
                + "                    SPARQL\n" + "                  </a>"));

        ListItem li4 = new ListItem();
        li4.addClassName("idsk-header-web__nav-list-item");
        li4.add(new Html("<a class=\"govuk-link idsk-header-web__nav-list-item-link\" href=\"/api\" title=\"API\">\n" + "                    API\n"
                + "                  </a>"));

        ListItem li5 = new ListItem();
        li5.addClassName("idsk-header-web__nav-list-item");
        li5.add(new Html("<a class=\"govuk-link idsk-header-web__nav-list-item-link\" href=\"/about\" title=\"O portáli\">\n"
                + "                    O portáli\n" + "                  </a>"));

        ulMenu.add(li1, li2, li3, li4, li5);
        //ulMenu.add(li1, li2, li4, li5);

        IDSKHeaderWebNav headerWebNav = new IDSKHeaderWebNav(ulMenu);
        IDSKHeaderWebMain headerWebMain = new IDSKHeaderWebMain("images/znalosti-logo3.svg", "znalosti.gov.sk", "Znalostný graf údajov verejnej správy");
        IDSKHeaderWeb headerWeb = new IDSKHeaderWeb();

        addToNavbar((headerWeb.create(headerWebMain.create(), headerWebNav.create())));

    }

}
