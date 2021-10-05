package emailclient;

public class OfficialFriend extends Official implements IBirthdayGreeting {
	private String recipientBirthday;
	
	public OfficialFriend(String name, String email, String designation, String recipientBirthday) {
		super(name,email,designation);
		this.recipientBirthday = recipientBirthday;
	}

	@Override
	public String birthdayGreetingMessage() {
		return "Wish you a Happy Birthday. ";
	}
	
	@Override
	public String getBirthday() {
		return recipientBirthday;
	}
	
	@Override
	public String getDetails() {
		return name + ", " + emailAddress + ", " + designation + ", " + recipientBirthday;
	}
}
