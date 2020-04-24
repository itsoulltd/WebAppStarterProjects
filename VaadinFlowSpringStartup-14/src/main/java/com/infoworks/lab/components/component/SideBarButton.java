package com.infoworks.lab.components.component;

import com.vaadin.flow.component.button.Button;

public class SideBarButton extends Button {

    public SideBarButton(){
        addClassName("button");
    }

    public SideBarButton(String text){
        this ();
        this.setText(text);
    }
}
