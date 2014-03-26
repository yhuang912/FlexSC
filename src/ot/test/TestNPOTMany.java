package ot.test;

import java.security.SecureRandom;
import java.util.Random;

import gc.*;

import org.junit.Assert;
import org.junit.Test;

import ot.NPOTReceiver;
import ot.NPOTSender;

public class TestNPOTMany {
	static int n = 00;
	Signal[][] m;
	boolean[] c;
	Signal[] rcvd;
	
	class SenderRunnable extends network.Server implements Runnable {
		NPOTSender snd;
		SenderRunnable () {}
		
		public void run() {
			SecureRandom rnd = new SecureRandom();
			try {
				listen(54321);

				m = new Signal[n][2];
				for (int i = 0; i < n; i++) {
					m[i][0] = Signal.freshLabel(rnd);
					m[i][1] = Signal.freshLabel(rnd);
				}
				snd = new NPOTSender(80, is, os);
				snd.send(m);
				
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class ReceiverRunnable extends network.Client implements Runnable {
		NPOTReceiver rcv;
		ReceiverRunnable () {}
		
		public void run() {
			try {
				connect("localhost", 54321);
				
				rcv = new NPOTReceiver(is, os);
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

	public void runThreads() throws Exception {
		SenderRunnable sender = new SenderRunnable();
		ReceiverRunnable receiver = new ReceiverRunnable();
		Thread tSnd = new Thread(sender);
		Thread tRcv = new Thread(receiver);
		tSnd.start(); 
		tRcv.start(); 
		tSnd.join();

		for (int i = 0; i < n; i++) {
//		System.out.println(m[c?1:0].toHexStr());
//		System.out.println(rcvd.toHexStr());
			System.out.println(i);
			Assert.assertEquals(rcvd[i], m[i][c[i]?1:0]);
		}
	}
	
	@Test
	public void testAllCases() throws Exception {
		System.out.println("Testing NPOT...");
		runThreads();
	}
}