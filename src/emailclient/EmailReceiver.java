package emailclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

public class EmailReceiver extends Thread {
	private String username;
	private String password;
	private Properties properties;
	private Session emailSession;
	private Store store;
	private Folder inbox;
	private Flags seen;
	private FlagTerm unseenFlagTerm;
	
	private ArrayList<EmailStatObserver> observers;
	private MyBlockingQueue queue;
	
	public EmailReceiver(String username, String password, MyBlockingQueue queue) {
		this.username = username;
		this.password = password;
		observers = new ArrayList<EmailStatObserver>();
		this.queue = queue;
		initialize();
	}
	
	private void initialize() {
		//setting up the settings for receiving email
		properties = new Properties();
		properties.put("mail.store.protocol", "imaps");	
		emailSession = Session.getInstance(properties);
		
		try {
			store = emailSession.getStore();
			store.connect("pop.gmail.com", username, password);
			
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	private void receiveEmail() {
		//receive unread email from the server
		Message[] messages = null;
		try {
			inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_WRITE);
			
			seen = new Flags(Flags.Flag.SEEN);
			unseenFlagTerm = new FlagTerm(seen, false);
			
			messages = inbox.search(unseenFlagTerm);
			
			for (Message msg: messages) {
				msg.setFlag(Flags.Flag.SEEN, true);
				String sender = msg.getFrom()[0].toString();
				String subject = msg.getSubject();
				String content = msg.getContent().toString();
				String sentDate = msg.getSentDate().toString();
				queue.enqueue(new Email(sender,subject,content,sentDate));
				notifyObservers();
			}
			inbox.close(false);
		} catch (MessagingException | IOException e) {
			e.printStackTrace();
			}
   }
	
	public void addObsever(EmailStatObserver observer) {
		observers.add(observer);
	}
	
	public void removeObserver(EmailStatObserver observer) {
		observers.remove(observer);
	}
	
	private void notifyObservers() {
		for (EmailStatObserver observer : observers) {
			observer.printEmail();
		}
	}
	
	public void closeAll() {
		try {
			store.close();
		} catch (MessagingException e) {e.printStackTrace();}
	}
	
	public void run() {
		while(true) {
			receiveEmail();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}			
		}
	}
}
