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
	int byteOffset; //should point to the first byte of the node
	public int maxObjects;
	private int maxPtrs;
	private int nextOpenSpot;
	private RandomAccessFile raf;
	private List<BTreeNode> children;
	private BTreeNode parent;
	public BTreeNode(int byteOffset, int maxObjects, RandomAccessFile raf) {
		this.byteOffset = byteOffset;
		this.raf = raf;
		nextOpenSpot = 0;
		objects = new ArrayList<TreeObject>();
		childPtrs = new ArrayList<Integer>();
		objects.add(new TreeObject(-1, -1));
		childPtrs.add(-1);
		this.maxObjects = maxObjects;
		this.maxPtrs = maxObjects + 1;
		parentPtr = 0;
		children = new ArrayList<BTreeNode>();
		children.add(null);
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
		if (index < objects.size()) {
			objects.set(index, object);
		}
		else {
			objects.add(object);
		}
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
		if (childPtrs.size() <= 1) {
			return true;
		}
		else return false;
	}
	public void setChild(int index, int n) {
		if (index < childPtrs.size()) {
			childPtrs.set(index, n);
		}
		else {
			childPtrs.add(n);
		}
	
	}
	
	public int getChild(int index) {
		return this.childPtrs.get(index);
	}
	
	public void removeChild(int index) {
		childPtrs.remove(index);
	}
	
	public void removeObject(int index) {
		objects.remove(index);
	}
	
	public void diskWrite() throws Exception {
	    raf.seek(byteOffset); // Go to byte at offset position 5.
	    raf.writeInt(byteOffset);
	    raf.writeInt(objects.size()-1);
	    if (isLeaf) {
	    	raf.writeInt(1);
	    }
	    else raf.writeInt(0);
	    raf.writeInt(parentPtr);
	    int i, j;
	    //Write all pointers. Unused pointers will be written as 0.
	    for (i = 1; i < childPtrs.size(); i++) {
	    	raf.writeInt(childPtrs.get(i));
	    }
	    for (j = i; j <= maxPtrs; j++) {
	    	raf.writeInt(-1);
	    }
	    for (i = 1; i < objects.size(); i++) {
	    	raf.writeLong(objects.get(i).getStream());
	    	raf.writeInt(objects.get(i).getFrequency());
	    }
	    for (j = i; j <= maxObjects; j++) {
	    	raf.writeLong(-1);
	    	raf.writeInt(0);
	    }
	    
	}
	
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
	    List<BTreeNode> allChildren = new ArrayList<BTreeNode>();
	    return newNode;
	}
	
	
	private BTreeNode diskReadChildren(int bytePos) throws Exception {
		BTreeNode newNode = new BTreeNode(bytePos, maxObjects, raf);
		System.out.println(bytePos);
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
