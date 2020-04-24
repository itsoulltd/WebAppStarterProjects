package com.infoworks.lab.components.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.server.PWA;

@PWA(name = "Time Tracking", shortName = "Ticker")
public abstract class MainContent extends Div {
   public MainContent(){
       addClassName("main-content");
   }
}
