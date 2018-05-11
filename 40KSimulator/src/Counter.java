
public class Counter {

	int counter;
	int max;
	
	Counter(){}
	
	Counter(int initial, int max) {
		this.counter = initial;
		this.max = max;
	}
	
	int getRoll() {
		return counter;
	}
	
	void incrementCounter() {
		counter++;
	}
	
	boolean isMax() {
		if (counter >= max) return true;
		else return false;
	}
	
	void reset() {
		counter = 1;
	}
}
