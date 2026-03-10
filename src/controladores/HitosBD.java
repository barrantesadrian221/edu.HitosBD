package controladores;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Font;

public class HitosBD extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton botonEnvio; 
    private JTextField textField;
    private JTextField textField_1;
    private int intentos = 3; // Fuera del método para que "recuerde" los fallos
    private final int MAX_INT = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HitosBD frame = new HitosBD();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public HitosBD() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(171, 79, 86, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(171, 125, 86, 20);
		contentPane.add(textField_1);
		
		botonEnvio = new JButton("Enviar");
		botonEnvio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				validarAcceso();
			}
		});
		botonEnvio.setBounds(168, 167, 89, 23);
		contentPane.add(botonEnvio);
		
		JLabel lblNewLabel = new JLabel("LOGIN");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 28));
		lblNewLabel.setBounds(171, 11, 86, 29);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("USUARIO");
		lblNewLabel_1.setBounds(101, 81, 60, 17);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("CONTRASEÑA");
		lblNewLabel_1_1.setBounds(90, 127, 68, 17);
		contentPane.add(lblNewLabel_1_1);

	}
	/**
	 * Metodo que cumple el hito 1
	 */
	private void validarAcceso() {
		
		
		
		String sql = "SELECT * FROM usuarios WHERE nombre = ? AND password = ?";
		String user = textField.getText();
		String pass = textField_1.getText();
		
ConexionBD db = new ConexionBD();
        
        try (Connection con = db.getConnection()) {
            // 2. Preparamos la sentencia con los "?"
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);

            // 3. Ejecutamos la consulta
            java.sql.ResultSet resultado = ps.executeQuery();

            if (resultado.next()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Acceso concedido");
            
            
            LocalDateTime ahora = LocalDateTime.now();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String horaTexto = ahora.format(formato);
            
            
            
            String sqlLog = "INSERT INTO logins (usuario, fecha_acceso) VALUES (?, ?)";
            PreparedStatement psLog = con.prepareStatement(sqlLog);
            psLog.setString(1, user);
            psLog.setString(2, horaTexto);
            psLog.executeUpdate();
            
            
            
            PaginaPrincipal principal = new PaginaPrincipal(user, horaTexto);
            principal.setVisible(true);
            this.dispose();
            
            
            }else {
            	
            	--intentos;
            	System.out.println("Le quedan "+intentos+ " restantes");
            	if(intentos <= MAX_INT) {
            		System.out.println("maximo de numero de intentos alcanzado");
            		botonEnvio.setEnabled(false);
                    javax.swing.JOptionPane.showMessageDialog(this, "BLOQUEADO: Superado el límite de intentos.");
            	}
            	}
            
            }catch(java.sql.SQLException e ) {
            	javax.swing.JOptionPane.showMessageDialog(this, "error en la base de datos");
            }
		
	}
}
