package scheduling.system.classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.table.DefaultTableModel;

import scheduling.system.M;
import scheduling.system.forms.FormDialog;

public class Schedule extends M { // Class M holds some of the Methods and Constant Variables in this Class
	
	// member variables
	public int id = 0;
	public String days = "";
	public String startTime = "";
	public String endTime = "";
	public int room_id = 0;
	public int subject_id = 0;
	public int course_id = 0;
	public int yearlevel_id = 0;
	public int semester_id = 0;
	public boolean isLab = false;

	public static boolean load(String sql, final DefaultTableModel model) { // bind schedules in a table
		model.setRowCount(0);
		final ArrayList<Schedule> schedules = Schedule.getMultiple(sql);
		
		final FormDialog dialog = new FormDialog();
		dialog.setTitle("Loading Schedules");
		dialog.lblMessage.setText("Please wait....");
		dialog.setVisible(true);
		
		if (schedules != null) {
			Thread t = new Thread(new Runnable() {
							
				boolean running = true;
				
				@Override
				public void run() {
					while(running){
						for (Schedule schedule : schedules) {
							String scheduleData[] = { 
									Long.toString(schedule.id),
									toDay(schedule.days), 
									schedule.startTime,
									schedule.endTime,
									Room.getSingle(schedule.room_id).code,
									Subject.getSingle(schedule.subject_id).code,
									Course.getSingle(schedule.course_id).name,
									YearLevel.getSingle(schedule.yearlevel_id).level + "",
									Semester.getSingle(schedule.semester_id).semester + "",
									schedule.isLab + ""
									};
							model.insertRow(model.getRowCount(), scheduleData);
						}
						running = false;
						dialog.dispose();
					}
				}
			});

			t.start();
		}
		return true;
	}

	public static ArrayList<Schedule> getMultiple(String sql) { // get 1 or more schedule
		ArrayList<Schedule> schedules = new ArrayList<Schedule>();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery(sql);
			while (result.next()) {
				Schedule schedule = new Schedule();
				schedule.id = result.getInt(SCHEDULE_ID);
				schedule.days = result.getString(DAYS);
				schedule.startTime = timeFormat(result.getTime(START_TIME));
				schedule.endTime = timeFormat(result.getTime(END_TIME));
				schedule.room_id = result.getInt(ROOM_ID);
				schedule.subject_id = result.getInt(SUBJECT_ID);
				schedule.course_id = result.getInt(COURSE_ID);
				schedule.yearlevel_id = result.getInt(YEARLEVEL_ID);
				schedule.semester_id = result.getInt(SEMESTER_ID);
				schedule.isLab = result.getBoolean(IS_LABORATORY);
				schedules.add(schedule);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return schedules;
	}
	
	public static Schedule getSingle(long id) { // get a single schedule
		Schedule schedule = new Schedule();
		try {
			db.open();
			ResultSet result = db.statement.executeQuery("SELECT * FROM " + TABLE_SCHEDULES + " WHERE " + SCHEDULE_ID + "=" + id);
			if (result.next()) {
				schedule.id = result.getInt(SCHEDULE_ID);
				schedule.days = result.getString(DAYS);
				schedule.startTime = timeFormat(result.getTime(START_TIME));
				schedule.endTime = timeFormat(result.getTime(END_TIME));
				schedule.room_id = result.getInt(ROOM_ID);
				schedule.subject_id = result.getInt(SUBJECT_ID);
				schedule.course_id = result.getInt(COURSE_ID);
				schedule.yearlevel_id = result.getInt(YEARLEVEL_ID);
				schedule.semester_id = result.getInt(SEMESTER_ID);
				schedule.isLab = result.getBoolean(IS_LABORATORY);
			}
			result.close();
			db.statement.close();
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return schedule;
	}
	
	public boolean add(){ // add a schedule in the database
		if(verify()){
			ArrayList<Schedule> conflictedSchedules = new ArrayList<Schedule>();
			conflictedSchedules = this.getConflicts();
			if(conflictedSchedules.size() == 0){
				try {
					String sql = "INSERT INTO " + 
							TABLE_SCHEDULES + " (" + 
							DAYS + "," + 
							START_TIME + ", " + 
							END_TIME + ", " + 
							ROOM_ID + ", " + 
							SUBJECT_ID + ", " + 
							COURSE_ID + ", " + 
							YEARLEVEL_ID + ", " + 
							SEMESTER_ID + ", " + 
							IS_LABORATORY + 
							") VALUES ('" +
							this.days + "', '" + 
							this.startTime + "', '" + 
							this.endTime + "', " + 
							this.room_id + ", " + 
							this.subject_id + ", " + 
							this.course_id + ", " + 
							this.yearlevel_id + ", " + 
							this.semester_id + ", " + 
							this.isLab + 
							")";
					db.statement.executeUpdate(sql);
					db.statement.close();
					db.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return true;
			}else{
				String warnings = "";
				for(Schedule schedule : conflictedSchedules){
					warnings+= "ID: "+ schedule.id +", Days: " +schedule.days+ 
							", Room: " + schedule.room_id + ", StartTime: " + schedule.startTime + 
							", EndTime: " + schedule.endTime;
				}
				if(warnings.length() > 0){
					messageBox("You can't continue because there is/are conflicts found: \n\n" + warnings);
				}
			}
		}else{
			messageBox("All fields are required.");
		}
		return false;
	}
	
	public static void generate(final int subject_id){
		
		final FormDialog dialog = new FormDialog();
		dialog.setTitle("Generating Schedules");
		dialog.lblMessage.setText("Please wait....");
		dialog.setVisible(true);
		
		final Thread t = new Thread(new Runnable() {
			
			public boolean running = true;
			
			@Override
			public void run() {
				Subject subject = Subject.getSingle(subject_id);
				
				Schedule schedule = new Schedule();
				schedule.course_id = subject.course_id;
				schedule.yearlevel_id = subject.yearlevel_id;
				schedule.semester_id = subject.yearlevel_id;
				schedule.subject_id = subject.id;
				
				//String[] days = {"M"};
				String[] days = {"M", "T", "W", "R", "F", "S"};
				int roomIDs[] = Room.getIDS();
				
				while(running){
					
					DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.AM_PM, Calendar.AM);

					String[] duration = subject.duration.split(":");
					int duration_hours = Integer.parseInt(duration[0]);
					int duration_minutes = Integer.parseInt(duration[1]);
					
					for(int iDays = 0; iDays < days.length; iDays++){
						schedule.days = days[iDays];
						
						int startingHour = 7;
						int startingMinute = 30;
						
						int roomIndex = 0;
						boolean finishedAllRooms = false;
						
						whileloop:
						while(!finishedAllRooms){
							
							if(roomIndex == roomIDs.length){ // no available rooms
								finishedAllRooms = true;
								break whileloop; // proceed to the next day
							}
	
							schedule.room_id = roomIDs[roomIndex];

							// set start time
							cal.set(Calendar.HOUR, startingHour);
							cal.set(Calendar.MINUTE, startingMinute);
							schedule.startTime = dateFormat.format(cal.getTime());
							
							// set end time
							cal.add(Calendar.HOUR, duration_hours);
							cal.add(Calendar.MINUTE, duration_minutes);
							schedule.endTime = dateFormat.format(cal.getTime());
							
							int endHour = cal.get(Calendar.HOUR);
							
							cal.add(Calendar.MINUTE, 1);
							
							startingHour = cal.get(Calendar.HOUR); // reset to 08 hour
							startingMinute = cal.get(Calendar.MINUTE); // reset to 00 minutes
							
							if((endHour >= 9) && (cal.get(Calendar.AM_PM) == Calendar.PM)){ // if over than 8 PM proceed next day
								startingHour = 7; // reset to 08 hour
								startingMinute = 30; // reset to 00 minutes
								roomIndex++; // proceed to the next room
								cal.set(Calendar.AM_PM, Calendar.AM); // reset to AM
							}else{
								if(schedule.getConflicts().size() == 0){ // if there are no conflicts found
									dialog.lblMessage.setText("Please wait....\n\nGenerating Schedules for:\n\nDay: " + toDay(days[iDays]) + "\n\n Room: " + Room.getSingle(schedule.room_id).code + "\n\nStart Time: " + schedule.startTime + ", End Time: " + schedule.endTime);
									schedule.add();
								}
							}
						}
					}
					running = false;
					
					if(subject.isLab){
						dialog.lblMessage.setText("Generating Schedules for Laboratory rooms");
						
						if(generateForLaboratory(subject.id)){
							dialog.dispose();
							M.form_schedules.loadSchedules();
							M.messageBox("Schedules successfully generated.");
						}
					}else{
						dialog.dispose();
						M.form_schedules.loadSchedules();
						M.messageBox("Schedules successfully generated.");
					}
				}
			}
		});
		
		t.start();
	}
	
	public static boolean generateForLaboratory(final int subject_id){

		final Thread t = new Thread(new Runnable() {
			
			public boolean running = true;
			
			@Override
			public void run() {
				Subject subject = Subject.getSingle(subject_id);
				
				Schedule schedule = new Schedule();
				schedule.course_id = subject.course_id;
				schedule.yearlevel_id = subject.yearlevel_id;
				schedule.semester_id = subject.yearlevel_id;
				schedule.subject_id = subject.id;
				schedule.isLab = subject.isLab;
				
				//String[] days = {"M"};
				String[] days = {"M", "T", "W", "R", "F", "S"};
				int roomIDs[] = Room.getLabIDS();
				
				while(running){
					
					DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.AM_PM, Calendar.AM);

					String[] duration = subject.lab_duration.split(":");
					int duration_hours = Integer.parseInt(duration[0]);
					int duration_minutes = Integer.parseInt(duration[1]);
					
					for(int iDays = 0; iDays < days.length; iDays++){
						schedule.days = days[iDays];
						
						int startingHour = 7;
						int startingMinute = 30;
						
						int roomIndex = 0;
						boolean finishedAllRooms = false;
						
						whileloop:
						while(!finishedAllRooms){
							
							if(roomIndex == roomIDs.length){ // no available rooms
								finishedAllRooms = true;
								break whileloop; // proceed to the next day
							}
	
							schedule.room_id = roomIDs[roomIndex];

							// set start time
							cal.set(Calendar.HOUR, startingHour);
							cal.set(Calendar.MINUTE, startingMinute);
							schedule.startTime = dateFormat.format(cal.getTime());
							
							// set end time
							cal.add(Calendar.HOUR, duration_hours);
							cal.add(Calendar.MINUTE, duration_minutes);
							schedule.endTime = dateFormat.format(cal.getTime());
							
							int endHour = cal.get(Calendar.HOUR);
							
							cal.add(Calendar.MINUTE, 1);
							
							startingHour = cal.get(Calendar.HOUR); // reset to 08 hour
							startingMinute = cal.get(Calendar.MINUTE); // reset to 00 minutes
							
							if((endHour >= 9) && (cal.get(Calendar.AM_PM) == Calendar.PM)){ // if over than 8 PM proceed next day
								startingHour = 7; // reset to 08 hour
								startingMinute = 30; // reset to 00 minutes
								roomIndex++; // proceed to the next room
								cal.set(Calendar.AM_PM, Calendar.AM); // reset to AM
							}else{
								if(schedule.getConflicts().size() == 0){ // if there are no conflicts found
									schedule.add();
								}
							}
						}
					}
					running = false;
				}
			}
		});
		
		t.start();

		while(t.isAlive()){}
		return true;
	}
	
	private ArrayList<Schedule> getConflicts(){
		ArrayList<Schedule> conflicted_schedules = new ArrayList<Schedule>();
		try {
			db.open();
			String sql = "SELECT "+ SCHEDULE_ID +" FROM " + TABLE_SCHEDULES + " WHERE " + 
			ROOM_ID +" = " + this.room_id + " AND " + 
			SEMESTER_ID +" = " + this.semester_id + " ";
			
			char days[] = this.days.toCharArray();
			for(int i = 0; i < days.length; i++){
				if(i == 0){
					sql+= "AND (" + DAYS + " LIKE '%" + Character.toString(days[i]) + "%' ";
				}else{
					sql+= "OR " + DAYS + " LIKE '%" + Character.toString(days[i]) + "%'";
				}
			}
			
			sql += ") AND ((" + START_TIME + " BETWEEN TIMEVALUE('"+ this.startTime +"') AND TIMEVALUE('"+ this.endTime +"')) OR (" + 
								END_TIME + " BETWEEN TIMEVALUE('"+ this.startTime +"') AND TIMEVALUE('"+ this.endTime +"')))";
			
			ResultSet result = db.statement.executeQuery(sql);
			
			while(result.next()){
				Schedule schedule = new Schedule();
				schedule = getSingle(result.getLong(SCHEDULE_ID));
				conflicted_schedules.add(schedule);
			}
			result.close();
			
		} catch (SQLException e) {e.printStackTrace();}
		return conflicted_schedules;
	}
	
	public boolean delete(){ // delete a schedule in the database
		boolean successful = false;
		try {
			db.open();
			String sql = "DELETE * FROM " + TABLE_SCHEDULES + " WHERE " + SCHEDULE_ID + "=" + this.id;
			db.statement.executeUpdate(sql);
			db.statement.close();
			db.close();
			successful = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return successful;
	}
	
	public static boolean deleteBySubjectID(int subject_id){ // delete a schedule in the database
		boolean successful = false;
		try {
			db.open();
			String sql = "DELETE * FROM " + TABLE_SCHEDULES + " WHERE " + SUBJECT_ID + "=" + subject_id;
			db.statement.executeUpdate(sql);
			db.statement.close();
			db.close();
			successful = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return successful;
	}
	
	public boolean update(){ // update a schedule in the database
		boolean successful = false;
		if(verify()){
			try {
				db.open();
				String sql = "UPDATE " + TABLE_SCHEDULES + " SET " + 
						DAYS + "='" + this.days + "', " +
						START_TIME + "='" + this.startTime + "', " +
						END_TIME + "='" + this.endTime + "', " +
						ROOM_ID + "= " + this.room_id + ", " +
						SUBJECT_ID + "= " + this.subject_id + ", " +
						COURSE_ID + "= " + this.course_id + ", " +
						YEARLEVEL_ID + "= " + this.yearlevel_id + ", " +
						SEMESTER_ID + "= " + this.semester_id + ", " +
						IS_LABORATORY + "= " + this.isLab + " " +
						"WHERE " + SCHEDULE_ID + "=" + this.id;
				db.statement.executeUpdate(sql);
				db.statement.close();
				db.close();
				successful = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			messageBox("All fields are required.");
		}
		return successful;
	}

	private boolean verify(){
		if(
				this.days.length() > 0 && 
				this.startTime.length() > 0 && 
				this.endTime.length() > 0 && 
				this.room_id != 0 && 
				this.subject_id != 0 && 
				this.course_id != 0 &&
				this.yearlevel_id != 0 && 
				this.semester_id != 0
				){
			return true;
		}else{
			return false;
		}
	}
}
