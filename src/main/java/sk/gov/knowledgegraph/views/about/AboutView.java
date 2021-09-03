package sk.gov.knowledgegraph.views.about;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import sk.gov.knowledgegraph.data.entity.Result;
import sk.gov.knowledgegraph.rest.SearchAPI;
import sk.gov.knowledgegraph.views.main.MainView;

@Route(value = "about", layout = MainView.class)
@PageTitle("About")
public class AboutView extends Div {

    private Grid<Result> grid4 = new Grid<>(Result.class, false);

    public AboutView(@Autowired SearchAPI searchService) {
        setId("about-view");

        add(new Html("<div><h3>&nbsp;&nbsp;Informácie</h3></div>"));
        
        add(new Html("<div>&nbsp;&nbsp;Projekt znalosti.gov.sk poskytuje otvorené údaje verejnej správy Slovenskej republiky vo forme prepojených údajov (linkeddata). "
        		+ "Sústreďuje sa reprezentáciu štrukturálnych metadát do formy prepojených údajov, akými je napr. Centrálny model údajov verejnej správy, tj. množina ontológií údajov v základných registroch spolu so všetkými číselníkmi reprezentovanými ako sémantickými hierarchickými taxonómiami. Uvedená množina prepojených metadát predstavuje tzv. základný znalostný graf (Knowledge Graph) údajov verejnej správy.</div>"));
        
        }
    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid4-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid4);
    }

    private void refreshGrid() {
        grid4.select(null);
        grid4.getDataProvider().refreshAll();
    }
}
