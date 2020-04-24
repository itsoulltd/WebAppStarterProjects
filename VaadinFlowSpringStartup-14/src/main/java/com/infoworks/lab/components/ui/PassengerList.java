package com.infoworks.lab.components.ui;


import com.infoworks.lab.layouts.AppLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route(value = "passengerList" , layout = AppLayout.class)
public class PassengerList extends MainContent {

    public static final String ROUTE_NAME="passengerList";
    Span span = new Span("this is OrderList page");

    public PassengerList(){
        add(span);
    }
}
