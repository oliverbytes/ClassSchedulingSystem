package scheduling.system.classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

import scheduling.system.M;

public class Semester extends M { // Class M holds some of the Methods and Constant Variables in this Class

	// member variables
	public int id = 0;
	public int semester = 0;
	
	public static ArrayList<Semester> getMultiple(String sql) { // get 1 or more semester
		ArrayList<Semester> semesters = new ArrayList<Semester>();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery(sql);
			while (result.next()) {
				Semester sem = new Semester();
				sem.semester = result.getInt(SEMESTER);
				sem.id = result.getInt(SEMESTER_ID);
				semesters.add(sem);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return semesters;
	}

	public static Semester getSingle(int id) { // get a single semester
		Semester sem = new Semester();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery("SELECT * FROM " + TABLE_SEMESTERS + " WHERE " + SEMESTER_ID + "=" + id);
			if(result.next()) {
				sem.id = result.getInt(SEMESTER_ID);
				sem.semester = result.getInt(SEMESTER);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sem;
	}
	
	public static Semester getSingle(String semNum) { // get a single semester
		Semester sem = new Semester();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery("SELECT * FROM " + TABLE_SEMESTERS + " WHERE " + SEMESTER + "=" + semNum);
			if(result.next()) {
				sem.id = result.getInt(SEMESTER_ID);
				sem.semester = result.getInt(SEMESTER);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sem;
	}
	
	public void add() { // add a semester in the database
		if (verify() == true) {
			if(!exists(semester)){
				try {
					db.open();
					sql = "INSERT INTO " + TABLE_SEMESTERS + " (" + YEARLEVEL + ") VALUES ('" + this.semester + "')";
					db.statement.executeUpdate(sql);
					db.statement.close();
					db.close();
					messageBox("Semester " + semester + " successfully added");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else{
				messageBox("Semester Code already exists.");
			}
		} else {
			messageBox("All fields are required.");
		}
	}

	public boolean update() { // update a semester in the database
		boolean successful = false;
		if (verify() == true) {
			if(!exists(semester)){
				try {
					db.open();
					db.statement.executeUpdate("UPDATE " + TABLE_SEMESTERS + " SET "
									+ YEARLEVEL + " = '" + this.semester + "' " + 
									" WHERE " + SEMESTER_ID + " = " + this.id);
					db.statement.close();
					db.close();
					successful = true;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else{
				messageBox("Semester Code already exists.");
			}
			
		} else {
			messageBox("All fields are required.");
		}
		return successful;
	}

	public boolean delete() { // delete a semester in the database
		boolean successful = false;
		try {
			db.open();
			db.statement.executeUpdate("DELETE * FROM " + TABLE_SEMESTERS
					+ " WHERE " + SEMESTER_ID + " = " + this.id);
			db.statement.close();
			db.close();
			successful = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return successful;
	}
	
	public boolean verify() { // verify if the semester semester is filled in
		if (this.semester != 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void load(String sql, DefaultTableModel model) { // load 1 or more semester in a table
		model.setRowCount(0);
		ArrayList<Semester> semester = Semester.getMultiple(sql);
		if (semester != null) {
			for (Semester semester1 : semester) {
				String semesterData[] = { 
						Integer.toString(semester1.semester), 
						Long.toString(semester1.id) };
				model.insertRow(model.getRowCount(), semesterData);
			}
		}
	}
	
	public static boolean exists(int semester) { // check if the semester already exists
		boolean doesExists = false;
		try {
			db.open();
			String sql = "SELECT COUNT(" + SEMESTER_ID
					+ ") as Count FROM " + TABLE_SEMESTERS + " WHERE "
					+ YEARLEVEL + "=" + semester;
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void bind(DefaultComboBoxModel model){ // bind semester in a combobox
		model.removeAllElements();
		ArrayList<Semester> semesters = getMultiple("SELECT * FROM " + TABLE_SEMESTERS);
		for(Semester semester1 : semesters){
			model.addElement(semester1.semester);
		}
	}
}
