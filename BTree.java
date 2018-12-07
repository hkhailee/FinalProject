import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
/**
 * @author Marcus Henke and Dominik Huffield and Hailee Kiesecker
 *
 * This class contains a constructor to create a BTree and the supporting 
 * methods that allow for the insertion and searching of nodes
 */
public class BTree {
	private int t; // this is the degree t of the BTree
	private int maxLoad;
	private BTreeNode root;
	int cursor; // byte position. always point to the end of the file
	private BTreeNode nodeOnMemory;
	private RandomAccessFile raf;
	Boolean bool = false;
	/**
	 * The constructor method for the BTree
	 * 
	 * @param t the degree for the size limitations on the nodes
	 * @param raf the random access file that the data is being written to on disk
	 * @throws Exception 
	 */
	public BTree(int t, RandomAccessFile raf) throws Exception {
		this.t = t;
		this.raf = raf;
		cursor = 0;
		maxLoad = (2 * t) - 1;
		root = new BTreeNode(allocateNode(), maxLoad, raf);
		root.isLeaf = true;
		BTreeNode x = root;
		nodeOnMemory = x;
		x.setIsLeaf(1);
		x.setNumObjects(0);
		x.diskWrite();
	}//End of BTree constructor 
	/**
	 * Allocates disk space for a node 
	 * 
	 * @return the byte position of the cursor pointing to the end of the file
	 * @throws Exception
	 */
	private int allocateNode() throws Exception {
		int x = cursor;
		raf.seek(cursor); // Go to byte at offset position 5.
		raf.writeInt(cursor);
		raf.writeInt(0);
		raf.writeInt(0);
		cursor += 3 * 4;
		// Initialize all pointers to -1
		for (int i = 1; i <= (2 * t + 1); i++) {
			raf.writeInt(-1);
			cursor += 4;
		}
		// Initialize all TreeObject values
		for (int i = 1; i <= (2 * t - 1); i++) {
			raf.writeLong(-1);
			raf.writeInt(0);
			cursor += 12;
		}
		return x;
	}//End of allocateNode method
	/**
	 * A boolean method that checks a child node for a duplicate of a tree object 
	 * if a duplicate is found that tree objects frequency is incremented and the 
	 * boolean returns false 
	 * 
	 * @param node the ancestor node of the child being searched for a duplicate 
	 * @param index the index position of the child
	 * @param input the tree object that is being inserted 
	 * @return false if the is a duplicate else true
	 * @throws Exception
	 */
	private boolean canSplit(BTreeNode node, int index, TreeObject input) throws Exception {
		BTreeNode tempNode = node.diskRead(index);
		int obj = tempNode.getNumObjects();

		for (int i = 1; i <= obj; i++) {
			if (input.getValue() == tempNode.getObject(i).getValue()) {
				tempNode.getObject(i).incrFreq();
				tempNode.diskWrite();
				return false;
			}
		}
		return true;
	}//End of canSplit method
	// When a node has a full child at given index this method will split that child
	// node
	/**
	 * When the child node of a parent is full and can hold no more object this method 
	 * will be called thus splitting the node in two
	 * 
	 * @param parentNode the ancestor of the node being split 
	 * @param index the index of the descendant (the full node)
	 * @throws Exception
	 */
	private void splitChild(BTreeNode parentNode, int index) throws Exception {
		// Creates a new node that will house the data for the right child after the
		// split
		BTreeNode newRightNode = new BTreeNode(allocateNode(), maxLoad, raf);
		// Pulls the full child and stores
		BTreeNode newLeftNode = parentNode.diskRead(index);
		newRightNode.isLeaf = parentNode.getIsLeaf();
		// Puts objects from the full node to the right node
		for (int j = 1; j < t; j++) {
			newRightNode.setObject(j, newLeftNode.getObject(j + t));
		}
		if (!newLeftNode.getIsLeaf()) {
			// now moving child pointers
			for (int j = 1; j <= t; j++) {
				newRightNode.setChild(j, newLeftNode.getChild(j + t));
			}
		}
		// Moving the parent node child pointers to add the right node
		for (int j = parentNode.getNumObjects() + 1; j > index; j--) {
			parentNode.setChild(j + 1, parentNode.getChild(j));
		}
		// Adds right node as child for parent node
		parentNode.setChild(index + 1, newRightNode.byteOffset);
		// Now creating the space for the median object to move up
		for (int j = parentNode.getNumObjects(); j >= index; j--) {
			parentNode.setObject(j + 1, parentNode.getObject(j));
		}
		// Moving the median object from the left node to the parent
		parentNode.setObject(index, newLeftNode.getObject(t));
		parentNode.setNumObjects(parentNode.getNumObjects() + 1);
		for (int j = newLeftNode.getNumObjects(); j > t; j--) {
			newLeftNode.removeObject(j);
		}
		if (!newLeftNode.getIsLeaf()) {
			for (int j = newLeftNode.getNumChldPtrs(); j > t; j--) {
				newLeftNode.removeChild(j);
			}
		}
		newLeftNode.removeObject(t);
		parentNode.setIsLeaf(0);
		newRightNode.setNumObjects(t - 1);
		newLeftNode.setNumObjects(t - 1);
		newLeftNode.diskWrite();
		newRightNode.diskWrite();
		parentNode.diskWrite();
	}//End of splitChild method
	/**
	 * The initial insertion method that will split a root if it is full and will 
	 * then pass the insertion object to insertnonfull
	 * @param input the threeobject being inserted 
	 * @throws Exception
	 */
	public void insert(TreeObject input) throws Exception {
		BTreeNode tempRoot = root;
		if (root.getNumObjects() == maxLoad) { // When root node is full
			BTreeNode newRoot = new BTreeNode(allocateNode(), maxLoad, raf);
			root = newRoot;
			newRoot.isLeaf = false;
			newRoot.setNumObjects(0);
			newRoot.setChild(1, tempRoot.byteOffset);
			if (!canSplit(newRoot, 1, input)) {
				return;
			}
			splitChild(newRoot, 1);
			insertNonfull(newRoot, input);
		} else {
			insertNonfull(root, input);
		}
	}//End of insert method
	/**
	 * Recursively calls to descend a tree until a position to insert is found. Will call 
	 * split tree when a full node is found  
	 * @param ancestor the parent node 
	 * @param input the object being inserted 
	 * @throws Exception
	 */
	private void insertNonfull(BTreeNode ancestor, TreeObject input) throws Exception {
		int i = ancestor.getNumObjects();
		for (int j = 1; j <= i; j++) {
			if (input.getValue() == ancestor.getObject(j).getValue()) {
				ancestor.getObject(j).incrFreq();
				ancestor.diskWrite();
				return;
			}
		}
		// Will recurse to traverse the tree until a leaf is reach for insertion
		if (ancestor.getIsLeaf()) {
			// Iterates through node until the insert position is located
			while (i >= 1 && input.getValue() < ancestor.getObject(i).getValue()) {
				ancestor.setObject(i + 1, ancestor.getObject(i));
				i--;
			}
			ancestor.setObject(i + 1, input);
			ancestor.setNumObjects(ancestor.getNumObjects() + 1);
			ancestor.diskWrite();
		} else { // If relevant node is internal
			// Iterates through the node until the relevant child node is located and stores
			// it to memory, will recurse on that child
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
				// After the split will enter if input is larger than all objects in left node
				if (input.getValue() > ancestor.getObject(i).getValue()) {
					i++;
					nodeOnMemory = ancestor.diskRead(i);
				}
			}
			insertNonfull(nodeOnMemory, input);
		}
	}//End of insertNonfull method
	/** 
	 * The method to be called to search a tree
	 * @param longVal the long value of the substring 
	 * @return the frequency of the object being searched for 
	 */
	public int search(long longVal) {
		return searchTree(root, longVal);
	}//End of search method
	/**
	 * The recursive method that traverses a tree looking for a tree object 
	 * @param ancestor the presented node to be searched 
	 * @param longVal the long value of the substring
	 * @return the frequency of the object being searched for 
	 */
	private int searchTree(BTreeNode ancestor, long longVal) {
		try {
			int i = 1;
			while (i <= ancestor.getNumObjects() && longVal > ancestor.getObject(i).getValue()) {
				i++;
			}
			if (i <= ancestor.getNumObjects() && longVal == ancestor.getObject(i).getValue()) {
				return ancestor.getObject(i).getFrequency();
			} else if (ancestor.getIsLeaf()) {
				return 0;
			} else {
				BTreeNode childNode = ancestor.diskRead(i);
				return searchTree(childNode, longVal);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}//End of searchTree method
	/**
	 * The caller method of traversing the tree 
	 * @param file the file name 
	 * @param longVal the long value of the substring 
	 * @param subLength the character length of the substring 
	 * @throws Exception
	 */
	public void traverseTree(String file, int longVal, int subLength) throws Exception {
		FileWriter fw = new FileWriter(new File(file+".btree.dump."+longVal));
		treeTraverse(root, fw, subLength);
		fw.close();
	}//End of traverseTree method
	/**
	 * The recursive method for the tree traversal 
	 * @param rootNode the root node of the tree to be traversed 
	 * @param fw the file writer object 
	 * @param subLength the length of the substring 
	 * @throws Exception
	 */
	private void treeTraverse(BTreeNode rootNode, FileWriter fw, int subLength) throws Exception {
		int chld = rootNode.getNumChldPtrs();
		int obj = rootNode.getNumObjects();
		int n = subLength;
		for (int i = 1; i <= chld; i++) {
			treeTraverse(rootNode.diskRead(i), fw, subLength);
			if (obj >= i) {
				String str = backToString(rootNode.getObject(i).getValue(), rootNode.getObject(i).getFrequency(), n);
				fw.write(str + "\n");
			}
		}
		if (rootNode.getIsLeaf()) {
			for (int j = 1; j <= obj; j++) {
				String str = backToString(rootNode.getObject(j).getValue(), rootNode.getObject(j).getFrequency(), n);
				fw.write(str + "\n");
			}
		}
	}//End of treeTraverse method
	/**
	 * A method use to convert a long value back to the binary string representation of the substring 
	 * @param stream the long value being converted 
	 * @param freq the frequency of the object 
	 * @param strLen the length of the string
	 * @return the binary string value 
	 */
	private String backToString(long stream, int freq, int strLen) {
		String str = Long.toBinaryString(stream);
		StringBuilder sb = new StringBuilder();
		int x = Math.abs(str.length() - (2 * strLen));
		for (int i = 0; i < x; i++) {
			sb.append('0');
		}
		sb.append(str);
		String val = sb.toString();
		sb = new StringBuilder();
		for (int i = 0; i < val.length(); i += 2) {
			String substr = val.substring(i, i + 2);
			if (substr.equals("00")) {
				sb.append('a');
			} else if (substr.equals("01")) {
				sb.append('c');
			} else if (substr.equals("10")) {
				sb.append('g');
			} else if (substr.equals("11")) {
				sb.append('t');
			}
		}
		sb.append(": " + freq);
		return sb.toString();
	}//End of backToStringMethod
} //End of BTree Class
