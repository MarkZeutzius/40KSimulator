import java.util.ArrayList;
import java.util.List;

public class Wargear {
	String wargearName; 
	int points;
	List<SpecialAbilities> wargearSpecials = new ArrayList<SpecialAbilities>();	
	
	Wargear(){}
	
	Wargear(Wargear w) {
		this(w.getWargearName(),w.getPoints(),w.getWargearSpecials());
	}
	
	Wargear(String wargearName, int points, List<SpecialAbilities> wargearSpecials){
		this.wargearName=wargearName;
		this.points=points;
		this.wargearSpecials=wargearSpecials;
	}
	
	int getPoints() {
		return points;
	}
	
	void setPoints(int points) {
		this.points = points;
	}

	public String getWargearName() {
		return wargearName;
	}

	public void setWargearName(String wargearName) {
		this.wargearName = wargearName;
	}

	public List<SpecialAbilities> getWargearSpecials() {
		return wargearSpecials;
	}

	public void setWargearSpecials(List<SpecialAbilities> wargearSpecials) {
		this.wargearSpecials = wargearSpecials;
	}
	
	public SpecialDoes getSpecialOn (SpecialOn sO) {
		for (SpecialAbilities s : wargearSpecials) {
			if (s.specialOn == sO) {
				return s.specialDoes;
			}
		}
		return null;
	}
	
}
