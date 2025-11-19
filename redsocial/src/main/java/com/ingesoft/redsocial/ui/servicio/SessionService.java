package com.ingesoft.redsocial.ui.servicio;

import org.springframework.stereotype.Component;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;

@Component
@VaadinSessionScope
public class SessionService {

    private String loginEnSesion;

    public String getLoginEnSesion() {
        return loginEnSesion;
    }

    public void setLoginEnSesion(String loginEnSesion) {
        this.loginEnSesion = loginEnSesion;
    }

}
