package scheduling.system.forms;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import scheduling.system.M;
import scheduling.system.classes.User;

@SuppressWarnings("serial")
public class FormUsers extends JFrame implements ListSelectionListener {

	private static JTable table;
	public static DefaultTableModel model;

	private JScrollPane scrollPane;
	private JButton btnRefresh;
	private JLabel lblSearch;
	private JTextField txtSearch;
	private JLabel lblUserCount;
	private JTextField txtUsername;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JButton btnAdd;
	private JButton btnUpdate;
	private JButton btnDelete;
	private JPanel panel;
	private JPasswordField txtPassword;
	private JLabel lblId;
	private JTextField txtID;

	public static void main(String[] args) {
		M.setWindowsTheme();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormUsers main = new FormUsers();
					main.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FormUsers() {
		getContentPane().setLayout(null);
		initialize();
		load();
	}

	@SuppressWarnings({})
	private void initialize() {
		setTitle("Users");
		setBounds(100, 100, 644, 335);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(13, 49, 386, 204);
		getContentPane().add(scrollPane);

		table = new JTable() {
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
		};
		table.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				lblUserCount.setText("Users: " + table.getRowCount());
			}
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int userID = Integer.parseInt(table.getValueAt(
							table.getSelectedRow(), 0).toString());
					ArrayList<User> users = User.get("SELECT * FROM "
							+ M.TABLE_USERS + " WHERE " + M.USER_ID + "="
							+ userID);
					User user = users.get(0);
					txtID.setText(user.id + "");
					txtUsername.setText(user.username);
					txtPassword.setText(user.password);
				}
			}
		});

		table.getSelectionModel().addListSelectionListener(this);

		scrollPane.setViewportView(table);

		model = new DefaultTableModel(new Object[][] {}, new String[] { "ID",
				"Username", "Password" });

		table.setModel(model);
		table.getColumnModel().getColumn(0).setPreferredWidth(79);
		table.getColumnModel().getColumn(1).setPreferredWidth(93);
		table.getColumnModel().getColumn(2).setPreferredWidth(103);
		table.setRowSelectionAllowed(true);

		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				load();
			}
		});

		btnRefresh.setBounds(13, 264, 82, 31);
		getContentPane().add(btnRefresh);

		lblSearch = new JLabel("Search");
		lblSearch.setBounds(14, 24, 39, 14);
		getContentPane().add(lblSearch);

		txtSearch = new JTextField();
		txtSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				load();
			}
		});

		txtSearch.setBounds(52, 21, 136, 20);
		getContentPane().add(txtSearch);
		txtSearch.setColumns(10);

		lblUserCount = new JLabel("Users:");
		lblUserCount.setBounds(343, 264, 56, 14);
		getContentPane().add(lblUserCount);

		panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "User Info",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(412, 49, 214, 204);
		getContentPane().add(panel);
		panel.setLayout(null);

		txtUsername = new JTextField();
		txtUsername.setBounds(10, 89, 202, 20);
		panel.add(txtUsername);
		txtUsername.setColumns(10);

		lblUsername = new JLabel("Username");
		lblUsername.setBounds(10, 74, 89, 14);
		panel.add(lblUsername);

		lblPassword = new JLabel("Password");
		lblPassword.setBounds(10, 120, 89, 14);
		panel.add(lblPassword);

		btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
				User user = new User();
				user.username = txtUsername.getText();
				user.password = txtPassword.getText();
				user.add();
				load();
				clear();
			}
		});
		btnAdd.setBounds(10, 166, 51, 31);
		panel.add(btnAdd);

		btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			@SuppressWarnings({ "deprecation" })
			public void actionPerformed(ActionEvent arg0) {
				if (txtUsername.getText().length() > 0
						&& txtPassword.getText().length() > 0
						&& txtID.getText().length() > 0) {
					ArrayList<User> users = User.get("SELECT * FROM "
							+ M.TABLE_USERS + " WHERE " + M.USER_ID + "="
							+ Integer.parseInt(txtID.getText()));
					if (users != null) {
						User user = users.get(0);
						user.username = txtUsername.getText();
						user.password = txtPassword.getText();
						user.update();
						load();
						clear();
					}
				}
			}
		});
		btnUpdate.setBounds(68, 166, 67, 31);
		panel.add(btnUpdate);

		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			@SuppressWarnings({ "deprecation", "unused" })
			public void actionPerformed(ActionEvent arg0) {
				if (txtUsername.getText().length() > 0
						&& txtPassword.getText().length() > 0
						&& txtID.getText().length() > 0) {
					ArrayList<User> users = User.get("SELECT * FROM "
							+ M.TABLE_USERS + " WHERE " + M.USER_ID + "="
							+ Integer.parseInt(txtID.getText()));
					if (users != null) {
						User user = users.get(0);
						users.get(0).delete();
						load();
						clear();
					}
				}
			}
		});
		btnDelete.setBounds(145, 166, 67, 31);
		panel.add(btnDelete);

		txtPassword = new JPasswordField();
		txtPassword.setBounds(10, 135, 202, 20);
		panel.add(txtPassword);

		lblId = new JLabel("ID");
		lblId.setBounds(10, 28, 89, 14);
		panel.add(lblId);

		txtID = new JTextField();
		txtID.setEnabled(false);
		txtID.setColumns(10);
		txtID.setBounds(10, 43, 202, 20);
		panel.add(txtID);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}
	}

	private void load() {
		User.load("SELECT * FROM " + M.TABLE_USERS + " WHERE " + M.USERNAME
				+ " LIKE '%" + txtSearch.getText() + "%'", model);
	}

	private void clear() {
		JTextField txts[] = { txtID, txtUsername, txtPassword };
		for (JTextField t : txts) {
			t.setText("");
		}
	}
}