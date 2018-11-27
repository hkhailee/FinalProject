

public class TreeObject {	
	
	private long stream;
	private int frequency;
	/*
	 * constructor
	 */
	public TreeObject(long stream) {
		this.stream = 0;
		frequency = 0;
	}
	
	public TreeObject(long stream, int frequency) {
		this.stream = stream;
		this.frequency = frequency;
	}
	
	public int getFrequency() {
		return this.frequency;
	}
	
	public long getSequence() {
		return this.stream;
	}
}
