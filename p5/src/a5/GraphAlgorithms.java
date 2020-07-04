package a5;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import a4.Heap;
import graph.Edge;
import graph.Node;
import graph.LabeledEdge;

/** We've provided depth-first search as an example; you need to implement Dijkstra's algorithm.
 */
public class GraphAlgorithms  {
	/** Return the Nodes reachable from start in depth-first-search order */
	public static <N extends Node<N,E>, E extends Edge<N,E>>
	List<N> dfs(N start) {
		
		Stack<N> worklist = new Stack<N>();
		worklist.add(start);
		
		Set<N>   visited  = new HashSet<N>();
		List<N>  result   = new ArrayList<N>();
		while (!worklist.isEmpty()) {
			// invariants:
			//    - everything in visited has a path from start to it
			//    - everything in worklist has a path from start to it
			//      that only traverses visited nodes
			//    - nothing in the worklist is visited
			N next = worklist.pop();
			visited.add(next);
			result.add(next);
			for (N neighbor : next.outgoing().keySet())
				if (!visited.contains(neighbor))
					worklist.add(neighbor);
		}
		return result;
	}
	
	/**
	 * Return a minimal path from start to end.  This method should return as
	 * soon as the shortest path to end is known; it should not continue to search
	 * the graph after that. 
	 * 
	 * @param <N> The type of nodes in the graph
	 * @param <E> The type of edges in the graph; the weights are given by e.label()
	 * @param start The node to search from
	 * @param end   The node to find
	 */
	public static <N extends Node<N,E>, E extends LabeledEdge<N,E,Integer>>
	List<N> shortestPath(N start, N end) {
		
		ArrayList<N> inheap = new ArrayList<N>();	
		HashMap<N, DB<N>> info = new HashMap<N, DB<N>>();
		Heap<N, Integer> f = new Heap<N, Integer>(Comparator.naturalOrder());
		
		f.add(start, 0);
		inheap.add(start);
		DB<N> first = new DB<N>(0, null);
		info.put(start, first);
		N to = start;
		
		while(f.size() != 0 && f.peek() != end) {
			N from = f.poll();
			if(inheap.contains(from)) {
				inheap.remove(from);
			}
			
			Map<N, ? extends E> fn_neighbors = from.outgoing();
			Set<N> neighbor_nodes = fn_neighbors.keySet();
			Iterator<N> iterator_nodes = neighbor_nodes.iterator();
			
			while(iterator_nodes.hasNext()) {
				to = iterator_nodes.next();
				
				if(!info.containsKey(to)) {
					info.put(to, new DB<N>(fn_neighbors.get(to).label() + info.get(from).distance, from));
					f.add(to, fn_neighbors.get(to).label() + info.get(from).distance);
					inheap.add(to);
					
				} else if(fn_neighbors.get(to).label() + info.get(from).distance < info.get(to).distance && inheap.contains(to)) {
					info.get(to).distance = fn_neighbors.get(to).label() + info.get(from).distance;
					info.get(to).backpointer = from;
					f.changePriority(to, fn_neighbors.get(to).label() + info.get(from).distance);
					
				}
			}
		}
		if(info.containsKey(end) && f.size() == 0) {
			ArrayList<N> n = new ArrayList<N>();
			N p = end;
			while(p != null) {
				n.add(0, p);
				p = info.get(p).backpointer;
			}
			return n;
		} else {
			return new ArrayList<N>();
		}
		
		
	}
	
	static class DB<N> {
		int distance;
		N backpointer;
		
		public DB(int d, N bk) {
			this.distance = d;
			this.backpointer = bk;
		}
	}
	
}
