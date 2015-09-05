package scheduling.system.forms;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

import scheduling.system.M;
import scheduling.system.classes.Course;
import scheduling.system.classes.Schedule;
import scheduling.system.classes.Semester;
import scheduling.system.classes.Subject;
import scheduling.system.classes.YearLevel;

@SuppressWarnings("serial")
public class FormSubjects extends JFrame implements ListSelectionListener {
	
	/** -------------- TABLE MODELS / DATA ------------------ **/
	private JTable tableSubjects;
	public static DefaultTableModel subjectModel;
	private JTextField txtSearch, txtSubjectID;
	@SuppressWarnings("rawtypes")
	public static DefaultComboBoxModel cboCourseModel, cboYearLevelModel, cboSemesterModel;
	@SuppressWarnings("rawtypes")
	private JComboBox cboCourses, cboYearLevels, cboSearchSubjectBy, cboSemesters;
	private JFormattedTextField txtDuration, txtLabDuration;
	private JTextField txtDescription;
	private JTextField txtCode;
	private JCheckBox chckbxIncludesLaboratory;
	
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormSubjects main = new FormSubjects();
					main.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FormSubjects() {
		M.setWindowsTheme();
		getContentPane().setLayout(null);
		initialize();
		Course.bind(cboCourseModel);
		YearLevel.bind(cboYearLevelModel);
		Semester.bind(cboSemesterModel);
	}

	@SuppressWarnings({"rawtypes", "unchecked" })
	private void initialize() {
		setTitle("Subjects");
		setBounds(100, 100, 1213, 590);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JScrollPane spSubjects = new JScrollPane();
		spSubjects.setBounds(272, 39, 923, 507);
		getContentPane().add(spSubjects);
		
		tableSubjects = new JTable() {
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
		};
		
		subjectModel = new DefaultTableModel(
				new Object[][] {},
				new String[] {
					"ID", "Description", "Code", "Duration", "Lab Duration", "Course", "Year Level", "Semester", "Includes Laboratory"
				}
			);
		
		tableSubjects.setModel(subjectModel);
		tableSubjects.getColumnModel().getColumn(0).setPreferredWidth(10);
		tableSubjects.getColumnModel().getColumn(1).setPreferredWidth(200);
		tableSubjects.getColumnModel().getColumn(2).setPreferredWidth(200);
		tableSubjects.getColumnModel().getColumn(3).setPreferredWidth(30);
		tableSubjects.getColumnModel().getColumn(4).setPreferredWidth(30);
		tableSubjects.getColumnModel().getColumn(5).setPreferredWidth(30);
		tableSubjects.getColumnModel().getColumn(6).setPreferredWidth(20);
		tableSubjects.getColumnModel().getColumn(5).setPreferredWidth(20);
		tableSubjects.getColumnModel().getColumn(6).setPreferredWidth(30);
		tableSubjects.getColumnModel().getColumn(7).setPreferredWidth(20);
		tableSubjects.getColumnModel().getColumn(8).setPreferredWidth(60);
		spSubjects.setViewportView(tableSubjects);
		
		tableSubjects.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_DELETE){
					int rows[] = tableSubjects.getSelectedRows();
					if(rows.length > 0){
						if(JOptionPane.showConfirmDialog(null, "Are you sure you want to delete these subjects? \n\nNOTE: Their schedules will also be deleted") == JOptionPane.YES_OPTION){
							for(int i = 0; i < rows.length; i++){
								int subject_id = Integer.parseInt(tableSubjects.getModel().getValueAt(rows[i], 0).toString());
								Subject.getSingle(subject_id).delete();
								Schedule.deleteBySubjectID(subject_id);
							}
							loadSubjects();
							M.messageBox("Subjects selected have been deleted.");
						}
					}else{
						M.messageBox("Please select a subject to delete.");
					}
				}
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {}
		});
		
		tableSubjects.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3){
					JPopupMenu menu = new JPopupMenu();
					menu.setLocation(e.getX(), e.getY());
					JMenuItem item = new JMenuItem();
					item.setText("Delete");
					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							int rows[] = tableSubjects.getSelectedRows();
							if(rows.length > 0){
								if(JOptionPane.showConfirmDialog(null, "Are you sure you want to delete these subjects? \n\nNOTE: Their schedules will also be deleted") == JOptionPane.YES_OPTION){
									for(int i = 0; i < rows.length; i++){
										int subject_id = Integer.parseInt(tableSubjects.getModel().getValueAt(rows[i], 0).toString());
										Subject.getSingle(subject_id).delete();
										Schedule.deleteBySubjectID(subject_id);
									}
									loadSubjects();
									M.messageBox("Subjects selected have been deleted.");
								}
							}else{
								M.messageBox("Please select a subject to delete.");
							}
						}
					});
					
					menu.add(item);
					menu.show(tableSubjects, e.getX(), e.getY());
				}
				
				if(e.getClickCount() == 2){
					int id = Integer.parseInt(tableSubjects.getValueAt(tableSubjects.getSelectedRow(), 0).toString());
					String duration = tableSubjects.getValueAt(tableSubjects.getSelectedRow(), 3).toString();
					String lab_duration = tableSubjects.getValueAt(tableSubjects.getSelectedRow(), 4).toString();
					Subject subject = Subject.getSingle(id);
					txtSubjectID.setText(subject.id +"");
					txtCode.setText(subject.code);
					txtDescription.setText(subject.description);
					txtDuration.setText(duration);
					if(subject.isLab){
						txtLabDuration.setText(lab_duration);
						chckbxIncludesLaboratory.setSelected(true);
					}else{
						txtLabDuration.setText("");
						chckbxIncludesLaboratory.setSelected(false);
					}
					cboCourses.setSelectedItem(Course.getSingle(subject.course_id).name);
					cboYearLevels.setSelectedItem(YearLevel.getSingle(subject.yearlevel_id).level);
					cboSemesters.setSelectedItem(Semester.getSingle(subject.semester_id).semester);
				}
			}
		});
		
		JLabel label_1 = new JLabel("Search");
		label_1.setBounds(797, 14, 39, 14);
		getContentPane().add(label_1);
		
		txtSearch = new JTextField();
		txtSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent key) {
				if(key.getKeyCode() == 10){
					loadSubjects();
				}
			}
		});
		
		txtSearch.setColumns(10);
		txtSearch.setBounds(832, 11, 183, 20);
		getContentPane().add(txtSearch);

		try {
			txtDuration = new JFormattedTextField(new MaskFormatter("#:##"));
		} catch (ParseException e1) {e1.printStackTrace();} 
		
		txtDuration.setBounds(10, 215, 116, 20);
		getContentPane().add(txtDuration);
		
		cboCourses = new JComboBox();
		cboCourses.setBounds(10, 260, 252, 20);
		getContentPane().add(cboCourses);
		
		JLabel lblCourse_1 = new JLabel("Course");
		lblCourse_1.setBounds(10, 246, 80, 14);
		getContentPane().add(lblCourse_1);
		
		JLabel lblYearLevel_1 = new JLabel("Year Level");
		lblYearLevel_1.setBounds(10, 291, 80, 14);
		getContentPane().add(lblYearLevel_1);
		
		cboYearLevels = new JComboBox();
		cboYearLevels.setBounds(10, 305, 252, 20);
		getContentPane().add(cboYearLevels);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(txtSubjectID.getText().length() > 0){
					if(JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this subject? Deleting this subject will delete all of it's schedules") == JOptionPane.YES_OPTION){
						if(txtSubjectID.getText().length() > 0){
							if(Subject.getSingle(Integer.parseInt(txtSubjectID.getText())).delete()){
								if(Schedule.deleteBySubjectID(Integer.parseInt(txtSubjectID.getText()))){
									loadSubjects();
									M.messageBox("successfully deleted");
								}
							}
						}
					}
					clear();
				}else{
					M.messageBox("Please select a subject first");
				}
			}
		});
		
		btnDelete.setBounds(148, 381, 63, 31);
		getContentPane().add(btnDelete);
		
		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(txtSubjectID.getText().length() > 0){
					if(JOptionPane.showConfirmDialog(null, "Are you sure you want to update this subject?") == JOptionPane.YES_OPTION){
						Subject subject = Subject.getSingle(Integer.parseInt(txtSubjectID.getText()));
						subject.code 			= txtCode.getText();
						subject.description 	= txtDescription.getText();
						subject.duration 		= txtDuration.getText();
						if(chckbxIncludesLaboratory.isSelected()){
							subject.isLab = true;
							subject.lab_duration = txtLabDuration.getText();
						}else{
							subject.lab_duration = "0:00";
						}
						subject.course_id 		= Course.getSingle(cboCourses.getSelectedItem().toString()).id;
						subject.yearlevel_id 	= YearLevel.getSingle(cboYearLevels.getSelectedItem().toString()).id;
						subject.semester_id 	= Semester.getSingle(cboSemesters.getSelectedItem().toString()).id;
						if(subject.update()){
							loadSubjects();
							M.messageBox("successfully updated");
						}
					}
				}else{
					M.messageBox("Please select a subject first");
				}
				clear();
			}
		});
		
		btnUpdate.setBounds(71, 381, 67, 31);
		getContentPane().add(btnUpdate);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Subject subject = new Subject();
				subject.code 			= txtCode.getText();
				subject.description 	= txtDescription.getText();
				subject.duration 		= txtDuration.getText();
				if(chckbxIncludesLaboratory.isSelected()){
					subject.isLab = true;
					subject.lab_duration = txtLabDuration.getText();
				}else{
					subject.lab_duration = "0:00";
				}
				subject.course_id 		= Course.getSingle(cboCourses.getSelectedItem().toString()).id;
				subject.yearlevel_id 	= YearLevel.getSingle(cboYearLevels.getSelectedItem().toString()).id;
				subject.semester_id 	= Semester.getSingle(cboSemesters.getSelectedItem().toString()).id;
				if(subject.add()){
					loadSubjects();
					if(JOptionPane.showConfirmDialog(null, "Successfully added.\n\nGenerate schedules for this subject?") == JOptionPane.YES_OPTION){
						Schedule.generate(Subject.getLastID());
					}
				}
				clear();
			}
		});
		
		btnAdd.setBounds(10, 381, 51, 31);
		getContentPane().add(btnAdd);
		
		JLabel lblNy = new JLabel("By");
		lblNy.setBounds(1025, 14, 26, 14);
		getContentPane().add(lblNy);
		
		cboSearchSubjectBy = new JComboBox();
		cboSearchSubjectBy.setModel(new DefaultComboBoxModel(new String[] {"SubjectCode", "Description", "CourseID", "YearLevelID", "SemesterID"}));
		cboSearchSubjectBy.setSelectedIndex(4);
		cboSearchSubjectBy.setBounds(1045, 11, 150, 20);
		getContentPane().add(cboSearchSubjectBy);
		tableSubjects.getSelectionModel().addListSelectionListener(this);

		txtSubjectID = new JTextField();
		txtSubjectID.setEnabled(false);
		txtSubjectID.setColumns(10);
		txtSubjectID.setVisible(false);
		txtSubjectID.setBounds(10, 11, 51, 20);
		getContentPane().add(txtSubjectID);
		
		cboSemesters = new JComboBox();
		cboSemesters.setBounds(10, 350, 252, 20);
		getContentPane().add(cboSemesters);
		
		JLabel lblSemester = new JLabel("Semester");
		lblSemester.setBounds(10, 336, 80, 14);
		getContentPane().add(lblSemester);
		
		JButton btnClear = new JButton("C");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clear();
			}
		});
		
		btnClear.setBounds(217, 381, 45, 31);
		getContentPane().add(btnClear);
		
		JLabel lblDuration_1 = new JLabel("Subject Duration:");
		lblDuration_1.setBounds(10, 202, 93, 14);
		getContentPane().add(lblDuration_1);
		
		cboSearchSubjectBy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadSubjects();
			}
		});

		txtDescription = new JTextField();
		txtDescription.setBounds(10, 110, 252, 55);
		getContentPane().add(txtDescription);
		txtDescription.setColumns(10);
		
		JLabel lblDescription = new JLabel("Description");
		lblDescription.setBounds(10, 85, 93, 14);
		getContentPane().add(lblDescription);
		
		txtCode = new JTextField();
		txtCode.setColumns(10);
		txtCode.setBounds(10, 54, 252, 20);
		getContentPane().add(txtCode);
		
		JLabel lblName = new JLabel("Code");
		lblName.setBounds(10, 39, 93, 14);
		getContentPane().add(lblName);
		
		cboCourseModel = new DefaultComboBoxModel(new String[] {""});
		cboCourses.setModel(cboCourseModel);
		
		cboYearLevelModel = new DefaultComboBoxModel(new String[] {""});
		cboYearLevels.setModel(cboYearLevelModel);
		
		cboSemesterModel = new DefaultComboBoxModel(new String[] {""});
		cboSemesters.setModel(cboSemesterModel);
		
		JButton btnGenerateSchedules = new JButton("Generate Schedules");
		btnGenerateSchedules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(txtSubjectID.getText().length() > 0){
					Schedule.generate(Integer.parseInt(txtSubjectID.getText()));
				}else{
					M.messageBox("Please select a subject where to generate schedules from");
				}
			}
		});
		btnGenerateSchedules.setBounds(10, 423, 252, 31);
		getContentPane().add(btnGenerateSchedules);
		
		chckbxIncludesLaboratory = new JCheckBox("Includes Laboratory?");
		chckbxIncludesLaboratory.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(chckbxIncludesLaboratory.isSelected()){
					txtLabDuration.setEnabled(true);
				}else{
					txtLabDuration.setEnabled(false);
				}
			}
		});
		chckbxIncludesLaboratory.setBounds(10, 172, 134, 23);
		getContentPane().add(chckbxIncludesLaboratory);
		
		JLabel lblLabDuration = new JLabel("Lab Duration:");
		lblLabDuration.setBounds(138, 202, 93, 14);
		getContentPane().add(lblLabDuration);

		try {
			txtLabDuration = new JFormattedTextField(new MaskFormatter("#:##"));
			txtLabDuration.setEnabled(false);
		} catch (ParseException e1) {e1.printStackTrace();} 
		
		txtLabDuration.setBounds(135, 216, 122, 20);
		getContentPane().add(txtLabDuration);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadSubjects();
			}
		});
		
		btnRefresh.setBounds(698, 10, 89, 23);
		getContentPane().add(btnRefresh);
		
		JButton btnSchedules = new JButton("Schedules");
		btnSchedules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				M.form_schedules.setVisible(true);
			}
		});
		btnSchedules.setBounds(598, 10, 89, 23);
		getContentPane().add(btnSchedules);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}
	}
	
	private void loadSubjects(){
		String column = cboSearchSubjectBy.getSelectedItem().toString();
		Subject.load("SELECT * FROM " + M.TABLE_SUBJECTS + " WHERE " + column + " LIKE '" + txtSearch.getText() + "%' ", subjectModel);
		Course.bind(cboCourseModel);
		YearLevel.bind(cboYearLevelModel);
		Semester.bind(cboSemesterModel);
	}
	
	private void clear(){
		txtSubjectID.setText("");
		txtDescription.setText("");
		txtCode.setText("");
		txtDuration.setText("");
		txtLabDuration.setText("");
		chckbxIncludesLaboratory.setSelected(false);
	}
}