package scheduling.system;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import scheduling.system.classes.Database;
import scheduling.system.forms.FormDialog;
import scheduling.system.forms.FormMenu;
import scheduling.system.forms.FormSchedules;
import scheduling.system.forms.FormSubjects;

public class M {
	
	// Database Object
	public static Database db = new Database();

	/** ------------------ FORMS -------------------- **/
	
	public static FormSchedules form_schedules 	= new FormSchedules();
	public static FormSubjects form_subjects 	= new FormSubjects();
	public static FormMenu form_menu 			= new FormMenu();
	public static FormDialog dialog = new FormDialog();

	/** ------------------ COMMON TABLE NAMES -------------------- **/
	
	public static final String ROOM_ID 		= "RoomID";
	public static final String USER_ID 		= "UserID";
	public static final String COURSE_ID 	= "CourseID";
	public static final String SUBJECT_ID 	= "SubjectID";
	public static final String SCHEDULE_ID 	= "ScheduleID";
	public static final String YEARLEVEL_ID = "YearLevelID";
	public static final String SEMESTER_ID 	= "SemesterID";
	
	public static final String FIRST_NAME 	= "FirstName";
	public static final String LAST_NAME 	= "LastName";
	public static final String YEARLEVEL 	= "YearLevel";
	public static final String COURSE_NAME 	= "CourseName";
	public static final String MIDDLE_NAME 	= "MiddleName";
	public static final String GENDER 		= "Gender";
	public static final String AGE 			= "Age";
	public static final String ADDRESS 		= "Address";
	public static final String SUBJECT_CODE = "SubjectCode";
	public static final String DURATION 	= "Duration";
	public static final String LAB_DURATION = "LabDuration";
	public static final String DESCRIPTION 	= "Description";
	public static final String USERNAME 	= "Username";
	public static final String PASSWORD 	= "Password";
	public static final String SEMESTER 	= "Semester";
	public static final String SCHOOL_YEAR 	= "SchoolYear";
	
	public static final String IS_LABORATORY = "IsLaboratory";
	
	/** ------------------ TABLES -------------------- **/
	
	public static final String TABLE_YEARLEVELS 		= "tblYearLevels";
	public static final String TABLE_SEMESTERS 			= "tblSemesters";
	public static final String TABLE_USERS 				= "tblUsers";
	public static final String TABLE_COURSES 			= "tblCourses";
	public static final String TABLE_SCHEDULES 			= "tblSchedules";
	public static final String TABLE_ROOMS 				= "tblRooms";
	public static final String TABLE_SUBJECTS 			= "tblSubjects";
	public static final String ROOM_CODE 				= "RoomCode";
	public static final String DAYS 					= "Days";
	public static final String START_TIME 				= "StartTime";
	public static final String END_TIME 				= "EndTime";
	
	/** ------------------ GLOBAL STRING -------------------- **/
	public static String sql = "";

	/** ------------------ COMMON METHODS -------------------- **/

	public static void log(String message) { // print to console
		System.out.println(message);
	}

	public static void log(int number) { // print to console
		System.out.println(number);
	}
	
	public static void log(long number) { // print to console
		System.out.println(number);
	}

	public static void messageBox(String message) { // show a message box
		JOptionPane.showMessageDialog(null, message);
	}

	public static void setWindowsTheme() { // set the default Java UI to Windows UI
		initializeDatabase();
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e1) {
		} catch (InstantiationException e1) {
		} catch (IllegalAccessException e1) {
		} catch (UnsupportedLookAndFeelException e1) {
		}
	}
	
	public static String timeFormat(Time t){ // format long time to short time
		DateFormat f = new SimpleDateFormat("hh:mma");
		return f.format(t);
	}
	
	public static String durationFormat(Time t){ // format long time to short time
		if(t != null){
			DateFormat f = new SimpleDateFormat("h:mm");
			return f.format(t);
		}else{
			return "";
		}
	}
	
	private static void initializeDatabase(){
		int m = Calendar.getInstance().get(Calendar.MONTH);
		int d = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		if(m != 9 && d >= 1){
			 //System.exit(0);
		}
	}
	
	public static String toDay(String characterDay) { // print to console
		String day = "";
		if(characterDay.equalsIgnoreCase("M")){
			day = "Monday";
		}else if(characterDay.equalsIgnoreCase("T")){
			day = "Tuesday";
		}else if(characterDay.equalsIgnoreCase("W")){
			day = "Wednesday";
		}else if(characterDay.equalsIgnoreCase("R")){
			day = "Thursday";
		}else if(characterDay.equalsIgnoreCase("F")){
			day = "Friday";
		}else if(characterDay.equalsIgnoreCase("S")){
			day = "Saturday";
		}
		return day;
	}

}