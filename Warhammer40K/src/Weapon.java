import java.util.ArrayList;
import java.util.List;

public class Weapon {
	String name; 
	String range;
	String type; 
	String shots;
	String strength; 
	int armorPenetration; 
	String damage;
	int pointsPerWeapon;
	List<SpecialAbilities> weaponSpecials = new ArrayList<SpecialAbilities>();	
	List<String> replacements = new ArrayList<String>();
	String firingMode;
	List<Weapon> extraFiringModes = new ArrayList<Weapon>();
	
	Weapon(){}
	
	Weapon(Weapon w) {
		this(w.getName(),w.getRange(),w.getType(),w.getShots(),w.getStrength(),w.getArmorPenetration(),w.getDamage(),w.getPointsPerWeapon(),w.getWeaponSpecials());
	}
	
	Weapon(String name, String range, String type, String shots, String strength, int armorPenetration, String damage, int pointsPerWeapon, List<SpecialAbilities> weaponSpecials){
		this.name=name;
		this.range=range;
		this.type=type;
		this.shots=shots;
		this.strength=strength;
		this.armorPenetration=armorPenetration;
		this.damage=damage;
		this.pointsPerWeapon=pointsPerWeapon;
		this.weaponSpecials=weaponSpecials;
	}
		
	public String getWeaponName() {
		return name;
	}
		
	public String getName() {
		return name;
	}
	
	public String getRange() {
		return range;
	}
	
	public String getType() {
		return type;
	}
	
	public String getShots() {
		return shots;
	}
	
	public String getStrength() {
		return strength;
	}
	
	public int getArmorPenetration() {
		return armorPenetration;
	}
	
	public String getDamage() {
		return damage;
	}
	
	public int getPointsPerWeapon() {
		return pointsPerWeapon;
	}
	
	public String getFiringMode() {
		return firingMode;
	}
	
	public List<Weapon>  getExtraFiringModes() {
		return extraFiringModes;
	}
	
	public List<SpecialAbilities> getWeaponSpecials() {
		return weaponSpecials;
	}
	
	public List<String> getReplacements() {
		return replacements;
	}
	
	public String getReplacementByIndex(int index) {
		return replacements.get(index);
	}
	
	public void setReplacements(List<String> replacements) {
		this.replacements=replacements;
	}
	
	public void setFiringMode(String firingMode) {
		this.firingMode=firingMode;
	}
	
	public void setExtraFiringModes(List<Weapon> extraFiringModes) {
		this.extraFiringModes=extraFiringModes;
	}
	
	public int getWeaponCombinations() {
		if ((replacements != null) || (replacements.size() > 0)) {
			return 1 + replacements.size();
		} 
		else {
			return 1;
		}
			
	}
	
	public List<SpecialAbilities> getHitSpecials() {
		List<SpecialAbilities> tempList = new ArrayList<SpecialAbilities>();
		for (SpecialAbilities sa : weaponSpecials) {
			if (sa.isHitSpecial()) {
				tempList.add(sa);
			}
		}
		return tempList;
	}
	
	public List<SpecialAbilities> getWoundSpecials() {
		List<SpecialAbilities> tempList = new ArrayList<SpecialAbilities>();
		for (SpecialAbilities sa : weaponSpecials) {
			if (sa.isWoundSpecial()) {
				tempList.add(sa);
			}
		}
		return tempList;
	}
	
	public List<SpecialAbilities> getSpecials() {
		List<SpecialAbilities> tempList = new ArrayList<SpecialAbilities>();
		for (SpecialAbilities sa : weaponSpecials) {
				tempList.add(sa);
		}
		return tempList;
	}
	
	public List<SpecialAbilities> getSpecials(int modeNum) {
		List<SpecialAbilities> tempList = new ArrayList<SpecialAbilities>();
		for (SpecialAbilities sa : extraFiringModes.get(modeNum).weaponSpecials) {
				tempList.add(sa);
		}
		return tempList;
	}
	
	public String toString() {
		return name;
	}
}
