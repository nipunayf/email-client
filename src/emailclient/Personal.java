package emailclient;

public class Personal extends Recipient implements IBirthdayGreeting{
	private String nickname;
	private String recipientBirthday;
	
	public Personal(String name, String nickname, String email, String recipientBirthday) {
		super(name, email);
		this.nickname = nickname;
		this.recipientBirthday = recipientBirthday;
	}
	
	@Override
	public String birthdayGreetingMessage() {
		return "Hugs and love on your birthday. ";
	}

	@Override
	public String getBirthday() {
		return recipientBirthday;
	}

	@Override
	public String getDetails() {
		return name + ", " + nickname + ", " + emailAddress + ", " + recipientBirthday;
	}
}
