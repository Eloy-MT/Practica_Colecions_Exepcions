import java.util.*;
import java.io.*;

// Excepciones personalizadas
class LimitProductesException extends Exception {
    public LimitProductesException(String message) {
        super(message);
    }
}

class NegatiuException extends Exception {
    public NegatiuException(String message) {
        super(message);
    }
}

// Clase base Producto
abstract class Producto {
    protected String nombre;
    protected double precio;
    protected String codigoBarras;

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

    public abstract double calcularPrecio();
}

// Clase Alimentacion
class Alimentacion extends Producto {
    private int diasCaducidad;

    public Alimentacion(String nombre, double precio, String codigoBarras, int diasCaducidad) throws NegatiuException {
        super(nombre, precio, codigoBarras);
        this.diasCaducidad = diasCaducidad;
    }

    @Override
    public double calcularPrecio() {
        return precio - (precio * (1.0 / (diasCaducidad + 1))) + (precio * 0.1);
    }

    public String getDetalles() {
        return "Data de caducitat (dies): " + diasCaducidad;
    }
}

// Clase Textil
class Textil extends Producto {
    private String composicion;

    public Textil(String nombre, double precio, String codigoBarras, String composicion) throws NegatiuException {
        super(nombre, precio, codigoBarras);
        this.composicion = composicion;
    }

    @Override
    public double calcularPrecio() {
        return precio;
    }

    public String getDetalles() {
        return "Composició tèxtil: " + composicion;
    }
}

// Clase Electronica
class Electronica extends Producto {
    private int diasGarantia;

    public Electronica(String nombre, double precio, String codigoBarras, int diasGarantia) throws NegatiuException {
        super(nombre, precio, codigoBarras);
        this.diasGarantia = diasGarantia;
    }

    @Override
    public double calcularPrecio() {
        return precio + (precio * (diasGarantia / 365.0) * 0.1);
    }

    public String getDetalles() {
        return "Dies de garantia: " + diasGarantia;
    }
}

// Clase principal que gestiona el carrito
class CarritoCompra {
    private List<Producto> productos = new ArrayList<>();
    private Map<String, Integer> cantidades = new HashMap<>();
    private static final int MAX_PRODUCTOS = 10; // Limitar a 10 productos por carrito

    public void agregarProducto(Producto p) throws LimitProductesException {
        if (productos.size() >= MAX_PRODUCTOS) {
            throw new LimitProductesException("No se pueden agregar más de " + MAX_PRODUCTOS + " productos al carrito.");
        }
        productos.add(p);
        cantidades.put(p.getCodigoBarras(), cantidades.getOrDefault(p.getCodigoBarras(), 0) + 1);
    }

    public void mostrarCarrito() {
        System.out.println("\n------------------");
        System.out.println("-- CARRO DE COMPRA --");
        System.out.println("------------------");
        for (Producto p : productos) {
            System.out.println("Nom producte: " + p.getNombre());
            System.out.println("Quantitat: " + cantidades.get(p.getCodigoBarras()));
            System.out.println(p instanceof Alimentacion ? ((Alimentacion)p).getDetalles() : p instanceof Textil ? ((Textil)p).getDetalles() : ((Electronica)p).getDetalles());
            System.out.println("------------------");
        }
    }

    public void generarTiquet() {
        double total = 0;
        Map<String, Double> precios = new HashMap<>();
        for (Producto p : productos) {
            precios.put(p.getCodigoBarras(), p.calcularPrecio());
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("comanda.txt"))) {
            writer.println("\nSAPAMERCAT");
            writer.println("-------------------------------");
            writer.println("Data: " + new Date());
            writer.println("-------------------------------");
            writer.printf("%-10s %5s %10s %10s\n", "Nom", "Qty", "Preu", "Total");
            writer.println("--------------------------------");

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

        productos.clear();
        cantidades.clear();
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CarritoCompra carrito = new CarritoCompra();
        boolean salir = false;

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
                        carrito.generarTiquet();
                        break;
                    case 3:
                        carrito.mostrarCarrito();
                        break;
                    case 0:
                        salir = true;
                        break;
                    default:
                        System.out.println("Opció no vàlida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Entrada no válida. Intenta nuevamente.");
                scanner.nextLine();
            } catch (LimitProductesException | NegatiuException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }
}