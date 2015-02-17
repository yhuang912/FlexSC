package network;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class IPManager {

	static String ips[];
	String masterGarblerIp;
	String masterEvaluatorIp;
	String[] gIp;
	String[] eIp;
	int machines;

	public static IPManager loadIPs(int machines, String machineConfig) throws IOException {
		ips = new String[9];
		ips[0] = "127.0.0.1";
		ips[1] = "10.42.120.15";
		ips[2] = "10.42.120.18";
		ips[3] = "10.42.120.19";
		ips[4] = "10.42.120.22";
		ips[5] = "10.42.120.23";
		ips[6] = "10.42.120.26";
		ips[7] = "10.42.120.27";
		ips[8] = "10.42.120.30";
		ips[9] = "54.149.48.132"; // first_amazon
		ips[10] = "54.153.64.43"; // second_amazon
		IPManager ipManager = new IPManager();
		BufferedReader br = null;
		try {
			// System.out.println(Constants.MACHINE_IPS);
//			br = new BufferedReader(new FileReader(Constants.MACHINE_IPS + "." + machines));
			br = new BufferedReader(new FileReader("machine_spec/" + machineConfig + "." + machines));
			ipManager.masterGarblerIp = ips[Integer.parseInt(br.readLine())];
			ipManager.masterEvaluatorIp = ips[Integer.parseInt(br.readLine())];
			ipManager.machines = Integer.parseInt(br.readLine());
			ipManager.gIp = new String[ipManager.machines];
			ipManager.eIp = new String[ipManager.machines];
			for (int i = 0; i < ipManager.machines; i++) {
//				ipManager.gIp[i] = br.readLine().split(",")[1];
				ipManager.gIp[i] = ips[Integer.parseInt(br.readLine())];
			}
			for (int i = 0; i < ipManager.machines; i++) {
//				ipManager.eIp[i] = br.readLine().split(",")[1];
				ipManager.eIp[i] = ips[Integer.parseInt(br.readLine())];
			}
			return ipManager;
		} catch(FileNotFoundException e) {
			System.out.println("File not found" + e.getMessage());
		}
		return null;
	}
}
