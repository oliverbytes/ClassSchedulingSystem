package scheduling.system.forms;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

import scheduling.system.M;
import scheduling.system.classes.Course;
import scheduling.system.classes.Room;
import scheduling.system.classes.Schedule;
import scheduling.system.classes.Semester;
import scheduling.system.classes.Subject;
import scheduling.system.classes.YearLevel;

@SuppressWarnings("serial")
public class FormSchedules extends JFrame implements ListSelectionListener {
	
	/** -------------- TABLE MODELS / DATA ------------------ **/
	public JTable tableSchedules;
	public static DefaultTableModel scheduleModel;
	private JTextField txtSearchSchedule, txtScheduleID;
	@SuppressWarnings("rawtypes")
	public static DefaultComboBoxModel cboRoomModel, cboSubjectModel, cboShowByCourseModel, cboCourseModel, cboYearLevelModel, cboSemesterModel;
	@SuppressWarnings("rawtypes")
	private JComboBox cboCourses, cboYearLevels, cboSearchScheduleBy, cboSubjects, cboRooms, cboSemesters, cboShowByCourse;
	private JFormattedTextField txtStartTime, txtEndTime;
	private JCheckBox chckbxM, chckbxT, chckbxW, chckbxR, chckbxF, chckbxS;
	private JLabel lblDuration;
	private JButton btnRefresh;
	private JButton btnSubjects;
	
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormSchedules main = new FormSchedules();
					main.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FormSchedules() {
		M.setWindowsTheme();
		getContentPane().setLayout(null);
		initialize();
		
		Room.bind(cboRoomModel);
		Subject.bind(cboSubjectModel);
		Course.bind(cboCourseModel);
		Course.bind(cboShowByCourseModel);
		YearLevel.bind(cboYearLevelModel);
		Semester.bind(cboSemesterModel);
		
		cboShowByCourse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int course_id = Course.getSingle(cboShowByCourse.getSelectedItem().toString()).id;
				String sql = "SELECT * FROM " + M.TABLE_SCHEDULES + " WHERE " + M.COURSE_ID + " = " + course_id;
				Schedule.load(sql, scheduleModel);
			}
		});
		
		txtSearchSchedule.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent key) {
				if(key.getKeyCode() == 10){
					loadSchedules();
				}
			}
		});
	}

	@SuppressWarnings({"rawtypes", "unchecked" })
	private void initialize() {
		setTitle("Schedules");
		setBounds(100, 100, 1213, 590);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JScrollPane spSchedule = new JScrollPane();	
		
		spSchedule.setBounds(259, 39, 936, 507);
		getContentPane().add(spSchedule);
		
		tableSchedules = new JTable() {
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
		};
		
		tableSchedules.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_DELETE){
					int rows[] = tableSchedules.getSelectedRows();
					if(rows.length > 0){
						if(JOptionPane.showConfirmDialog(null, "Are you sure you want to delete these schedules?") == JOptionPane.YES_OPTION){
							for(int i = 0; i < rows.length; i++){
								int schedule_id = Integer.parseInt(tableSchedules.getModel().getValueAt(rows[i], 0).toString());
								Schedule.getSingle(schedule_id).delete();
							}
							loadSchedules();
							M.messageBox("Schedules selected have been deleted.");
						}
					}else{
						M.messageBox("Please select a schedule to delete.");
					}
				}
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {}
		});
		
		tableSchedules.addMouseListener(new MouseAdapter() {
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
							int rows[] = tableSchedules.getSelectedRows();
							if(rows.length > 0){
								if(JOptionPane.showConfirmDialog(null, "Are you sure you want to delete these schedules?") == JOptionPane.YES_OPTION){
									for(int i = 0; i < rows.length; i++){
										int schedule_id = Integer.parseInt(tableSchedules.getModel().getValueAt(rows[i], 0).toString());
										Schedule.getSingle(schedule_id).delete();
									}
									loadSchedules();
									M.messageBox("Schedules selected have been deleted.");
								}
							}else{
								M.messageBox("Please select a schedule to delete.");
							}
						}
					});
					
					menu.add(item);
					menu.show(tableSchedules, e.getX(), e.getY());
				}
				
				if(e.getClickCount() == 2){
					int id = Integer.parseInt(tableSchedules.getValueAt(tableSchedules.getSelectedRow(), 0).toString());
					Schedule schedule = Schedule.getSingle(id);
					txtScheduleID.setText(schedule.id +"");
					cboRooms.setSelectedItem(Room.getSingle(schedule.room_id).code);
					cboSubjects.setSelectedItem(Subject.getSingle(schedule.subject_id).code);
					cboCourses.setSelectedItem(Course.getSingle(schedule.course_id).name);
					txtStartTime.setText(schedule.startTime);
					txtEndTime.setText(schedule.endTime);
					
					chckbxM.setSelected(false);
					chckbxT.setSelected(false);
					chckbxW.setSelected(false);
					chckbxR.setSelected(false);
					chckbxF.setSelected(false);
					chckbxS.setSelected(false);
					
					char dayLetters[] = schedule.days.toCharArray();
					for(int i = 0; i < dayLetters.length; i++){
						String letter = Character.toString(dayLetters[i]);
						if(letter.equals("M")){
							chckbxM.setSelected(true);
						}else if(letter.equals("T")){
							chckbxT.setSelected(true);
						}else if(letter.equals("W")){
							chckbxW.setSelected(true);
						}else if(letter.equals("R")){
							chckbxR.setSelected(true);
						}else if(letter.equals("F")){
							chckbxF.setSelected(true);
						}else if(letter.equals("S")){
							chckbxS.setSelected(true);
						}
					}
				}
			}
		});
		
		scheduleModel = new DefaultTableModel(
				new Object[][] {},
				new String[] {
					"ID", "Days", "Start Time", "End Time", "Room", "Subject", "Course", "Year Level", "Semester", "Is Laboratory"
				}
			);
		
		tableSchedules.setModel(scheduleModel);
		tableSchedules.getColumnModel().getColumn(0).setPreferredWidth(30);
		tableSchedules.getColumnModel().getColumn(1).setPreferredWidth(30);
		tableSchedules.getColumnModel().getColumn(2).setPreferredWidth(30);
		tableSchedules.getColumnModel().getColumn(3).setPreferredWidth(30);
		tableSchedules.getColumnModel().getColumn(4).setPreferredWidth(60);
		tableSchedules.getColumnModel().getColumn(5).setPreferredWidth(150);
		tableSchedules.getColumnModel().getColumn(6).setPreferredWidth(64);
		tableSchedules.getColumnModel().getColumn(7).setPreferredWidth(30);
		tableSchedules.getColumnModel().getColumn(8).setPreferredWidth(30);
		tableSchedules.getColumnModel().getColumn(9).setPreferredWidth(30);
		spSchedule.setViewportView(tableSchedules);
		
		JLabel label_1 = new JLabel("Search");
		label_1.setBounds(797, 14, 39, 14);
		getContentPane().add(label_1);
		
		txtSearchSchedule = new JTextField();
		txtSearchSchedule.setColumns(10);
		txtSearchSchedule.setBounds(832, 11, 183, 20);
		getContentPane().add(txtSearchSchedule);
		
		JLabel lblDays = new JLabel("Days");
		lblDays.setBounds(10, 39, 39, 14);
		getContentPane().add(lblDays);
	
		chckbxM = new JCheckBox("M");
		chckbxM.setBounds(10, 55, 39, 25);
		getContentPane().add(chckbxM);
		
		chckbxT = new JCheckBox("T");
		chckbxT.setBounds(51, 55, 39, 25);
		getContentPane().add(chckbxT);
		
		chckbxW = new JCheckBox("W");
		chckbxW.setBounds(92, 55, 39, 25);
		getContentPane().add(chckbxW);
		
		chckbxR = new JCheckBox("R");
		chckbxR.setBounds(133, 55, 39, 25);
		getContentPane().add(chckbxR);
		
		chckbxF = new JCheckBox("F");
		chckbxF.setBounds(174, 55, 39, 25);
		getContentPane().add(chckbxF);
		
		chckbxS = new JCheckBox("S");
		chckbxS.setBounds(214, 55, 39, 25);
		getContentPane().add(chckbxS);
		
		txtStartTime = null;
		try {txtStartTime = new JFormattedTextField(new MaskFormatter("##:##-AA"));
			txtStartTime.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent arg0) {
					
					boolean canConvert = true;
					
					for(int i = 0; i < 8; i++){
						
						String x = txtStartTime.getText().substring(i, i + 1);
						
						if(x.equals(" ")){
							canConvert = false;
							break;
						}
						
						if(i == 6){
							if(!x.equalsIgnoreCase("A") && !x.equalsIgnoreCase("P")){
								canConvert = false;
								break;
							}
						}
						
						if(i == 7){
							if(!x.equalsIgnoreCase("M")){
								canConvert = false;
								break;
							}
						}
					}

					if(canConvert){
						txtStartTime.setText(txtStartTime.getText().toUpperCase());
						String ampm = txtStartTime.getText().substring(6, 8);
						String startTime = txtStartTime.getText().replaceAll("-", "");
						startTime = startTime.replaceAll("AM", "");
						startTime = startTime.replaceAll("PM", "");
						
						String[] startTimetimes = startTime.split(":");
						int startTimehour = Integer.parseInt(startTimetimes[0]);
						int startTimeminutes = Integer.parseInt(startTimetimes[1]);
						
						Calendar c1 = Calendar.getInstance();
						
						if(ampm.equals("AM")){
							c1.set(Calendar.AM_PM, Calendar.AM);
						}else{
							c1.set(Calendar.AM_PM, Calendar.PM);
						}

						c1.set(Calendar.HOUR, startTimehour);
						c1.set(Calendar.MINUTE, startTimeminutes);
						
						String duration = lblDuration.getText();
						
						String[] times = duration.split(":");
						int hours = Integer.parseInt(times[0]);
						int minutes = Integer.parseInt(times[1]);
						
						c1.add(Calendar.HOUR, hours);
						c1.add(Calendar.MINUTE, minutes);
						
						SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aaa");
				        String endTime = sdf.format(c1.getTime());
				        
				        txtEndTime.setText(endTime);
					}else{
						txtEndTime.setText("");
					}
				}
			});
		} catch (ParseException e1) {e1.printStackTrace();} 
		
		txtStartTime.setBounds(10, 107, 121, 20);
		getContentPane().add(txtStartTime);
		
		txtEndTime = null;
		txtEndTime = new JFormattedTextField();
		txtEndTime.setEditable(false);
			
		txtEndTime.setBounds(133, 107, 116, 20);
		getContentPane().add(txtEndTime);
		
		JLabel lblStartTime = new JLabel("Start Time ex: 08:00-AM");
		lblStartTime.setBounds(10, 87, 121, 14);
		getContentPane().add(lblStartTime);
		
		JLabel lblEndTime = new JLabel("End Time");
		lblEndTime.setBounds(143, 87, 80, 14);
		getContentPane().add(lblEndTime);
		
		cboRooms = new JComboBox();
		cboRooms.setBounds(10, 152, 239, 20);
		getContentPane().add(cboRooms);
		
		JLabel lblRoomCode = new JLabel("Room");
		lblRoomCode.setBounds(10, 138, 80, 14);
		getContentPane().add(lblRoomCode);
		
		JLabel lblSubject = new JLabel("Subject");
		lblSubject.setBounds(10, 183, 80, 14);
		getContentPane().add(lblSubject);
		
		cboSubjects = new JComboBox();		
		cboSubjects.setBounds(10, 197, 239, 20);
		getContentPane().add(cboSubjects);
		
		cboSubjects.addItemListener(new ItemListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void itemStateChanged(ItemEvent e){
				if (e.getStateChange() == 1) {
					String subject = e.getItem() + "";
					if(!subject.equals("")){
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String duration = Subject.getDuration(Subject.getSingle(subject).id);
						try {
							Date d = formatter.parse(duration);
							lblDuration.setText(d.getHours() + ":" + d.getMinutes());
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
	            }
			}
		});
		
		cboCourses = new JComboBox();
		cboCourses.setBounds(10, 267, 239, 20);
		getContentPane().add(cboCourses);
		
		JLabel lblCourse_1 = new JLabel("Course");
		lblCourse_1.setBounds(10, 253, 80, 14);
		getContentPane().add(lblCourse_1);
		
		JLabel lblYearLevel_1 = new JLabel("Year Level");
		lblYearLevel_1.setBounds(10, 298, 80, 14);
		getContentPane().add(lblYearLevel_1);
		
		cboYearLevels = new JComboBox();
		cboYearLevels.setBounds(10, 312, 239, 20);
		getContentPane().add(cboYearLevels);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(txtScheduleID.getText().length() > 0){
					if(JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this schedule?") == JOptionPane.YES_OPTION){
						if(Schedule.getSingle(Long.parseLong(txtScheduleID.getText())).delete()){
							loadSchedules();
							M.messageBox("successfully updated.");
						}
						clear();
					}
				}else{
					M.messageBox("Please select a schedule first");
				}
			}
		});
		
		btnDelete.setBounds(10, 472, 239, 31);
		getContentPane().add(btnDelete);
		
		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(txtScheduleID.getText().length() > 0){
					if(JOptionPane.showConfirmDialog(null, "Are you sure you want to update this schedule?") == JOptionPane.YES_OPTION){
						Schedule schedule = Schedule.getSingle(Long.parseLong(txtScheduleID.getText()));
						String days = "";
						if(chckbxM.isSelected()){
							days += "M";
						}if(chckbxT.isSelected()){
							days += "T";
						}if(chckbxW.isSelected()){
							days += "W";
						}if(chckbxR.isSelected()){
							days += "R";
						}if(chckbxF.isSelected()){
							days += "F";
						}if(chckbxS.isSelected()){
							days += "S";
						}
						
						schedule.days = days;
						schedule.startTime = txtStartTime.getText().replaceAll("-", "").trim();
						schedule.endTime = txtEndTime.getText().replaceAll("-", "").trim();
						schedule.room_id = Room.getSingle(cboRooms.getSelectedItem().toString()).id;
						schedule.subject_id = Subject.getSingle(cboSubjects.getSelectedItem().toString()).id;
						schedule.course_id = Course.getSingle(cboCourses.getSelectedItem().toString()).id;
						if(schedule.update()){
							loadSchedules();
							M.messageBox("successfully updated.");
						}
						clear();
					}
				}else{
					M.messageBox("Please select a schedule first");
				}
			}
		});
		
		btnUpdate.setBounds(10, 430, 239, 31);
		getContentPane().add(btnUpdate);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Schedule schedule = new Schedule();
				String days = "";
				if(chckbxM.isSelected()){
					days += "M";
				}if(chckbxT.isSelected()){
					days += "T";
				}if(chckbxW.isSelected()){
					days += "W";
				}if(chckbxR.isSelected()){
					days += "R";
				}if(chckbxF.isSelected()){
					days += "F";
				}if(chckbxS.isSelected()){
					days += "S";
				}
				schedule.days = days;
				schedule.startTime 		= txtStartTime.getText().replaceAll("-", "").trim();
				schedule.endTime 		= txtEndTime.getText().replaceAll("-", "").trim();
				schedule.room_id 		= Room.getSingle(cboRooms.getSelectedItem().toString()).id;
				schedule.subject_id 	= Subject.getSingle(cboSubjects.getSelectedItem().toString()).id;
				schedule.course_id 		= Course.getSingle(cboCourses.getSelectedItem().toString()).id;
				schedule.yearlevel_id 	= YearLevel.getSingle(cboYearLevels.getSelectedItem().toString()).id;
				schedule.semester_id 	= Semester.getSingle(cboSemesters.getSelectedItem().toString()).id;

				if(schedule.add()){
					loadSchedules();
					M.messageBox("Schedule successfully added.");
				}
				clear();
			}
		});
		
		btnAdd.setBounds(10, 388, 239, 31);
		getContentPane().add(btnAdd);
		
		JLabel lblNy = new JLabel("By");
		lblNy.setBounds(1025, 14, 26, 14);
		getContentPane().add(lblNy);
		
		cboSearchScheduleBy = new JComboBox();
		cboSearchScheduleBy.setModel(new DefaultComboBoxModel(new String[] {"Days", "StartTime", "EndTime", "RoomCode", "SubjectCode", "CourseName", "YearLevel", "Semester"}));
		cboSearchScheduleBy.setSelectedIndex(4);
		cboSearchScheduleBy.setBounds(1045, 11, 150, 20);
		getContentPane().add(cboSearchScheduleBy);
		tableSchedules.getSelectionModel().addListSelectionListener(this);

		txtScheduleID = new JTextField();
		txtScheduleID.setVisible(false);
		txtScheduleID.setEnabled(false);
		txtScheduleID.setColumns(10);
		txtScheduleID.setBounds(10, 11, 51, 20);
		getContentPane().add(txtScheduleID);
		
		cboSemesters = new JComboBox();
		cboSemesters.setBounds(10, 357, 239, 20);
		getContentPane().add(cboSemesters);
		
		JLabel lblSemester = new JLabel("Semester");
		lblSemester.setBounds(10, 343, 80, 14);
		getContentPane().add(lblSemester);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clear();
			}
		});
		
		btnClear.setBounds(10, 514, 239, 31);
		getContentPane().add(btnClear);
		
		lblDuration = new JLabel("");
		lblDuration.setBounds(101, 228, 148, 14);
		getContentPane().add(lblDuration);
		
		JLabel lblDuration_1 = new JLabel("Subject Duration:");
		lblDuration_1.setBounds(10, 228, 93, 14);
		getContentPane().add(lblDuration_1);
		
		cboRoomModel = new DefaultComboBoxModel(new String[] {""});
		cboRooms.setModel(cboRoomModel);
		
		cboSubjectModel = new DefaultComboBoxModel(new String[] {""});
		cboSubjects.setModel(cboSubjectModel);
		
		cboCourseModel = new DefaultComboBoxModel(new String[] {""});
		cboCourses.setModel(cboCourseModel);
		
		cboYearLevelModel = new DefaultComboBoxModel(new String[] {""});
		cboYearLevels.setModel(cboYearLevelModel);
		
		cboSemesterModel = new DefaultComboBoxModel(new String[] {""});
		cboSemesters.setModel(cboSemesterModel);
		
		cboShowByCourse = new JComboBox();
		cboShowByCourse.setBounds(311, 11, 170, 20);
		getContentPane().add(cboShowByCourse);
		
		cboShowByCourseModel = new DefaultComboBoxModel(new String[] {""});
		cboShowByCourse.setModel(cboShowByCourseModel);
		
		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadSchedules();
			}
		});
		
		btnRefresh.setBounds(695, 10, 89, 23);
		getContentPane().add(btnRefresh);
		
		btnSubjects = new JButton("Subjects");
		btnSubjects.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				M.form_subjects.setVisible(true);
			}
		});
		
		btnSubjects.setBounds(596, 10, 89, 23);
		getContentPane().add(btnSubjects);
		
		JLabel lblShowBy = new JLabel("Show By:");
		lblShowBy.setBounds(259, 14, 51, 14);
		getContentPane().add(lblShowBy);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}
	}
	
	public void loadSchedules(){
		String column = cboSearchScheduleBy.getSelectedItem().toString();
		String criteria = txtSearchSchedule.getText();
		String column_id = "";
		String table = "";
		String sql = "";
		
		if(column.equals(M.COURSE_NAME)){
			column_id = M.COURSE_ID;
			table = M.TABLE_COURSES;
			sql = "SELECT * FROM tblSchedules LEFT JOIN " + table + " ON tblSchedules." + column_id + " = " + table + "." + column_id + " WHERE " + table + "." + column + " LIKE '%" + criteria + "'";
		}else if(column.equals(M.ROOM_CODE)){
			column_id = M.ROOM_ID;
			table = M.TABLE_ROOMS;
			sql = "SELECT * FROM tblSchedules LEFT JOIN " + table + " ON tblSchedules." + column_id + " = " + table + "." + column_id + " WHERE " + table + "." + column + " LIKE '%" + criteria + "'";
		}else if(column.equals(M.SEMESTER)){
			column_id = M.SEMESTER_ID;
			table = M.TABLE_SEMESTERS;
			sql = "SELECT * FROM tblSchedules LEFT JOIN " + table + " ON tblSchedules." + column_id + " = " + table + "." + column_id + " WHERE " + table + "." + column + " LIKE '%" + criteria + "'";
		}else if(column.equals(M.YEARLEVEL)){
			column_id = M.YEARLEVEL_ID;
			table = M.TABLE_YEARLEVELS;
			sql = "SELECT * FROM tblSchedules LEFT JOIN " + table + " ON tblSchedules." + column_id + " = " + table + "." + column_id + " WHERE " + table + "." + column + " LIKE '%" + criteria + "'";
		}else if(column.equals(M.SUBJECT_CODE)){
			column_id = M.SUBJECT_ID;
			table = M.TABLE_SUBJECTS;
			sql = "SELECT * FROM tblSchedules LEFT JOIN " + table + " ON tblSchedules." + column_id + " = " + table + "." + column_id + " WHERE " + table + "." + column + " LIKE '%" + criteria + "'";
		}else{
			sql = "SELECT * FROM tblSchedules WHERE " + column + " LIKE '%" + criteria + "%'";		
		}

		Schedule.load(sql, scheduleModel);
	}
	
	private void clear(){
		chckbxM.setSelected(false);
		chckbxT.setSelected(false);
		chckbxW.setSelected(false);
		chckbxR.setSelected(false);
		chckbxF.setSelected(false);
		chckbxS.setSelected(false);
		txtScheduleID.setText("");
		txtStartTime.setText("");
		txtEndTime.setText("");
	}
}