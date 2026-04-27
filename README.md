# Votify README

- TODO Sprint 1
- [ ] Arreglar barra vertical izquierda
- [ ] CSS
- [ ] Barra busqueda en competiciones
- [ ] Actualizar securityConfig
- [ ] Arreglar modelo de base de datos para actualizacion/borrado

## Arquitectura y flujo request-response
La aplicación sigue una arquitectura en capas. Cada acción del usuario recorre el siguiente ciclo completo:

```
CLIENTE (Navegador)
│
└── interacción UI
▼
VIEW (Vaadin)
• CompetitionView
• LoginView
• RegisterView
• UserProfileView
• UserProjectsView
• VoteView
│
└── llamada a método
▼
SERVICE (Lógica de negocio)
• CompetitionService
• ProjectService
• UserService
• VoteService
│
└── consulta / persistencia
▼
REPOSITORY (Acceso a datos - JPA)
• CompetitionRepository
• ProjectRepository
• UserRepository
• VoteRepository
│
└── mapeo ORM
▼
ENTITY (Modelo de dominio)
• Usuario
• Competicion
• Proyecto
• Voto
```

Ejemplo de ciclo completo — un usuario vota un proyecto:

El usuario pulsa "Votar" en VoteView.
VoteView llama a VoteService.registerVote(userId, projectId).
VoteService valida que el usuario no haya votado ya ese proyecto en esa competición.
VoteService llama a VoteRepository.save(voto) para persistir el voto.
El repositorio mapea el objeto Voto a su tabla en la BD mediante JPA.
El resultado sube de vuelta hasta VoteView, que actualiza el contador en pantalla.


## Estructura del proyecto



## Project Structure

This project has the following structure:

```
src
├── main/java
│   └── [application package]
│       ├── base
│       │   └── ui
│       │       ├── ViewToolbar.java
│       │       └── MainLayout.java
│       ├── entity
│       │   ├── Usuario.java
│       │   ├── Competicion.java
│       │   ├── Proyecto.java
│       │   ├── Voto.java
│       │   └──...
│       ├── repository
│       │   ├── UserRepository.java
│       │   ├── CompetitionRepository.java
│       │   ├── ProjectRepository.java
│       │   ├── VoteRepository.java
│       │   └──...
│       ├── service
│       │   ├── UserService.java
│       │   ├── CompetitionService.java
│       │   ├── ProjectService.java
│       │   ├──VoteService.java
│       │   └──...
│       ├── factory
│       │   └── [implementación de patrones de diseño]
│       ├── views
│       │   ├── LoginView.java
│       │   ├── RegisterView.java
│       │   ├── CompetitionView.java
│       │   ├── UserProfileView.java
│       │   ├── UserProjectsView.java
│       │   ├── VoteView.java
|       |   └──...
│       └── Application.java
├── main/resources
│   ├── META-INF/resources
│   │   └── styles.css
│   └── application.properties
└── test/java
    └── [application package]
        └── [tests unitarios e integración]             
```
El punto de entrada es Application.java, que contiene el método main() que arranca Spring Boot.

## Descripción de capas

entity/ — Clases que mapean las tablas de la base de datos mediante JPA/Hibernate.
repository/ — Interfaces de acceso a datos; extienden JpaRepository para operaciones CRUD automáticas.
service/ — Lógica de negocio; orquestan operaciones entre repositorios y aplican las reglas del dominio.
factory/ — Implementación de patrones de diseño (Factory y otros) para la creación de objetos complejos.
views/ — Frontend en Vaadin; cada clase es una pantalla con su lógica de presentación.

## Workflow

`entity -> repository -> service -> patrones -> views`

El workflow desado es: Primero la creación de la base de la clase entity y posteriormente su acceso en la base de
datos en repository, posteriormente implementar la lógica y posibles patrones en service y factory... y por último
el desarrollo de la UI en views.


- TODO Sprint 2
- [ ] Arreglar barra vertical izquierda
- [ ] CSS
- [ ] Barra busqueda en competiciones
- [ ] Actualizar securityConfig
- [ ] Arreglar modelo de base de datos para actualizacion/borrado

## Starting in Development Mode

To start the application in development mode, import it into your IDE and run the `Application` class. 
You can also start the application from the command line by running: 

```bash
./mvnw
```

## Building for Production

To build the application in production mode, run:

```bash
./mvnw package
```

To build a Docker image, run:

```bash
docker build -t my-application:latest .
```

If you use commercial components, pass the license key as a build secret:

```bash
docker build --secret id=proKey,src=$HOME/.vaadin/proKey .
```

## Next Steps

The [Building Apps](https://vaadin.com/docs/v25/building-apps) guides contain hands-on advice for adding features to 
your application.
