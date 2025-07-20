# MyTiendita 🛒

**Sprint 4 - Proyecto académico - Universidad Nacional de Colombia**  
Aplicación móvil Android para administrar ventas, catálogo y análisis estadístico de una tienda de artículos deportivos: **MaxGear**  
**Slogan**: *"Tu cuerpo lo pide, tu mente lo exige"*

---

## Índice

1. [Descripción del Proyecto](#descripción-del-proyecto)  
2. [Objetivos](#objetivos)  
3. [Funcionalidades Generales](#funcionalidades-generales)  
4. [Entorno Operativo](#entorno-operativo)  
5. [Gestión de Usuarios y Autenticación](#gestión-de-usuarios-y-autenticación)  
6. [Lógica de Negocio y Flujo de Datos](#lógica-de-negocio-y-flujo-de-datos)  
7. [Interfaz Gráfica y Pantallas Principales](#interfaz-gráfica-y-pantallas-principales)  
8. [Clases Representativas del Proyecto](#clases-representativas-del-proyecto)  
9. [Módulo de Análisis Visual de Ventas](#módulo-de-análisis-visual-de-ventas)  
10. [Instalación y Despliegue](#instalación-y-despliegue)  
11. [Autores](#autores)  

---

## Descripción del Proyecto

**MyTiendita** es una aplicación móvil Android que permite gestionar una tienda en línea de artículos deportivos, facilitando procesos como autenticación de usuarios, visualización de productos, adición de ítems al carrito, compras, recargas de saldo y visualización de estadísticas de ventas.

---

## Objetivos

- Automatizar el proceso de ventas para la tienda MaxGear.  
- Facilitar la gestión de inventario y productos en Firebase.  
- Permitir que los usuarios vean su saldo, compren productos y revisen sus compras.  
- Generar análisis visuales de las ventas para mejorar la toma de decisiones.

---

## Funcionalidades Generales

| Funcionalidad                  | Estado     |
|-------------------------------|------------|
| Registro de usuarios          | Implementada |
| Inicio de sesión              | Implementada |
| Catálogo de productos         | Implementada |
| Agregar al carrito            | Implementada |
| Carrito editable              | Implementada |
| Recarga de saldo              | Implementada |
| Registro de ventas            | Implementada |
| Gráficas de ventas            | Implementada |
| Roles diferenciados (admin)   | No implementado aún |
| Notificaciones push           | No implementado |

---

## Entorno Operativo

### Plataforma y herramientas

| Herramienta / Tecnología      | Rol en el proyecto                                |
|------------------------------|----------------------------------------------------|
| Android Studio               | IDE principal de desarrollo                        |
| Java                         | Lenguaje de programación                          |
| Firebase Authentication      | Login con email/contraseña                        |
| Firebase Realtime Database   | Almacenamiento de productos, carritos y ventas    |
| Firebase UI                  | Sincronización automática con RecyclerAdapter     |
| Gradle (KTS)                 | Gestión de dependencias                           |
| XML Layouts                  | Diseño de interfaces gráficas                     |
| Google Services Plugin       | Integración Firebase                              |

### Requisitos de ejecución

| Requisito              | Valor mínimo recomendado        |
|------------------------|---------------------------------|
| Sistema operativo      | Android 5.0 (API 21) o superior |
| RAM                    | 1 GB                            |
| Conectividad           | Obligatoria                     |
| Almacenamiento         | ~50 MB                          |

---

## Gestión de Usuarios y Autenticación

Cualquier usuario puede registrarse y obtener acceso. Se usa Firebase Authentication con email y contraseña.  
Al registrarse, se le asigna automáticamente el rol "cliente" y un saldo inicial de 0.  

Flujo:
- Registro → Nodo `usuarios` en Firebase → Email, rol, saldo  
- Login → Redirección a `MainActivity` si es exitoso  

---

## Lógica de Negocio y Flujo de Datos

Toda la lógica se centra en la sincronización con Firebase. Las rutas más importantes son:

- `/usuarios` → Información del usuario (saldo, rol, email)  
- `/productos` → Catálogo general  
- `/carritos/{uid}` → Carrito individual por usuario  
- `/ventas` → Registro histórico de ventas realizadas  

Cada compra:
1. Se valida el saldo.
2. Se registra la venta.
3. Se actualiza el saldo.
4. Se vacía el carrito.

---

## Interfaz Gráfica y Pantallas Principales

**Pantallas principales basadas en layouts XML:**

- `activity_login.xml` → Pantalla de acceso  
- `activity_register.xml` → Registro de nuevos usuarios  
- `activity_database.xml` → Catálogo y administración  
- `activity_carrito.xml` → Carrito de compras  
- `activity_checkout.xml` → Revisión y compra final  
- `activity_resumen_ventas.xml` → Gráficas y sugerencias  
- `activity_recargar.xml` → Recarga de saldo

> Espacios para imágenes de interfaz:  
> - Pantalla de inicio  
> - Vista de carrito  
> - Catálogo completo  
> - Checkout con saldo y resumen  
> - Estadísticas de ventas y gráficas  

---

## Clases Representativas del Proyecto

### `Producto.java`
Modelo base de productos. Define `nombre`, `precio`, y `stock`.

### `CarritoItem.java`
Modelo intermedio del carrito. Permite calcular totales y editar cantidades.

### `CheckoutActivity.java`
Lógica de compra completa: verificación de saldo, registro de ventas, limpieza del carrito.

### `Database.java`
Interfaz de visualización del catálogo. Muestra botones de agregar o modificar según el usuario.

### `ResumenVentasActivity.java`
Clase encargada de generar estadísticas a partir de las ventas usando MPAndroidChart.  
Calcula los productos más vendidos, los menos vendidos y ofrece sugerencias de descuentos y reabastecimiento.

---

## Módulo de Análisis Visual de Ventas

Este módulo permite analizar el comportamiento de ventas para una mejor toma de decisiones.

**Lo que ofrece:**
- Gráfica de barras con productos más vendidos.
- Gráfico de torta con distribución porcentual.
- Texto dinámico con sugerencias de descuento.
- Texto dinámico con sugerencias de reabastecimiento.

> Espacios sugeridos para imágenes:
> - `grafico_barras_ventas.png`
> - `grafico_torta_distribucion.png`
> - `sugerencias_promociones.png`

---

## Instalación y Despliegue

Pasos para probar la app:

1. Clonar el repositorio.
2. Abrir el proyecto en Android Studio (2023.1 o superior).
3. Asegurarse de tener configurado el archivo `google-services.json` para Firebase.
4. Ejecutar la app en un dispositivo o emulador Android 5.0+.
5. Crear una cuenta o iniciar sesión.

---

## Autores

- **Juan José Ríos Trujillo**  
- **Javier Esteban Moreno Garzón**  
- **Samuel Eduardo García Corredor**

**Docente:** Néstor Germán Bolívar Pulgarín  
**Universidad Nacional de Colombia**  
**Sprint 4**  
**Fecha de entrega:** Julio 2025  

---

