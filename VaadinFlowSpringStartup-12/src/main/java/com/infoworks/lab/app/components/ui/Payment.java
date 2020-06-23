package com.infoworks.lab.app.components.ui;

import com.infoworks.lab.app.layouts.AppLayout;
import com.infoworks.lab.app.layouts.RoutePath;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route(value = RoutePath.PAYMENT_VIEW, layout = AppLayout.class)
public class Payment extends MainContent {

    Span span = new Span("This is payment Class");

    public Payment(){
        add(span);
    }
}
