
public class Dbl {
	double value;
	
	Dbl(int value) {
		this.value=value;
	}
	
	Dbl(String value) {
		if (value == null || value=="") {
			this.value = 0;
		} else {
			this.value=Double.parseDouble(value);
		}
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	public void addOne() {
		this.value = this.value+1;
	}
	
}
