package network;

import java.util.HashMap;
import java.util.Map;

public enum Command {
	LISTEN(0),
	CONNECT(1),
	COMPUTE(2),
	SET_MACHINE_ID(3);

	private int value;

	private static Map<Integer, Command> map = new HashMap<Integer, Command>();

	static {
		for (Command command : Command.values()) {
			map.put(command.value, command);
		}
	}

	private Command(final int value) {
		this.value = value;
	}

	public static Command valueOf(int value) {
		return map.get(value);
	}

	public int getValue() {
		return value;
	}
}
