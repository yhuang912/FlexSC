package insecure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class PageRankInsecureSingle {

	static int ITERATIONS = -1;

	public PageRankInsecureSingle() {
	}

	private static final class OutgoingVertexLast implements
			Comparator<PageRankNode> {
		@Override
		public int compare(PageRankNode a, PageRankNode b) {
			if (a.u == b.u) {
				return (a.isVertex - b.isVertex);
			}
			return (a.u - b.u);
		}
	}

	private static final class OutgoingVertexFirst implements
			Comparator<PageRankNode> {
		@Override
		public int compare(PageRankNode a, PageRankNode b) {
			if (a.u == b.u) {
				return (b.isVertex - a.isVertex);
			}
			return (a.u - b.u);
		}
	}

	private static final class IncomingVertexLast implements
			Comparator<PageRankNode> {
		@Override
		public int compare(PageRankNode a, PageRankNode b) {
			if (a.v == b.v) {
				return (a.isVertex - b.isVertex);
			}
			return (a.v - b.v);
		}
	}

	private static final class AllVertexFirst implements
			Comparator<PageRankNode> {
		@Override
		public int compare(PageRankNode a, PageRankNode b) {
			if (a.isVertex == b.isVertex) {
				return (a.u - b.u);
			}
			return (b.isVertex - a.isVertex);
		}
	}

	void print(PageRankNode[] nodes) {
		for (int i = 0; i < nodes.length; i++) {
			System.out.println(nodes[i].u + " " + nodes[i].v + " " + nodes[i].isVertex + " " + nodes[i].pageRank + " " + nodes[i].l);
		}
		System.out.println();
	}


	public void sort(PageRankNode[] nodes, Comparator c, boolean print) throws IOException, ClassNotFoundException {
		Arrays.sort(nodes, c);
	}

	public void prComputeL(PageRankNode[] nodes) throws IOException, ClassNotFoundException {
		int cntToReceive = 0;

		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].isVertex == 1) {
				nodes[i].l = cntToReceive;
				cntToReceive = 0;
			} else {
				cntToReceive++;
			}
		}
	}

	void prScatter(PageRankNode[] nodes) throws IOException {
		double valToReceive = 0;
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].isVertex == 1) {
				valToReceive = nodes[i].pageRank / nodes[i].l;
			} else {
				nodes[i].pageRank = valToReceive;
			}
		}
	}

	void prGather(PageRankNode[] nodes) throws IOException, ClassNotFoundException {
		double aggToReceive = 0;
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].isVertex == 1) {
				nodes[i].pageRank = aggToReceive;
				aggToReceive = 0;
			} else {
				aggToReceive = aggToReceive + nodes[i].pageRank;
			}
		}
	}

	public static void main(String args[]) 
			throws NumberFormatException, ClassNotFoundException, IOException {
		PageRankInsecureSingle main = new PageRankInsecureSingle();
		ITERATIONS = Integer.parseInt(args[1]);
		int inputLength = Integer.parseInt(args[0]);
		BufferedReader br = new BufferedReader(new FileReader("in/PageRank" + inputLength + ".in"));
		PageRankNode[] nodes = new PageRankNode[inputLength];
		
		for (int i = 0; i < inputLength; i++) {
			String readLine = br.readLine();
			String[] split = readLine.split(" ");
			int u = Integer.parseInt(split[0]);
			int v = Integer.parseInt(split[1]);
			int isVertex = Integer.parseInt(split[2]);
			nodes[i] = new PageRankNode(u, v, isVertex);
		}

		main.sort(nodes, new OutgoingVertexLast(), false);
		main.prComputeL(nodes);
		long startTime = System.nanoTime();
		for (int i = 0; i < ITERATIONS; i++) {
			main.sort(nodes, new OutgoingVertexFirst(), false);
			main.prScatter(nodes);
			main.sort(nodes, new IncomingVertexLast(), false);
			main.prGather(nodes);
		}
		long endTime = System.nanoTime();
//		main.print(nodes);
		System.out.println(inputLength + "," + 1.0 * (endTime - startTime)/(1000 * 1000 * 1000));
		main.sort(nodes, new AllVertexFirst(), false);
	}
}
