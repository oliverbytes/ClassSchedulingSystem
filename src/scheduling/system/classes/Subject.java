package scheduling.system.classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

import scheduling.system.M;
import scheduling.system.forms.FormDialog;

public class Subject extends M { // Class M holds some of the Methods and Constant Variables in this Class

	// member variables
	public int id = 0;
	public String description = "";
	public String code = "";
	public String duration = "";
	public String lab_duration = "";
	public int course_id = 0;
	public int yearlevel_id = 0;
	public int semester_id = 0;
	public boolean isLab = false;
	
	public static ArrayList<Subject> getMultiple(String sql) { // get 1 or more subject
		ArrayList<Subject> subjects = new ArrayList<Subject>();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery(sql);
			while (result.next()) {
				Subject subject = new Subject();
				subject.id = result.getInt(SUBJECT_ID);
				subject.description = result.getString(DESCRIPTION);
				subject.code = result.getString(SUBJECT_CODE);
				subject.duration = durationFormat(result.getTime(DURATION));
				subject.lab_duration = durationFormat(result.getTime(LAB_DURATION));
				subject.course_id = result.getInt(COURSE_ID);
				subject.yearlevel_id = result.getInt(YEARLEVEL_ID);
				subject.semester_id = result.getInt(SEMESTER_ID);
				subject.isLab = result.getBoolean(IS_LABORATORY);
				subjects.add(subject);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subjects;
	}

	public static Subject getSingle(long id) { // get a single subject
		Subject subject = new Subject();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery("SELECT * FROM " + TABLE_SUBJECTS + " WHERE " + SUBJECT_ID + "=" + id);
			if(result.next()) {
				subject.id = result.getInt(SUBJECT_ID);
				subject.description = result.getString(DESCRIPTION);
				subject.code = result.getString(SUBJECT_CODE);
				subject.duration = durationFormat(result.getTime(DURATION));
				subject.lab_duration = durationFormat(result.getTime(LAB_DURATION));
				subject.course_id = result.getInt(COURSE_ID);
				subject.yearlevel_id = result.getInt(YEARLEVEL_ID);
				subject.semester_id = result.getInt(SEMESTER_ID);
				subject.isLab = result.getBoolean(IS_LABORATORY);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subject;
	}
	
	public static Subject getSingle(String code) { // get a single subject
		Subject subject = new Subject();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery("SELECT * FROM " + TABLE_SUBJECTS + " WHERE " + SUBJECT_CODE + "='" + code + "'");
			if(result.next()) {
				subject.id = result.getInt(SUBJECT_ID);
				subject.description = result.getString(DESCRIPTION);
				subject.code = result.getString(SUBJECT_CODE);
				subject.duration = result.getString(DURATION);
				subject.lab_duration = durationFormat(result.getTime(LAB_DURATION));
				subject.course_id = result.getInt(COURSE_ID);
				subject.yearlevel_id = result.getInt(YEARLEVEL_ID);
				subject.semester_id = result.getInt(SEMESTER_ID);
				subject.isLab = result.getBoolean(IS_LABORATORY);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subject;
	}
	
	public boolean add() { // add a subject in the database
		if (verify() == true) {
			if(!exists(code)){
				try {
					db.open();
					sql = "INSERT INTO " + TABLE_SUBJECTS + " (" + 
							DESCRIPTION + ", " + 
							SUBJECT_CODE + ", " + 
							DURATION + ", " + 
							LAB_DURATION + ", " + 
							COURSE_ID + ", " + 
							YEARLEVEL_ID + ", " + 
							SEMESTER_ID + ", " + 
							IS_LABORATORY +
							") VALUES ('" + 
							this.description + "', '" +
							this.code + "', '" +
							this.duration + "', '" +
							this.lab_duration + "', " +
							this.course_id + ", " +
							this.yearlevel_id + ", " +
							this.semester_id + ", " +
							this.isLab +
							")";
					M.log(sql);
					db.statement.executeUpdate(sql);
					db.statement.close();
					db.close();
					messageBox(description + " successfully added");
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else{
				messageBox("Subject Code already exists");
			}
			
		} else {
			messageBox("All fields are required.");
		}
		return false;
	}

	public boolean update() { // update a subject in the database
		boolean successful = false;
		if (verify() == true) {
			try {
				db.open();
				
				sql 	= "UPDATE " + TABLE_SUBJECTS + " SET "
						+ DESCRIPTION + " = '" + this.description + "', "
						+ SUBJECT_CODE + " = '" + this.code + "', "
						+ DURATION + " = '" + this.duration + "', "
						+ LAB_DURATION + " = '" + this.lab_duration + "', "
						+ COURSE_ID + " = " + this.course_id + ", "
						+ YEARLEVEL_ID + " = " + this.yearlevel_id + ", "
						+ SEMESTER_ID + " = " + this.semester_id + ", "
						+ IS_LABORATORY + " = " + this.isLab + " "
						+ "WHERE " + SUBJECT_ID + " = " + this.id;
				
				log(sql);
				db.statement.executeUpdate(sql);
				db.statement.close();
				db.close();
				successful = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			messageBox("All fields are required.");
		}
		return successful;
	}

	public boolean delete() {  // delete a subject in the database
		boolean successful = false;
		try {
			db.open();
			db.statement.executeUpdate("DELETE * FROM " + TABLE_SUBJECTS
					+ " WHERE " + SUBJECT_ID + " = " + this.id);
			db.statement.close();
			db.close();
			successful = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return successful;
	}
	
	public boolean verify() { // verify if the subject fields are filled in
		if (
			this.description.length() >= 1 &&
			this.code.length() >= 1 && 
			this.duration.length() >= 1 && 
			this.course_id != 0 && 
			this.yearlevel_id != 0 &&
			this.semester_id != 0
			) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void load(String sql, final DefaultTableModel model) { // bind subjects in a table
		model.setRowCount(0);
		final ArrayList<Subject> subjects = Subject.getMultiple(sql);
		
		final FormDialog dialog = new FormDialog();
		dialog.setTitle("Loading Subjects");
		dialog.lblMessage.setText("Please wait....");
		dialog.setVisible(true);
		
		if (subjects != null) {
			Thread t = new Thread(new Runnable() {
				
				boolean running = true;
				
				@Override
				public void run() {
					while(running){
						for (Subject subject : subjects) {
							String subjectData[] = { 
									subject.id + "",
									subject.description, 
									subject.code,
									subject.duration,
									subject.lab_duration,
									Course.getSingle(subject.course_id).name, 
									YearLevel.getSingle(subject.yearlevel_id).level + "", 
									Semester.getSingle(subject.semester_id).semester + "",
									subject.isLab + ""
									};
							model.insertRow(model.getRowCount(), subjectData);
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						running = false;
						dialog.dispose();
					}
				}
			});
			
			t.start();
			
		}
	}
	
	public static boolean exists(String subject) { // check if a subject already exists
		boolean doesExists = false;
		try {
			db.open();
			String sql = "SELECT COUNT(" + SUBJECT_ID
					+ ") as Count FROM " + TABLE_SUBJECTS + " WHERE "
					+ SUBJECT_CODE + "='" + subject + "'";
			ResultSet result = db.statement.executeQuery(sql);
			if (result.next()) {
				if (result.getInt("Count") > 0) {
					doesExists = true;
				} else {
					doesExists = false;
				}
			} else {
				doesExists = false;
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return doesExists;
	}
	
	public static String getDuration(int subject_id) {
		String duration = null;
		try {
			db.open();
			String sql = "SELECT "+ DURATION +" FROM " + TABLE_SUBJECTS + " WHERE " + SUBJECT_ID + "=" + subject_id;
			ResultSet result = db.statement.executeQuery(sql);
			if (result.next()) {
				duration = result.getString(DURATION);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return duration;
	}
	
	public static String getLabDuration(int subject_id) {
		String duration = null;
		try {
			db.open();
			String sql = "SELECT "+ LAB_DURATION +" FROM " + TABLE_SUBJECTS + " WHERE " + SUBJECT_ID + "=" + subject_id;
			ResultSet result = db.statement.executeQuery(sql);
			if (result.next()) {
				duration = result.getString(DURATION);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return duration;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void bind(DefaultComboBoxModel model){ // bind subjects in a combobox
		model.removeAllElements();
		ArrayList<Subject> subjects = getMultiple("SELECT * FROM " + TABLE_SUBJECTS);
		for(Subject subject : subjects){
			model.addElement(subject.code);
		}
	}

	public static int getLastID() {
		int last_id = 0;
		
		try {
			db.open();
			String sql = "SELECT LAST("+ SUBJECT_ID +") as lastID FROM " + TABLE_SUBJECTS;
			ResultSet result = db.statement.executeQuery(sql);
			if (result.next()) {
				last_id = result.getInt("lastID");
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return last_id;
	}
}
