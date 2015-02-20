/**
 * 
 */
package flexsc;

import java.util.Set;

import oram.SecureArray;

/**
 * @author Allen McIntosh
 *
 */
public interface CpuFcn<T> {
	public T[] function(SecureArray<T> reg, T[] inst, T[]pc, SecureArray<T> hiLo) throws Exception;
	public Set<String> getOpcodesImplemented();
}