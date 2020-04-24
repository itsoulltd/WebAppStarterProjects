package com.infoworks.lab.app.components.ui;

import com.infoworks.lab.app.layouts.AppLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route(value = "payment",layout = AppLayout.class)
public class Payment extends MainContent {

    public static final String ROUTE_NAME="payment";
    Span span = new Span("This is payment Class");

    public Payment(){
        add(span);
    }
}
