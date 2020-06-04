package com.infoworks.lab.components.ui;

import com.infoworks.lab.domain.repository.AuthRepository;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

public class BaseComposite<D extends Div> extends Composite<D> implements BeforeEnterObserver {
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(UI.getCurrent().getSession().getAttribute(AuthRepository.TOKEN) == null) {
            beforeEnterEvent.rerouteTo("");
        }
    }
}
