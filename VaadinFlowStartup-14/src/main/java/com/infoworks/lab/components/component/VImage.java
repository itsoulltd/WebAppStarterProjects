package com.infoworks.lab.components.component;

import com.vaadin.flow.component.html.Image;

public class VImage extends Image {

    public static Image loadFromImages(String fileName, String altText){
        return new Image("img/" + fileName, altText);
    }

}
