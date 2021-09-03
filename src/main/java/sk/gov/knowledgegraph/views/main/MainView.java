package sk.gov.knowledgegraph.views.main;

import java.util.Optional;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import sk.gov.knowledgegraph.views.main.MainView;
import sk.gov.knowledgegraph.views.search.SearchView;
import sk.gov.knowledgegraph.views.data.DataView;
import sk.gov.knowledgegraph.views.resource.ResourceView;
import sk.gov.knowledgegraph.views.about.AboutView;

/**
 * The main view is a top-level placeholder for other views.
 */
@CssImport(value = "./styles/views/main/main-view.css", themeFor = "vaadin-app-layout")
@CssImport("./styles/views/main/main-view.css")
@PWA(name = "SKKnowledgeGraph", shortName = "SKKnowledgeGraph", enableInstallPrompt = false)
@JsModule("./styles/shared-styles.js")
public class MainView extends AppLayout {

    private final Tabs menu;

    public MainView() {
        HorizontalLayout header = createHeader();
        menu = createMenuTabs();
        addToNavbar(createTopBar(header, menu));
    }

    private VerticalLayout createTopBar(HorizontalLayout header, Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.getThemeList().add("dark");
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(header, menu);
        return layout;
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setId("header");
        
        header.add(new Html("<table width=\"100%\"><tr><td align=left width=\"50%\"><a href=\"search\"><img height=\"35px\" src=\"images/logo.png\"></a>&nbsp;<font size=\"6\">znalosti.gov.sk</font></td><td align=right><a target=new href=\"http://datalab.digital\"><img height=\"50px\" src=\"images/dtlb.png\"></a></td></tr></table"));

        
       // header.add(new Html("<div><a href=\"search\"><img height=\"50px\" src=\"images/logo.png\"/>&nbsp;</div"));
        
               
       
     //   Image dtlb = new Image("images/dtlb.png", "Dátová kancelária");
     //   dtlb.setId("dtlb");

        
       // Avatar avatar = new Avatar();
       // avatar.setId("avatar");
     //   header.add(new H1("znalosti.gov.sk"));
//        header.add(dtlb);

       // header.add(avatar);
        
        
        return header;
    }

    private static Tabs createMenuTabs() {
        final Tabs tabs = new Tabs();
        tabs.getStyle().set("max-width", "100%");
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private static Tab[] getAvailableTabs() {
        //return new Tab[]{createTab("Search", SearchView.class), createTab("Data", DataView.class),
        //        createTab("Resource", ResourceView.class), createTab("About", AboutView.class)};

        return new Tab[]{createTab("Hľadaj", SearchView.class), createTab("Datasety", DataView.class),
            createTab("Informácie", AboutView.class)};
}

    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }
}
