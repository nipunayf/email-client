package emailclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class InputOutputStream {
	
	public void saveRecipient(String details){
		//save recipient info in a text file
		FileWriter file;
		try {
			file = new FileWriter("clientList.txt",true);
			BufferedWriter writer = new BufferedWriter(file);
		    writer.write(details); writer.newLine();
		    writer.close(); file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public ArrayList<String> retrieveRecipients() {
		//retrieve recipient data from the text file
		ArrayList<String> recipientDetailsList = null;
		FileReader file;
		try {
			file = new FileReader("clientList.txt");
			BufferedReader reader = new BufferedReader(file);
			recipientDetailsList = new ArrayList<String>();
			String recipientDetails = reader.readLine();
			while (recipientDetails != null) {
				recipientDetailsList.add(recipientDetails);
				recipientDetails = reader.readLine();
			}
			file.close(); reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return recipientDetailsList;
	}
	
	public void serializeObject(Object obj, String fileName) {
		FileOutputStream file;
		try {
			file = new FileOutputStream(fileName);
			ObjectOutputStream writer = new ObjectOutputStream(file);
			writer.writeObject(obj);
			file.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Object deserializeObject(String fileName) {
		Object obj = null;
		FileInputStream file;
		try {
			file = new FileInputStream(fileName);
			ObjectInputStream reader = new ObjectInputStream(file);
			obj = reader.readObject();
			file.close(); reader.close();
		} catch(ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public void printReceivedEmails(String message){
		FileWriter file;
		try {
			file = new FileWriter("receievedEmailsLog.txt",true);
			BufferedWriter writer = new BufferedWriter(file);
		    writer.write(message); writer.newLine();
		    writer.close(); file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void serializeReceivedEmail(Email email) {
		FileOutputStream file;
		try {
			file = new FileOutputStream("receivedEmails.sar",true);
			ObjectOutputStream writer = new ObjectOutputStream(file);
			writer.writeObject(email);
			file.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
