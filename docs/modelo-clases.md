# Modelo de Clases

## Diagrama de Clases

```plantuml
@startuml
skinparam nodesep 200
skinparam linetype ortho

class Usuario {
  - login: String
  - nombre: String
  - password: String
}

class SolicitudAmistad {
  - id: long
  - fechaSolicitud: LocalDate
  - aceptado: Boolean
  - fechaRespuesta: LocalDate
}

class Grupo {
  - id: long
  - nombreGrupo: String
}

class ParticipantesGrupo {
  - id: long
}

class PerfilAcademico {
  id: long
  carrera: String
  semestre: String
  habilidades: String
}

class Publicacion {
  id: long
  contenido: String
  fechaCreacion: LocalDateTime
}

class Comentario {
  id: long
  texto: String
  fecha: LocalDateTime
}

Usuario "1" -right- "*" SolicitudAmistad : "remitente"
Usuario "1" -right- "*" SolicitudAmistad : "destinatario"
Usuario "1" -down- "*" Grupo : "creador"
Usuario "1" -down- "*" ParticipantesGrupo : "participantes"
ParticipantesGrupo "*" -left- "1" Grupo : "grupo"
Usuario "1" -up- "1" PerfilAcademico : "tiene"
Usuario "1" -left- "*" Publicacion : "publica"
Usuario "1" -- "*" Comentario : "publica"
Publicacion "1" -- "*" Comentario : "contiene"


@enduml
```
