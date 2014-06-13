package flexsc;

public interface secureComputable<T> {
	public  Object secureCompute(CompEnv<T> e, Object[] o) throws Exception;
}
