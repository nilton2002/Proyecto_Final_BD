import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import javax.imageio.ImageIO;

public class ProyectoBD {
    private JFrame frame;
    private JPanel bienvenidaPanel, principalPanel;
    private JTextField nombreField;
    private Connection conexion;

    // Constructor
    public ProyectoBD() {
        prepararVentana();
        conectarBaseDeDatos();
        mostrarBienvenida();
    }

    private void prepararVentana() {
        frame = new JFrame("Iximche");
        frame.setSize(1000, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Cargar la imagen del ícono y establecerla en la ventana
        try {
            Image icono = ImageIO.read(new File("imagenes/logo.png"));
            frame.setIconImage(icono);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void conectarBaseDeDatos() {
        String url = "jdbc:mysql://localhost:3306/bdiximche";
        String usuario = "root";
        String contraseña = "Niltin!17";

        try {
            conexion = DriverManager.getConnection(url, usuario, contraseña);
            JOptionPane.showMessageDialog(frame, "Conexión a la base de datos exitosa.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarBienvenida() {
        bienvenidaPanel = new JPanel();
        bienvenidaPanel.setLayout(new BoxLayout(bienvenidaPanel, BoxLayout.Y_AXIS));
        bienvenidaPanel.setBackground(Color.gray);

        ImageIcon originalLogo = new ImageIcon("imagenes/logo.png");
        Image logoImage = originalLogo.getImage().getScaledInstance(1200, 250, Image.SCALE_SMOOTH);
        ImageIcon scaledLogo = new ImageIcon(logoImage);
        JLabel logoLabel = new JLabel(scaledLogo);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tituloLabel = new JLabel("Cervecería IXIMCHE", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 70));
        tituloLabel.setForeground(Color.BLACK);
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton productosButton = new JButton("Administrar Productos");
        productosButton.setFont(new Font("Arial", Font.PLAIN, 20));
        productosButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        productosButton.addActionListener((ActionEvent e) -> mostrarPantallaProductos());

        bienvenidaPanel.add(Box.createVerticalStrut(50));
        bienvenidaPanel.add(logoLabel);
        bienvenidaPanel.add(Box.createVerticalStrut(20));
        bienvenidaPanel.add(tituloLabel);
        bienvenidaPanel.add(Box.createVerticalStrut(20));
        bienvenidaPanel.add(productosButton);

        frame.add(bienvenidaPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void mostrarPantallaProductos() {
        frame.remove(bienvenidaPanel);

        principalPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        principalPanel.setBackground(Color.lightGray);

        JLabel codigoLabel = new JLabel("Código:");
        JTextField codigoField = new JTextField();
        JLabel nombreLabel = new JLabel("Nombre:");
        JTextField nombreField = new JTextField();
        JLabel precioLabel = new JLabel("Precio:");
        JTextField precioField = new JTextField();
        JLabel cantidadLabel = new JLabel("Cantidad:");
        JTextField cantidadField = new JTextField();
        JLabel fechaLabel = new JLabel("Fecha de Vencimiento (YYYY-MM-DD):");
        JTextField fechaField = new JTextField();

        JButton insertarButton = new JButton("Insertar Producto");
        insertarButton.addActionListener(e -> {
            insertarProducto(
                codigoField.getText(),
                nombreField.getText(),
                Double.parseDouble(precioField.getText()),
                Integer.parseInt(cantidadField.getText()),
                fechaField.getText()
            );
        });

        JButton listarButton = new JButton("Listar Productos");
        listarButton.addActionListener(e -> listarProductos());

        principalPanel.add(codigoLabel);
        principalPanel.add(codigoField);
        principalPanel.add(nombreLabel);
        principalPanel.add(nombreField);
        principalPanel.add(precioLabel);
        principalPanel.add(precioField);
        principalPanel.add(cantidadLabel);
        principalPanel.add(cantidadField);
        principalPanel.add(fechaLabel);
        principalPanel.add(fechaField);
        principalPanel.add(insertarButton);
        principalPanel.add(listarButton);

        frame.add(principalPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void insertarProducto(String codigo, String nombre, double precio, int cantidad, String fecha) {
        String query = "INSERT INTO producto (codigoProducto, nombreProducto, precioUnitario, cantidadProducto, fechaVencimiento) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conexion.prepareStatement(query)) {
            pst.setString(1, codigo);
            pst.setString(2, nombre);
            pst.setDouble(3, precio);
            pst.setInt(4, cantidad);
            pst.setDate(5, java.sql.Date.valueOf(fecha));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Producto insertado correctamente");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error al insertar producto: " + e.getMessage());
        }
    }

    private void listarProductos() {
        String query = "SELECT * FROM producto";
        StringBuilder productos = new StringBuilder();
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                productos.append("Código: ").append(rs.getString("codigoProducto")).append("\n");
                productos.append("Nombre: ").append(rs.getString("nombreProducto")).append("\n");
                productos.append("Precio: ").append(rs.getDouble("precioUnitario")).append("\n");
                productos.append("Cantidad: ").append(rs.getInt("cantidadProducto")).append("\n");
                productos.append("Fecha de Vencimiento: ").append(rs.getDate("fechaVencimiento")).append("\n\n");
            }
            JOptionPane.showMessageDialog(frame, productos.length() > 0 ? productos.toString() : "No hay productos disponibles.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error al listar productos: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ProyectoBD();
    }
}
