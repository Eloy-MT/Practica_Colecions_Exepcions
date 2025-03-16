import java.util.*;
import java.io.*;

// Excepciones personalizadas
// Excepción que se lanza cuando se excede el límite de productos en el carrito
class LimitProductesException extends Exception {
    public LimitProductesException(String message) {
        super(message);
    }
}

// Excepción que se lanza cuando el precio de un producto es negativo
class NegatiuException extends Exception {
    public NegatiuException(String message) {
        super(message);
    }
}

// Clase base Producto
// Clase abstracta que define la estructura básica de un producto
abstract class Producto {
    protected String nombre;          // Nombre del producto
    protected double precio;          // Precio del producto
    protected String codigoBarras;    // Código de barras del producto

    // Constructor que valida que el precio no sea negativo
    public Producto(String nombre, double precio, String codigoBarras) throws NegatiuException {
        if (precio < 0) {
            throw new NegatiuException("El precio no puede ser negativo.");
        }
        this.nombre = nombre;
        this.precio = precio;
        this.codigoBarras = codigoBarras;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public String getNombre() {
        return nombre;
    }

    // Método abstracto para calcular el precio, a implementar en las subclases
    public abstract double calcularPrecio();
}

// Clase Alimentacion
// Representa productos de alimentación, hereda de Producto
class Alimentacion extends Producto {
    private int diasCaducidad;  // Días de caducidad del producto

    // Constructor que inicializa el producto de alimentación con su nombre, precio, código de barras y días de caducidad
    public Alimentacion(String nombre, double precio, String codigoBarras, int diasCaducidad) throws NegatiuException {
        super(nombre, precio, codigoBarras);
        this.diasCaducidad = diasCaducidad;
    }

    // Calcula el precio final del producto, teniendo en cuenta la caducidad
    @Override
    public double calcularPrecio() {
        return precio - (precio * (1.0 / (diasCaducidad + 1))) + (precio * 0.1);
    }

    // Devuelve detalles adicionales del producto
    public String getDetalles() {
        return "Data de caducitat (dies): " + diasCaducidad;
    }
}

// Clase Textil
// Representa productos textiles, hereda de Producto
class Textil extends Producto {
    private String composicion;  // Composición del producto textil

    // Constructor que inicializa el producto textil con su nombre, precio, código de barras y composición
    public Textil(String nombre, double precio, String codigoBarras, String composicion) throws NegatiuException {
        super(nombre, precio, codigoBarras);
        this.composicion = composicion;
    }

    // Calcula el precio final del producto, en este caso, no hay alteraciones, se mantiene igual
    @Override
    public double calcularPrecio() {
        return precio;
    }

    // Devuelve detalles adicionales sobre la composición del producto textil
    public String getDetalles() {
        return "Composició tèxtil: " + composicion;
    }
}

// Clase Electronica
// Representa productos electrónicos, hereda de Producto
class Electronica extends Producto {
    private int diasGarantia;  // Días de garantía del producto

    // Constructor que inicializa el producto electrónico con su nombre, precio, código de barras y días de garantía
    public Electronica(String nombre, double precio, String codigoBarras, int diasGarantia) throws NegatiuException {
        super(nombre, precio, codigoBarras);
        this.diasGarantia = diasGarantia;
    }

    // Calcula el precio final del producto, agregando un porcentaje adicional según la cantidad de días de garantía
    @Override
    public double calcularPrecio() {
        return precio + (precio * (diasGarantia / 365.0) * 0.1);
    }

    // Devuelve detalles adicionales sobre los días de garantía del producto electrónico
    public String getDetalles() {
        return "Dies de garantia: " + diasGarantia;
    }
}

// Clase principal que gestiona el carrito
// Clase que permite agregar productos al carrito, mostrar los productos y generar un tiquet
class CarritoCompra {
    private List<Producto> productos = new ArrayList<>();  // Lista de productos en el carrito
    private Map<String, Integer> cantidades = new HashMap<>();  // Mapa que mantiene el conteo de productos por código de barras
    private static final int MAX_PRODUCTOS = 10;  // Límite máximo de productos en el carrito

    // Método para agregar un producto al carrito
    // Lanza una excepción si se excede el límite de productos
    public void agregarProducto(Producto p) throws LimitProductesException {
        if (productos.size() >= MAX_PRODUCTOS) {
            throw new LimitProductesException("No se pueden agregar más de " + MAX_PRODUCTOS + " productos al carrito.");
        }
        productos.add(p);
        cantidades.put(p.getCodigoBarras(), cantidades.getOrDefault(p.getCodigoBarras(), 0) + 1);
    }

    // Método para mostrar los productos en el carrito
    public void mostrarCarrito() {
        System.out.println("\n------------------");
        System.out.println("-- CARRO DE COMPRA --");
        System.out.println("------------------");
        for (Producto p : productos) {
            System.out.println("Nom producte: " + p.getNombre());
            System.out.println("Quantitat: " + cantidades.get(p.getCodigoBarras()));
            // Muestra detalles específicos de cada tipo de producto (alimentación, textil, electrónica)
            System.out.println(p instanceof Alimentacion ? ((Alimentacion)p).getDetalles() : p instanceof Textil ? ((Textil)p).getDetalles() : ((Electronica)p).getDetalles());
            System.out.println("------------------");
        }
    }

    // Método para generar un tiquet de compra y guardarlo en un archivo "comanda.txt"
    public void generarTiquet() {
        double total = 0;
        Map<String, Double> precios = new HashMap<>();  // Mapa para almacenar los precios calculados de cada producto
        for (Producto p : productos) {
            precios.put(p.getCodigoBarras(), p.calcularPrecio());
        }

        // Genera un archivo de texto con los detalles de la compra
        try (PrintWriter writer = new PrintWriter(new FileWriter("comanda.txt"))) {
            writer.println("\nSAPAMERCAT");
            writer.println("-------------------------------");
            writer.println("Data: " + new Date());
            writer.println("-------------------------------");
            writer.printf("%-10s %5s %10s %10s\n", "Nom", "Qty", "Preu", "Total");
            writer.println("--------------------------------");

            // Escribe los detalles de cada producto en el tiquet
            for (String codigo : cantidades.keySet()) {
                Producto p = productos.stream().filter(prod -> prod.getCodigoBarras().equals(codigo)).findFirst().get();
                double precioUnitario = precios.get(codigo);
                int cantidad = cantidades.get(codigo);
                writer.printf("%-10s %5d %10.2f %10.2f\n", p.getNombre(), cantidad, precioUnitario, cantidad * precioUnitario);
                total += cantidad * precioUnitario;
            }

            writer.println("--------------------------------");
            writer.printf("Total: %10.2f\n", total);
            System.out.println("Tiquet generat i guardat a 'comanda.txt'");

        } catch (IOException e) {
            System.out.println("Error al guardar el tiquet: " + e.getMessage());
        }

        // Limpiar el carrito después de generar el tiquet
        productos.clear();
        cantidades.clear();
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CarritoCompra carrito = new CarritoCompra();  // Crear una instancia del carrito de compra
        boolean salir = false;

        // Bucle principal del programa que muestra el menú y gestiona las opciones del usuario
        while (!salir) {
            System.out.println("\nBENVINGUT AL SAPAMERCAT");
            System.out.println("----------------------");
            System.out.println("--- INICI ---");
            System.out.println("----------------------");
            System.out.println("1) Introduir producte");
            System.out.println("2) Passar per caixa");
            System.out.println("3) Mostrar carret de compra");
            System.out.println("0) Acabar");
            System.out.print("Elige una opción: ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        // Agregar un producto al carrito
                        System.out.println("----------------------");
                        System.out.println("--- PRODUCTE ---");
                        System.out.println("----------------------");
                        System.out.println("1) Alimentació\n2) Tèxtil\n3) Electrònica\n0) Tornar");
                        System.out.print("Selecciona el tipus de producte: ");
                        int tipo = scanner.nextInt();
                        scanner.nextLine();

                        if (tipo == 0) break;

                        System.out.print("Nom producte: ");
                        String nombre = scanner.nextLine();
                        System.out.print("Preu: ");
                        double precio = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.print("Codi de barres: ");
                        String codigo = scanner.nextLine();

                        if (tipo == 1) {
                            System.out.print("Data de caducitat (dies): ");
                            int caducidad = scanner.nextInt();
                            scanner.nextLine();
                            carrito.agregarProducto(new Alimentacion(nombre, precio, codigo, caducidad));
                        } else if (tipo == 2) {
                            System.out.print("Composició tèxtil: ");
                            String composicion = scanner.nextLine();
                            carrito.agregarProducto(new Textil(nombre, precio, codigo, composicion));
                        } else if (tipo == 3) {
                            System.out.print("Dies de garantia: ");
                            int garantia = scanner.nextInt();
                            scanner.nextLine();
                            carrito.agregarProducto(new Electronica(nombre, precio, codigo, garantia));
                        }
                        break;
                    case 2:
                        // Generar el tiquet de compra
                        carrito.generarTiquet();
                        break;
                    case 3:
                        // Mostrar los productos en el carrito
                        carrito.mostrarCarrito();
                        break;
                    case 0:
                        salir = true;  // Salir del bucle
                        break;
                    default:
                        System.out.println("Opció no vàlida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Entrada no válida. Intenta nuevamente.");
                scanner.nextLine();  // Limpiar el buffer de entrada
            } catch (LimitProductesException | NegatiuException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();  // Cerrar el scanner
    }
}
