package emailclient;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class EmailStatObserver {
	private String currentTime;
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	
	public EmailStatObserver() {
		currentTime = "";
	}
	
	public abstract void printEmail();
	
	protected String getMessage() {
		//captures the current time
		currentTime = timeFormat.format(Calendar.getInstance().getTime());
		return "an email is received at " + currentTime;
	}
}
