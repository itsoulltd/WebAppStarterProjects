package com.infoworks.lab.components.ui;

import com.infoworks.lab.layouts.DashboardLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route(value = "trends", layout = DashboardLayout.class)
public class TrendsView extends Composite<Div> {
    public TrendsView() {
        getContent().add(new Span("Trends"));
    }
}