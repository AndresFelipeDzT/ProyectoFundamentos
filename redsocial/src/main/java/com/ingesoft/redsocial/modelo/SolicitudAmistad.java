package com.ingesoft.redsocial.modelo;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SolicitudAmistad {

    private Long id;
    private Boolean aceptado;
    private LocalDate fechaSolicitud;
    private LocalDate fechaRespuesta;

    private Usuario remitente;
    private Usuario destinatario;
}
