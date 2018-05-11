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
	private List<String> replaceThis = new ArrayList<String>();
	private List<String> withThat = new ArrayList<String>();
	private List<Dbl> howManyReplacements = new ArrayList<Dbl>();
	private double woundsRemaining;
	
	Model(){}
	
	Model(Model m) {
		this(m.getName(),m.getMove(),m.getWeaponSkill(),m.getBallisticSkill(),m.getStrength(),m.getToughness(),m.getWounds(),m.getAttacks(),m.getLeadership(),m.getArmorSave(),m.getPointsPerModel(),m.getWeapons(),m.getModelSpecials(),m.getReplaceThis(),m.getWithThat(),m.getHowManyReplacements());
	}
	
	Model(String name, int move, int weaponSkill, int ballisticSkill, int strength, int toughness, int wounds, int attacks, int leadership, int armorSave, int pointsPerModel, List<Weapon> weapons, List<SpecialAbilities> modelSpecials, List<String> replaceThis, List<String> withThat, List<Dbl> howManyReplacements){
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
		this.replaceThis=replaceThis;
		this.withThat=withThat;
		this.howManyReplacements=howManyReplacements;
		this.woundsRemaining = (double)wounds;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMove() {
		return move;
	}

	public void setMove(int move) {
		this.move = move;
	}

	public int getWeaponSkill() {
		return weaponSkill;
	}

	public void setWeaponSkill(int weaponSkill) {
		this.weaponSkill = weaponSkill;
	}

	public int getBallisticSkill() {
		return ballisticSkill;
	}

	public void setBallisticSkill(int ballisticSkill) {
		this.ballisticSkill = ballisticSkill;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getToughness() {
		return toughness;
	}

	public void setToughness(int toughness) {
		this.toughness = toughness;
	}

	public int getWounds() {
		return wounds;
	}

	public void setWounds(int wounds) {
		this.wounds = wounds;
	}

	public int getAttacks() {
		return attacks;
	}

	public void setAttacks(int attacks) {
		this.attacks = attacks;
	}

	public int getLeadership() {
		return leadership;
	}

	public void setLeadership(int leadership) {
		this.leadership = leadership;
	}

	public int getArmorSave() {
		return armorSave;
	}

	public void setArmorSave(int armorSave) {
		this.armorSave = armorSave;
	}

	public int getPointsPerModel() {
		return pointsPerModel;
	}

	public void setPointsPerModel(int pointsPerModel) {
		this.pointsPerModel = pointsPerModel;
	}

	public List<Weapon> getWeapons() {
		return weapons;
	}

	public void setWeapons(List<Weapon> weapons) {
		this.weapons = weapons;
	}

	public List<Wargear> getWargear() {
		return wargear;
	}

	public void setWargear(List<Wargear> wargear) {
		this.wargear = wargear;
	}

	public int getInvulSave() {
		return invulSave;
	}

	public void setInvulSave(int invulSave) {
		if (invulSave < this.invulSave) {
			this.invulSave = invulSave;
		}
	}

	public List<String> getReplaceThis() {
		return replaceThis;
	}

	public void setReplaceThis(List<String> replaceThis) {
		this.replaceThis = replaceThis;
	}

	public List<String> getWithThat() {
		return withThat;
	}

	public void setWithThat(List<String> withThat) {
		this.withThat = withThat;
	}

	public List<SpecialAbilities> getModelSpecials(){
		return this.modelSpecials;
	}
	
	public void setModelSpecials(List<SpecialAbilities> modelSpecials) {
		this.modelSpecials = modelSpecials;
	}

	public void setModelCost(int modelCost) {
		this.modelCost = modelCost;
	}

	public Weapon getWeapon(int i){
		return weapons.get(i);
	}
	
	public int getNumWeapons(){
		return weapons.size();
	}
	
	public List<Dbl> getHowManyReplacements() {
		return howManyReplacements;
	}

	public void setHowManyReplacements(List<Dbl> howManyReplacements) {
		this.howManyReplacements = howManyReplacements;
	}
	
	public String getWeaponsForPrint() {
		StringBuilder allNames = new StringBuilder();
		for (Weapon weapon : weapons) {
			allNames.append(weapon.getName()+"\n");
		}
		return allNames.toString();
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
					setInvulSave(6);
				} 
				else if (s.specialDoes == SpecialDoes.invul5p) {
					setInvulSave(5);
				} 
				else if (s.specialDoes == SpecialDoes.invul4p) {
					setInvulSave(4);
				} 
				else if (s.specialDoes == SpecialDoes.invul3p) {
					setInvulSave(3);
				}
			}
		}

		StringBuilder modelName = new StringBuilder(name);
		for (Weapon w : weapons) {
			if (!w.getType().equals("grenade")) {
				modelName.append(" w "+w);
			}
		}
		name=modelName.toString();
	}
	
	public List<SpecialAbilities> getSpecials(int weaponNum) {
		return getSpecials(this.getWeapon(weaponNum));
	}
	
	public List<SpecialAbilities> getSpecials(Weapon weapon) {
		List<SpecialAbilities> tempList = new ArrayList<SpecialAbilities>();
		for (SpecialAbilities sa : modelSpecials) {
				tempList.add(sa);
		}
		tempList.addAll(weapon.getSpecials());
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
	
	public Boolean hasWeapon(String weaponName) {
		Boolean hasWeapon = false;
		for (Weapon w : weapons) {
			if (w.getWeaponName().equals(weaponName)) hasWeapon=true;
		}
		return hasWeapon;
	}
	
	public Boolean canThrowGrenade() {
		Boolean canThrowGrenade = true;
		for (Weapon w : weapons) {
			if (w.getType().equals("pistol") || w.getType().equals("assault") || w.getType().equals("rapid fire") || w.getType().equals("heavy")) canThrowGrenade = false;
		}
		return canThrowGrenade;
	}
	
	public void removeWeapons(List<String> weaponNames) {
		for (String wN : weaponNames) {
			for (int i=0 ; i<weapons.size() ; i++) {
				if (weapons.get(i).getName().equals(wN.trim())) weapons.remove(i);
			}
		}
	}
	
	public void addWeapon(Weapon w) {
		weapons.add(w);
	}
	
	public void addWargear(Wargear w) {
		wargear.add(w);
	}
	
	public void takeDamage(double damage) {
		woundsRemaining = woundsRemaining - damage;
	}
	
	public double getWoundsRemaining() {
		return woundsRemaining;
	}
	
	public String toString() {
		return name+"\n";
	}
	
}
