package emailclient;

public class EmailStatPrinter extends EmailStatObserver{
	private InputOutputStream ios;
	
	public EmailStatPrinter() {
		super();
		ios = new InputOutputStream();
	}
	@Override
	public void printEmail() {
		ios.printReceivedEmails(getMessage());
	}
}
