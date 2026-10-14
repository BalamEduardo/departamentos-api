# Departamentos API

API REST para administrar propiedades, habitaciones, clientes, reservas y pagos de alquiler. Incluye consultas de disponibilidad y reportes de ocupación, ingresos y pagos vencidos.

Proyecto de la asignatura. La carpeta y el nombre interno de la aplicación son `apartamentos`. No se implementa inicio de sesión.

## Tecnologías

- Java 21
- Spring Boot 4.1.1
- Maven
- Spring Data JPA
- MySQL
- HTML, CSS y JavaScript para la interfaz de prueba
- H2 para ejecución local opcional

## Instalación y ejecución

1. Instalar Java 21 y MySQL e iniciar el servicio de MySQL.
2. Clonar el repositorio y entrar en la carpeta:

```bash
git clone https://github.com/BalamEduardo/departamentos-api.git
cd departamentos-api
```

3. Crear la base de datos en MySQL:

```sql
CREATE DATABASE IF NOT EXISTS apartamentos_blue;
```

4. Configurar la conexión en la terminal. Ejemplo en PowerShell; sustituir los valores por los de tu instalación:

```powershell
$env:MYSQL_URL = "jdbc:mysql://localhost:3306/apartamentos_blue"
$env:MYSQL_USER = "root"
$env:MYSQL_PASSWORD = "tu_password"
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
export MYSQL_URL='jdbc:mysql://localhost:3306/apartamentos_blue'
export MYSQL_USER='root'
export MYSQL_PASSWORD='tu_password'
chmod +x mvnw
./mvnw spring-boot:run
```

La API se ejecuta en `http://localhost:8081`. La interfaz está disponible en esa misma dirección. JPA crea o actualiza las tablas al iniciar mediante `ddl-auto=update`; no se necesita importar datos de prueba.

Para ejecutar sin MySQL usando la base local H2:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

## Variables de entorno

| Variable | Uso | Valor por defecto |
| --- | --- | --- |
| `MYSQL_URL` | Dirección de la base MySQL | `jdbc:mysql://localhost:3306/apartamentos_blue` |
| `MYSQL_USER` | Usuario MySQL | `root` |
| `MYSQL_PASSWORD` | Contraseña MySQL | Vacía; configurar si tu usuario tiene contraseña |
| `PORT` | Puerto de la aplicación | `8081` |

Las credenciales reales se configuran en la terminal y no se incluyen en el repositorio.

## Endpoints

Todas las rutas de esta tabla parten de `http://localhost:8081`.

| Método | Ruta | Descripción |
| --- | --- | --- |
| GET | `/api/clientes` | Listar clientes y propietarios |
| GET | `/api/clientes/{id}` | Consultar un cliente |
| POST | `/api/clientes` | Registrar un cliente o propietario |
| PUT | `/api/clientes/{id}` | Actualizar sus datos |
| DELETE | `/api/clientes/{id}` | Inactivar conservando su historial |
| PUT | `/api/clientes/{id}/activar` | Reactivar un cliente |
| GET | `/api/propiedades` | Listar propiedades; filtros opcionales `tipo` y `propietarioId` |
| GET | `/api/propiedades/{id}` | Consultar una propiedad |
| POST | `/api/propiedades` | Registrar una propiedad |
| PUT | `/api/propiedades/{id}` | Actualizar una propiedad |
| DELETE | `/api/propiedades/{id}` | Eliminar si sus relaciones lo permiten |
| GET | `/api/propiedades/{propiedadId}/habitaciones` | Listar habitaciones de la propiedad |
| POST | `/api/propiedades/{propiedadId}/habitaciones` | Registrar una habitación |
| GET | `/api/habitaciones/{id}` | Consultar una habitación |
| PUT | `/api/habitaciones/{id}` | Actualizar una habitación |
| DELETE | `/api/habitaciones/{id}` | Eliminar si sus relaciones lo permiten |
| GET | `/api/disponibilidades` | Listar intervalos; filtro opcional `propiedadId` |
| POST | `/api/disponibilidades` | Registrar bloqueo o tarifa especial |
| PUT | `/api/disponibilidades/{id}` | Actualizar un intervalo |
| DELETE | `/api/disponibilidades/{id}` | Eliminar un intervalo |
| GET | `/api/reservas` | Listar reservas |
| GET | `/api/reservas/{id}` | Consultar una reserva |
| POST | `/api/reservas` | Crear una reserva y calcular su total |
| PUT | `/api/reservas/{id}` | Actualizar una reserva |
| GET | `/api/reservas/disponibilidad` | Consultar fechas disponibles |
| PUT | `/api/reservas/{id}/confirmar` | Confirmar una reserva |
| PUT | `/api/reservas/{id}/cancelar` | Cancelar una reserva |
| GET | `/api/pagos` | Listar pagos |
| GET | `/api/pagos/{id}` | Consultar un pago |
| GET | `/api/reservas/{reservaId}/pagos` | Consultar pagos de una reserva |
| POST | `/api/reservas/{reservaId}/pagos` | Registrar un abono pendiente |
| PUT | `/api/pagos/{id}/marcar-pagado` | Marcar un abono como pagado |
| GET | `/api/reservas/{reservaId}/saldo` | Consultar saldo pendiente |
| GET | `/api/reportes/ocupacion` | Consultar reservas confirmadas en un periodo |
| GET | `/api/reportes/ingresos` | Consultar total cobrado en un periodo |
| GET | `/api/reportes/morosidad` | Consultar abonos pendientes vencidos |

La consulta de disponibilidad requiere `propiedadId`, `fechaInicio` y `fechaFin`; `habitacionId` es opcional. Los reportes aceptan `mes=AAAA-MM` o el par `fechaInicio` y `fechaFin`, además de `propiedadId` y `clienteId` opcionales. La fecha final queda excluida.

## Ejemplos en Postman

Para POST y PUT seleccionar **Body → raw → JSON**. Los IDs de estos ejemplos son ilustrativos: usar los que devuelva tu base de datos.

### Registrar propietario y arrendatario

`POST /api/clientes`

```json
{
  "tipo": "PROPIETARIO",
  "nombres": "Ana",
  "apellidos": "Perez",
  "email": "ana@example.test",
  "password": "ejemplo"
}
```

Para registrar un arrendatario usar `tipo: "CLIENTE"` y otro email. El modelo conserva un campo `password` obligatorio, aunque esta entrega no tiene login. Ese campo no se devuelve en las respuestas.

Respuesta HTTP 200; extracto:

```json
{
  "id": 1,
  "nombres": "Ana",
  "apellidos": "Perez",
  "email": "ana@example.test",
  "tipo": "PROPIETARIO",
  "estatus": "ACTIVO"
}
```

### Registrar propiedad

`POST /api/propiedades`

```json
{
  "propietario": { "id": 1 },
  "tipo": "DEPARTAMENTO",
  "titulo": "Departamento Centro",
  "descripcion": "Departamento amueblado",
  "direccion": "Calle Ejemplo 10",
  "ciudad": "Tuxtla",
  "estidadFederativa": "Chiapas",
  "codigo_postal": 29000,
  "pais": "Mexico",
  "latitud": 16.75,
  "longitud": -93.1,
  "precio_noche": 1000,
  "capacidad": 4,
  "amueblada": true,
  "num_estacionamientos": 1,
  "num_habitaciones": 1,
  "num_banos": 1,
  "metro_cuadrados": 60,
  "comodidades": "Wifi",
  "reglas": "Sin fumar",
  "estado": "DISPONIBLE"
}
```

El nombre `estidadFederativa` se escribe así en el modelo actual. La respuesta HTTP 200 incluye la propiedad registrada y su `id_propiedad`.

### Registrar habitación

`POST /api/propiedades/1/habitaciones`

```json
{
  "numero": "101",
  "tipo_habitacion": "DOBLE",
  "descripcion": "Habitacion amueblada",
  "capacidad": 2,
  "num_camas": 2,
  "bano_privado": true,
  "amueblada": true,
  "metro_cuadrados": 20,
  "nivel": 1,
  "precio_noche": 400,
  "comodidades": "Wifi",
  "estado": "DISPONIBLE"
}
```

La respuesta HTTP 200 incluye la habitación y su `id_habitacion`.

### Crear reserva

`POST /api/reservas`

```json
{
  "arrendatario": { "id": 2 },
  "propiedad": { "id_propiedad": 1 },
  "habitacion": { "id_habitacion": 1 },
  "fecha_inicio": "2027-02-01",
  "fecha_fin": "2027-02-04",
  "num_personas": 2
}
```

Para alquilar toda la propiedad omitir `habitacion`. Con una habitación de 400 por noche y sin tarifa especial, el total de tres noches es 1200.

Respuesta HTTP 200; extracto:

```json
{
  "id_reserva": 1,
  "fecha_inicio": "2027-02-01",
  "fecha_fin": "2027-02-04",
  "num_personas": 2,
  "monto_total": 1200,
  "estado": "PENDIENTE"
}
```

### Registrar y cobrar un abono

`POST /api/reservas/1/pagos`

```json
{
  "monto": 500,
  "fecha_vencimiento": "2027-02-01",
  "metodo": "TRANSFERENCIA"
}
```

Respuesta HTTP 200; extracto:

```json
{
  "id_pago": 1,
  "monto": 500,
  "fecha_vencimiento": "2027-02-01",
  "metodo": "TRANSFERENCIA",
  "estado": "PENDIENTE"
}
```

Después enviar `PUT /api/pagos/1/marcar-pagado`, sin cuerpo. Para una reserva de 1200 con 500 pagados, `GET /api/reservas/1/saldo` devuelve:

```json
{ "saldo": 700 }
```

### Consultar disponibilidad y reportes

```text
GET /api/reservas/disponibilidad?propiedadId=1&habitacionId=1&fechaInicio=2027-02-01&fechaFin=2027-02-04
GET /api/reportes/ocupacion?mes=2027-02
GET /api/reportes/ingresos?mes=2027-02
GET /api/reportes/morosidad
```

La primera consulta devuelve HTTP 200:

```json
{ "disponible": false }
```

### Manejo de errores

La API devuelve 400 para datos inválidos, 404 para registros no encontrados en las rutas de consulta y 409 para conflictos como fechas ocupadas o relaciones que impiden eliminar un registro.

Ejemplo de una reserva que se cruza con otra; HTTP 409:

```json
{ "message": "Ya existe una reserva que ocupa esas fechas." }
```

## Comprobaciones realizadas

Se probaron propiedades, habitaciones, reservas, tarifas especiales, pagos, saldo y reportes con MySQL. También se verificaron rechazos por fechas ocupadas y datos inválidos. Para ejecutar la prueba automática del contexto con la conexión MySQL configurada:

```powershell
.\mvnw.cmd test
```

Esa prueba verifica el arranque de la aplicación; las operaciones de la API se comprueban mediante peticiones HTTP.
