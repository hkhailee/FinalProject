import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

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
	Boolean bool = false;
	public BTree(int t, RandomAccessFile raf) throws Exception {
		this.t = t;
		this.raf = raf;	
		cursor = 0;
		maxLoad = (2*t)-1;
		initialize();
		root = new BTreeNode(allocateNode(), maxLoad, raf);
		root.isLeaf = true;
		BTreeNode x = root;
		nodeOnMemory = x;
		x.setIsLeaf(1);
		x.setNumObjects(0);
		x.diskWrite();
		currentLoad = 0;
	}
	
	public BTree(RandomAccessFile raf, boolean exists) throws IOException {
		nodeOnMemory = root;
		raf.seek(0);
		this.t= raf.readInt();
		maxLoad = (2*t)-1;
		root = new BTreeNode(4, maxLoad, raf);
		root = root.diskReadRoot();
	}
	
	private void initialize() throws Exception {
		raf.seek(0);
		raf.writeInt(t);
		cursor = 4;
		allocateNode();
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
	private boolean canSplit(BTreeNode node, int index, TreeObject input) throws Exception {
		BTreeNode tempNode = node.diskRead(index);
		int obj = tempNode.getNumObjects();
		
		for (int i = 1; i <= obj; i ++) {
			if (input.getValue() == tempNode.getObject(i).getValue()) {
				tempNode.getObject(i).incrFreq();
				tempNode.diskWrite();
				return false;
			}
		}
		return true;
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
		if (input.getValue() == 3) {
			int x = 0;
		}
		if (input.getValue() < 22) {
			 int x = 4;
		}
		BTreeNode tempRoot = root;
//		System.out.println(22);
		if (root.getNumObjects() == maxLoad) { //When root node is full
			BTreeNode newRoot = new BTreeNode(allocateNode(), maxLoad, raf);
			newRoot.isLeaf = false;
			newRoot.setNumObjects(0);
			newRoot.setChild(1, tempRoot.byteOffset);
			if (!canSplit(newRoot, 1, input) ) {
				return;
			}
				splitChild(newRoot, 1);
				insertNonfull(newRoot, input);
				root = newRoot;
		} else {
			insertNonfull (root, input);
		}
	}
	
	private void insertNonfull(BTreeNode ancestor, TreeObject input) throws Exception {
//		boolean done = false;
//		BTreeNode temp = ancestor;
		int i = ancestor.getNumObjects();
		for (int j = 1; j <= i; j++) {
			if (input.getValue() == ancestor.getObject(j).getValue()) {
				ancestor.getObject(j).incrFreq();
				ancestor.diskWrite();
				return;
			}
		}
		
		
		
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
					if (!canSplit(ancestor, i, input)) {
						return;
					}
					splitChild(ancestor, i);
					nodeOnMemory = ancestor.diskRead(i);
					//After the split will enter if input is larger than all objects in left node
					if (input.getValue() > ancestor.getObject(i).getValue()) {
						i++;
						nodeOnMemory = ancestor.diskRead(i);
					}
				} 
				insertNonfull(nodeOnMemory, input);
			}
			//If object fails to insert this will cause to run recursively until successful, the node given as a parameter must be saved to memory 
	}
	
	public void finish() throws Exception {
		raf.seek(4);
		root.diskWriteAsRoot();
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
	
	public void traverseTree(String file, int longVal, int subLength) throws Exception {
		FileWriter fw = new FileWriter(new File(file+".btree.dump."+longVal));
		treeTraverse(root, fw, subLength);
		fw.close();
	}
	
	private void treeTraverse(BTreeNode rootTrav, FileWriter fw, int subLength) throws Exception {
		
		int chld = rootTrav.getNumChldPtrs();
		int obj = rootTrav.getNumObjects();
		
		int n = subLength;
		
		for (int i = 1; i <= chld; i++) {
			treeTraverse(rootTrav.diskRead(i), fw, n);
			if (obj >= i) {
				String str = backToString(rootTrav.getObject(i).getValue(), rootTrav.getObject(i).getFrequency(), n);
				fw.write(str + "\n");
			}	
		}
		if (rootTrav.getIsLeaf()) {
			for (int j = 1; j <= obj; j++) {
				String str = backToString(rootTrav.getObject(j).getValue(), rootTrav.getObject(j).getFrequency(), n);
				fw.write(str + "\n");
			}
		}
		
		
		
	}
	
	private String backToString(long stream, int freq, int strLen) {
		String str = Long.toBinaryString(stream);
		StringBuilder sb = new StringBuilder();
		int x = Math.abs(str.length()-(2*strLen));
		for (int i = 0; i < x; i++) {
			sb.append('0');
		}
		sb.append(str);
		String val = sb.toString();
		sb = new StringBuilder();
		for (int i = 0; i < val.length(); i+=2) {
			String substr = val.substring(i, i+2);
			if (substr.equals("00")) {
				sb.append('a');
			}
			else if(substr.equals("01")) {
				sb.append('c');
			}
			else if(substr.equals("10")) {
				sb.append('g');
			}
			else if(substr.equals("11")) {
				sb.append('t');
			}
		}
		sb.append(": " + freq);
		return sb.toString();
	}
	
	
	
}
