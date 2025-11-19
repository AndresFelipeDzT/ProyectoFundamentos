package com.ingesoft.redsocial.ui.componentes;

import org.springframework.stereotype.Component;

import com.ingesoft.redsocial.ui.servicio.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;

@Component
@UIScope
public class TituloComponent extends VerticalLayout {

    // == Constructor
    // - Crea el componente

    public TituloComponent() {

        HorizontalLayout barraTitulo = new HorizontalLayout();
        setWidthFull();

        HorizontalLayout titulo = new HorizontalLayout();
        titulo.setWidthFull();
        titulo.add(new H1("Red Social"));
        barraTitulo.add(titulo);
        
        add(barraTitulo);
        add(new Hr());

    } 

}
