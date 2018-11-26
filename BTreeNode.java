
public class BTreeNode {
	private TreeObject[] objects;
	private int[] childPtrs;
	private int parentPtr;
	private boolean isLeaf;
	private int numObjects;
	private int byteOffset;
	private int max;
	private int nextOpenSpot;
	public BTreeNode(boolean isLeaf, int byteOffset, int maxObjects) {
		this.isLeaf = isLeaf;
		this.byteOffset = byteOffset;
		nextOpenSpot = 0;
		objects = new TreeObject[maxObjects];
		childPtrs = new int[maxObjects+1];
		parentPtr = 0;
	}
	
	public void setParentPointer(int p) {
		this.parentPtr = p;
	}
	
	public void setChildPointer(int index, int p) {
		this.childPtrs[index] = p;
	}
}
