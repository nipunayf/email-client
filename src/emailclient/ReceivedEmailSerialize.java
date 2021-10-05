package emailclient;

public class ReceivedEmailSerialize extends Thread{
	private MyBlockingQueue queue;
	private InputOutputStream ios;
	
	public ReceivedEmailSerialize(MyBlockingQueue queue, InputOutputStream ios) {
		this.queue = queue;
		this.ios = ios;
	}
	
	private void saveEmail (Email email) {
			ios.serializeReceivedEmail(email);
	}
	
	public void run() {
		while(true) {
			Email email;
			email  = queue.dequeue();
			if (email != null) {
				saveEmail(email);
			}
		}
	}
}
