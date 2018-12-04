public class TreeObject implements Comparable<TreeObject> {

	public long Stream;
	public int freq;
	private long Value;
	public String seq;

	public TreeObject(long stream) {
		this.Stream = stream;
		this.freq = 1;
		this.Value = stream;
	}

	public TreeObject(long stream, int frequency) {
		this.Stream = stream;
		this.freq = frequency;
		this.Value = stream;
//		this.seq = dna;
	}

	public long getValue() {
		long value = Value;
		return value;
	}
	
	public int compareTo(TreeObject t) {
		if (Stream > t.getStream()) {
			return 1;
		}
		if (Stream < t.getStream()) {
			return -1;
		}
		else return 0;
	}
	
	public boolean equals(TreeObject t) {
		if (this.Stream == t.getStream()) {
			return true;
		}
		else return false;
	}

	public long getStream() {
		long strm = Stream;
		return strm;
	}

	public int getFrequency() {

		return freq;
	}

	public String getObject() {

		return seq;
	}

}

