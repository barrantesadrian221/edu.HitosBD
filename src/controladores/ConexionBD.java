package controladores;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
   
//Variables importantes
   private static String driver="com.mysql.jdbc.Driver";
   private static String usuario="root";
   private static String password="Root";
   private static String url="jdbc:mysql://localhost:3306/ejemplo_conexion";
   private Connection con =null;

   public Connection getConnection() {
try {
con = DriverManager.getConnection(url, usuario, password);
System.out.println("Conectado a mysql, bienvenido impre");
} catch (SQLException e) {
System.out.println("Error de conexion: " + e.getMessage());
}
return con;
}

public void close() {
try {
if (con != null) con.close();
System.out.println("Conexion cerrada");
} catch (SQLException e) {
e.printStackTrace();
}
}

}