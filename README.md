# GestorEventosDeportivos

## Descripción
Este es un sistema de gestión de eventos deportivos desarrollado en Java utilizando Servlets y DAOs. Permite la administración de eventos deportivos, la venta de entradas y la actualización de estados de los eventos.

## Tecnologías Utilizadas
- Java 23
- Jakarta Servlet API 6.1.0
- JSON Processing (org.json)
- Lombok
- Servidor Apache Tomcat

## Estructura del Proyecto
```
/eventosDeportivos
├── src/main/java/com/ces3/
│   ├── dao/               # Acceso a datos (DAO)
│   │   ├── EventoDAO.java
│   │   ├── EquipoDAO.java
│   │   └── UsuarioDAO.java
│   │   └── EstadoEvento.java  # Enumeración de estados de eventos
│   ├── servlet/           # Controladores Servlet
│   │   ├── EventoServlet.java
│   │   ├── EquipoServlet.java
│   │   └── UsuarioServlet.java
├── pom.xml                 # Configuración de dependencias y build
└── README.md               # Documentación
```

## DAO (Data Access Object)
Los DAOs permiten interactuar con la base de datos:
- `EventoDAO`: Gestiona eventos deportivos.
- `EquipoDAO`: Maneja los equipos participantes.
- `UsuarioDAO`: Controla la información de los usuarios.

## Servlets
Los servlets actúan como controladores para gestionar las peticiones HTTP.

## API REST
El sistema cuenta con un conjunto de APIs REST para la gestión de equipos, jugadores y eventos deportivos.

### **1. Equipos**
#### **Crear un equipo**
- **Método:** `POST`
- **URL:** `http://localhost:8081/eventosDeportivos_war_exploded/equipos`
- **Cuerpo (JSON o QueryParams):**
```json
{
    "nombre": "Los Titanes2",
    "deporte": "Fútbol",
    "ciudad": "Bogotá",
    "fechaFundacion": "2010-05-20",
    "logo": "https://th.bing.com/th/id/OIP.2IfXGd6AttRIIHZPLjKBlwHaHa?rs=1&pid=ImgDetMain"
}
```
#### **Obtener equipos**
- **Método:** `GET`
- **URL:** `http://localhost:8081/eventosDeportivos_war_exploded/equipos`
- **Respuesta obtenida:**
```json
[
    {
        "fechaFundacion": "2010-05-20",
        "deporte": "Fútbol",
        "ciudad": "Bogotá",
        "logo": "https://th.bing.com/th/id/OIP.2IfXGd6AttRIIHZPLjKBlwHaHa?rs=1&pid=ImgDetMain",
        "id": 1,
        "jugadores": [
            {
                "posicion": "Delantero",
                "apellido": "Messi",
                "id": 1,
                "nombre": "Lionel"
            }
        ],
        "nombre": "Los Titanes2"
    },
    {
        "fechaFundacion": "2010-05-20",
        "deporte": "Fútbol",
        "ciudad": "Bogotá",
        "logo": "https://th.bing.com/th/id/OIP.2IfXGd6AttRIIHZPLjKBlwHaHa?rs=1&pid=ImgDetMain",
        "id": 2,
        "jugadores": [],
        "nombre": "Los Titanes"
    }
]
```

### **2. Jugadores**
#### **Crear un jugador**
- **Método:** `POST`
- **URL:** `http://localhost:8081/eventosDeportivos_war_exploded/jugadores`
- **Cuerpo (JSON o QueryParams):**
```json
{
    "nombre": "Lionel",
    "apellido": "Messi",
    "fechaNacimiento": "1987-06-24",
    "nacionalidad": "Argentina",
    "posicion": "Delantero",
    "numero": 11,
    "equipoId": 1,
    "estadoActivo": true
}
```
#### **Obtener jugadores**
- **Método:** `GET`
- **URL:** `http://localhost:8081/eventosDeportivos_war_exploded/jugadores`
- **Respuesta obtenida:**
```json
{
    "posicion": "Delantero",
    "equipoId": 1,
    "estadoActivo": true,
    "equipoNombre": "Los Titanes2",
    "numero": 11,
    "fechaNacimiento": "1987-06-24",
    "apellido": "Messss3",
    "id": 1,
    "nombre": "Lioness2",
    "nacionalidad": "Argentina"
}
```

### **3. Eventos**
#### **Crear un evento**
- **Método:** `POST`
- **URL:** `http://localhost:8081/eventosDeportivos_war_exploded/eventos`
- **Cuerpo (JSON o QueryParams):**
```json
{
    "nombre": "Final Copa Libertadores",
    "fecha": "2025-11-30",
    "lugar": "Estadio Maracaná, Brasil",
    "deporte": "Fútbol",
    "equiposParticipantes": [1,2],
    "capacidad": 78000,
    "entradasVendidas": 50000,
    "estado": "PROGRAMADO"
}
```
#### **Obtener eventos**
- **Método:** `GET`
- **URL:** `http://localhost:8081/eventosDeportivos_war_exploded/eventos`
- **Respuesta obtenida:**
```json
{
    "fecha": "Sun Nov 30 00:00:00 GMT-05:00 2025",
    "estado": "EN_CURSO",
    "deporte": "Fútbol",
    "equiposParticipantes": [
        {
            "id": 1,
            "nombre": "Los Titanes"
        },
        {
            "id": 2,
            "nombre": "Los Titanes2"
        }
    ],
    "lugar": "Estadio Maracaná, Brasil",
    "id": 1,
    "entradasVendidas": 76000,
    "nombre": "Final Copa Libertadores",
    "capacidad": 78000
}
```

### **4. Actualizar Venta de Entradas**
- **Método:** `PUT`
- **URL:** `http://localhost:8081/eventosDeportivos_war_exploded/eventos/vender-entradas?eventoId=1&cantidad=13000`

### **5. Actualizar Estado de Entradas**
- **Método:** `PUT`
- **URL:** `http://localhost:8081/eventosDeportivos_war_exploded/eventos/actualizar-estado?eventoId=1&estado=EN_CURSO`

## Autores
- [Melany Suarez](https://github.com/Melany1227)


