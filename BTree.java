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
		root = new BTreeNode(true, allocateNode(), maxLoad, raf);
		BTreeNode x = root;
		nodeOnMemory = x;
		x.setIsLeaf(1);
		x.setNumObjects(0);
		x.diskWrite();
		currentLoad = 0;
		maxLoad = (2*t)-1;
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
		BTreeNode newRightNode = new BTreeNode(parentNode.getChild(index).getIsLeaf(), allocateNode(), (2*t)-1, raf);
		
		//Pulls the full child and stores 
		BTreeNode newLeftNode = parentNode.getChild(index);
		
		//Puts objects from the full node to the right node 
		for (int j = 1; j < t; j++) {
			newRightNode.setObject(j, newLeftNode.getObject(j + t));
			newLeftNode.removeObject(j + t);
		}
		if ( ! newLeftNode.getIsLeaf()) {
			//now moving child pointers
			for (int j = 1; j <= t; j++) {
				newRightNode.setChild(j, newLeftNode.getChild(j + t));
				newLeftNode.removeChild(j + t);
			}
		}
		//Moving the parent node child pointers to add the right node
		for (int j = parentNode.getNumObjects(); j > index; j--) {
			parentNode.setChild(j+1, parentNode.getChild(j));
		}
		//Adds right node as child for parent node
		parentNode.setChild(index + 1, newRightNode);
		//Now creating the space for the median object to move up
		for (int j = parentNode.getNumObjects(); j >= index; j--) {
			parentNode.setObject(j+1, parentNode.getObject(j));
		}
		//Moving the median object from the left node to the parent
		parentNode.setObject(index, newLeftNode.getObject(t));
		newLeftNode.removeObject(t);
		//Updates all object counts for relevant nodes
		parentNode.setNumObjects(1 + parentNode.getNumObjects());
		newLeftNode.setNumObjects(t-1);
		newRightNode.setNumObjects(t-1);
		
		newLeftNode.diskWrite();
		newRightNode.diskWrite();		
		parentNode.diskWrite();		
	
	}
	
	public void insert(TreeObject input) throws Exception {
		BTreeNode tempRoot = root;
		System.out.println(root.getNumObjects());
//		System.out.println(22);
		if (root.getNumObjects() == maxLoad) { //When root node is full
			BTreeNode newRoot = new BTreeNode(false, allocateNode(), maxLoad, raf);
			newRoot.setChild(1, tempRoot);
			splitChild(newRoot, 1);
			insertNonfull(newRoot, input);
		} else {
			insertNonfull (tempRoot, input);
		}
	}
	
	private void insertNonfull(BTreeNode ancestor, TreeObject input) throws Exception {
		boolean done = false;
		int i = ancestor.getNumObjects() ;
		//Will recurse to traverse the tree until a leaf is reach for insertion 
		if (ancestor.getIsLeaf()) {
			//Iterates through node until the insert position is located
			while (i >= 1 && input.getValue() < ancestor.getObject(i).getValue() ) {
				//Moves an object up one position with each interval
				ancestor.setObject(i+1, ancestor.getObject(i));
				i--;
			}
			//Adds the input object to the vacant position
			ancestor.setObject(i+1, input);
//			ancestor.setNumObjects(1 + ancestor.getNumObjects());

			ancestor.diskWrite();
			done = true;
			
		} else { //If relevant node is internal
			//Iterates through the node until the relevant child node is located and stores it to memory, will recurse on that child
			while (i >= 1 && input.getValue() < ancestor.getObject(i).getValue()) {
				i--;
			}
			i++;
			

			nodeOnMemory = ancestor.diskRead(i);
			
			
			
		
			//Splits node if newly accessed node is full
			if (nodeOnMemory.getNumObjects() == maxLoad) {
				System.out.println(i);
				System.out.println(nodeOnMemory.getNumObjects());
				splitChild(ancestor, i);
				
	
				nodeOnMemory = ancestor.diskRead(i);
				
				
				//After the split will enter if input is larger than all objects in left node
				if (input.getValue() > ancestor.getObject(i).getValue()) {
					
	
					nodeOnMemory = ancestor.diskRead(i+1);
				}
			} 
			
			//If object fails to insert this will cause to run recursively until successful, the node given as a parameter must be saved to memory 
			if (done == false) {
				insertNonfull(nodeOnMemory, input);
			}
		}
	}
	
	
	
	
}import java.io.File;
2
import java.io.RandomAccessFile;
3
​
4
public class BTree {
5
​
6
        private int t;  //this is the degree t of the BTree
7
        private int maxLoad;
8
        private int size; //the size (in nodes) of the BTree
9
        private int rootPosition;
10
        private BTreeNode root;
11
        private int currentLoad;
12
        int cursor; //byte position. always point to the end of the file
13
        private BTreeNode nodeOnMemory;
14
        private RandomAccessFile raf;
15
        public BTree(int t, RandomAccessFile raf) throws Exception {
16
                this.t = t;
17
                this.raf = raf;
18
                root = new BTreeNode(true, 0, maxLoad, raf);
19
                BTreeNode x = root;
20
                nodeOnMemory = x;
21
                x.setIsLeaf(1);
22
                x.setNumObjects(0);
23
                x.diskWrite();
24
                currentLoad = 0;
25
                cursor = 0;
26
                maxLoad = (2*t)-1;
27
        }
28
        
29
        
30
        private int allocateNode() throws Exception  {
31
                int x = cursor;
32
            raf.seek(cursor); // Go to byte at offset position 5.
33
            raf.writeInt(cursor);
34
            raf.writeInt(0);
35
            raf.writeInt(0);
36
            cursor += 3*4;
37
            //Initialize all pointers to -1
38
            for (int i = 1; i <= (2*t + 1); i++) {
39
                raf.writeInt(-1);
40
                cursor+=4;
41
            }
42
            //Initialize all TreeObject values
43
            for (int i = 1; i <= (2*t - 1); i++) {
44
                raf.writeLong(-1);
45
                raf.writeInt(0);
46
                cursor += 12;
47
            }
48
            raf.close();  
49
            return x;
