class node {
	int value;
	node left, right;
	node(int val) {
		value = val;
		left = right = null;
	}
}

class BinSearchTree {
	node root;
	void insert(int value) {
		root = inserting(root, value);
	}
	node inserting(node not, int value) {
		if(not == null) return new node(value);
		if(value < not.value) not.left = inserting(not.left, value);
		else if (value > not.value) not.right = inserting(not.right, value);
		return not;
	}
	
	//Search feature
}

class AVLNode {
	int value, height;
	AVLNode left, right;
	
	AVLNode(int val) {
		value = val;
		height = 1; //default as a leaf
	}
}

public class phanh {
	public static void main(String[] args) {
		node root = new node(1);
		root.left = new node(2);
		root.right = new node(3);
		root.left.left = new node(4);
		root.left.right = new node(5);
		
		// Traversal
		System.out.println("PreOder sequence: ");
		preOrder(root);
		System.out.println();
		System.out.println("InOrder sequence: ");
		inOrder(root);
		System.out.println();
		System.out.println("PostOrder sequence: ");
		postOrder(root);
		
		System.out.println("Insertion for bst tree");
		BinSearchTree bst = new BinSearchTree();
		int[] inputList = {8, 4, 12, 2, 6, 10, 14};
		for(int single : inputList) {
			bst.insert(single);
		}
		System.out.println("PreOder of the BST Tree");
		preOrder(bst.root);
		
		
	}
	static void preOrder(node not) {
		if(not == null) return;
		System.out.print(not.value + " ");
		preOrder(not.left);
		preOrder(not.right);
	}
	static void inOrder(node not) {
		if(not == null) return;
		inOrder(not.left);
		System.out.print(not.value + " ");
		inOrder(not.right);
	}
	static void postOrder(node not) {
		if(not == null) return;
		postOrder(not.left);
		postOrder(not.right);
		System.out.print(not.value + " ");
	}
}
