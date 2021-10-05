package emailclient;

public class Official extends Recipient {
	protected String designation;
	
	public Official(String name, String email, String designation) {
		super(name,email);
		this.designation = designation;
	}
	
	@Override
	public String getDetails() {
		return name + ", " + emailAddress + ", " + designation;
	}
}
