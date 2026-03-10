package controladores;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PaginaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;
    private String usuarioActual;
    private JTextArea areaListaEmpleados; // La declaramos aquí para que todos los métodos la vean

    public PaginaPrincipal(String usuario, String hora) {
        this.usuarioActual = usuario;
        
        setTitle("Sistema de Gestión - Usuario: " + usuario);
        setSize(750, 650); // Un poco más de altura para que quepa todo
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- HITO 2: Crear el contenedor de pestañas ---
        JTabbedPane panelConPestanas = new JTabbedPane();

        // Añadimos las 3 pestañas llamando a sus métodos de creación
        panelConPestanas.addTab("Hito 2: Empleados", crearPestanaEmpleados());
        panelConPestanas.addTab("Hito 3: Productos", crearPestanaProductos());
        panelConPestanas.addTab("Hito 4: Análisis", crearPestanaAnalisis());

        add(panelConPestanas);
        
        // Al arrancar, cargamos la lista de empleados automáticamente
        obtenerDiezPrimerosEmpleados();
    }

    // ============================================================
    // MÉTODOS DE CREACIÓN DE INTERFAZ (LO QUE FALTABA)
    // ============================================================

    private JPanel crearPestanaEmpleados() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Parte superior: Lista de empleados (Visible para todos)
        areaListaEmpleados = new JTextArea(10, 40);
        areaListaEmpleados.setEditable(false);
        panelPrincipal.add(new JScrollPane(areaListaEmpleados), BorderLayout.CENTER);

        // Parte inferior: Solo si es ADMIN (Hito 2.2)
        if (usuarioActual.equalsIgnoreCase("admin")) {
            JPanel panelAdmin = new JPanel(new GridLayout(3, 2, 5, 5));
            panelAdmin.setBorder(BorderFactory.createTitledBorder("Opciones de Administrador"));

            JTextField campoNombreEmpleado = new JTextField();
            JTextField campoIDDepartamento = new JTextField();
            JButton botonAnadir = new JButton("Añadir Empleado");
            JButton botonEliminar = new JButton("Eliminar por Nombre");

            panelAdmin.add(new JLabel("Nombre Empleado:"));
            panelAdmin.add(campoNombreEmpleado);
            panelAdmin.add(new JLabel("ID Departamento:"));
            panelAdmin.add(campoIDDepartamento);
            panelAdmin.add(botonAnadir);
            panelAdmin.add(botonEliminar);

            // Acción Añadir
            botonAnadir.addActionListener(e -> {
                ejecutarConsultaSimple("INSERT INTO empleados (nombre_empleado, id_departamento) VALUES (?, ?)", 
                                       campoNombreEmpleado.getText(), campoIDDepartamento.getText());
            });

            // Acción Eliminar
            botonEliminar.addActionListener(e -> {
                ejecutarConsultaSimple("DELETE FROM empleados WHERE nombre_empleado = ?", 
                                       campoNombreEmpleado.getText(), null);
            });

            panelPrincipal.add(panelAdmin, BorderLayout.SOUTH);
        }
        return panelPrincipal;
    }

    private JPanel crearPestanaProductos() {
        JPanel panelProductos = new JPanel(new GridLayout(6, 2, 10, 10));
        panelProductos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField campoNombreProducto = new JTextField();
        JTextField campoPrecioProducto = new JTextField();
        JTextField campoCantidadStock = new JTextField();
        JTextField campoIDAlmacen = new JTextField();
        JButton botonGuardar = new JButton("Guardar Producto y Stock");
        JButton botonDescuento = new JButton("Aplicar Descuento Global");

        panelProductos.add(new JLabel("Nombre Producto:"));
        panelProductos.add(campoNombreProducto);
        panelProductos.add(new JLabel("Precio:"));
        panelProductos.add(campoPrecioProducto);
        panelProductos.add(new JLabel("Stock:"));
        panelProductos.add(campoCantidadStock);
        panelProductos.add(new JLabel("ID Almacén:"));
        panelProductos.add(campoIDAlmacen);
        panelProductos.add(botonGuardar);
        panelProductos.add(botonDescuento);

        // Lógica de validación Hito 3
        botonGuardar.addActionListener(e -> {
            try {
                double precio = Double.parseDouble(campoPrecioProducto.getText());
                int stock = Integer.parseInt(campoCantidadStock.getText());

                if (precio < 0 || stock < 0) {
                    JOptionPane.showMessageDialog(this, "No se permiten precios o stock negativos");
                    return;
                }
                guardarProductoYStock(campoNombreProducto.getText(), precio, stock, campoIDAlmacen.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Por favor, introduce números válidos");
            }
        });

        botonDescuento.addActionListener(e -> {
            String porcentajeStr = JOptionPane.showInputDialog("Introduce el % de descuento:");
            if (porcentajeStr != null) {
                aplicarDescuentoGlobal(Double.parseDouble(porcentajeStr));
            }
        });

        return panelProductos;
    }

    private JPanel crearPestanaAnalisis() {
        JPanel panelAnalisis = new JPanel(new FlowLayout());

        JTextField campoPais = new JTextField("España", 10);
        JButton botonConsultar = new JButton("Consultar por País");
        
        // Hito 4.1: Stock total
        JTextPane areaStockTotal = new JTextPane();
        areaStockTotal.setPreferredSize(new Dimension(500, 100));
        areaStockTotal.setEditable(false);

        // Hito 4.2: Almacén TOP
        JLabel etiquetaAlmacenTop = new JLabel("Almacén con más unidades: ---");
        etiquetaAlmacenTop.setPreferredSize(new Dimension(500, 30));

        // Hito 4.3: Almacenes Vacíos
        JTextArea areaVacios = new JTextArea(5, 45);
        areaVacios.setBorder(BorderFactory.createTitledBorder("Almacenes sin productos"));

        botonConsultar.addActionListener(e -> {
            obtenerInformacionHito4(campoPais.getText(), areaStockTotal, etiquetaAlmacenTop, areaVacios);
        });

        panelAnalisis.add(new JLabel("País a filtrar:"));
        panelAnalisis.add(campoPais);
        panelAnalisis.add(botonConsultar);
        panelAnalisis.add(new JScrollPane(areaStockTotal));
        panelAnalisis.add(etiquetaAlmacenTop);
        panelAnalisis.add(new JScrollPane(areaVacios));

        return panelAnalisis;
    }

    // ============================================================
    // LÓGICA DE BASE DE DATOS (TUS MÉTODOS MEJORADOS)
    // ============================================================

    private void obtenerDiezPrimerosEmpleados() {
        String consultaSql = "SELECT empleados.nombre_empleado, departamentos.nombre_departamento " +
                             "FROM empleados " +
                             "JOIN departamentos ON empleados.id_departamento = departamentos.id " +
                             "ORDER BY empleados.nombre_empleado ASC LIMIT 10";
        
        ConexionBD baseDeDatos = new ConexionBD();
        try (Connection conexionAbierta = baseDeDatos.getConnection();
             PreparedStatement sentenciaSql = conexionAbierta.prepareStatement(consultaSql);
             ResultSet resultadoConsulta = sentenciaSql.executeQuery()) {
            
            StringBuilder constructorDeTexto = new StringBuilder("--- LISTA 10 EMPLEADOS ---\n");
            while (resultadoConsulta.next()) {
                constructorDeTexto.append(resultadoConsulta.getString("nombre_empleado"))
                                  .append(" (").append(resultadoConsulta.getString("nombre_departamento")).append(")\n");
            }
            areaListaEmpleados.setText(constructorDeTexto.toString());
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void ejecutarConsultaSimple(String consultaSql, String primerParametro, String segundoParametro) {
        ConexionBD baseDeDatos = new ConexionBD();
        try (Connection conexionAbierta = baseDeDatos.getConnection(); 
             PreparedStatement sentenciaPreparada = conexionAbierta.prepareStatement(consultaSql)) {
            
            sentenciaPreparada.setString(1, primerParametro);
            if (segundoParametro != null) sentenciaPreparada.setInt(2, Integer.parseInt(segundoParametro));
            
            sentenciaPreparada.executeUpdate();
            obtenerDiezPrimerosEmpleados(); // Refrescar lista
            JOptionPane.showMessageDialog(this, "Operación realizada correctamente");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void guardarProductoYStock(String nombreProducto, double precioProducto, int cantidadStock, String idAlmacenDestino) {
        ConexionBD baseDeDatos = new ConexionBD();
        try (Connection conexionAbierta = baseDeDatos.getConnection()) {
            // Insertar producto
            String sqlProducto = "INSERT INTO productos (nombre_producto, precio_producto) VALUES (?, ?)";
            PreparedStatement sentenciaProd = conexionAbierta.prepareStatement(sqlProducto, Statement.RETURN_GENERATED_KEYS);
            sentenciaProd.setString(1, nombreProducto);
            sentenciaProd.setDouble(2, precioProducto);
            sentenciaProd.executeUpdate();

            ResultSet claves = sentenciaProd.getGeneratedKeys();
            if (claves.next()) {
                // Insertar stock
                String sqlStock = "INSERT INTO stock_productos (id_producto, id_almacen, cantidad_stock) VALUES (?, ?, ?)";
                PreparedStatement sentenciaStock = conexionAbierta.prepareStatement(sqlStock);
                sentenciaStock.setInt(1, claves.getInt(1));
                sentenciaStock.setInt(2, Integer.parseInt(idAlmacenDestino));
                sentenciaStock.setInt(3, cantidadStock);
                sentenciaStock.executeUpdate();
                JOptionPane.showMessageDialog(this, "Producto y Stock guardados");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void aplicarDescuentoGlobal(double porcentaje) {
        ConexionBD baseDeDatos = new ConexionBD();
        String sql = "UPDATE productos SET precio_producto = precio_producto - (precio_producto * ? / 100)";
        try (Connection conexionAbierta = baseDeDatos.getConnection(); 
             PreparedStatement sentencia = conexionAbierta.prepareStatement(sql)) {
            sentencia.setDouble(1, porcentaje);
            sentencia.executeUpdate();
            JOptionPane.showMessageDialog(this, "Descuento aplicado a todos los productos");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void obtenerInformacionHito4(String paisFiltro, JTextPane panelStockTotal, JLabel etiquetaAlmacenTop, JTextArea areaAlmacenesVacios) {
        ConexionBD baseDeDatos = new ConexionBD();
        try (Connection conexionAbierta = baseDeDatos.getConnection()) {
            
            // 1. Stock Total
            String sql1 = "SELECT productos.nombre_producto, SUM(stock_productos.cantidad_stock) AS total_unidades " +
                          "FROM productos JOIN stock_productos ON productos.id = stock_productos.id_producto " +
                          "JOIN almacenes ON stock_productos.id_almacen = almacenes.id " +
                          "WHERE almacenes.pais = ? GROUP BY productos.nombre_producto";
            PreparedStatement sent1 = conexionAbierta.prepareStatement(sql1);
            sent1.setString(1, paisFiltro);
            ResultSet res1 = sent1.executeQuery();
            StringBuilder sb1 = new StringBuilder();
            while(res1.next()) sb1.append(res1.getString("nombre_producto")).append(": ").append(res1.getInt("total_unidades")).append(" unidades\n");
            panelStockTotal.setText(sb1.toString());

           
            String sql2 = "SELECT almacenes.nombre_almacen FROM almacenes " +
                          "JOIN stock_productos ON almacenes.id = stock_productos.id_almacen " +
                          "WHERE almacenes.pais = ? GROUP BY almacenes.id ORDER BY SUM(stock_productos.cantidad_stock) DESC LIMIT 1";
            PreparedStatement sent2 = conexionAbierta.prepareStatement(sql2);
            sent2.setString(1, paisFiltro);
            ResultSet res2 = sent2.executeQuery();
            if(res2.next()) etiquetaAlmacenTop.setText("Almacén con más unidades: " + res2.getString("nombre_almacen"));

            // 3. Vacíos
            String sql3 = "SELECT nombre_almacen FROM almacenes " +
                          "LEFT JOIN stock_productos ON almacenes.id = stock_productos.id_almacen " +
                          "WHERE stock_productos.id_producto IS NULL";
            ResultSet res3 = conexionAbierta.createStatement().executeQuery(sql3);
            StringBuilder sb3 = new StringBuilder();
            while(res3.next()) sb3.append(res3.getString("nombre_almacen")).append("\n");
            areaAlmacenesVacios.setText(sb3.toString());

        } catch (SQLException e) { e.printStackTrace(); }
    }
}