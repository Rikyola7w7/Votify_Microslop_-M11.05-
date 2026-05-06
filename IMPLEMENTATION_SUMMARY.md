# Implementación: Interfaz de Configuración de Competición

## Resumen de Cambios

Se ha implementado completamente la interfaz "Configurar Competición" según los requisitos especificados en la imagen de diseño.

## Archivos Modificados

### 1. `src/main/java/com/microslop/entity/Competition.java`
**Cambios realizados:**
- Agregados 5 nuevos campos de configuración:
  - `voterType`: Tipo de votante ("JUDGES" o "ALL")
  - `autoVote`: Habilitar/deshabilitar autovoto (booleano)
  - `maxVotesPerPerson`: Cantidad de votos permitidos por persona
  - `judgeWeightMultiplier`: Multiplicador de peso para votos de jueces
  - `standardUserWeightMultiplier`: Multiplicador de peso para votos de usuarios estándares

- Agregados getters y setters explícitos para cada nuevo campo
- Todos los campos tienen valores por defecto razonables

### 2. `src/main/java/com/microslop/views/ConfigureCompetitionView.java` (NUEVO)
**Funcionalidad implementada:**

#### Sección GENERAL
- Muestra fecha y hora de inicio (read-only)
- Muestra fecha y hora de final (read-only)

#### Sección PARTICIPACIÓN
- ComboBox "QUIÉN PUEDE VOTAR": Opciones "Jueces" o "Todos"
- ComboBox "AUTOVOTO": Opciones "ON" o "OFF"
- IntegerField "CANTIDAD DE VOTOS POR PERSONA": Campo numérico con mínimo de 1

#### Sección PONDERACIÓN DE VOTOS
- NumberField "Juez Senior: x": Multiplicador para votos de jueces (valor por defecto: 2.0)
- NumberField "Usuario Estándar: x": Multiplicador para votos de usuarios estándares (valor por defecto: 1.0)

#### Funcionalidad de Botones
- **Botón Guardar**: 
  - Actualiza los campos modificados en la competición
  - Muestra notificación de éxito
  - Redirige al panel de administrador después de 1.5 segundos
  
- **Botón Cancelar**:
  - Si no hay cambios: navega directamente atrás
  - Si hay cambios: muestra diálogo de confirmación preguntando si desea salir sin guardar

#### Validaciones
- Solo el creador de la competición puede configurarla
- Control de cambios para mostrar diálogo de confirmación en cancelación
- Validación de acceso y redireccionamiento automático

### 3. `src/main/java/com/microslop/views/AdminDashboardView.java`
**Cambios realizados:**
- Actualizado el botón "Configure Competition"
- Antes: Mostraba notificación "Functionality coming soon"
- Ahora: Navega a `configure-competition/{competitionId}`

### 4. `pom.xml`
**Cambios realizados:**
- Agregado plugin maven-compiler-plugin explícito
- Configuración de procesador de anotaciones para Lombok
- Esto resolvió problemas de generación automática de getters/setters

### 5. `lombok.config` (NUEVO)
Configuración adicional para Lombok:
```
config.stopBubbling = true
lombok.addLombokGeneratedAnnotation = true
```

## Acceso a la Funcionalidad

1. Usuario navega a su panel de administrador: `/{username}/competitions`
2. En cada competición de la cual es administrador, hace clic en botón "Configure Competition"
3. Se abre la vista de configuración en ruta: `/configure-competition/{competitionId}`
4. Modifica los campos deseados
5. Guarda los cambios o cancela con confirmación si hay cambios pendientes

## Especificación de Requisitos Implementados

✅ Acceso a través de botón en AdminDashboardView
✅ Campo GENERAL: Fechas de inicio y fin (read-only según especificación)
✅ Campo PARTICIPACIÓN:
  - Opciones de votantes: "Jueces" o "Todos"
  - Opción de autovoto: ON/OFF
  - Cantidad de votos por persona con opción de múltiples votos
✅ Campo PONDERACIÓN DE VOTOS:
  - PESO POR ROL con multiplicadores personalizables
  - Juez Senior (x)
  - Usuario Estándar (x)
✅ Botón Guardar: Actualiza los campos
✅ Botón Cancelar: Con alerta de confirmación si hay cambios

## Compilación

El proyecto compila exitosamente sin errores:
```bash
mvn clean compile -DskipTests
```

**Resultado:** BUILD SUCCESS

## Notas

- Todos los campos nuevos se guardan automáticamente en la base de datos a través del servicio CompetitionService
- Las migraciones de base de datos deberán ejecutarse para crear las nuevas columnas si el proyecto usa control de migraciones
- El componente es completamente funcional y listo para pruebas

## Próximos Pasos Opcionales

1. Crear migraciones de base de datos para las nuevas columnas
2. Actualizar el DTOde Competition si es necesario
3. Pruebas funcionales en el navegador
