package network;

public class BadCommandException extends Exception {

	private static final long serialVersionUID = 1L;

	public BadCommandException(String message) {
		super(message);
	}
}
