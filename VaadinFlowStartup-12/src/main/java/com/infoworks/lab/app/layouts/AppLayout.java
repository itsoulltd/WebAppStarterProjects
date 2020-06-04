package com.infoworks.lab.app.layouts;

import com.infoworks.lab.app.components.component.SideBarButton;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.RouterLayout;

@Push
@StyleSheet("styles/dash_style.css")
public class AppLayout extends Div implements RouterLayout {

    Div header = new Div();
    Div logoContainer = new Div();
    H3 logo = new H3("Logo");
    Div headerContent = new Div();
    Div headerLeft = new Div();
    Div headerRight = new Div();
    Div userProfile = new Div();
    Div userAvatar = new Div();

    Icon userimage = new Icon(VaadinIcon.USER);
    Button addYourStoreLocationButton = new Button("Add your Store Location");
    Button createOrderButton = new Button("Create Order");
    Button overViewButton = new Button();
    SideBarButton passengerListButton = new SideBarButton("Passenger List" );
    SideBarButton liveLocation = new SideBarButton("Live location" );
    SideBarButton paymentButton = new SideBarButton("Payment");
    SideBarButton settingsButton = new SideBarButton("Settings");
    SideBarButton logoutButton = new SideBarButton("Logout");

    //SideBar
    Div sideBar = new Div();
    Div nav = new Div();
    Div navBottom = new Div();


    //USER NAME RATING
    Div userNameRating = new Div();
    Div userName = new Div();
    Div userRating = new Div();
    Span uName = new Span("Sayed");
    Paragraph rating = new Paragraph("4.8");
    Icon star = new Icon(VaadinIcon.STAR);

    //NOTIFICATION
    Div notification = new Div();
    Icon notificationIcon = new Icon(VaadinIcon.BELL);
    Span notificationCounter = new Span("12");

    //NOTIFICATION CONTEXT MENU
    ContextMenu notificationContextMenu = new ContextMenu();

    void createLogoContainer() {
        logoContainer.addClassName("logo-container");
        logo.addClassName("logo");
        logoContainer.add(logo);
    }

    void createHeaderContent() {
        //header
        header.addClassName("header");
        headerContent.addClassName("header-content");
        //HEADER LEFT
        headerLeft.addClassName("header-left");
        addYourStoreLocationButton.addClassName("button");
        addYourStoreLocationButton.addClassName("button-light");
        addYourStoreLocationButton.addClassName("button-rounded");
        headerLeft.add(addYourStoreLocationButton);

        //HEADER RIGHT
        headerRight.addClassName("header-right");
        createOrderButton.addClassName("button");
        createOrderButton.addClassName("button-dark");
        createOrderButton.addClassName("button-radius");

        //UserNameRating
        userProfile.addClassName("user-profile");
        userAvatar.addClassName("user-avatar");
        userNameRating.addClassName("user-name-rating");
        userName.addClassName("user-name");
        userRating.addClassName("user-rating");
        userAvatar.add(userimage);
        userNameRating.add(userName);
        userNameRating.add(userRating);
        userName.add(uName);
        userRating.add(star, rating);
        userProfile.add(userAvatar, userNameRating);

        //Notification
        notification.addClassName("notification-container");
        notificationCounter.addClassName("notification-counter");
        notification.add(notificationIcon, notificationCounter);

        //Add everything to headerRight
        headerRight.add(createOrderButton, userProfile, notification);
        headerContent.add(headerLeft, headerRight);

        //add to header
        header.add(logoContainer, headerContent);
    }

    void createSideBar() {
        sideBar.addClassName("sidebar");
        nav.addClassName("nav");
        navBottom.addClassName("nav");
        navBottom.addClassName("nav-bottom");
        overViewButton.addClassName("button");
        overViewButton.setText("OverView");
        nav.add(overViewButton, passengerListButton, liveLocation, paymentButton, settingsButton);
        navBottom.add(logoutButton);
        sideBar.add(nav, navBottom);

    }

    void sideBarButtonListener() {
        paymentButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                UI.getCurrent().navigate(RoutePath.PAYMENT_VIEW);
            }
        });
        passengerListButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                UI.getCurrent().navigate(RoutePath.PASSENGERS_CRUD_VIEW);
            }
        });
        liveLocation.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                UI.getCurrent().navigate(RoutePath.LIVE_VIEW);
            }
        });

    }

    void createNotificationContextMenu () {
        notificationContextMenu.setTarget(notificationIcon);
        notificationContextMenu.addItem("this is first one " , menuItemClickEvent -> {});
        notificationContextMenu.addItem("this is Second one " , menuItemClickEvent -> {});
        notificationContextMenu.addItem("this is Third one " , menuItemClickEvent -> {});
        notificationContextMenu.setOpenOnClick(true);

    }

    protected AppLayout() {
        addClassName("wrapper");
        createLogoContainer();
        createHeaderContent();
        createSideBar();
        createNotificationContextMenu();
        sideBarButtonListener();
        add(header, sideBar);
    }
}
