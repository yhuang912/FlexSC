package test.ot;

import java.security.SecureRandom;

import gc.*;

import org.junit.Assert;
import org.junit.Test;

import ot.NPOTReceiver;
import ot.NPOTSender;

public class TestNPOT {
	GCSignal[] m;
	boolean c;
	GCSignal rcvd;
	
	class SenderRunnable extends network.Server implements Runnable {
		NPOTSender snd;
		SenderRunnable () {}
		
		public void run() {
			SecureRandom rnd = new SecureRandom();
			try {
				listen(54321);

				m = new GCSignal[2];
				m[0] = GCSignal.freshLabel(rnd);
				m[1] = GCSignal.freshLabel(rnd);
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

		System.out.println(m[c?1:0].toHexStr());
		System.out.println(rcvd.toHexStr());
		Assert.assertEquals(rcvd, m[c?1:0]);
	}

	@Test
	public void testAllCases() throws Exception {
		System.out.println("Testing NPOT...");
		runThreads();
	}
}