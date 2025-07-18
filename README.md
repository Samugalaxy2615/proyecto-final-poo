# 🛍 MyTiendita - MaxGear

**Sprint 4 - Proyecto Final de Programación Orientada a Objetos**  
**Universidad Nacional de Colombia**  
**Docente:** Néstor Germán Bolívar Pulgarín  
**Estudiantes:** Juan José Ríos Trujillo, Javier Esteban Moreno Garzón, Samuel Eduardo García Corredor  

---

##  Descripción General

**MyTiendita** es una aplicación Android nativa diseñada para gestionar la tienda deportiva **MaxGear**, bajo el lema:

> *"Tu cuerpo lo pide, tu mente lo exige."*

El sistema permite a los usuarios registrarse, consultar productos disponibles, recargar saldo virtual, realizar compras y visualizar su carrito. A su vez, ofrece a los administradores funciones de gestión de inventario y reportes visuales de ventas.

Desarrollado en **Java** sobre **Android Studio**, con integración de servicios en la nube mediante **Firebase**.

---

##  Funcionalidades Principales

- Registro e inicio de sesión con correo electrónico
- Visualización del catálogo de productos con stock en tiempo real
- Agregado de productos al carrito
- Cálculo automático del total a pagar
- Recarga de saldo virtual desde la app
- Proceso de compra completo con descuento automático de saldo
- Registro automático de ventas en Firebase
- Visualización gráfica de productos más y menos vendidos
- CRUD de productos accesible solo para usuarios con rol de administrador

---

##  Tecnologías y Herramientas Utilizadas

| Tecnología / Herramienta       | Rol en el proyecto                                      |
|-------------------------------|---------------------------------------------------------|
| Android Studio (2023.1+)      | Entorno de desarrollo integrado                         |
| Java                          | Lenguaje de programación principal                      |
| Firebase Authentication       | Manejo de usuarios y autenticación                      |
| Firebase Realtime Database    | Base de datos en tiempo real para usuarios y ventas     |
| Firebase UI (RecyclerAdapter) | Sincronización visual del catálogo y carrito            |
| MPAndroidChart                | Gráficos de barras y pastel para estadísticas de ventas |
| XML Layouts                   | Diseño visual de las pantallas                         |
| Gradle (Kotlin DSL)           | Compilación y gestión de dependencias                   |

---

##  Requisitos Técnicos

| Requisito                 | Detalle mínimo recomendado                        |
|--------------------------|---------------------------------------------------|
| Sistema operativo        | Android 5.0 (API 21) o superior                   |
| Conexión a Internet      | Obligatoria para autenticación y sincronización  |
| RAM del dispositivo      | 1 GB o más                                        |
| Almacenamiento disponible| ~50 MB                                            |
| Cuenta de usuario        | Correo electrónico válido registrado              |
| Permisos requeridos      | Solo acceso a Internet                           |

---

##  Estructura del Proyecto

```bash
com.example.mytiendita/
├── adapters/
│   └── ProductoViewHolder.java
├── models/
│   ├── Producto.java
│   └── CarritoItem.java
├── Login.java
├── Register.java
├── MainActivity.java
├── Database.java
├── AddProductActivity.java
├── EditProductActivity.java
├── CarritoActivity.java
├── CheckoutActivity.java
├── RecargarActivity.java
├── ResumenVentasActivity.java
├── layouts/
│   ├── activity_login.xml
│   ├── activity_register.xml
│   ├── activity_main.xml
│   ├── activity_database.xml
│   ├── activity_add_product.xml
│   ├── activity_edit_product.xml
│   ├── activity_carrito.xml
│   ├── activity_checkout.xml
│   ├── activity_recargar.xml
│   └── activity_resumen_ventas.xml
├── drawable/
│   └── iconos, fondos, etc.
└── values/
    └── strings.xml, colors.xml, themes.xml
