package network;

public class BadResponseException extends Exception {

	private static final long serialVersionUID = 1L;

	public BadResponseException(String message) {
		super(message);
	}
}
