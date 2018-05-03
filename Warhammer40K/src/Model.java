import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {

	private String name;
	private int move;
	private int	weaponSkill;
	private int ballisticSkill;
	private int strength;
	private int toughness;
	private int wounds;
	private int attacks;
	private int leadership;
	private int armorSave;
	private int pointsPerModel;
	private List<Weapon> weapons = new ArrayList<Weapon>();
	private List<Wargear> wargear = new ArrayList<Wargear>();
	private List<SpecialAbilities> modelSpecials = new ArrayList<SpecialAbilities>();
	private int modelCost;
	private int invulSave;
	
	Model(){}
	
	Model(Model m) {
		this(m.getName(),m.getMove(),m.getWeaponSkill(),m.getBallisticSkill(),m.getStrength(),m.getToughness(),m.getWounds(),m.getAttacks(),m.getLeadership(),m.getArmorSave(),m.getPointsPerModel(),m.getWeapons(),m.getModelSpecials());
	}
	
	Model(String name, int move, int weaponSkill, int ballisticSkill, int strength, int toughness, int wounds, int attacks, int leadership, int armorSave, int pointsPerModel, List<Weapon> weapons, List<SpecialAbilities> modelSpecials){
		this.name=name;
		this.move=move;
		this.weaponSkill=weaponSkill;
		this.ballisticSkill=ballisticSkill;
		this.strength=strength;
		this.toughness=toughness;
		this.wounds=wounds;
		this.attacks=attacks;
		this.leadership=leadership;
		this.armorSave=armorSave;
		this.pointsPerModel=pointsPerModel;
		this.weapons=weapons;
		this.modelSpecials=modelSpecials;
	}
	
	public String getName() {
		return name;
	}
	
	public int getMove() {
		return move;
	}
	
	public int getWeaponSkill() {
		return weaponSkill;
	}
	
	public int getBallisticSkill() {
		return ballisticSkill;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public int getToughness() {
		return toughness;
	}
	
	public int getWounds() {
		return wounds;
	}
	
	public int getAttacks() {
		return attacks;
	}
	
	public int getLeadership() {
		return leadership;
	}
	
	public int getArmorSave() {
		return armorSave;
	}
	
	public int getPointsPerModel() {
		return armorSave;
	}
	
	public int getInvulSave() {
		return invulSave;
	}
	
	public List<Weapon> getWeapons(){
		return this.weapons;
	}
	
	public Weapon getWeapon(int i){
		return weapons.get(i);
	}
	
	public int getNumWeapons(){
		return weapons.size();
	}
	
	public void setWeapon(int index, Weapon replacementWeapon){
		this.weapons.set(index, replacementWeapon);
	}
	
	public List<SpecialAbilities> getModelSpecials(){
		return this.modelSpecials;
	}
	
	public int getModelWeaponCombinations() {
		int product = 1;
		for (Weapon weapon : weapons) {
			product = product * weapon.getWeaponCombinations();
		}
		return product;
	}
	
	public void setWeaponByCombinationNumber(int index, Formation formation) {
		int currentLocation = 0;
		int weaponNum = 0;
		Weapon combinationWeapon = new Weapon();
		for (Weapon weapon : weapons) {
			int combinations = weapon.getWeaponCombinations();
			if (index < currentLocation + combinations) {
				if (index == currentLocation) {
					combinationWeapon = weapon;
				}
				else {
					combinationWeapon = formation.getWeaponByName(weapon.getReplacementByIndex(index-currentLocation-1));
				}
				break;
			}
			else {
				currentLocation = currentLocation + combinations;
				weaponNum = weaponNum + 1;
			}
		}
		this.weapons.set(weaponNum, combinationWeapon);
	}
	
	public String getWeaponsForPrint() {
		StringBuilder allWeapons = new StringBuilder();
		for (Weapon weapon : weapons) {
			allWeapons.append(weapon.getWeaponName()+", ");
		}
		return allWeapons.toString();
	}
	
	public int getModelCost( ) {
		return modelCost;
	}
	
	public void finalize() {
		modelCost = pointsPerModel;
		
		for (Weapon w : weapons) {
			modelCost = modelCost + w.getPointsPerWeapon();
		}
		for (Wargear w : wargear) {
			modelCost = modelCost + w.getPoints();
		}
		for (SpecialAbilities s : modelSpecials) {
			if (s.specialOn == SpecialOn.defend) {
				if (s.specialDoes == SpecialDoes.invul6p) {
					invulSave = 6;
				} 
				else if (s.specialDoes == SpecialDoes.invul5p) {
					invulSave = 5;
				} 
				else if (s.specialDoes == SpecialDoes.invul4p) {
					invulSave = 4;
				} 
				else if (s.specialDoes == SpecialDoes.invul3p) {
					invulSave = 3;
				}
			}
		}

		StringBuilder modelName = new StringBuilder();
		modelName.append(name+" w "+weapons.get(0));
		name=modelName.toString();
	}
	
	public List<SpecialAbilities> getSpecials(int weaponNum) {
		List<SpecialAbilities> tempList = new ArrayList<SpecialAbilities>();
		for (SpecialAbilities sa : modelSpecials) {
				tempList.add(sa);
		}
		tempList.addAll(this.getWeapon(weaponNum).getSpecials());
		return tempList;
	}
	
	public List<SpecialAbilities> getSpecials(int weaponNum, int weaponMode) {
		List<SpecialAbilities> tempList = new ArrayList<SpecialAbilities>();
		for (SpecialAbilities sa : modelSpecials) {
				tempList.add(sa);
		}
		tempList.addAll(this.getWeapon(weaponNum).getSpecials(weaponMode));
		return tempList;
	}
	
	public String toString() {
		return name+"\n";
	}
	
}
