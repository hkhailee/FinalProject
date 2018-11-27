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
	private int maxObjects;
	private int maxPtrs;
	private int nextOpenSpot;
	public BTreeNode(boolean isLeaf, int byteOffset, int maxObjects) {
		this.isLeaf = isLeaf;
		this.byteOffset = byteOffset;
		nextOpenSpot = 0;
		objects = new ArrayList<TreeObject>();
		childPtrs = new ArrayList<Integer>();
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
	
	public void setNumObjects(int x) {
		this.numObjects = x;
	}
	
	public int getNumObjects() {
		return objects.size();
	}
	
	public void setObject(int index, TreeObject object) {
		objects.set(index, object);
	}
	
	public void getObject(int index) {
		objects.get(index);
	}
	
	public void setIsLeaf(int x) {
		if (x == 1) {
			isLeaf = true;
		}
		else isLeaf = false;
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
	    	if (x != -1) {
	    		treeObjs.add(new TreeObject(x, y));
	    	}
	    }
	    newNode.setObjects(treeObjs);
	    return newNode;
	}
}
