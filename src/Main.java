import java.util.*;

// Clase base Producto
abstract class Producto {
    protected String nombre;
    protected double precio;
    protected String codigoBarras;

    public Producto(String nombre, double precio, String codigoBarras) {
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

    public Alimentacion(String nombre, double precio, String codigoBarras, int diasCaducidad) {
        super(nombre, precio, codigoBarras);
        this.diasCaducidad = diasCaducidad;
    }

    @Override
    public double calcularPrecio() {
        return precio - (precio * (1.0 / (diasCaducidad + 1))) + (precio * 0.1);
    }

    public String getDetalles() {
        return "Data de caducitat (dd/mm/aaaa): " + diasCaducidad;
    }
}

// Clase Textil
class Textil extends Producto {
    private String composicion;

    public Textil(String nombre, double precio, String codigoBarras, String composicion) {
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

    public Electronica(String nombre, double precio, String codigoBarras, int diasGarantia) {
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

    public void agregarProducto(Producto p) {
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
        System.out.println("\nSAPAMERCAT");
        System.out.println("-------------------------------");
        System.out.println("Data: " + new Date());
        System.out.println("-------------------------------");
        double total = 0;

        Map<String, Double> precios = new HashMap<>();
        for (Producto p : productos) {
            precios.put(p.getCodigoBarras(), p.calcularPrecio());
        }

        System.out.printf("%-10s %5s %10s %10s\n", "Nom", "Qty", "Preu", "Total");
        System.out.println("--------------------------------");
        for (String codigo : cantidades.keySet()) {
            Producto p = productos.stream().filter(prod -> prod.getCodigoBarras().equals(codigo)).findFirst().get();
            double precioUnitario = precios.get(codigo);
            int cantidad = cantidades.get(codigo);
            System.out.printf("%-10s %5d %10.2f %10.2f\n", p.getNombre(), cantidad, precioUnitario, cantidad * precioUnitario);
            total += cantidad * precioUnitario;
        }
        System.out.println("--------------------------------");
        System.out.printf("Total: %10.2f\n", total);
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
        }
        scanner.close();
    }
}
