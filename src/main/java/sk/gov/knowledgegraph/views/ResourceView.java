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
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
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

    public ResourceView(@Autowired ResourceService resourceService) {
        this.resourceService = resourceService;

        setId("resource-view");
        addClassName("govuk-width-container");

        Main main = new Main();
        main.addClassName("govuk-main-wrapper");
        main.setId("main-content");
        add(main);

        Div whiteSpace = new Div();
        whiteSpace.addClassName("app-whitespace-highlight");
        main.add(whiteSpace);

        Div dataResults = new Div();
        dataResults.addClassName("govuk-grid-column-full");
        whiteSpace.add(dataResults);

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
        String prefLabel = "";
        String type = "";
        String typeLabel = "";

        Resource res = new Resource();

        try {
            res = resourceService.getBaseProperties(uriString);

            if (res.getPrefLabel() != null)
                prefLabel = res.getPrefLabel();

            if (res.getType() != null)
                type = res.getType();

            if (res.getTypeLabel() != null)
                typeLabel = "(" + res.getTypeLabel() + ")";

            String imageString = "";

            //   String jsonLDIconString =  "<a><img src=json-ld-data-32.png></a>";
            String jsonLDIconString = "<a target=new href=api/resource?uri=" + uriString + "&content-type="
                    + URLEncoder.encode("application/ld+json", StandardCharsets.UTF_8.toString()) + "><img src=images/json-ld-data-32.png valign=bottom></a>";

            if (prefLabel != "") {
                add(new Html("<div></br><table><tr><td><font color=DarkBlue size=5><b>znalosti o:</b></font><br><font size=5><b>" + prefLabel
                        + "</b></font>&nbsp;<a href=resource?uri=" + type + "><font size=5 color=gray>" + typeLabel
                        + "</font></a></td></tr><tr><td><font size=4 color=DarkBlue><b>" + uriString + " " + jsonLDIconString
                        + "</b></font>&nbsp;<img src=></td></tr></table></div>"));

            } else {
                if (type != "")
                    add(new Html(
                            "<div><font size=5 color=DarkBlue><b>&nbsp;&nbsp;znalosti o:&nbsp;</font>" + uriString + jsonLDIconString + "aaaaa" + "</div>"));
                else
                    add(new Html("<div><font size=5 color=DarkBlue><b>&nbsp;&nbsp;znalosti o:&nbsp;</font>" + uriString + "&nbsp;<a href=resource?uri=" + type
                            + "><font size=5 color=gray>" + typeLabel + "</font></a> " + jsonLDIconString + "</div>"));
            }

        } catch (Exception e) {
            add(e.toString());
        }

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setColumnReorderingAllowed(true);

        grid.setHeightByRows(true);

        //     grid.setHeightFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        //     grid.setVerticalScrollingEnabled(false);
        //      grid.setHeightByRows(true);        
        grid.removeAllColumns();

        try {

            add(new Html("<br>"));

            grid.setItems(resourceService.describeUriBySelect(uriString));
            grid.setHeightByRows(true);

            //  grid.setWidth("80%");

            // grid.getColumns().forEach(col -> col.setAutoWidth(true));           

            grid.addColumn(new ComponentRenderer<>(resource -> {

                if (resource.getIsInverse().contains("false")) {
                    return new Html("<div><b><a href=resource?uri=" + resource.getPredicate().replace("#", "%23") + ">" + resource.getPredicateShort()
                            + "</b></a>" + resource.getResourceIcon(resource.getPredicateShort()));

                    //Image logo = new Image("images/logo.png", "SKKnowledgeGraph logo");
                    //  return new Image("dede.jpg", "resource");

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
                    //   return new Image(resource.getObject(), resource.getObject());
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

            grid.addColumn(TemplateRenderer.<Resource> of("<a href=resource?uri=[[item.graph]]>[[item.graphName]]</a>")
                    .withProperty("graph", Resource::getGraph).withProperty("graphName", Resource::getGraphName)).setWidth("20%")
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

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            add(e.toString());
        }
    }

}
