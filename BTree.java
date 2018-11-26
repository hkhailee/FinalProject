import java.io.File;
import java.io.RandomAccessFile;

public class BTree {

	private int t;	//this is the degree t of the BTree
	private int size; //the size (in nodes) of the BTree
	private BTreeNode root;
	private BTreeNode currentNode;
	int cursor; //byte position. always point to the end of the file
	File file;
	public BTree(int t, String fileName) {
		this.t = t;
		root = new BTreeNode(true, 0, 2*t);
		currentNode = root;
		cursor = 0;
		file = new File(fileName);
	}
	
	private void allocateNode() throws Exception  {
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
	    raf.seek(cursor); // Go to byte at offset position 5.
	    raf.writeInt(cursor);
	    raf.writeInt(0);
	    raf.writeInt(0);
	    cursor += 3;
	    //Initialize all pointers to -1
	    for (int i = 1; i <= (2*t + 1); i++) {
	    	raf.writeInt(-1);
	    	cursor++;
	    }
	    //Initialize all TreeObject values
	    for (int i = 1; i <= (2*t - 1); i++) {
	    	raf.writeInt(-1);
	    	raf.writeInt(0);
	    	cursor += 2;
	    }
	    raf.close();  
	}
	
	private void splitChild() {
		//TODO
	}
	
	public void insert(BTreeNode x, int childIndex) {
		//TODO
	}
	
	private void insertNonfull(BTreeObject k, BTreeNode x) {
		//TODO
	}
	
	
	
	
	
}
