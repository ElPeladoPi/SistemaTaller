# Sistema de Gestión de Taller Mecánico 🚗🔧

Este proyecto es una aplicación de escritorio desarrollada en **Java** para administrar los procesos operativos de un taller automotriz. El sistema está diseñado para gestionar el flujo de trabajo desde la recepción del vehículo hasta la facturación, pasando por el control de stock de repuestos.

## 📋 Características Principales

* **Gestión de Inventario:** Control de stock de repuestos (Altas, bajas y modificaciones).
* **Administración de Vehículos:** Registro de vehículos y vinculación con sus propietarios.
* **Historial de Servicios:** Seguimiento de las reparaciones y mantenimientos realizados a cada unidad.
* **Facturación:** Generación de órdenes de trabajo y facturas detalladas (`ItemFactura`).
* **Persistencia de Datos:** Conexión robusta a base de datos relacional.


## 🏗️ Arquitectura del Proyecto

El software sigue una arquitectura en capas para asegurar la escalabilidad y el mantenimiento, implementando el patrón de diseño **DAO (Data Access Object)** para desacoplar la lógica de negocio de la persistencia de datos.

### Estructura del Código (`src/com.taller`)

La estructura de paquetes está organizada de la siguiente manera:

* **`modelo`**: Contiene las entidades del dominio (POJOs) que mapean las tablas de la base de datos.
* **`dao`**: Capa de acceso a datos. Contiene las clases encargadas de realizar las operaciones CRUD.
* **`vista`**: Capa de presentación (Interfaz Gráfica).
* **`conexion`**: Gestiona la conexión a la base de datos MySQL.
* **`app`**: Punto de entrada de la aplicación (`Main`).

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java (JDK 17)
* **Base de Datos:** MySQL
* **IDE:** IntelliJ IDEA
* **Control de Versiones:** Git

## 👤 Autor

**Andrés Dario De Noni**
Estudiante de Ingeniería en Sistemas - UTN