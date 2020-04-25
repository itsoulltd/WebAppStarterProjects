package com.infoworks.lab.config;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;

public class AppServiceInitListener implements VaadinServiceInitListener  {
    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent) {
        System.out.println("serviceInit: init");
    }

    private static class SecurityListener implements BeforeEnterListener {
        @Override
        public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
            final UI ui = UI.getCurrent();
            final VaadinSession vaadinSession = ui.getSession();
            System.out.println("VaadinSession: init");
        }
    }
}
