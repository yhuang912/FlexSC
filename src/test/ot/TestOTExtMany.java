package test.ot;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import gc.*;

import org.junit.Assert;
import org.junit.Test;

import ot.OTExtReceiver;
import ot.OTExtSender;

public class TestOTExtMany {
	static final int n = 50000;
	GCSignal[][] m;
	boolean[] c;
	GCSignal[] rcvd;
	
	class SenderRunnable extends network.Server implements Runnable {
		OTExtSender snd;
		SenderRunnable () {}
		
		public void run() {
			SecureRandom rnd = new SecureRandom();
			try {
				listen(54321);

				m = new GCSignal[n][2];
				for (int i = 0; i < n; i++) {
					m[i][0] = GCSignal.freshLabel(rnd);
					m[i][1] = GCSignal.freshLabel(rnd);
				}
				snd = new OTExtSender(80, is, os);
				snd.send(m);
				
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class ReceiverRunnable extends network.Client implements Runnable {
		OTExtReceiver rcv;
		ReceiverRunnable () {}
		
		public void run() {
			try {
				connect("localhost", 54321);
				
				rcv = new OTExtReceiver(is, os);
				c = new boolean[n];
				Random rnd = new Random();
				for (int i = 0; i < n; i++)
					c[i] = rnd.nextBoolean();
				rcvd = rcv.receive(c);
				
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void test1Case() throws Exception {
		SenderRunnable sender = new SenderRunnable();
		ReceiverRunnable receiver = new ReceiverRunnable();
		Thread tSnd = new Thread(sender);
		Thread tRcv = new Thread(receiver);
		tSnd.start(); 
		tRcv.start(); 
		tSnd.join();
		tRcv.join();

		for (int i = 0; i < n; i++) {
//		System.out.println(m[c?1:0].toHexStr());
//		System.out.println(rcvd.toHexStr());
//			System.out.println(i);
			try {
				Assert.assertEquals(rcvd[i], m[i][c[i]?1:0]);
			} catch (AssertionError e) {
				System.out.println("rcvd[" + i + "]: " + rcvd[i].toHexStr());
				System.out.println("m[" + i + "][c[" + i + "]]: " + m[i][c[i]?1:0].toHexStr());
				
				System.out.println("rcvd[" + i + "]: " + Arrays.toString(rcvd[i].bytes));
				System.out.println("m[" + i + "][c[" + i + "]]: " + Arrays.toString(m[i][c[i]?1:0].bytes));
				throw e;
			}
		}
		
	}

	@Test
	public void testAllCases() throws Exception {
		System.out.println("Testing OT Extension...");
		test1Case();
	}
}