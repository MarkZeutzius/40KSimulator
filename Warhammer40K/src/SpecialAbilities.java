
public class SpecialAbilities {
	SpecialOn specialOn;
	SpecialDoes specialDoes;
	
	SpecialAbilities(){}
	
	SpecialAbilities(SpecialOn specialOn, SpecialDoes specialDoes) {
		this.specialOn = specialOn;
		this.specialDoes = specialDoes;
	}
	
	public SpecialOn getSpecialOn() {
		return specialOn;
	}
	
	public void setSpecialOn(SpecialOn specialOn) {
		this.specialOn = specialOn;
	}
	
	public SpecialDoes getSpecialDoes() {
		return specialDoes;
	}
	
	public void setSpecialDoes(SpecialDoes specialDoes) {
		this.specialDoes = specialDoes;
	}
	
	public boolean isHitSpecial() {
		if (specialOn.toString().startsWith("hit")) {
			return true;
		}
		return false;
	}
	
	public boolean isWoundSpecial() {
		if (specialOn.toString().startsWith("wound")) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return "\tSpecialOn: "+specialOn+"\tSpecialDoes: "+specialDoes;
	}
}
