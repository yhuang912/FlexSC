package insecure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;

public class PageRankInsecure {

	static int ITERATIONS = -1;
	static int PORT = 59000;
	public static String LOCALHOST = "localhost";
	static int PROCESSORS = 2;
	int id;

	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	private ServerSocket serverSock;
	private Socket sock;

	public PageRankInsecure(int id) {
		this.id = id;
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

	public void connectToClient() throws IOException {
		serverSock = new ServerSocket(PORT);
		Socket sock = serverSock.accept();
		outStream = new ObjectOutputStream(sock.getOutputStream());
		inStream = new ObjectInputStream(sock.getInputStream());
	}

	public void connectToServer() throws UnknownHostException, IOException {
		sock = new Socket(LOCALHOST, PORT);
		outStream = new ObjectOutputStream(sock.getOutputStream());
		inStream = new ObjectInputStream(sock.getInputStream());
	}

	public void sort(PageRankNode[] nodes, Comparator c, boolean print) throws IOException, ClassNotFoundException {
		Arrays.sort(nodes, c);
		if (print) {
			print(nodes);
		}
		outStream.writeObject(nodes.clone());
		outStream.flush();
		PageRankNode[] otherNodes  = (PageRankNode[]) inStream.readObject();
		int i = 0, j = 0;
		PageRankNode[] res = new PageRankNode[otherNodes.length + nodes.length];
		int k = 0;
		if (print) {
//			print(nodes);
			print(otherNodes);
//			PageRankNode[] someMore  = (PageRankNode[]) inStream.readObject();
//			print(someMore);
		}
		while (true) {
			if (c.compare(nodes[i], otherNodes[j]) < 0) {
				res[k++] = nodes[i++];
			} else {
				res[k++] = otherNodes[j++];
			}
			if (i == nodes.length) {
				while (j != otherNodes.length) {
					res[k++] = otherNodes[j++];
				}
				break;
			} else if (j == otherNodes.length) {
				while (i != nodes.length) {
					res[k++] = nodes[i++];
				}
				break;
			}
		}
//		print(res);
		for (int p = 0; p < nodes.length; p++) {
			// should be id * inputLength / PROCESSORS
			nodes[p] = res[p + id * nodes.length];
		}
//		print(nodes);
	}

	public void prComputeL(PageRankNode[] nodes) throws IOException, ClassNotFoundException {
		int cntToSend = 0;
		for (int i = nodes.length - 1; i >= 0; i--) {
			if (nodes[i].isVertex == 0) {
				cntToSend++;
			} else {
				break;
			}
		}
		int nodeToSend = nodes[nodes.length - 1].u;
		int cntToReceive = 0, nodeToReceive = -1;
		if (id == 0) {
			outStream.writeObject(new Integer(cntToSend));
			outStream.writeObject(new Integer(nodeToSend));
			outStream.flush();
			outStream.reset();
		} else if (id == 1) {
			cntToReceive = (Integer) inStream.readObject();
			nodeToReceive = (Integer) inStream.readObject();
//			inStream.readInt();
//			System.out.println(inStream.available());
			if (nodeToReceive != nodes[0].u) {
				cntToReceive = 0;
			}
		}

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
		double valToSend = 0;

		for (int i = nodes.length - 1; i >= 0; i--) {
			if (nodes[i].isVertex == 1) {
				valToSend = nodes[i].pageRank / nodes[i].l;
				break;
			}
		}

		double valToReceive = 0;
		if (id == 0) {
			outStream.writeDouble(valToSend);
			outStream.flush();
			outStream.reset();
		} else if (id == 1) {
			valToReceive = inStream.readDouble();
		}

		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].isVertex == 1) {
				valToReceive = nodes[i].pageRank / nodes[i].l;
			} else {
				nodes[i].pageRank = valToReceive;
			}
		}
	}

	void prGather(PageRankNode[] nodes) throws IOException, ClassNotFoundException {
		double aggToSend = 0;
		for (int i = nodes.length - 1; i >= 0; i--) {
			if (nodes[i].isVertex == 1) {
				break;
			} else {
				aggToSend = aggToSend + nodes[i].pageRank / nodes[i].l;
			}
		}

		double aggToReceive = 0;
		int nodeToSend = nodes[nodes.length - 1].u;
		int nodeToReceive = -1;
		if (id == 0) {
			outStream.writeDouble(aggToSend);
			outStream.writeObject(new Integer(nodeToSend));
			outStream.flush();
		} else if (id == 1) {
			aggToReceive = inStream.readDouble();
			nodeToReceive = (Integer) inStream.readObject();
			if (nodeToReceive != nodes[0].u) {
				aggToReceive = 0;
			}
		}

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
		int id = Integer.parseInt(args[1]);
		PageRankInsecure main = new PageRankInsecure(id);
		ITERATIONS = Integer.parseInt(args[2]);
		if (id == 0) {
			main.connectToClient();
		} else if (id == 1) {
			main.connectToServer();
		}
		int inputLength = Integer.parseInt(args[0]);
		BufferedReader br = new BufferedReader(new FileReader("/home/kartik/code/scratch/pr/PageRank" + inputLength + ".in"));
//		BufferedReader br = new BufferedReader(new FileReader("/home/kartik/code/scratch/pr/PageRank.in"));
		PageRankNode[] nodes = new PageRankNode[inputLength / PROCESSORS];
		
		int start = id * inputLength / PROCESSORS;
		for (int i = 0; i < inputLength; i++) {
			String readLine = br.readLine();
			if (i >= start && i < start + inputLength / PROCESSORS) {
				String[] split = readLine.split(" ");
				int u = Integer.parseInt(split[0]);
				int v = Integer.parseInt(split[1]);
				int isVertex = Integer.parseInt(split[2]);
				nodes[i - start] = new PageRankNode(u, v, isVertex);
			}
		}

		main.sort(nodes, new OutgoingVertexLast(), false);
		main.outStream.reset();
		main.prComputeL(nodes);
		long startTime = System.nanoTime();
		for (int i = 0; i < ITERATIONS; i++) {
			main.sort(nodes, new OutgoingVertexFirst(), false);
			main.outStream.reset();
			main.prScatter(nodes);
			main.sort(nodes, new IncomingVertexLast(), false);
			main.prGather(nodes);
		}
		long endTime = System.nanoTime();
		System.out.println("2," + id + "," + 1.0 * (endTime - startTime)/(1000 * 1000 * 1000));
		main.outStream.reset();
		main.sort(nodes, new AllVertexFirst(), false);
//		main.print(nodes);
	}
}
