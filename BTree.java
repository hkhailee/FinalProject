import java.io.File;
import java.io.RandomAccessFile;

public class BTree {

	private int t;	//this is the degree t of the BTree
	private int maxLoad;
	private int size; //the size (in nodes) of the BTree
	private int rootPosition;
	private BTreeNode root;
	private int currentLoad;
	int cursor; //byte position. always point to the end of the file
	private BTreeNode nodeOnMemory;
	private RandomAccessFile raf;
	public BTree(int t, RandomAccessFile raf) throws Exception {
		this.t = t;
		this.raf = raf;	
		cursor = 0;
		maxLoad = (2*t)-1;
		root = new BTreeNode(allocateNode(), maxLoad, raf);
		root.isLeaf = true;
		BTreeNode x = root;
		nodeOnMemory = x;
		x.setIsLeaf(1);
		x.setNumObjects(0);
		x.diskWrite();
		currentLoad = 0;
		
	}
	
	
	private int allocateNode() throws Exception  {
		int x = cursor;
	    raf.seek(cursor); // Go to byte at offset position 5.
	    raf.writeInt(cursor);
	    raf.writeInt(0);
	    raf.writeInt(0);
	    cursor += 3*4;
	    //Initialize all pointers to -1
	    for (int i = 1; i <= (2*t + 1); i++) {
	    	raf.writeInt(-1);
	    	cursor+=4;
	    }
	    //Initialize all TreeObject values
	    for (int i = 1; i <= (2*t - 1); i++) {
	    	raf.writeLong(-1);
	    	raf.writeInt(0);
	    	cursor += 12;
	    }
	    return x;
	}
	
	// When a node has a full child at given index this method will split that child node
	private void splitChild(BTreeNode parentNode, int index) throws Exception {
		//Creates a new node that will house the data for the right child after the split
		BTreeNode newRightNode = new BTreeNode(allocateNode(), maxLoad, raf);		
		//Pulls the full child and stores 
		BTreeNode newLeftNode = parentNode.diskRead(index);
		newRightNode.isLeaf = parentNode.getIsLeaf();
		//Puts objects from the full node to the right node 
		for (int j = 1; j < t; j++) {
			newRightNode.setObject(j, newLeftNode.getObject(j + t));
		}
		if (!newLeftNode.getIsLeaf()) {
			//now moving child pointers
			for (int j = 1; j <= t; j++) {
					newRightNode.setChild(j, newLeftNode.getChild(j + t));
			}
		}
		//Moving the parent node child pointers to add the right node
		for (int j = parentNode.getNumObjects()+1; j > index; j--) {
			parentNode.setChild(j+1, parentNode.getChild(j));
		}
		//Adds right node as child for parent node
		parentNode.setChild(index + 1, newRightNode.byteOffset);
		//Now creating the space for the median object to move up
		for (int j = parentNode.getNumObjects(); j >= index; j--) {
			parentNode.setObject(j+1, parentNode.getObject(j));
		}
		//Moving the median object from the left node to the parent
		parentNode.setObject(index, newLeftNode.getObject(t));
		parentNode.setNumObjects(parentNode.getNumObjects()+1);
		for (int j = newLeftNode.getNumObjects(); j > t; j--) {
			newLeftNode.removeObject(j);
		}
//		
		if (!newLeftNode.getIsLeaf()) {
			for (int j = newLeftNode.getNumChldPtrs(); j > t; j--) {
						newLeftNode.removeChild(j);
			}
		}	
		newLeftNode.removeObject(t);
		//Updates all object counts for relevant nodes
//		parentNode.setNumObjects(1 + parentNode.getNumObjects());
//		newLeftNode.setNumObjects(t-1);
//		newRightNode.setNumObjects(t-1);
		parentNode.setIsLeaf(0);
		newRightNode.setNumObjects(t-1);
		newLeftNode.setNumObjects(t-1);
		newLeftNode.diskWrite();
		newRightNode.diskWrite();		
		parentNode.diskWrite();		
	
	}
	
	public void insert(TreeObject input) throws Exception {
	//	System.out.println(root.getNumObjects() + " " + root.getNumChldPtrs());
	//	System.out.println(root.byteOffset);
		BTreeNode tempRoot = root;
//		System.out.println(22);
		if (root.getNumObjects() == maxLoad) { //When root node is full
			BTreeNode newRoot = new BTreeNode(allocateNode(), maxLoad, raf);
			root = newRoot;
			newRoot.isLeaf = false;
			newRoot.setNumObjects(0);
			newRoot.setChild(1, tempRoot.byteOffset);
			splitChild(newRoot, 1);
			insertNonfull(newRoot, input);
		} else {
			insertNonfull (tempRoot, input);
		}
	}
	
	private void insertNonfull(BTreeNode ancestor, TreeObject input) throws Exception {
//		boolean done = false;
//		BTreeNode temp = ancestor;
		int i = ancestor.getNumObjects();
		//Will recurse to traverse the tree until a leaf is reach for insertion 
		if (ancestor.getIsLeaf()) {
			//Iterates through node until the insert position is located
			while (i >= 1 && input.getValue() < ancestor.getObject(i).getValue() ) {
					ancestor.setObject(i+1, ancestor.getObject(i));
					i--;		
			}
			ancestor.setObject(i+1, input);
			ancestor.setNumObjects(ancestor.getNumObjects()+1);
			ancestor.diskWrite();
			
		} else { //If relevant node is internal
			//Iterates through the node until the relevant child node is located and stores it to memory, will recurse on that child
			while (i >= 1 && input.getValue() < ancestor.getObject(i).getValue()) {
				i--;
			}
				i++;		
				nodeOnMemory = ancestor.diskRead(i);

				if (nodeOnMemory.getNumObjects() == maxLoad) {
					splitChild(ancestor, i);
					nodeOnMemory = ancestor.diskRead(i);
					//After the split will enter if input is larger than all objects in left node
					if (input.getValue() > ancestor.getObject(i).getValue()) {
						i++;
					}
				} 
				insertNonfull(nodeOnMemory, input);
			}
			//If object fails to insert this will cause to run recursively until successful, the node given as a parameter must be saved to memory 
	}
	
	public int search(long k) {
		return searchTree(root, k);
	}
	
	private int searchTree(BTreeNode x, long k) {
		try {
			int i = 1;
			while (i <= x.getNumObjects() && k > x.getObject(i).getValue()) {
				i++;
			}
			if (i <= x.getNumObjects() && k == x.getObject(i).getValue()) {
				return x.getObject(i).getFrequency();
			}
			else if (x.getIsLeaf()) {
				return 0;
			}
			else {
				BTreeNode y = x.diskRead(i);
				return searchTree(y, k);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	
	
	
}
