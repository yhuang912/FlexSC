package network;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class IPManager {

	String masterGarblerIp;
	String masterEvaluatorIp;
	String[] gIp;
	String[] eIp;
	int machines;

	public static IPManager loadIPs(int machines, String machineConfig) throws IOException {
		IPManager ipManager = new IPManager();
		BufferedReader br = null;
		try {
			// System.out.println(Constants.MACHINE_IPS);
//			br = new BufferedReader(new FileReader(Constants.MACHINE_IPS + "." + machines));
			br = new BufferedReader(new FileReader(machineConfig + "." + machines));
			ipManager.masterGarblerIp = br.readLine();
			ipManager.masterEvaluatorIp = br.readLine();
			ipManager.machines = Integer.parseInt(br.readLine());
			ipManager.gIp = new String[ipManager.machines];
			ipManager.eIp = new String[ipManager.machines];
			for (int i = 0; i < ipManager.machines; i++) {
//				ipManager.gIp[i] = br.readLine().split(",")[1];
				ipManager.gIp[i] = br.readLine();
			}
			for (int i = 0; i < ipManager.machines; i++) {
//				ipManager.eIp[i] = br.readLine().split(",")[1];
				ipManager.eIp[i] = br.readLine();
			}
			return ipManager;
		} catch(FileNotFoundException e) {
			System.out.println("File not found");
		}
		return null;
	}
}
