package insecure;

import java.io.Serializable;

public class PageRankNode implements Serializable {

	int u, v;
	int isVertex;
	double pageRank;
	int l;

	public PageRankNode(int u, int v, int isVertex) {
		super();
		this.u = u;
		this.v = v;
		this.isVertex = isVertex;
		this.pageRank = isVertex;
		this.l = 0;
	}
}
