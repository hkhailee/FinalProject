import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
/**
 * A class for the nodes within BTree
 * @author Marcus Henke & Dominik Huffield & Hailee Kiesecker
 *
 */
public class BTreeNode {
	private List<TreeObject> objects;
	private List<Integer> childPtrs;
	private int parentPtr;
	boolean isLeaf;
	private int numObjects;
	int byteOffset; //should point to the first byte of the node
	public int maxObjects;
	private int maxPtrs;
	private RandomAccessFile raf;
	private List<BTreeNode> children;
	private BTreeNode parent;
	/**
	 * The constructor method for the nodes of the BTree
	 * @param byteOffset the byte positioning of the node on the random access file 
	 * @param maxObjects the max number of objects the node can hold
	 * @param raf the random access file the node will be written to  
	 */
	public BTreeNode(int byteOffset, int maxObjects, RandomAccessFile raf) {
		this.byteOffset = byteOffset;
		this.raf = raf;
		objects = new ArrayList<TreeObject>();
		childPtrs = new ArrayList<Integer>();
		objects.add(new TreeObject(-1, -1));
		childPtrs.add(-1);
		this.maxObjects = maxObjects;
		this.maxPtrs = maxObjects + 1;
		parentPtr = 0;
		children = new ArrayList<BTreeNode>();
		children.add(null);
		this.numObjects = 0;
	}//End of BTreeNode constructor 
	/**
	 * Sets a new parent of this node 
	 * @param p the pointer to the parent node 
	 */
	public void setParentPointer(int p) {
		this.parentPtr = p;
	}
	/**
	 * Sets a new child pointer for this node 
	 * @param list an array list of child pointers 
	 */
	public void setChildPointers(List<Integer> list) {
		this.childPtrs = list;
	}
	/**
	 * Sets a new object on the node 
	 * @param list an array list of tree objects for the node 
	 */
	public void setObjects(List<TreeObject> list) {
		this.objects = list;
	}
	/**
	 * Sets a new parent node for this node 
	 * @param n the new parent node 
	 */
	public void setParent(BTreeNode n) {
		this.parent = n;
	}
	/**
	 * A getter method for the parent pointer of this node 
	 * @return the parent pointer for this node
	 */
	public int getParentPointer() {
		return parentPtr;
	}
	/**
	 * Sets the number of objects for this node to x
	 * @param x the number of objects
	 */
	public void setNumObjects(int x) {
		this.numObjects = x;
	}
	/**
	 * A getter method for the number of objects in this node 
	 * @return the number of objects
	 */
	public int getNumObjects() {
		return this.numObjects;
	}
	/**
	 * A getter method for the number of child pointers in this node 
	 * @return the number of child pointers in the node 
	 */
	public int getNumChldPtrs() {
		return childPtrs.size() - 1;
	}
	/**
	 * Sets the object at the given index to the given object 
	 * @param index the index to be changed 
	 * @param object the new object 
	 */
	public void setObject(int index, TreeObject object) {
		if (index < objects.size()) {
			objects.set(index, object);
		}
		else {
			objects.add(object);
		}
	}
	/**
	 * Adds a new object to the objects array list
	 * @param index  
	 * @param object the object to be added 
	 */
	public void addObject(int index, TreeObject object) {
		objects.add(index, object);
	}
	/**
	 * A getter method for the object at the given index 
	 * @param index
	 * @return the TreeObject at the given index 
	 */
	public TreeObject getObject(int index) {
		TreeObject object = objects.get(index);
		return object;
	}
	/**
	 * When the parameter is 1 the boolean for leaf will become true otherwise it will be false 
	 * @param x the int value to change the node to a leaf 
	 */
	public void setIsLeaf(int x) {
		if (x == 1) {
			isLeaf = true;
		}
		else isLeaf = false;
	}
	/**
	 * Retrieves the boolean for whether the node is a leaf 
	 * @return 
	 */
	public boolean getIsLeaf() {
		if (childPtrs.size() == 1) {
			return true;
		}
		else return false;
	}
	/**
	 * Sets the child pointer at index to n
	 * @param index the index to be changed
	 * @param n the new child pointer 
	 */
	public void setChild(int index, int n) {
		if (index < childPtrs.size()) {
			childPtrs.set(index, n);
		}
		else if (index == childPtrs.size()) {
			childPtrs.add(n);
		}
	
	}
	/**
	 * A getter method for the child pointer at the given index 
	 * @param index the index of the child pointer to be retrieved 
	 * @return The child pointer 
	 */
	public int getChild(int index) {
		return this.childPtrs.get(index);
	}
	/**
	 * Removes the child pointer at the given index  
	 * @param index
	 */
	public void removeChild(int index) {
		childPtrs.remove(index);
	}
	/**
	 * Removes the object at the given index 
	 * @param index
	 */
	public void removeObject(int index) {
		objects.remove(index);
	}
	/**
	 * A method to write the data of a node so that it may be stored on disk 
	 * @throws Exception
	 */
	public void diskWrite() throws Exception {
	    raf.seek(byteOffset); // seek to byte offset
	    raf.writeInt(byteOffset);
	    raf.writeInt(getNumObjects());
	    if (isLeaf) {
	    	raf.writeInt(1);
	    }
	    else raf.writeInt(0);
	    raf.writeInt(parentPtr);
	    int i, j;
	    //Write all pointers. Unused pointers will be written as 0.
	    for (i = 1; i < childPtrs.size(); i++) {
	    	if (i <= maxObjects+1) {
	    		raf.writeInt(childPtrs.get(i));
	    	}
	    }
	    for (j = i; j <= maxPtrs; j++) {
	    	raf.writeInt(-1);
	    }
	    for (i = 1; i <= numObjects; i++) {
	    	if (i <= maxObjects) {
	    		raf.writeLong(objects.get(i).getStream());
		    	raf.writeInt(objects.get(i).getFrequency());
	    	}
	    }
	    for (j = i; j <= maxObjects; j++) {
	    	raf.writeLong(-1);
	    	raf.writeInt(0);
	    }	    
	}//End of the diskWrite method 
	/**
	 * Writes the root node to the beginning of the random access file so that 
	 * it is easily accessible 
	 * @throws Exception
	 */
	public void diskWriteAsRoot() throws Exception {
	    raf.seek(4); // Go to byte at offset position 4.
	    raf.writeInt(byteOffset);
	    raf.writeInt(getNumObjects());
	    if (isLeaf) {
	    	raf.writeInt(1);
	    }
	    else raf.writeInt(0);
	    raf.writeInt(parentPtr);
	    int i, j;
	    //Write all pointers. Unused pointers will be written as 0.
	    for (i = 1; i < childPtrs.size(); i++) {
	    	if (i <= maxObjects+1) {
	    		raf.writeInt(childPtrs.get(i));
	    	}
	    }
	    for (j = i; j <= maxPtrs; j++) {
	    	raf.writeInt(-1);
	    }
	    for (i = 1; i <= numObjects; i++) {
	    	if (i <= maxObjects) {
	    		raf.writeLong(objects.get(i).getStream());
		    	raf.writeInt(objects.get(i).getFrequency());
	    	}
	    }
	    for (j = i; j <= maxObjects; j++) {
	    	raf.writeLong(-1);
	    	raf.writeInt(0);
	    }	    
	}//End of the diskWriteAsRoot method 
	/**
	 * Return a BTreeNode representing the ith child of this node
	 * @param i
	 */
	public BTreeNode diskRead(int childIndex) throws Exception {
		BTreeNode newNode = new BTreeNode(childPtrs.get(childIndex), maxObjects, raf);
	    raf.seek(childPtrs.get(childIndex));
	    raf.readInt();
	    newNode.setNumObjects(raf.readInt());
	    newNode.setIsLeaf(raf.readInt());
	    raf.readInt();
	    newNode.setParentPointer(byteOffset);
	    //set child pointers
	    List<Integer> ptrs = new ArrayList<Integer>();
	    ptrs.add(-1);
	    for (int i = 0; i < maxPtrs; i++) {
	    	int x = raf.readInt();
	    	if (x != -1) {
	    		ptrs.add(x);
	    	}
	    }
	    newNode.setChildPointers(ptrs);
	    //set tree objects
	    List<TreeObject> treeObjs = new ArrayList<TreeObject>();
	    treeObjs.add(null);
	    for (int i = 0; i < maxObjects; i++) {
	    	long x = raf.readLong();
	    	int y = raf.readInt();
	    	if (x != -1) {
	    		treeObjs.add(new TreeObject(x, y));
	    	}
	    }
	    newNode.setObjects(treeObjs);
	    return newNode;
	}//End of the diskRead method 
	/**
	 * A getter for the root data from the random access file 
	 * @return The BTreeNode object for the root 
	 * @throws IOException
	 */
	public BTreeNode diskReadRoot() throws IOException {
		BTreeNode newNode = new BTreeNode(4, maxObjects, raf);
	    raf.seek(4);
	    raf.readInt();
	    newNode.setNumObjects(raf.readInt());
	    newNode.setIsLeaf(raf.readInt());
	    raf.readInt();
	    newNode.setParentPointer(byteOffset);
	    //set child pointers
	    List<Integer> ptrs = new ArrayList<Integer>();
	    ptrs.add(-1);
	    for (int i = 0; i < maxPtrs; i++) {
	    	int x = raf.readInt();
	    	if (x != -1) {
	    		ptrs.add(x);
	    	}
	    }
	    newNode.setChildPointers(ptrs);
	    //set tree objects
	    List<TreeObject> treeObjs = new ArrayList<TreeObject>();
	    treeObjs.add(null);
	    for (int i = 0; i < maxObjects; i++) {
	    	long x = raf.readLong();
	    	int y = raf.readInt();
	    	if (x != -1) {
	    		treeObjs.add(new TreeObject(x, y));
	    	}
	    }
	    newNode.setObjects(treeObjs);
	    return newNode;
	}//End of the diskReadRoot method 
}//End of the BTreeNode class 
