package com.infoworks.lab.components.ui;

import com.infoworks.lab.layouts.RootAppLayout;
import com.infoworks.lab.layouts.RoutePath;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route(value = RoutePath.TRENDS_VIEW, layout = RootAppLayout.class)
public class TrendsView extends Composite<Div> {
    public TrendsView() {
        getContent().add(new Span("Trends"));
    }
}