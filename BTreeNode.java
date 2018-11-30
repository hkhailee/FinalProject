import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class BTreeNode {
	private List<TreeObject> objects;
	private List<Integer> childPtrs;
	private int parentPtr;
	private boolean isLeaf;
	private int numObjects;
	private int byteOffset; //should point to the first byte of the node
	public int maxObjects;
	private int maxPtrs;
	private int nextOpenSpot;
	private List<BTreeNode> children;
	private BTreeNode parent;
	public BTreeNode(boolean isLeaf, int byteOffset, int maxObjects) {
		this.isLeaf = isLeaf;
		this.byteOffset = byteOffset;
		this.
		nextOpenSpot = 0;
		objects = new ArrayList<TreeObject>();
		childPtrs = new ArrayList<Integer>();
		objects.add(new TreeObject(-1, -1));
		childPtrs.add(-1);
		this.maxObjects = maxObjects;
		this.maxPtrs = maxObjects + 1;
		parentPtr = 0;
	}
	
	public void setParentPointer(int p) {
		this.parentPtr = p;
	}
	
	public void setChildPointers(List<Integer> list) {
		this.childPtrs = list;
	}
	
	public void setObjects(List<TreeObject> list) {
		this.objects = list;
	}
	
	public void setParent(BTreeNode n) {
		this.parent = n;
	}
	
	public int getParentPointer() {
		return parentPtr;
	}
	
	public void setNumObjects(int x) {
		this.numObjects = x;
	}
	
	public int getNumObjects() {
		return objects.size()-1;
	}
	
	public int getNumChldPtrs() {
		return childPtrs.size() - 1;
	}
	
	public void setObject(int index, TreeObject object) {
		objects.set(index, object);
	}
	
	public TreeObject getObject(int index) {
		TreeObject object = objects.get(index);
		return object;
	}
	
	public void setIsLeaf(int x) {
		if (x == 1) {
			isLeaf = true;
		}
		else isLeaf = false;
	}
	public boolean getIsLeaf() {
		boolean val = isLeaf;
		return val;
	}
	public void setChild(int index, BTreeNode n) {
		this.children.set(index, n);
	}
	
	public BTreeNode getChild(int index) {
		return this.children.get(index);
	}
	
	public void diskWrite(File file) throws Exception {
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
	    raf.seek(byteOffset); // Go to byte at offset position 5.
	    raf.writeInt(byteOffset);
	    raf.writeInt(objects.size());
	    if (isLeaf) {
	    	raf.writeInt(1);
	    }
	    else raf.writeInt(0);
	    raf.writeInt(parentPtr);
	    int i, j;
	    //Write all pointers. Unused pointers will be written as 0.
	    for (i = 0; i < childPtrs.size(); i++) {
	    	raf.writeInt(childPtrs.get(i));
	    }
	    for (j = i; j < maxPtrs; j++) {
	    	raf.writeInt(-1);
	    }
	    for (i = 0; i < objects.size(); i++) {
	    	raf.writeLong(objects.get(i).getSequence());
	    	raf.writeInt(objects.get(i).getFrequency());
	    }
	    for (j = i; i < maxObjects; i++) {
	    	raf.writeLong(-1);
	    	raf.writeInt(0);
	    }
	    
	}
	
	/**
	 * Return a BTreeNode representing the ith child of this node
	 * @param i
	 */
	public BTreeNode diskRead(int childIndex, File file) throws Exception {
		BTreeNode newNode = new BTreeNode(false, childPtrs.get(childIndex), maxObjects);
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
	    raf.seek(childPtrs.get(childIndex));
	    raf.readInt();
	    newNode.setNumObjects(raf.readInt());
	    newNode.setIsLeaf(raf.readInt());
	    newNode.setParentPointer(raf.readInt());
	    
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
	    for (int i = 0; i < maxObjects; i++) {
	    	long x = raf.readLong();
	    	int y = raf.readInt();
	    	treeObjs.add(new TreeObject(-1, -1));
	    	if (x != -1) {
	    		treeObjs.add(new TreeObject(x, y));
	    	}
	    }
	    newNode.setObjects(treeObjs);
	    List<BTreeNode> allChildren = new ArrayList<BTreeNode>();
	    allChildren.add(null);
	    for (int i = 1; i < newNode.childPtrs.size(); i++) {
	    	allChildren.add(diskReadChildren(childPtrs.get(i), file));
	    }
	    if (newNode.parentPtr != -1) {
	    	newNode.setParent(diskReadChildren(newNode.getParentPointer(), file));
	    }
	    return newNode;
	}
	
	
	private BTreeNode diskReadChildren(int bytePos, File file) throws Exception {
		BTreeNode newNode = new BTreeNode(false, bytePos, maxObjects);
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
	    raf.seek(bytePos);
	    raf.readInt();
	    newNode.setNumObjects(raf.readInt());
	    newNode.setIsLeaf(raf.readInt());
	    newNode.setParentPointer(raf.readInt());
	    
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
	    for (int i = 0; i < maxObjects; i++) {
	    	long x = raf.readLong();
	    	int y = raf.readInt();
	    	treeObjs.add(new TreeObject(-1, -1));
	    	if (x != -1) {
	    		treeObjs.add(new TreeObject(x, y));
	    	}
	    }
	    newNode.setObjects(treeObjs);
	    return newNode;
	}
}
