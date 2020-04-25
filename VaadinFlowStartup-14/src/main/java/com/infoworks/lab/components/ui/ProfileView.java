package com.infoworks.lab.components.ui;

import com.infoworks.lab.layouts.DashboardLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route(value = "profile", layout = DashboardLayout.class)
public class ProfileView extends Composite<Div> {
    public ProfileView() {
        getContent().add(new Span("Profile"));
    }
}