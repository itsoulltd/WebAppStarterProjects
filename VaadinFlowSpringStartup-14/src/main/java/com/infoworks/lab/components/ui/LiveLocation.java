package com.infoworks.lab.components.ui;

import com.infoworks.lab.layouts.AppLayout;
import com.infoworks.lab.layouts.RoutePath;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route(value = RoutePath.LIVE_VIEW, layout = AppLayout.class)
public class LiveLocation extends MainContent {

    Span span = new Span(" hello ");
    Span span2 = new Span("Live Location");

    public LiveLocation(){
       add(span , span2);
    }
}
