package com.infoworks.lab.components.ui;

import com.infoworks.lab.domain.repository.AuthRepository;
import com.infoworks.lab.layouts.RoutePath;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * The Login view contains a button and a click listener.
 */
@Route(value = "")
@Theme(Lumo.class)
@Viewport("width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover")
public class LoginView extends VerticalLayout {

    private LoginForm login ;

    public LoginView() {
        loginWindowInit();
        initListeners();
        add(login);
        setSizeFull();
        setHorizontalComponentAlignment(Alignment.CENTER ,login);
    }

    private void loginWindowInit () {
        this.login = new LoginForm();
        this.login.setForgotPasswordButtonVisible(false);
    }

    private void initListeners(){
        this.login.addLoginListener(new ComponentEventListener<AbstractLogin.LoginEvent>() {
            @Override
            public void onComponentEvent(AbstractLogin.LoginEvent loginEvent) {
                new AuthRepository().login(loginEvent.getUsername() , loginEvent.getPassword(), (isSuccess, error) -> {
                    if(isSuccess){
                        UI.getCurrent().navigate(RoutePath.PROFILE_VIEW);
                    }else {
                        loginEvent.getSource().setError(true);
                    }
                });
            }
        });
    }


}
