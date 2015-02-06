/**
 * 
 */
package flexsc;

import oram.SecureArray;

/**
 * @author mcintosh
 *
 */
public interface CpuFcn<T> {
	public T[] function(SecureArray<T> reg, T[] inst, T[]pc) throws Exception;
}
