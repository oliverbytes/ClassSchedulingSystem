package scheduling.system.classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

import scheduling.system.M;

public class Course extends M { // Class M holds some of the Methods and Constant Variables in this Class

	// member variables
	public int id = 0;
	public String name = "";
	
	public static ArrayList<Course> getMultiple(String sql) { // get 1 or more name
		ArrayList<Course> courses = new ArrayList<Course>();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery(sql);
			while (result.next()) {
				Course sem = new Course();
				sem.name = result.getString(COURSE_NAME);
				sem.id = result.getInt(COURSE_ID);
				courses.add(sem);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return courses;
	}

	public static Course getSingle(long id) { // get a single name
		Course sem = new Course();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery("SELECT * FROM " + TABLE_COURSES + " WHERE " + COURSE_ID + "=" + id);
			if(result.next()) {
				sem.id = result.getInt(COURSE_ID);
				sem.name = result.getString(COURSE_NAME);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sem;
	}
	
	public static Course getSingle(String name) { // get a single name
		Course sem = new Course();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery("SELECT * FROM " + TABLE_COURSES + " WHERE " + COURSE_NAME + "='" + name + "'");
			if(result.next()) {
				sem.id = result.getInt(COURSE_ID);
				sem.name = result.getString(COURSE_NAME);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sem;
	}
	
	public void add() { // add a name in the database
		if (verify() == true) {
			if(!exists(name)){
				try {
					db.open();
					sql = "INSERT INTO " + TABLE_COURSES + " (" + COURSE_NAME + ") VALUES ('" + this.name + "')";
					db.statement.executeUpdate(sql);
					db.statement.close();
					db.close();
					messageBox("Course " + name + " successfully added");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else{
				messageBox("Course Code already exists.");
			}
		} else {
			messageBox("All fields are required.");
		}
	}

	public boolean update() { // update a name in the database
		boolean successful = false;
		if (verify() == true) {
			if(!exists(name)){
				try {
					db.open();
					db.statement.executeUpdate("UPDATE " + TABLE_COURSES + " SET "
									+ COURSE_NAME + " = '" + this.name + "' " + 
									" WHERE " + COURSE_ID + " = " + this.id);
					db.statement.close();
					db.close();
					successful = true;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else{
				messageBox("Course Code already exists.");
			}
			
		} else {
			messageBox("All fields are required.");
		}
		return successful;
	}

	public boolean delete() { // delete a name in the database
		boolean successful = false;
		try {
			db.open();
			db.statement.executeUpdate("DELETE * FROM " + TABLE_COURSES
					+ " WHERE " + COURSE_ID + " = " + this.id);
			db.statement.close();
			db.close();
			successful = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return successful;
	}
	
	public boolean verify() { // verify if the name name is filled in
		if (!this.name.equals("")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void load(String sql, DefaultTableModel model) { // load 1 or more name in a table
		model.setRowCount(0);
		ArrayList<Course> courses = Course.getMultiple(sql);
		if (courses != null) {
			for (Course course : courses) {
				String nameData[] = { 
						course.name,
						Long.toString(course.id) };
				model.insertRow(model.getRowCount(), nameData);
			}
		}
	}
	
	public static boolean exists(String name) { // check if the name already exists
		boolean doesExists = false;
		try {
			db.open();
			String sql = "SELECT COUNT(" + COURSE_ID
					+ ") as Count FROM " + TABLE_COURSES + " WHERE "
					+ COURSE_NAME + "='" + name + "'";
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
	public static void bind(DefaultComboBoxModel model){ // bind name in a combobox
		model.removeAllElements();
		ArrayList<Course> courses = getMultiple("SELECT * FROM " + TABLE_COURSES);
		for(Course name1 : courses){
			model.addElement(name1.name);
		}
	}
}
