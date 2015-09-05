package scheduling.system.classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import scheduling.system.M;

public class User extends M { // Class M holds some of the Methods and Constant Variables in this Class
	
	// member variables
	public long id = 0;
	public String username = "";
	public String password = "";

	public boolean authenticate() { // check if the username and password is in the database
		boolean isUserExist = false;
		if(verify()){
			try {
				db.open();
				sql = "SELECT COUNT(" + USER_ID + ") as UserCount FROM "
						+ TABLE_USERS + " WHERE " + USERNAME + "='" + this.username
						+ "' AND " + PASSWORD + "='" + this.password + "'";
				ResultSet result = db.statement.executeQuery(sql);
				if(result.next()) {
					if (result.getInt("UserCount") > 0) {
						isUserExist = true;
					} else {
						isUserExist = false;
						messageBox("Incorrect username or password.");
					}
				} else {
					isUserExist = false;
					messageBox("Incorrect username or password.");
				}
				result.close();
				db.statement.close();
				db.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			messageBox("You must enter a username and a password inorder to login.");
		}
		
		return isUserExist;
	}
	
	public boolean userExists(){ // check if the username already exists
		boolean isUserExist = false;
		try {
			db.open();
			sql = "SELECT COUNT(" + USER_ID + ") as UserCount FROM " + TABLE_USERS + " WHERE " + USERNAME + "='" + this.username + "'";
			ResultSet result = db.statement.executeQuery(sql);
			if(result.next()) {
				if (result.getInt("UserCount") > 0) {
					isUserExist = true;
				} else {
					isUserExist = false;
				}
			} else {
				isUserExist = false;
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isUserExist;
	}

	public void add() { // add a student in the database
		if (verify() == true) {
			if(!userExists()){
				try {
					db.open();
					sql = "INSERT INTO " + TABLE_USERS + " (" + 
							USERNAME + ", "+ 
							PASSWORD + 
							") VALUES ('" + 
							this.username + "', '"+ 
							this.password + "')";
					db.statement.executeUpdate(sql);
					db.statement.close();
					db.close();
					messageBox(username + " successfully added");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else{
				messageBox("Username already exists.");
			}
			
		} else {
			messageBox("All fields are required.");
		}
	}

	public boolean update() { // update a student in the database
		boolean successful = false;
		if (verify() == true) {
			try {
				db.open();
				db.statement.executeUpdate("UPDATE " + TABLE_USERS + " SET " + 
						USERNAME + " = '" + this.username + "', " + 
						PASSWORD + " = '" + this.password + "' " +
						" WHERE " + USER_ID + " = " + this.id);
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

	public boolean delete() { // delete a student in the database
		boolean successful = false;
		try {
			db.open();
			db.statement.executeUpdate("DELETE * FROM " + TABLE_USERS+ " WHERE " + USER_ID + " = " + this.id);
			db.statement.close();
			db.close();
			successful = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return successful;
	}

	private boolean verify() { // verify if username and passwords are filled in
		if (this.username.length() >= 1 && this.password.length() >= 1) {
			return true;
		} else {
			return false;
		}
	}

	public static ArrayList<User> get(String sql){ // get 1 or more user
		try {
			ArrayList<User> users = new ArrayList<User>();
			db.open();
			ResultSet result = db.statement.executeQuery(sql);
			int rows = 0;
			while (result.next()) {
				User user = new User();
				user.id = result.getInt(USER_ID);
				user.username = result.getString(USERNAME);
				user.password = result.getString(PASSWORD);
				users.add(user);
				rows += 1;
			}
			result.close();
			db.statement.close();
			db.close();
			if (rows > 0) {
				return users;
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void load(String sql, DefaultTableModel model){ // bind users in a table
		model.setRowCount(0);
		ArrayList<User> users = get(sql);
		if(users != null){
			for (User user : users) {
				String userData[] = { Long.toString(user.id), user.username, user.password};
				model.insertRow(model.getRowCount(), userData);
			}
		}
	}
}
