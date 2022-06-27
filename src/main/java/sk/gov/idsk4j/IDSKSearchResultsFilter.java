package sk.gov.idsk4j;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Label;

public class IDSKSearchResultsFilter {

    public Div create() {
        Div searchResultsFilter = new Div();

        searchResultsFilter.addClassName("idsk-search-results__filter");
        searchResultsFilter.addClassName("govuk-grid-column-one-quarter");

        Div searchResultsLinkPanel1 = new Div();

        searchResultsLinkPanel1.addClassName("idsk-search-results__link-panel");
        searchResultsLinkPanel1.addClassName("idsk-search-results__link-panel--expanded");

        Button btn1 = new Button();
        btn1.addClassName("idsk-search-results__link-panel-button");
        btn1.setText("Graf");

        Div searchResultsList = new Div();
        searchResultsList.addClassName("idsk-search-results__list");

        Div customersSurveysRadios = new Div();
        customersSurveysRadios.addClassName("idsk-customer-surveys-radios");

        searchResultsList.add(customersSurveysRadios);

        Div govukradios = new Div();
        govukradios.addClassName("govuk-radios");

        customersSurveysRadios.add(govukradios);

        Div govukradiositem1 = new Div();
        govukradiositem1.addClassName("govuk-radios__item");

        govukradios.add(govukradiositem1);

        Input input1 = new Input();
        input1.addClassName("govuk-radios__input");
        input1.setId("MetaIS");
        input1.setType("radio");
        input1.setValue("MetaIS");

        Label label1 = new Label();
        label1.setText("Metainformačný systém");
        label1.addClassName("govuk-label");
        label1.addClassName("govuk-radios__label");
        label1.setFor("MetaIS");

        govukradiositem1.add(input1, label1);

        searchResultsLinkPanel1.add(btn1);
        searchResultsLinkPanel1.add(searchResultsList);

        searchResultsFilter.add(searchResultsLinkPanel1);

        return searchResultsFilter;
    }

}
