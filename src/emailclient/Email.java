package emailclient;

import java.io.Serializable;

public class Email implements Serializable {
	private String emailAddress;
	private String subject;
	private String content;
	private String sentDate;
	
	public Email(String emailAddress, String subject, String content, String sentDate) {
		this.emailAddress = emailAddress;
		this.subject = subject;
		this.content = content;
		this.sentDate = sentDate;
	}
	public String getEmailAddress() {return emailAddress;}
	
	public String getSubject() {return subject;}
	
	public String getContent() {return content;}
	
	public String getSentDate() {return sentDate;}
	
	public String getDetails() {return emailAddress + ", " + subject + ", " + content;}
}


