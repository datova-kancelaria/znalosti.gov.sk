package sk.gov.idsk4j;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.UnorderedList;

public class IDSKHeaderWebNav {

    private UnorderedList menu;

    public IDSKHeaderWebNav(UnorderedList menu) {
        this.menu = menu;
    }


    public Div create() {
        
        Div divNav = new Div();

        divNav.addClassName("idsk-header-web__nav");

        Div container = new Div();
        container.addClassName("govuk-width-container");
        divNav.add(container);

        Div gridRow = new Div();
        gridRow.addClassName("govuk-grid-row");

        container.add(gridRow);

        Div columnFull1 = new Div();
        columnFull1.addClassName("govuk-grid-column-full");

        Div columnFull2 = new Div();
        columnFull2.addClassName("govuk-grid-column-full");

        Nav nav = new Nav();
        columnFull2.add(nav);

        nav.add(this.menu);

        gridRow.add(columnFull1, columnFull2);

        return divNav;
    }
}
