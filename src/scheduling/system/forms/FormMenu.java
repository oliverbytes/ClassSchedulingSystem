package scheduling.system.forms;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import scheduling.system.M;

@SuppressWarnings("serial")
public class FormMenu extends JFrame {

	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormMenu frame = new FormMenu();
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
	public FormMenu() {
		M.setWindowsTheme();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 332, 177);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnSubjects = new JButton("Subjects");
		btnSubjects.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				M.form_subjects.setVisible(true);
			}
		});
		btnSubjects.setBounds(64, 58, 89, 23);
		contentPane.add(btnSubjects);
		
		JButton btnSchedules = new JButton("Schedules");
		btnSchedules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				M.form_schedules.setVisible(true);
			}
		});
		btnSchedules.setBounds(163, 58, 89, 23);
		contentPane.add(btnSchedules);
		
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
	}
}
