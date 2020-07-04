package a4;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.lang.Math;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


public class Heap<E,P> implements a4.PriorityQueue<E,P>{
	private Comparator<P> c;
	private ArrayList<Node> a = new ArrayList<Node>(); //creating ArrayList to store nodes of the heap
	private HashMap<E,Integer> map = new HashMap<E, Integer>(); //creating HashMap to get index of nodes in the heap
	
	
	/** Constructor creates an empty heap with given comparator. 
	 * 
	 * @param c The comparator given to the heap.
	 */
	public Heap(Comparator<P> c) {
		this.c = c;
	}

	/** Returns the comparator used for ordering priorities. */
	@Override
	public Comparator<? super P> comparator() {
		return c;
	}
	
	/** Returns the number of elements in this heap. Runs in O(1) time. */
	@Override
	public int size() {
		return a.size();
	}
	
	/** Returns the index of a Node. Uses the HashMap to get index in constant time.
	 * 
	 * @param n The given Node.
	 * @return The index of the specified Node in the ArrayLists.
	 */
	public int index(Node n) {
		return map.get(n.data);
	}
	
	/** Swaps two nodes, as well as the indexes of their elements in the HashMap 
	 * Order of the nodes does not matter.
	 * @param n1 The first given node.
	 * @param n2 The second given node.
	 */
	private void swap(Node n1, Node n2) {
		Node tmp = n1;
		a.set(index(n1), n2);
		a.set(index(n2), tmp);
		int tmpindex = index(n1);
		map.replace(n1.data, index(n2));
		map.replace(n2.data, tmpindex);
	}

	
	/** Gets the parent of a node n in the partitioned array as if it were in a tree.
	 * 
	 * @param n The given Node.
	 * @return the parent of the given node, or null if the node is a root.
	 */
	public Node getParent(Node n) {
		//returns null if the node is the root
		if(index(n) == 0) {
			return null;
		}
		
		//rest of the cases
		int childLayer; // the number of steps from root to get to the node
		int childPos; // the position from left to right of a node at its layer
		int parentLayer;
		int parentPos;
		
		//getting child layer and position
		childLayer = (int) (Math.log10(index(n)+1)/Math.log10(2));
		childPos = index(n) - (int) Math.pow(2, childLayer) + 1;
		
		//getting parent layer and position
		parentLayer = childLayer - 1;
		parentPos = childPos/2;
		
		//getting index of parent from its layer and position
		return a.get((int) Math.pow(2, parentLayer) + parentPos -  1);
	}
		
	/** Gets the left child of a Node n in the partitioned array as if it were in a tree.
	 * 
	 * @param n The given node.
	 * @return the left child of the node, or null if the Node is the only node,
	 * a leaf, or previous node(s)'s child is the last node.
	 */
	public Node getLeft(Node n) {
		int parentLayer; // the number of steps from root to get to the node
		int parentPos; // the position from left to right of a node at its layer
		int leftLayer;
		int leftPos;
		
		//getting parent layer and position
		parentLayer = (int) (Math.log10(index(n)+1)/Math.log10(2)); //log2 of index rounded down
		if (parentLayer == 0) {
			parentPos = 0;
			}
		else {
			parentPos = index(n) - (int) Math.pow(2, parentLayer) + 1;
			}
		
		//returns null if the node is the only node in heap
		if (this.size() == 1) {
			return null;
		}
		
		//returns null if the node is a leaf
		else if(parentLayer == (int)(Math.log10(a.size())/Math.log10(2))) { //layer of last index
			return null;
		}
		
		//returns null if the node is not a leaf but its previous node(s)'s left
		//left or right child is the last node.
		else if (index(getParent(a.get(a.size()-1))) <= index(n) - 1) {
			return null;
		}
		
		//rest of the cases
		else {
			leftLayer = parentLayer + 1;
			leftPos = parentPos * 2;
		
			//getting index of left child from its layer and position
			return a.get((int) Math.pow(2, (leftLayer)) + leftPos - 1);
		}
	}
	
	/** Gets the right child of a node n in the partitioned array as if it were in a tree.
	 *
	 * @param n The given node.
	 * @return the right child of the node, or null if the node is the only node,
	 * a leaf, or previous node(s)'s child is the last node.
	 */
	public Node getRight(Node n) {
		int parentLayer; // the number of steps from root to get to the node
		int parentPos; // the position from left to right of a node at its layer
		int rightLayer;
		int rightPos;
			
		//getting parent layer and position
		parentLayer = (int) (Math.log10(index(n)+1)/Math.log10(2)); //log2 of index rounded down
		parentPos = index(n) - (int) Math.pow(2, parentLayer) + 1;
		
		//returns null if the node is the only node in heap
		if (this.size() == 1) {
			return null;
		}
		
		//returns null if the node is a leaf
		else if (parentLayer == (int) (Math.log10(a.size())/Math.log10(2))) { //layer of last index
			return null;
		}
		
		//returns null if the node is not a leaf but its previous node(s)'s left
		//left or right child is the last node
		else if (index(getParent(a.get(a.size()-1))) <= index(n) - 1) {
			return null;
		}
		
		//returns null if the node's left child is the last node in heap
		else if (index(getLeft(n)) == a.size() - 1) {
			return null;
		}
		
		//rest of the cases
		//getting right child layer and position
		else {
			rightLayer = parentLayer + 1;
			rightPos = parentPos*2 + 1;
		
			//getting index of right child from its layer and position
			return a.get((int) Math.pow(2, rightLayer) + rightPos - 1);
			}
	}
	
	/**
	 * Remove and return the largest element of this, according to comparator().
	 * Runs in O(log n) time.
	 * @returns The original root that was removed from the heap.
	 * @throws NoSuchElementException if the heap is empty.
	 */
	@Override
	public E poll() throws NoSuchElementException {
		//case if the heap is empty
		if(size() == 0) {
			throw new NoSuchElementException();
		}
		
		//stores the element of the root node to return at the end of the function
		E root = a.get(0).data;
		
		//case if the root is the only item in the heap (swapping not needed)
		if(size() == 1) {
			map.remove(a.get(a.size()-1).data); //remove the root from the HashMap
			a.remove(0); //remove the root from the Heap
			return root;
		}
		
		//rest of the cases
		
		//replace root with last node in the heap
		swap(a.get(0), a.get(a.size()-1));
		map.remove(a.get(a.size()-1).data); //remove the last node from the HashMap
		a.remove(a.size()-1); //remove the last node from the Heap
		
		Node n = a.get(0);
		
		fixInvariant(n); //reorders heap to fix invariants
		
		return root;
		
	}
	
	/**
	 * Returns the node with maximum priority in the heap, aka the root.
	 * @return the current root of the heap.
	 * @throws NoSuchElementException if the heap is empty.
	 */
	@Override
	public E peek() throws NoSuchElementException {
		//case if the heap is empty
		if(size() == 0) {
			throw new NoSuchElementException();
		}
		
		//rest of the cases
		return a.get(0).data;
	}

	/**
	 * Adds the element e with priority p to the heap. Runs in O(log n + a) time,
	 * where a is the time it takes to append an element to an ArrayList of size
	 * n.
	 * 
	 * @param e The new element to be added.
	 * @param p The priority to be associated with the new element/node.
	 * @throws IllegalArgumentException if this already contains an element that
	 *                                  is equal to e (according to .equals())
	 */
	@Override
	public void add(E e, P p) throws IllegalArgumentException {
		//case if element e is already in the heap
		if(map.containsKey(e)) {
			throw new IllegalArgumentException();
		}
		
		//creates new node with element e to add to the heap, also adds it to the HashMap
		Node n = new Node(e, p, a.size());	
		a.add(n); //adds the new node to the heap, already added to HashMap when the node was created
		
		fixInvariant(n); //reorders heap to fix invariants
	}

	/**
	 * Change the priority of node associated with e to p.
	 * 
	 * @param e The element associated with the node whose priority is to be changed.
	 * @param p The new priority assigned to the node.
	 * @throws NoSuchElementException if the heap does not contain e.
	 */
	@Override
	public void changePriority(E e, P p) throws NoSuchElementException {
		//case if heap does not contain e
		if(!map.containsKey(e)) {
			throw new NoSuchElementException();
		}
		
		//rest of the cases
		a.get(map.get(e)).priority = p; //change the priority of the node with specified element
		
		fixInvariant(a.get(map.get(e))); //reorders heap to fix invariants
	}
	
	/** Reestablishes the heap invariant using a specified node.
	 * 
	 * @param n The given node.
	 */
	private void fixInvariant(Node n) {
		int parentDif;
		int leftDif;
		int rightDif;
		
		boolean invariant = false;
		//variable representing if the heap properties are satisfied
		
		//loop runs until heap invariants are satisfied
		while (invariant != true) {
			
			//handling null cases
			if (getParent(n) == null) {
				parentDif = -1;
			} else {
				parentDif = c.compare(getParent(n).priority, n.priority);
			}
			if (getLeft(n) == null) {
				leftDif = Integer.MAX_VALUE;
			} else {
				leftDif = c.compare(getLeft(n).priority, n.priority);
			}
			if (getRight(n) == null) {
				rightDif = Integer.MAX_VALUE;
			} else {
				rightDif = c.compare(getRight(n).priority, n.priority);
			}
		
			
			//case if parent is larger and n should swap up
			if (parentDif > 0) {
				swap(getParent(n), n); //progress: n goes up the heap
			}
			
			//cases if children are smaller and n should swap down
			else if (leftDif < 0 || rightDif < 0) {
				
				//if both a left and right child exist
				if(getLeft(n) != null && getRight(n) !=null) {
					if (c.compare(getLeft(n).priority, getRight(n).priority) < 0) {
						swap(getLeft(n), n); //progress: n goes down the heap
					}
					else {
						swap(getRight(n), n); //progress: n goes down the heap
					}
				}
				
				//if at least 1 child is null
				else if (leftDif < rightDif) {
					swap(getLeft(n), n); //progress: n goes down the heap
				}
				else {
					swap(getRight(n), n); //progress: n goes down the heap
				}
			}//if left is null, right is definitely null (heap is a full tree)
			
			//if n is in a position that satisfies the heap invariant
			else {
				invariant = true;
			}
		}
	
		
		
	}
	
	/** Returns the node at contains the specified element and its priority.
	 * 
	 * @param e element of which to get the node it is contained within.
	 * @throws NoSuchElementException if the heap does not contain e.
	 */
	public Node get(E e) throws NoSuchElementException {
		//case if element e isn't in the heap
		if(!map.containsKey(e)) {
			throw new NoSuchElementException();
		}
		return a.get(map.get(e));
	}
	
	
	/** Node class: used for holding data
	 * Contains an element, a priority and location in the ArrayList.
	 */
	private class Node{
		E data;
		
		P priority;
		
		//Constructor for Node, initializes variables including location in tree.
		public Node(E data, P priority, int index) {
			this.data = data;
			this.priority = priority;                              
			map.put(this.data, index); //updates the HashMap when creating a Node
		}
	}

	//////////////////////////////////////////////////////////////////////////
	/** Glass box tests for Heap. */
	public static class Tests {
		
		/**Basic integer Comparator for testing. */
		private static class IntegerComparator implements Comparator<Integer> {
			@Override
			public int compare(Integer o1, Integer o2) {
				if(o1 > o2) {
					return 1;
				}
				if(o1 < o2 ) {
					return -1;
				}
				else {
					return 0;
				}
			}
		}
		
		/** Asserts that Heap satisfies its invariants.*/
		private static void AssertInvariants(Heap<?,?> Heap) {
			//check that the HeapMap stores the indexes correctly
			for(int i = 0; i < Heap.size(); i++) {
				assertEquals(Heap.map.get(Heap.a.get(i).data),i);
			}
			assertTrue(Heap.size()>=0);
		}
		
		@Test
		public void testIndex() {
			Comparator<Integer> c = new IntegerComparator();
			Heap<String,Integer> Heap = new Heap<String,Integer>(c);
			Heap.add("Mike", 1);
			Heap.add("Joe", 3);
			Heap.add("Carl", -3);
			Heap.add("Bob", 7);
			Heap.add("Grant", 12);
			for(Heap.Node n : Heap.a) {
				assertEquals(Heap.index(n), Heap.a.indexOf(n));
			}
			AssertInvariants(Heap);
		}
		
		@Test
		public void testSwap() {
			Comparator<Integer> c = new IntegerComparator();
			Heap<String,Integer> Heap = new Heap<String,Integer>(c);
			Heap.add("Mike", 1);
			Heap.add("Joe", 3);
			Heap.add("Carl", -3);
			Heap.add("Bob", 7);
			Heap.add("Grant", 12);
			Heap.swap(Heap.a.get(Heap.map.get("Mike")), Heap.a.get(Heap.map.get("Carl")));
			Heap.swap(Heap.a.get(Heap.map.get("Bob")), Heap.a.get(Heap.map.get("Carl")));
			assertEquals(Heap.a.get(0).data,"Grant");
			assertEquals(Heap.a.get(1).data,"Carl");
			assertEquals(Heap.a.get(2).data,"Mike");
			assertEquals(Heap.a.get(3).data,"Bob");
			assertEquals(Heap.a.get(4).data, "Joe");
			Heap.swap(Heap.a.get(Heap.map.get("Mike")), Heap.a.get(Heap.map.get("Grant")));
			assertEquals(Heap.a.get(2).data,"Grant");
			assertEquals(Heap.a.get(0).data,"Mike");
			
			AssertInvariants(Heap);
		}
		
		@Test
		public void testGetParent() {
			Comparator<Integer> c = new IntegerComparator();
			Heap<String,Integer> Heap = new Heap<String,Integer>(c);
			Heap.add("Mike", 1);
			Heap.add("Joe", 3);
			Heap.add("Carl", -3);
			Heap.add("Bob", 7);
			Heap.add("Grant", 12);
			assertEquals(Heap.getParent(Heap.a.get(Heap.map.get("Grant"))), null);
			assertEquals(Heap.getParent(Heap.a.get(Heap.map.get("Carl"))), Heap.a.get(Heap.map.get("Grant")));
			assertEquals(Heap.getParent(Heap.a.get(Heap.map.get("Mike"))), Heap.a.get(Heap.map.get("Bob")));
			assertEquals(Heap.getParent(Heap.a.get(Heap.map.get("Bob"))), Heap.a.get(Heap.map.get("Grant")));
			assertEquals(Heap.getParent(Heap.a.get(Heap.map.get("Joe"))), Heap.a.get(Heap.map.get("Bob")));
			
			AssertInvariants(Heap);
			}
		
		@Test
		public void testGetLeft() {
			Comparator<Integer> c = new IntegerComparator();
			Heap<String,Integer> Heap = new Heap<String,Integer>(c);
			Heap.add("Mike", 1);
			Heap.add("Joe", 3);
			Heap.add("Carl", -3);
			Heap.add("Bob", 7);
			Heap.add("Grant", 12);
			Heap.add("Ron", 2);
			Heap.add("Adam", 6);
			Heap.add("Pete", 8);
			Heap.add("Dan", 9);
			assertEquals(Heap.getLeft(Heap.a.get(Heap.map.get("Grant"))), Heap.a.get(Heap.map.get("Dan")));
			assertEquals(Heap.getLeft(Heap.a.get(Heap.map.get("Dan"))), Heap.a.get(Heap.map.get("Pete")));
			assertEquals(Heap.getLeft(Heap.a.get(Heap.map.get("Pete"))), Heap.a.get(Heap.map.get("Mike")));
			assertEquals(Heap.getLeft(Heap.a.get(Heap.map.get("Carl"))), null);
			assertEquals(Heap.getLeft(Heap.a.get(Heap.map.get("Joe"))), null);
			assertEquals(Heap.getLeft(Heap.a.get(Heap.map.get("Mike"))), null);
			assertEquals(Heap.getLeft(Heap.a.get(Heap.map.get("Adam"))), Heap.a.get(Heap.map.get("Carl")));
			
			AssertInvariants(Heap);
			}
		
		@Test
		public void testGetRight() {
			Comparator<Integer> c = new IntegerComparator();
			Heap<String,Integer> Heap = new Heap<String,Integer>(c);
			Heap.add("Mike", 1);
			Heap.add("Joe", 3);
			Heap.add("Carl", -3);
			Heap.add("Bob", 7);
			Heap.add("Grant", 12);
			Heap.add("Ron", 2);
			Heap.add("Adam", 6);
			Heap.add("Pete", 8);
			Heap.add("Dan", 9);
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Grant"))), Heap.a.get(Heap.map.get("Adam")));
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Adam"))), Heap.a.get(Heap.map.get("Ron")));
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Dan"))), Heap.a.get(Heap.map.get("Joe")));
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Pete"))), Heap.a.get(Heap.map.get("Bob")));
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Carl"))), null);
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Joe"))), null);
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Ron"))), null);
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Adam"))), Heap.a.get(Heap.map.get("Ron")));
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Mike"))), null);
			
			AssertInvariants(Heap);
			}
		
		@Test
		public void testPoll() {
			Comparator<Integer> c = new IntegerComparator();
			Heap<String,Integer> Heap = new Heap<String,Integer>(c);
			assertThrows(NoSuchElementException.class, () -> {Heap.poll();});
			Heap.add("Mike", 1);
			assertEquals(Heap.poll(), "Mike");
			Heap.add("Mike", 1);			
			Heap.add("Joe", 3);
			Heap.add("Carl", -3);
			Heap.add("Bob", 7);
			Heap.add("Grant", 12);
			Heap.add("Ron", 2);
			Heap.add("Adam", 6);
			Heap.add("Pete", 8);
			Heap.add("Dan", 9);
			assertEquals(Heap.a.get(0), Heap.a.get(Heap.map.get("Grant")));
			String s = Heap.poll();
			assertEquals(s, "Grant");
			assertEquals(Heap.size(), 8);
			assertEquals(Heap.a.get(0), Heap.a.get(Heap.map.get("Dan")));
			assertEquals(Heap.getLeft(Heap.a.get(Heap.map.get("Dan"))), Heap.a.get(Heap.map.get("Pete")));
			assertEquals(Heap.getLeft(Heap.a.get(Heap.map.get("Pete"))), Heap.a.get(Heap.map.get("Bob")));
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Dan"))), Heap.a.get(Heap.map.get("Adam")));
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Pete"))), Heap.a.get(Heap.map.get("Joe")));
			assertEquals(Heap.getLeft(Heap.a.get(Heap.map.get("Bob"))), Heap.a.get(Heap.map.get("Mike")));
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Joe"))), null);
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Carl"))), null);
			assertThrows(IllegalArgumentException.class, () -> {Heap.add("Dan", 13);});

			AssertInvariants(Heap);
		}
		
		@Test
		public void testPeek() {
			Comparator<Integer> c = new IntegerComparator();
			Heap<String,Integer> Heap = new Heap<String,Integer>(c);
			assertThrows(NoSuchElementException.class, () -> {Heap.peek();});
			Heap.add("Mike", 1);
			Heap.add("Joe", 3);
			Heap.add("Carl", -3);
			Heap.add("Bob", 7);
			Heap.add("Grant", 12);
			Heap.add("Ron", 2);
			Heap.add("Adam", 6);
			Heap.add("Pete", 8);
			Heap.add("Dan", 9);
			assertEquals(Heap.peek(), "Grant");
			Heap.poll();
			assertEquals(Heap.peek(), "Dan");
			String s = Heap.poll();
			assertEquals(s, "Dan");
			assertEquals(Heap.peek(), "Pete");
			
			AssertInvariants(Heap);
		}
		
		@Test
		public void testChangePriority() {
			Comparator<Integer> c = new IntegerComparator();
			Heap<String,Integer> Heap = new Heap<String,Integer>(c);
			Heap.add("Mike", 1);
			Heap.add("Joe", 3);
			Heap.add("Carl", -3);
			Heap.add("Bob", 7);
			Heap.add("Grant", 12);
			assertThrows(NoSuchElementException.class, () -> {Heap.changePriority("Luke", 7);});
			Heap.changePriority("Mike", 13);
			Heap.changePriority("Carl", 17);
			Heap.changePriority("Grant", 5);
			assertEquals(Heap.getParent(Heap.a.get(Heap.map.get("Grant"))), Heap.a.get(Heap.map.get("Bob")));
			assertEquals(Heap.getLeft(Heap.a.get(Heap.map.get("Carl"))), Heap.a.get(Heap.map.get("Bob")));
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Carl"))), Heap.a.get(Heap.map.get("Mike")));
			assertEquals(Heap.getLeft(Heap.a.get(Heap.map.get("Bob"))), Heap.a.get(Heap.map.get("Grant")));
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Bob"))), Heap.a.get(Heap.map.get("Joe")));
			assertEquals(Heap.getLeft(Heap.a.get(Heap.map.get("Grant"))), null);
			assertEquals(Heap.getRight(Heap.a.get(Heap.map.get("Joe"))), null);
			
			AssertInvariants(Heap);
		}
		
		@Test
		public void testGet() {
			Comparator<Integer> c = new IntegerComparator();
			Heap<String,Integer> Heap = new Heap<String,Integer>(c);
			Heap.add("Mike", 1);
			Heap.add("Joe", 3);
			Heap.add("Carl", -3);
			Heap.add("Bob", 7);
			Heap.add("Grant", 12);
			assertThrows(NoSuchElementException.class, () -> {Heap.get("Kevin");});
			for(Heap.Node n : Heap.a) {
				assertEquals(Heap.get(n.data.toString()), n);
			}
			
			AssertInvariants(Heap);
		}
		
		//Test for remaining cases that previous tests does not cover.
		@Test
		public void testOthers(){
			Comparator<Integer> c = new IntegerComparator();
			Heap<String,Integer> Heap = new Heap<String,Integer>(c);
			Heap.add("Mike", 1);
			Heap.add("Joe", 3);
			Heap.add("Carl", -3);
			Heap.add("Bob", 7);
			Heap.changePriority("Joe", -5);
			Heap.add("Sam", 7);
			Heap.changePriority("Carl", 6);
			Heap.add("Kevin", 0);
			Heap.add("Quinn", 4);
			Heap.changePriority("Carl", -3);
			
			AssertInvariants(Heap);
		}
	} 
}


