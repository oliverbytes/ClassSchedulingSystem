package scheduling.system.classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

import scheduling.system.M;

public class Room extends M { // Class M holds some of the Methods and Constant Variables in this Class

	// member variables
	public int id = 0;
	public String code = "";
	public boolean isLab = false;
	
	public static ArrayList<Room> getMultiple(String sql) { // get 1 or more room
		ArrayList<Room> rooms = new ArrayList<Room>();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery(sql);
			while (result.next()) {
				Room room = new Room();
				room.id = result.getInt(ROOM_ID);
				room.code = result.getString(ROOM_CODE);
				room.isLab = result.getBoolean(IS_LABORATORY);
				rooms.add(room);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rooms;
	}

	public static Room getSingle(long id) { // get a single room
		Room room = new Room();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery("SELECT * FROM " + TABLE_ROOMS + " WHERE " + ROOM_ID + "=" + id);
			if(result.next()) {
				room.id = result.getInt(ROOM_ID);
				room.code = result.getString(ROOM_CODE);
				room.isLab = result.getBoolean(IS_LABORATORY);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return room;
	}
	
	public static Room getSingle(String code) { // get a single room
		Room room = new Room();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery("SELECT * FROM " + TABLE_ROOMS + " WHERE " + ROOM_CODE + "='" + code + "'");
			if(result.next()) {
				room.id = result.getInt(ROOM_ID);
				room.code = result.getString(ROOM_CODE);
				room.isLab = result.getBoolean(IS_LABORATORY);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return room;
	}
	
	public void add() { // add a room in the database
		if (verify() == true) {
			if(!exists(code)){
				try {
					db.open();
					sql = "INSERT INTO " + TABLE_ROOMS + " (" + ROOM_CODE + ", " + IS_LABORATORY + ") VALUES ('" + this.code + "', '" + this.isLab + "')";
					db.statement.executeUpdate(sql);
					db.statement.close();
					db.close();
					messageBox("Room " + code + " successfully added");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else{
				messageBox("Room Code already exists.");
			}
		} else {
			messageBox("All fields are required.");
		}
	}

	public boolean update() { // update a room in the database
		boolean successful = false;
		if (verify() == true) {
			if(!exists(code)){
				try {
					db.open();
					db.statement.executeUpdate(
									"UPDATE " + TABLE_ROOMS + " SET "
									+ ROOM_CODE + " = '" + this.code + "', "
									+ isLab + " = '" + this.isLab + "' " + 
									" WHERE " + ROOM_ID + " = " + this.id
									);
					db.statement.close();
					db.close();
					successful = true;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else{
				messageBox("Room Code already exists.");
			}
			
		} else {
			messageBox("All fields are required.");
		}
		return successful;
	}

	public boolean delete() { // delete a room in the database
		boolean successful = false;
		try {
			db.open();
			db.statement.executeUpdate(
					"DELETE * FROM " + TABLE_ROOMS
					+ " WHERE " + ROOM_ID + " = " + this.id
					);
			db.statement.close();
			db.close();
			successful = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return successful;
	}
	
	public boolean verify() { // verify if the room code is filled in
		if (this.code.length() >= 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void load(String sql, DefaultTableModel model) { // load 1 or more rooms in a table
		model.setRowCount(0);
		ArrayList<Room> rooms = Room.getMultiple(sql);
		if (rooms != null) {
			for (Room room : rooms) {
				String roomData[] = { 
						room.code, 
						Long.toString(room.id) };
				model.insertRow(model.getRowCount(), roomData);
			}
		}
	}
	
	public static boolean exists(String roomCode) { // check if the room already exists
		boolean doesExists = false;
		try {
			db.open();
			String sql = "SELECT COUNT(" + ROOM_ID
					+ ") as Count FROM " + TABLE_ROOMS + " WHERE "
					+ ROOM_CODE + "='" + roomCode + "'";
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
	public static void bind(DefaultComboBoxModel model){ // bind rooms in a combobox
		model.removeAllElements();
		ArrayList<Room> rooms = getMultiple("SELECT * FROM " + TABLE_ROOMS);
		for(Room room : rooms){
			model.addElement(room.code);
		}
	}

	public static int[] getIDS() {
		ArrayList<Room> rooms = getMultiple("SELECT * FROM " + TABLE_ROOMS + " WHERE " + IS_LABORATORY + " = FALSE");
		int[] roomIDS = new int[rooms.size()];
		int index = 0;
		for(Room room : rooms){
			roomIDS[index] = room.id;
			index++;
		}
		return roomIDS;
	}
	
	public static int[] getLabIDS() {
		ArrayList<Room> rooms = getMultiple("SELECT * FROM " + TABLE_ROOMS + " WHERE " + IS_LABORATORY + " = TRUE");
		int[] roomIDS = new int[rooms.size()];
		int index = 0;
		for(Room room : rooms){
			roomIDS[index] = room.id;
			index++;
		}
		return roomIDS;
	}
}
