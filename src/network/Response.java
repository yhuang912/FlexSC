package network;

import java.util.HashMap;
import java.util.Map;

public enum Response {
	SUCCESS(1), FAILURE(2);

	private int value;

	private static Map<Integer, Response> map = new HashMap<Integer, Response>();

	static {
		for (Response response : Response.values()) {
			map.put(response.value, response);
		}
	}

	private Response(final int value) {
		this.value = value;
	}

	public static Response valueOf(int value) {
		return map.get(value);
	}

	public int getValue() {
		return value;
	}
}
