import java.util.*;

class Edge implements Comparable<Edge> {
	int from;
	int to;
	int weights;
	public Edge(int from, int to, int weights) {
		this.from = from;
		this.to = to;
		this.weights = weights;
		
	}
	
	@Override 
	public int compareTo(Edge other) {
		return Integer.compare(this.weights, other.weights);
	}
	
	@Override 
	public String toString() {
		return String.format("(%d -> %d == %d)", from, to, weights);
	}
}

public class Prim {
	private final int V;
	private List<List<Edge>> adj;
	private boolean[] visited;
	private PriorityQueue<Edge> minPQ;
	private List<Edge> mstEdge;
	private int mstCost;
	
	public Prim(int V) {
		this.V = V;
		adj = new ArrayList<>(V);
		for(int i = 0; i < V; i++) {
			adj.add(new ArrayList<>());
		}
		visited = new boolean[V];
		minPQ = new PriorityQueue<>();
		mstEdge = new ArrayList<>();
		mstCost = 0;
		
	}
	
	public void addEdge(int u, int v, int weight) {
		adj.get(u).add(new Edge(u,v,weight));
		adj.get(v).add(new Edge(v,u,weight));
	}
	
	//Main algorithm
	public boolean lazyPrim(int s) {
		addEdges(s);
		
		int targetEdgeCount = V - 1;
		int edgeCount = 0;
		
		while(!minPQ.isEmpty() && edgeCount < targetEdgeCount) {
			Edge e = minPQ.poll();
			int v = e.to;
			
			if(visited[v]) {
				continue;
			}
			
			mstEdge.add(e);
			mstCost += e.weights;
			edgeCount++;
			
			addEdges(v);
			
		}
		
		return(edgeCount == targetEdgeCount);
	}
	
	private void addEdges(int nodeIndex) {
		visited[nodeIndex] = true;
		
		//for every edge from node index to some neighbor
		for(Edge e : adj.get(nodeIndex)) {
			if(!visited[e.to]) {
				//Only enqueue edges whose end point is not yet in MST
				minPQ.offer(e);
			}
		}
	}
	
	public List<Edge> getMSTEdges() {
		return mstEdge;
	}
	
	public int getMSTCost() {
		return mstCost;
	}
	public static void main(String[] args) {
	    int V = 8;
	    Prim graph = new Prim(V);

	    // Add edges exactly as in the provided diagram:
	    graph.addEdge(0, 1, 10);
	    graph.addEdge(0, 2, 1);
	    graph.addEdge(0, 3, 4);
	    graph.addEdge(1, 2, 3);
	    graph.addEdge(1, 4, 0);
	    graph.addEdge(2, 3, 2);
	    graph.addEdge(2, 5, 8);
	    graph.addEdge(3, 5, 2);
	    graph.addEdge(3, 6, 7);
	    graph.addEdge(4, 5, 1);
	    graph.addEdge(5, 6, 6);
	    graph.addEdge(5, 7, 9);
	    graph.addEdge(4, 7, 8);
	    graph.addEdge(6, 7, 12);

	    // Run Lazy Prim’s, starting from vertex 0:
	    boolean success = graph.lazyPrim(0);

	    if (!success) {
	        System.out.println("Graph is disconnected; no spanning tree exists.");
	    } else {
	        System.out.println("Edges in the MST (in the order added):");
	        for (Edge e : graph.getMSTEdges()) {
	            System.out.printf("  %d -- %d  (weight = %d)%n", e.from, e.to, e.weights);
	        }
	        System.out.println("Total cost of MST = " + graph.getMSTCost());
	    }
	}

}
