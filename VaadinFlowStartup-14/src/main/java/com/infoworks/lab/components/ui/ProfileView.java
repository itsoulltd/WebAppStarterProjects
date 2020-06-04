package com.infoworks.lab.components.ui;

import com.infoworks.lab.layouts.RootAppLayout;
import com.infoworks.lab.layouts.RoutePath;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route(value = RoutePath.PROFILE_VIEW, layout = RootAppLayout.class)
public class ProfileView extends Composite<Div> {
    public ProfileView() {
        getContent().add(new Span("Profile"));
    }
}