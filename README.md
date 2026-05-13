# Votify

Votify es una plataforma web desarrollada con **Spring Boot** y **Vaadin** que permite la gestión de competiciones y la votación de proyectos. El sistema ofrece un entorno seguro e intuitivo para que los usuarios puedan registrarse, participar en competiciones, explorar proyectos categorizados y emitir sus votos.

## Características Principales

- **Gestión de Usuarios:** Registro, inicio de sesión seguro y perfiles de usuario.
- **Competiciones y Proyectos:** Exploración de competiciones activas y los proyectos asociados a cada una.
- **Categorización y Filtrado:** Los proyectos pueden tener múltiples categorías asignadas, permitiendo filtrar de manera eficiente en la vista de votaciones.
- **Sistema de Votaciones:** Los usuarios pueden votar por sus proyectos favoritos dentro de una competición de manera sencilla.
- **Interfaz Moderna y Responsiva:** UI construida con el sistema de diseño Vaadin Lumo, ofreciendo una experiencia fluida con tarjetas (cards), botones consistentes, iconos y diseño adaptable.

## Arquitectura y Flujo Request-Response

La aplicación sigue una arquitectura en capas. Cada acción del usuario recorre el siguiente ciclo completo:

```
CLIENTE (Navegador)
│
└── interacción UI
▼
VIEW (Vaadin - Frontend)
• CompetitionView
• LoginView
• RegisterView
• UserProfileView
• UserProjectsView
• VotingView
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
• Categoria
```

Ejemplo de ciclo completo — un usuario vota un proyecto:

1. El usuario pulsa "Votar" en `VotingView`.
2. `VotingView` llama a `VoteService.registerVote(userId, projectId)`.
3. `VoteService` valida que el usuario no haya votado ya ese proyecto en esa competición.
4. `VoteService` llama a `VoteRepository.save(voto)` para persistir el voto.
5. El repositorio mapea el objeto `Voto` a su tabla en la BD mediante JPA.
6. El resultado sube de vuelta hasta `VotingView`, que actualiza el contador o estado del botón en pantalla.

## Estructura del Proyecto

```text
src
├── main/java
│   └── com/microslop
│       ├── base
│       │   └── ui
│       │       ├── ViewToolbar.java
│       │       └── MainLayout.java
│       ├── entity
│       │   ├── Usuario.java
│       │   ├── Competicion.java
│       │   ├── Proyecto.java
│       │   ├── Voto.java
│       │   ├── Categoria.java
│       │   └── ...
│       ├── repository
│       │   ├── UserRepository.java
│       │   ├── CompetitionRepository.java
│       │   ├── ProjectRepository.java
│       │   ├── VoteRepository.java
│       │   └── ...
│       ├── service
│       │   ├── UserService.java
│       │   ├── CompetitionService.java
│       │   ├── ProjectService.java
│       │   ├── VoteService.java
│       │   └── ...
│       ├── views
│       │   ├── LoginView.java
│       │   ├── RegisterView.java
│       │   ├── CompetitionView.java
│       │   ├── UserProfileView.java
│       │   ├── UserProjectsView.java
│       │   ├── VotingView.java
│       │   └── ...
│       └── Application.java
├── main/resources
│   ├── META-INF/resources
│   │   └── styles.css
│   └── application.properties
└── test/java
    └── ...             
```

El punto de entrada es `Application.java`, que contiene el método `main()` que arranca Spring Boot.

### Descripción de capas

- **`entity/`** — Clases que mapean las tablas de la base de datos mediante JPA/Hibernate.
- **`repository/`** — Interfaces de acceso a datos; extienden `JpaRepository` para operaciones CRUD automáticas.
- **`service/`** — Lógica de negocio; orquestan operaciones entre repositorios y aplican las reglas del dominio.
- **`views/`** — Frontend en Vaadin; cada clase es una pantalla con su lógica de presentación.

## Workflow de Desarrollo

`entity -> repository -> service -> views`

El flujo recomendado para añadir nuevas funcionalidades es:
1. Creación o modificación de la clase en `entity`.
2. Definición del acceso a base de datos en `repository`.
3. Implementación de la lógica de negocio en `service`.
4. Desarrollo de la interfaz gráfica y presentación en `views`.

## Ejecución y Despliegue

### Modo Desarrollo

Para arrancar la aplicación en modo desarrollo, puedes importarla en tu IDE y ejecutar la clase `Application.java`, o desde la terminal ejecutar:

```bash
./mvnw spring-boot:run
```

### Producción

Para compilar la aplicación para producción:

```bash
./mvnw package -Pproduction
```

Para construir una imagen Docker:

```bash
docker build -t votify-app:latest .
```
