package com.infoworks.lab.app.components.ui;

import com.infoworks.lab.app.layouts.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = AppLayout.class)
public class LiveLocation extends Div {
    public static final String ROUTE_NAME="";
    Span span = new Span(" hello ");
    Span span2 = new Span("Live Location");

    LiveLocation(){
       addClassName("main-content");
       add(span , span2);
    }
}
