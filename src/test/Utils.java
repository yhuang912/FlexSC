package test;

public class Utils {
	public static boolean[] fromInt(int value, int width) {
		boolean[] res = new boolean[width];
		for (int i = 0; i < width; i++)
			res[i] = (((value >> i) & 1) == 0) ? false : true;
		
		return res;
	}
	
	public static int toInt(boolean[] value) {
		int res = 0;
		for (int i = 0; i < value.length; i++)
			res =  (value[i]) ? (res | (1<<i)) : res;
		
		return res;
	}
	
	public static boolean[] fromLong(long value, int width) {
		boolean[] res = new boolean[width];
		for (int i = 0; i < width; i++)
			res[i] = (((value >> i) & 1) == 0) ? false : true;
		
		return res;
	}
	
	public static long toLong(boolean[] value) {
		long res = 0;
		for (int i = 0; i < value.length; i++)
			res =  (value[i]) ? (res | (1<<i)) : res;
		
		return res;
	}

	public static float toFloat(boolean[] value) {
		return Float.intBitsToFloat(toInt(value));
	}
	
	public static boolean[] fromFloat(float value) {
		return fromInt(Float.floatToIntBits(value), 32);
	}
}
