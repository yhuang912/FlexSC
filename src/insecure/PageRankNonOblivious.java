package insecure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PageRankNonOblivious {

	public static void main(String args[]) throws IOException {
		int iterations = Integer.parseInt(args[1]);
		int inputLength = Integer.parseInt(args[0]);
		BufferedReader br = new BufferedReader(new FileReader("in/PageRank" + inputLength + ".in"));
		int i = 0;
		Node[] nodes = null;
		for (i = 0; i < inputLength; i++) {
			String readLine = br.readLine();
			String[] split = readLine.split(" ");
			int u = Integer.parseInt(split[0]);
			int v = Integer.parseInt(split[1]);
			int isVertex = Integer.parseInt(split[2]);
			if (isVertex == 0) {
				nodes = new Node[i];
				for (int j = 0; j < i; j++) {
					nodes[j] = new Node();
				}
				nodes[v - 1].u.add(u);
				nodes[u - 1].l++;
				break;
			}
		}
		for (int j = i + 1; j < inputLength; j++) {
			String readLine = br.readLine();
			String[] split = readLine.split(" ");
			int u = Integer.parseInt(split[0]);
			int v = Integer.parseInt(split[1]);
			int isVertex = Integer.parseInt(split[2]);
			nodes[v - 1].u.add(u);
			nodes[u - 1].l++;
		}
		long startTime = System.nanoTime();
		for (int it = 0; it < iterations; it++) {
			for (int j = 0; j < nodes.length; j++) {
				nodes[j].prTemp = 0;
				for (int k = 0; k < nodes[j].u.size(); k++) {
					int u = nodes[j].u.get(k);
					nodes[j].prTemp += (nodes[u - 1].pr / nodes[u - 1].l);
				}
			}
//			for (int j = 0; j < nodes.length; j++) {
//				nodes[j].pr = nodes[j].prTemp;
//			}
		}
		long endTime = System.nanoTime();
		for (int j = 0; j < nodes.length; j++) {
			System.out.println(j + " " + nodes[j].pr);
		}
		System.out.println(inputLength + "," + iterations + "," + 1.0 * (endTime - startTime)/(1000 * 1000 * 1000));
		br.close();
	}
}
