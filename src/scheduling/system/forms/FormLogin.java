package scheduling.system.forms;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import scheduling.system.M;
import scheduling.system.classes.User;

@SuppressWarnings("serial")
public class FormLogin extends JFrame {

	private JTextField txtUsername;
	private JPasswordField txtPassword;

	public static void main(String[] args) {
		M.setWindowsTheme();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormLogin login = new FormLogin();
					login.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public FormLogin() {

		setResizable(false);
		setTitle("User Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 270, 219);
		setLocationRelativeTo(null);
		
		getContentPane().setLayout(null);

		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				login();
			}
		});
		btnLogin.setBounds(32, 133, 196, 28);
		getContentPane().add(btnLogin);

		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(32, 38, 83, 14);
		getContentPane().add(lblUsername);

		txtUsername = new JTextField();
		txtUsername.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					login();
				}
			}
		});
		txtUsername.setBounds(32, 56, 196, 20);
		getContentPane().add(txtUsername);
		txtUsername.setColumns(10);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(32, 84, 83, 14);
		getContentPane().add(lblPassword);

		txtPassword = new JPasswordField();
		txtPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					login();
				}
			}
		});
		txtPassword.setBounds(32, 102, 197, 20);
		getContentPane().add(txtPassword);
	}

	@SuppressWarnings("deprecation")
	private void login() {
		if (txtUsername.getText().length() > 0
				&& txtPassword.getText().length() > 0) {
			User user = new User();
			user.username = txtUsername.getText();
			user.password = txtPassword.getText();
			if (user.authenticate() == true) {
				dispose();
				M.form_menu.setVisible(true);
			} else {
				clear();
			}
		} else {
			clear();
			M.messageBox("Please enter a username and a password.");
		}
	}
	
	private void clear(){
		JTextField txts[] = {txtUsername, txtPassword};
		for(JTextField t : txts){
			t.setText("");
		}
	}
}
