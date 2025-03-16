# Sistema de Gestión de Carrito de Compras

Este proyecto es un sistema básico de carrito de compras que permite agregar productos al carrito, generar un tiquet de compra y guardar el tiquet en un archivo de texto.

## Características

- Gestión de productos de diferentes tipos: Alimentación, Textil, y Electrónica.
- Límite de productos en el carrito.
- Excepciones personalizadas para:
  - Superar el límite de productos en el carrito.
  - Manejar precios negativos en los productos.
- Generación de un tiquet de compra en formato texto.
- Detalles adicionales para cada tipo de producto:
  - **Alimentación**: Días de caducidad.
  - **Textil**: Composición del producto.
  - **Electrónica**: Días de garantía.

## Estructura del Código

### Excepciones Personalizadas
1. **LimitProductesException**: Se lanza cuando se excede el límite máximo de productos en el carrito.
2. **NegatiuException**: Se lanza cuando el precio de un producto es negativo.

### Clases de Productos
- **Producto**: Clase abstracta que define la estructura básica de un producto.
- **Alimentacion**: Representa productos de alimentación y calcula su precio en función de la caducidad.
- **Textil**: Representa productos textiles y no altera el precio.
- **Electronica**: Representa productos electrónicos y ajusta el precio según los días de garantía.

### Carrito de Compra
La clase **CarritoCompra** permite:
- Agregar productos al carrito (con validación del límite de productos).
- Mostrar los productos en el carrito.
- Generar un tiquet de compra con los detalles de los productos y su precio total.

## Instrucciones

### Requisitos
- **Java 8 o superior**.

### Uso
1. Ejecuta el programa y elige las opciones a través del menú:
   - Opción 1: Agregar un producto al carrito.
   - Opción 2: Generar el tiquet de compra.
   - Opción 3: Mostrar el contenido del carrito.
   - Opción 0: Salir del programa.

### Ejemplo de Interacción

```text
BENVINGUT AL SAPAMERCAT
----------------------
--- INICI ---
----------------------
1) Introduir producte
2) Passar per caixa
3) Mostrar carret de compra
0) Acabar
Elige una opción: 1

----------------------
--- PRODUCTE ---
----------------------
1) Alimentació
2) Tèxtil
3) Electrònica
0) Tornar
Selecciona el tipus de producte: 1
Nom producte: Leche
Preu: 1.99
Codi de barres: 12345
Data de caducitat (dies): 15

Tiquet Generado (comanda.txt)

SAPAMERCAT
-------------------------------
Data: Thu Mar 16 14:45:10 CET 2025
-------------------------------
Nom        Qty        Preu     Total
--------------------------------------
Leche      1         1.99      1.99
--------------------------------------
Total:      1.99

