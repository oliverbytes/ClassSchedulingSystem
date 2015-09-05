package scheduling.system.forms;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import scheduling.system.M;

@SuppressWarnings("serial")
public class FormDialog extends JDialog {
	
	public JProgressBar progressBar;
	public JLabel lblMessage;

	public static void main(String[] args) {
		try {
			FormDialog dialog = new FormDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FormDialog() {
		M.setWindowsTheme();
		setBounds(100, 100, 450, 151);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setBounds(10, 80, 404, 21);
		getContentPane().add(progressBar);
		
		lblMessage = new JLabel("Loading");
		lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
		lblMessage.setBounds(10, 11, 404, 58);
		getContentPane().add(lblMessage);
	}
}
