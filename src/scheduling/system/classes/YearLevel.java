package scheduling.system.classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

import scheduling.system.M;

public class YearLevel extends M { // Class M holds some of the Methods and Constant Variables in this Class

	// member variables
	public int id = 0;
	public int level = 0;
	
	public static ArrayList<YearLevel> getMultiple(String sql) { // get 1 or more yearlevel
		ArrayList<YearLevel> yearlevels = new ArrayList<YearLevel>();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery(sql);
			while (result.next()) {
				YearLevel yearlevel = new YearLevel();
				yearlevel.level = result.getInt(YEARLEVEL);
				yearlevel.id = result.getInt(YEARLEVEL_ID);
				yearlevels.add(yearlevel);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return yearlevels;
	}

	public static YearLevel getSingle(long id) { // get a single yearlevel
		YearLevel yearlevel = new YearLevel();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery("SELECT * FROM " + TABLE_YEARLEVELS + " WHERE " + YEARLEVEL_ID + "=" + id);
			if(result.next()) {
				yearlevel.id = result.getInt(YEARLEVEL_ID);
				yearlevel.level = result.getInt(YEARLEVEL);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return yearlevel;
	}
	
	public static YearLevel getSingle(String level) { // get a single yearlevel
		YearLevel yearlevel = new YearLevel();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery("SELECT * FROM " + TABLE_YEARLEVELS + " WHERE " + YEARLEVEL + "=" + level);
			if(result.next()) {
				yearlevel.id = result.getInt(YEARLEVEL_ID);
				yearlevel.level = result.getInt(YEARLEVEL);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return yearlevel;
	}
	
	public void add() { // add a yearlevel in the database
		if (verify() == true) {
			if(!exists(level)){
				try {
					db.open();
					sql = "INSERT INTO " + TABLE_YEARLEVELS + " (" + YEARLEVEL + ") VALUES ('" + this.level + "')";
					db.statement.executeUpdate(sql);
					db.statement.close();
					db.close();
					messageBox("Year Level " + level + " successfully added");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else{
				messageBox("Year Level Code already exists.");
			}
		} else {
			messageBox("All fields are required.");
		}
	}

	public boolean update() { // update a yearlevel in the database
		boolean successful = false;
		if (verify() == true) {
			if(!exists(level)){
				try {
					db.open();
					db.statement.executeUpdate("UPDATE " + TABLE_YEARLEVELS + " SET "
									+ YEARLEVEL + " = '" + this.level + "' " + 
									" WHERE " + YEARLEVEL_ID + " = " + this.id);
					db.statement.close();
					db.close();
					successful = true;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else{
				messageBox("Year Level Code already exists.");
			}
			
		} else {
			messageBox("All fields are required.");
		}
		return successful;
	}

	public boolean delete() { // delete a yearlevel in the database
		boolean successful = false;
		try {
			db.open();
			db.statement.executeUpdate("DELETE * FROM " + TABLE_YEARLEVELS
					+ " WHERE " + YEARLEVEL_ID + " = " + this.id);
			db.statement.close();
			db.close();
			successful = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return successful;
	}
	
	public boolean verify() { // verify if the yearlevel level is filled in
		if (this.level != 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void load(String sql, DefaultTableModel model) { // load 1 or more yearlevels in a table
		model.setRowCount(0);
		ArrayList<YearLevel> yearlevels = YearLevel.getMultiple(sql);
		if (yearlevels != null) {
			for (YearLevel yearlevel : yearlevels) {
				String yearlevelData[] = { 
						Integer.toString(yearlevel.level), 
						Long.toString(yearlevel.id) };
				model.insertRow(model.getRowCount(), yearlevelData);
			}
		}
	}
	
	public static boolean exists(int level) { // check if the yearlevel already exists
		boolean doesExists = false;
		try {
			db.open();
			String sql = "SELECT COUNT(" + YEARLEVEL_ID
					+ ") as Count FROM " + TABLE_YEARLEVELS + " WHERE "
					+ YEARLEVEL + "=" + level;
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
	public static void bind(DefaultComboBoxModel model){ // bind yearlevels in a combobox
		model.removeAllElements();
		ArrayList<YearLevel> yearlevels = getMultiple("SELECT * FROM " + TABLE_YEARLEVELS);
		for(YearLevel yearlevel : yearlevels){
			model.addElement(yearlevel.level);
		}
	}
}
