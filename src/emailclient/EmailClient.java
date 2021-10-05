//180176R
package emailclient;

//import libraries
import java.util.Scanner;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Properties;
import java.text.SimpleDateFormat;

public class EmailClient {
	private ArrayList<IBirthdayGreeting> bdayRecipients;//personal and official friend objects
	private ArrayList<Recipient> recipients; //all the recipient objects;
	private ArrayList<Email> emailList; //all email objects
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	
	private InputOutputStream ios;
	private Scanner input;
	private Client client;
	
	private MyBlockingQueue queue;
	private EmailReceiver receiver;
	private EmailStatPrinter esp;
	private EmailStatRecorder esr;
	private ReceivedEmailSerialize res;
	
	public EmailClient() {
		bdayRecipients = new ArrayList<IBirthdayGreeting>();
		recipients = new ArrayList<Recipient>();
		ios = new InputOutputStream();
		input = new Scanner(System.in);
		client = new Client();
		queue = new MyBlockingQueue(100);
		receiver = new EmailReceiver(client.getClientEmail(),client.getClientPassword(),queue);
		res = new ReceivedEmailSerialize(queue,ios);
		esp = new EmailStatPrinter();
		esr = new EmailStatRecorder();
		receiver.addObsever(esp);
		receiver.addObsever(esr);
	}

	@SuppressWarnings("unchecked")
	public void start() {
		//starting all the threads
		receiver.start();
		res.start();
		
		//initializing the recipient objects	
		ArrayList<String> recipientDetailsList = ios.retrieveRecipients(); //retrieve recipient information from the text file
		if (recipientDetailsList == null) {
			recipientDetailsList = new ArrayList<>();
		} else {
			for(String recipientDetails : recipientDetailsList) makeRecipient(recipientDetails); //make recipient objects
		}
		
		//initializing the email objects
		emailList = (ArrayList<Email>)ios.deserializeObject("emailDetails.sar");
		if (emailList == null) {
			emailList = new ArrayList<>(); //creating a new array if not existing
		}
		
		//sending out all the birthday wishes for today
		String lastBirthdaySentDate = (String)ios.deserializeObject("lastBirthdaySentDate.sar");
		if (lastBirthdaySentDate == null) {
			sendBirthdayWishes(); //sending out all the birthday wishes
			ios.serializeObject(dateFormat.format(new Date()),"lastBirthdaySentDate.sar");
		} else if (!lastBirthdaySentDate.equals(dateFormat.format(new Date()))) {//checking if the birthday wishes are sent out for today
			sendBirthdayWishes(); //sending out all the birthday wishes
			ios.serializeObject(dateFormat.format(new Date()),"lastBirthdaySentDate.sar");
		}
		
		

		menu:
		while (true) {
			System.out.println("Enter option type: \n"
					  + "1 - Adding a new recipient\n"
				      + "2 - Sending an email\n"
				      + "3 - Printing out all the recipients who have birthdays\n"
				      + "4 - Printing out details of all the emails sent\n"
				      + "5 - Printing out the number of recipient objects in the application\n"
				      + "6 - Change email account\n"
				      + "7 - Exit");
			  
			int option = 0;
			try {option = input.nextInt();}
			catch (InputMismatchException e) { //handling invalid input type
				input.nextLine();
				printFooter(true);
				continue menu;
			}
			
			input.nextLine();
			String inputText = "";
			String date;
			  
			switch(option){
				case 1: //adding a new recipient
					System.out.println("Format:\n"
							+ "Personal: <name>, <nickname>, <email>, <birthday>\n"
							+ "Official: <name>, <email>, <designation>\n"
							+ "Office_friend: <name>, <email>, <designation>, <birthday>");
					inputText = input.nextLine();
					try {makeRecipient(inputText); //creating recipient object
					} catch(java.lang.Exception e) { //handling invalid inputs
						printFooter(true);
						continue menu;
					}
					ios.saveRecipient(inputText); //saving the recipient object details to a text file 
					break;
					 
				case 2: // sending an email
					System.out.println("Format:\n<email>, <subject>, <message>");
			    	//format <email>, <subject>, <content>
					String[] emailDetails  = input.nextLine().trim().split(",");
					if (emailDetails.length != 3) {
						printFooter(true);
						continue menu;
					}
					for (int i = 0; i < emailDetails.length; i++)
						emailDetails[i] = emailDetails[i].trim(); //removing whitespace if there's any
					Email email = new Email(emailDetails[0],emailDetails[1],emailDetails[2],dateFormat.format(new Date()));
					emailList.add(email);
					sendEmail(email);
					break;
					 
			    case 3: //printing recipients who have birthdays on the given date
				    System.out.println("Format:\nyyyy/MM/dd");
			    	date = input.nextLine();
			    	for (Recipient recipient: recipients) {
			    		if (recipient.getClass()!=Official.class) { //considering only Personal and OfficialFriend classes
				    		String bday = ((IBirthdayGreeting)recipient).getBirthday();
				    		if (bday.equals(date)) System.out.println(recipient.getDetails());
			    		}
			    	}
				    break;
				    
			    case 4: //printing out all the emails sent on the given date
			    	System.out.println("Format:\nyyyy/MM/dd");
			    	date = input.nextLine();
			    	for (Email e: emailList) {
			    		if(date.equals(e.getSentDate())) {
			    			System.out.println(e.getDetails());
			    		}
			    	}
				    break;
				    
			    case 5: //printing number of recipient objects
			    	System.out.println("Number of recipient objects: " 
			    			+ Integer.toString(Recipient.getNum_of_recipients()));
				    break;
				    
			    case 6: //changing email account
			    	client.clientLogin(input);
			    	break;
			    	
			    case 7: //exiting the client
			    	ios.serializeObject(emailList,"emailDetails.sar");
			    	receiver.closeAll();
			    	break menu;
			}
			if (option < 1 || option > 7) System.out.println("Invalid input");
			printFooter(false);
		}
		input.close();
	}

	private class Client {
		private String clientName;
		private String clientEmail;
		private String clientPassword;
		private InputOutputStream ios;
		
		public Client(){
			ios = new InputOutputStream();
			String[] clientDetails = (String[])ios.deserializeObject("clientDetails.sar");
			if (clientDetails == null) { //check whether client details are empty. Taking inputs if they're empty.
				clientLogin(input);
			} else {
				clientName = clientDetails[0];
				clientEmail = clientDetails[1];
				clientPassword = clientDetails[2];
			}
		}
		
		public String getClientName() {return clientName;}
		public String getClientEmail() {return clientEmail;}
		public String getClientPassword() {return clientPassword;}
		
		public void clientLogin(Scanner input){
			System.out.print("Your name: ");
	    	this.clientName = input.nextLine();
	    	System.out.print("Your email: ");
	    	this.clientEmail = input.nextLine();
	    	System.out.print("Password: ");
	    	this.clientPassword = input.nextLine();
	    	ios.serializeObject(new String[] {clientName, clientEmail, clientPassword},"clientDetails.sar");
		}
	}
	
	private void printFooter(boolean Error) {
		if (Error) System.out.println("Invalid Input");
		System.out.println("Press enter to go to the menu");
		input.nextLine();
		System.out.println("============================");
	}
	
	private void makeRecipient(String details) {
		 //creating recipient object
		 String[] recipientDetailsArray  = details.split(":");
		 String recipientType = recipientDetailsArray[0];
		 String[] recipientAttributes = recipientDetailsArray[1].trim().split(",");
		 for (int i = 0; i < recipientAttributes.length; i++)
			 recipientAttributes[i] = recipientAttributes[i].trim(); //removing whitespace if there's any
		 
		 SimpleDateFormat bdayFormat = new SimpleDateFormat("MM/dd");
		 Recipient recipient;
		 if (recipientType.equals("Personal")) {
			 //format Personal: <name>,<nick-name>,<email>,<recipient's birthday>
			 recipient = new Personal(recipientAttributes[0],recipientAttributes[1],recipientAttributes[2],recipientAttributes[3]);
			 recipients.add(recipient);
			 //maintaining a list of recipients to whom a birthday greeting should be sent 
			 if (recipientAttributes[3].substring(5).equals(bdayFormat.format(new Date())))
				 bdayRecipients.add((Personal) recipient);
		 }  else if (recipientType.equals("Official")) {
			 //format Official: <name>,<email>,<designation>
			 recipient = new Official(recipientAttributes[0],recipientAttributes[1],recipientAttributes[2]);
			 recipients.add(recipient);
		 } else if(recipientType.equals("Office_friend")) {
			 //format Office_friend: <name>,<email>,<designation>,<recipient's birthday>
			 recipient = new OfficialFriend(recipientAttributes[0],recipientAttributes[1],recipientAttributes[2],recipientAttributes[3]);
			 recipients.add(recipient);
			 //maintaining a list of recipients to whom a birthday greeting should be sent 
			 if (recipientAttributes[3].substring(5).equals(bdayFormat.format(new Date())))
				 bdayRecipients.add((OfficialFriend) recipient);
		 } else {
			 System.out.println("Invalid input");
		 }
	}
	
	private void sendBirthdayWishes() {
		//sending out birthday email
		String recipientEmail, bdayMessage;
		System.out.println("Sending birthday wishes.......  ");
		for (IBirthdayGreeting recipient: bdayRecipients) {
			bdayMessage = recipient.birthdayGreetingMessage();
			bdayMessage += client.getClientName();
			recipientEmail = ((Recipient)recipient).getEmailAddress();
			Email bdayEmail = new Email(recipientEmail,"Happy Birthday",bdayMessage, dateFormat.format(new Date()));
			emailList.add(bdayEmail);
			sendEmail(bdayEmail);
		}
		System.out.println();
	}
	
	private void sendEmail(Email email) {
		//sending an email
		//initializing the settings
	   Properties property = new Properties();  
	   property.put("mail.smtp.host","smtp.gmail.com");  
	   property.put("mail.smtp.auth", "true");  
	   property.put("mail.smtp.port", "587");
	   property.put("mail.smtp.starttls.enable", "true");
	   
	    Session session = Session.getInstance(property,  
	    new Authenticator() {  
	      protected PasswordAuthentication getPasswordAuthentication() { 
	    return new PasswordAuthentication(client.getClientEmail(), client.getClientPassword());  
	      }  
	    });
	  
	   //composing the email
		try {  
			MimeMessage message = new MimeMessage(session);  
			message.setFrom(new InternetAddress(client.getClientEmail()));  
			message.addRecipient(Message.RecipientType.TO,new InternetAddress(email.getEmailAddress()));  
			message.setSubject(email.getSubject());  
			message.setText(email.getContent());  
		   
		//sending the email
		    Transport.send(message);   
		    System.out.println("Email sent");
	     } catch (MessagingException e) {System.out.println("Unable to send email");}  
	  }
}
