package emailclient;

public class EmailStatRecorder extends EmailStatObserver{
	
	@Override
	public void printEmail() {
		System.out.println(getMessage());
	}
		
}
