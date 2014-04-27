package cv;

import flexsc.CompEnv;

public class CVCompEnv implements CompEnv<Boolean> {

	@Override
	public Boolean inputOfGen(boolean in) throws Exception {
		throw new Exception("No need to prepare inputs.");
	}

	@Override
	public Boolean inputOfEva(boolean in) throws Exception {
		throw new Exception("No need to prepare inputs.");
	}

	@Override
	public boolean outputToGen(Boolean out) throws Exception {
		throw new Exception("No need to prepare outputs.");
	}

	@Override
	public Boolean and(Boolean a, Boolean b) throws Exception {
		return a && b;
	}

	@Override
	public Boolean xor(Boolean a, Boolean b) {
		return a ^ b;
	}

	@Override
	public Boolean not(Boolean a) {
		return !a;
	}

	@Override
	public Boolean ONE() {
		return true;
	}

	@Override
	public Boolean ZERO() {
		return false;
	}

	@Override
	public Boolean[] newTArray(int len) {
		Boolean[] res = new Boolean[len];
		return res;
	}

	@Override
	public Boolean newT(boolean v) {
		return v;
	}

}
