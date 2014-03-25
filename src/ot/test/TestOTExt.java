package ot.test;

import java.security.SecureRandom;
import java.util.Random;

import gc.*;

import org.junit.Assert;
import org.junit.Test;

import ot.OTExtReceiver;
import ot.OTExtSender;

public class TestOTExt {
//	static int n = 100;
	Signal[] m;
	boolean c;
	Signal rcvd;
	
	class SenderRunnable extends network.Server implements Runnable {
		OTExtSender snd;
		SenderRunnable () {}
		
		public void run() {
			SecureRandom rnd = new SecureRandom();
			try {
				listen(54321);

				m = new Signal[2];
//				for (int i = 0; i < n; i++) {
					m[0] = Signal.freshLabel(rnd);
					m[1] = Signal.freshLabel(rnd);
//				}
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
//				c = new boolean[n];
				Random rnd = new Random();
//				for (int i = 0; i < n; i++)
					c = rnd.nextBoolean();
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

//		for (int i = 0; i < n; i++) {
//		System.out.println(m[c?1:0].toHexStr());
//		System.out.println(rcvd.toHexStr());
//			System.out.println(i);
			Assert.assertEquals(rcvd, m[c?1:0]);
//		}
	}

	@Test
	public void testAllCases() throws Exception {
		System.out.println("Testing OT Extension...");
		test1Case();
	}
}