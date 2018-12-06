import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * @author Marcus Henke This is the class for Cache objects. The cache object
 *         uses an ArrayList for storing objects, and the ArrayList has a
 *         maximum size. Any object can be stored in a Cache.
 * @param <T>
 */
public class Cache<T> {
	// Initialize instance variables
	private int cacheSize;
	private ArrayList<TreeObject> cacheList;
	private ArrayList<TreeObject> printList;
	private int numReferences;
	private int numHits;

	/**
	 * Constructor for the Cache object. Initializes all instance variables.
	 * 
	 * @param size, size of the cache
	 */
	public Cache(int size) {
		cacheList = new ArrayList<TreeObject>();
		cacheSize = size;
		// numReferences = 0;
		numHits = 0;
	}

	/**
	 * adds object to the top of the Cache. If the Cache has reached its max size,
	 * delete the object at the bottom of the Cache.
	 * 
	 * @param object, object to add to the Cache
	 */
	public void addObject(TreeObject DNAobject) {
		if (cacheList.size() == cacheSize) {
			removeObject();
		}
		cacheList.add(DNAobject);

	}

	/**
	 * Determines if a given object is already in the Cache
	 * 
	 * @param object, object to determine
	 * @return true if the Cache contains the object
	 * @return false if the Cache doesn't contain the object
	 */
	public boolean getObject(TreeObject n) {
		// numReferences++;
		// n.freq++;
		for (TreeObject t : cacheList) {
			if (t.getStream() == n.getStream()) {
				//t.freq++;
				return true;
			}
		}
//		if (cacheList.contains(n)) {
//			n.freq++;
//			
//			return true;
//		}
		return false;
	}
	
	public int getFreq(int index) {
		TreeObject node = cacheList.get(index);
		if(getObject(node)) {
			return node.freq;
		}
		else {
			return 0;
		}
	}

	/**
	 * Moves the given object to the top of the Cache
	 * 
	 * @param object, object to move
	 */
	public void moveToTop(TreeObject DNAobject) {
		cacheList.remove(DNAobject);
		cacheList.add(DNAobject);
		DNAobject.incrFreq();
	}

	/**
	 * Removes all objects from the Cache
	 */
	public void clearCache() {
		cacheList.clear();
	}

	/**
	 * Removes the object at the bottom of the Cache
	 */
	public void removeObject() {
		cacheList.remove(cacheSize - 1);
	}

	/**
	 * @return numReferences, the Cache's number of references
	 */
	public int getReferences() {
		return numReferences;
	}

	/**
	 * @return numHits, the Cache's number of hits
	 */
	public int getHits() {
		return numHits;
	}

	public String toString() {
		try {
			String S = "";
			TreeObject entry;
			TreeObject entry2;
			File file = new File("dump");
			FileWriter rw = new FileWriter(file, true);
			for (int i = 0; i < cacheList.size(); i++) {

				entry = cacheList.get(i);
				// entry2 = cacheList.get(i+1);
				rw.write(entry.getValue() + ": " + entry.getFrequency() + "\n");
				S += (entry.getValue() + ": " + entry.getFrequency() + "\n");

			}
			rw.close();
			System.out.println(cacheList.size());
			return S;
		} catch (Exception e) {
		}
		return null;

	}

}
