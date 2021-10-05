package emailclient;

public abstract class Recipient {
	protected String name;
	protected String emailAddress;
	private static int num_of_recipients = 0;
	
	public Recipient(String name, String emailAddress) {
		this.name = name;
		this.emailAddress = emailAddress;
		num_of_recipients++;
	}

	public String getEmailAddress() {return emailAddress;}
	
	public abstract String getDetails();

	public static int getNum_of_recipients() {
		return num_of_recipients;
	}
}
