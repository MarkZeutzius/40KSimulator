import java.util.ArrayList;
import java.util.List;

public class Formation {
	
	String factionName;
	String formationType;
	List<Unit> unitList = new ArrayList<Unit>();
	List<Weapon> weaponList = new ArrayList<Weapon>();
	List<Wargear> wargearList = new ArrayList<Wargear>();
	
	Formation(){}
	Formation(String formationType, String factionName)
	{
		this.factionName = factionName;
		this.formationType = formationType;
	}
	
	void addUnit(Unit unitToAdd) {
		unitList.add(unitToAdd);
	}
		
	void addWeapon(Weapon weaponToAdd) {
		weaponList.add(weaponToAdd);
	}
	
	List<Unit> getUnitList() {
		return unitList;
	}
	
	Weapon getWeaponByName(String weaponName) {
		for (Weapon weapon : weaponList) {
			if (weapon.getWeaponName().equals(weaponName)) {
				return weapon;
			}
		}
		return null;
	}
	
	public String toString() {
		StringBuilder formationString = new StringBuilder();
		formationString.append("\nFormation Name:\t"+factionName+"\t"+formationType);
		for (Unit unit : unitList) {
			formationString.append(unit);
		}
		formationString.append("\n");
		return formationString.toString();
	}
}
