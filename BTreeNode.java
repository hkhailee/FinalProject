import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class BTreeNode {
	private List<TreeObject> objects;
	private List<Integer> childPtrs;
	private int parentPtr;
	boolean isLeaf;
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
		this.numObjects = 0;
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
//		if (numObjects > maxObjects) {
//			this.numObjects = maxObjects;
//		}
		return this.numObjects;
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
	
	public void addObject(int index, TreeObject object) {
		objects.add(index, object);
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
		if (childPtrs.size() == 1) {
			return true;
		}
		else return false;
	}
	public void setChild(int index, int n) {
		if (index < childPtrs.size()) {
			childPtrs.set(index, n);
		}
		else if (index == childPtrs.size()) {
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
	}
	
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
	    return newNode;
	}
	
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
	}
}
