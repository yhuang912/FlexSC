package gc;

import java.security.SecureRandom;
import org.junit.Test;
import org.junit.Assert;

public class TestGarbler {
	SecureRandom rnd = new SecureRandom();
	
	public void test() {
		Signal a = Signal.freshLabel(rnd);
		Signal b = Signal.freshLabel(rnd);
		Signal m = Signal.freshLabel(rnd);
		Garbler gb = new Garbler();
		
		Assert.assertTrue(m.equals(gb.dec(a, b, 0L, gb.enc(a, b, 0L, m))));
	}

	@Test
	public void test1000() {
		for(int i = 0; i<10000; i++)
			test();
	}
}
