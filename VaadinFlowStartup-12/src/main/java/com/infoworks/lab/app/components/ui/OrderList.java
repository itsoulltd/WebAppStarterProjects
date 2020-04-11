package com.infoworks.lab.app.components.ui;


import com.infoworks.lab.app.layouts.AppLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route(value = "orderList" , layout = AppLayout.class)
public class OrderList extends MainContent {
    public static final String ROUTE_NAME="orderList";
    Span span = new Span("this is OrderList page");

    OrderList(){
        add(span);
    }
}
