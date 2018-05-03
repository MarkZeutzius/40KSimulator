import java.util.ArrayList;
import java.util.List;

public class Army {
	
	String armyName;
	List<Formation> formationList = new ArrayList<Formation>();
	
	Army(){}
	
	Army(String armyName) {
		this.armyName = armyName;
	}
	
	void addFormation(String formationType, String factionName) {
		Formation formationToAdd = new Formation(formationType,factionName);
		formationList.add(formationToAdd);
	}
	
	Formation getFormation(int formationNum) {
		return formationList.get(formationNum);
	}
	
	List<Formation> getFormationList() {
		return formationList;
	}
	
	public String toString() {
		StringBuilder armyString = new StringBuilder();
		armyString.append("\nARMY NAME:\t"+armyName);
		for (Formation formation : formationList) {
			armyString.append(formation);
		}
		armyString.append("\n");
		return armyString.toString();
	}

}
