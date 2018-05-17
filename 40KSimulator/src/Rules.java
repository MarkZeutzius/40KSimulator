import java.util.ArrayList;
import java.util.List;

public class Rules {
	
	static final double ONE_SIX = 0.1667;
	static final double TWO_SIX = 0.3333;
	static final double THREE_SIX = 0.5;
	static final double FOUR_SIX = 0.6667;
	static final double FIVE_SIX = 0.8333;
	
	Model attacker;
	Model defender;
	Weapon aWeapon;
	List<SpecialAbilities> modelAndWeaponSpecials = new ArrayList<SpecialAbilities>();
	
	double numberOfShots = 0;
	double hit = 0;
	double wound = 0;
	double save = 0;
	double damage = 0;
	double mortalWounds = 0;
	
	double numberOfShotsSpecial = 0;
	double hitSpecial = 0;
	double woundSpecial = 0;
	double saveSpecial = 0;
	double damageSpecial = 0;
	double mortalWoundsSpecial = 0;
	
	double results = 0;
	double resultsSpecial = 0;
	int weaponNum = 0;
	double range=0;
	boolean ran=false;
	boolean moved=false;
	
	Rules(){}
	
	double fight(Model attacker, int weaponNum, int modeNum, Model defender) {
		return fight(attacker, attacker.getWeapon(weaponNum).getExtraFiringModes().get(modeNum),defender, 1.0);
	}
	
	double fight(Model attacker, int weaponNum, Model defender) {
		return fight(attacker, attacker.getWeapon(weaponNum), defender, 1.0);
	}
	
	double fight(Model attacker, Weapon weapon, Model defender,double range, boolean ran, boolean moved) {
		this.ran=ran;
		this.moved=moved;
		this.attacker=attacker;
		this.defender=defender;
		this.aWeapon=weapon;
		this.range=range;
		modelAndWeaponSpecials = new ArrayList<SpecialAbilities>(attacker.getSpecials(weapon));
		return calculateDps();
	}
	
	double fight(Model attacker, Weapon weapon, Model defender,double range) {
		this.attacker=attacker;
		this.defender=defender;
		this.aWeapon=weapon;
		this.range=range;
		modelAndWeaponSpecials = new ArrayList<SpecialAbilities>(attacker.getSpecials(weapon));
		return calculateDps();
	}
	
	double calculateDps() {
		clear();
		String numberOfShotsString = aWeapon.getShots();
		numberOfShots = getNumberOfShots(numberOfShotsString);
		if ((aWeapon.getType().equals("rapid fire")) && (range < aWeapon.getRangeInt()/2.0)) numberOfShots = numberOfShots*2;
		hit = hitChance();
		wound = woundChance();
		save = saveChance();
		damage = damagePerAttack();
		
		results = numberOfShots*hit*wound*(save*damage+mortalWounds);

		if (woundSpecial > 0) {
			if (numberOfShotsSpecial == 0) numberOfShotsSpecial = numberOfShots;
			if (hitSpecial == 0) hitSpecial = hit;
			if (saveSpecial == 0) saveSpecial = save;
			if (damageSpecial == 0) damageSpecial = damage;
			if (mortalWoundsSpecial == 0) mortalWoundsSpecial = mortalWounds;
			resultsSpecial = numberOfShotsSpecial*hitSpecial*woundSpecial*(saveSpecial*damageSpecial+mortalWoundsSpecial);
		}
		
		return results+resultsSpecial;		
	}
	
	double hitChance() {
		double hitChance = (7.0-(double)attacker.getBallisticSkill())/6.0;
		for (SpecialAbilities sA : modelAndWeaponSpecials) {
			if (sA.specialOn == SpecialOn.miss1m) {
				hitChance = ONE_SIX*hitChance + hitChance;
			}
		}
		if (ran) {
			if (aWeapon.getType().equals("assault")) hitChance = hitChance - ONE_SIX;
			if (aWeapon.getType().equals("rapid fire")) hitChance = 0;
			if (aWeapon.getType().equals("heavy")) hitChance = 0;
		} else if (moved) {
			if (aWeapon.getType().equals("heavy")) hitChance = hitChance - ONE_SIX;
		}
		return hitChance;
	}
	
	double woundChance() {
		int woundTemp = 0;
		int weaponStrength = Integer.parseInt(aWeapon.getStrength());
		if (weaponStrength >= 2 * defender.getToughness()) {
			woundTemp = 5;
		} 
		else if (weaponStrength > defender.getToughness()) {
			woundTemp = 4;
		}
		else if (weaponStrength == defender.getToughness()) {
			woundTemp = 3;
		}
		else if (weaponStrength * 2 >= defender.getToughness()) {
			woundTemp = 2;
		}
		else {
			woundTemp = 1;
		}
		
		for (SpecialAbilities sA : modelAndWeaponSpecials) {
			if (sA.specialOn == SpecialOn.wound6p) {
				woundTemp = woundTemp - 1;
				woundSpecial = ONE_SIX;
				computeSpecialEffect(sA);
			}
		}
		
		return woundTemp/6.0;
	}
	
	double saveChance() {
		return saveChance(aWeapon.getArmorPenetration());
	}
	
	double saveChance(int armorPenetration) {
		int armorSave = defender.getArmorSave() + armorPenetration;
		if ((defender.getInvulSave() > 0) && (armorSave > defender.getInvulSave())) {
			armorSave = defender.getInvulSave();
		}
		return ((double)armorSave - 1.0) / 6.0;
	}
	
	double damagePerAttack() {
		String dpaString = aWeapon.getDamage();
		double dpa = 1;
		int numberOfDice = 1;
		int numberOfSides = 1;
		
		if (dpaString.contains("d")) {
			String[] tokens = dpaString.split("d");
			if (tokens[0].length()>0) {
				numberOfDice = Integer.parseInt(tokens[0]);
			}
			numberOfSides = Integer.parseInt(tokens[1]);
		}
		else {
			dpa = Integer.parseInt(dpaString);
		}
		
		//If damage is fixed
		if ((numberOfSides == 1 ) && (numberOfDice == 1)) {
			if (dpa > defender.getWounds()) {
				dpa = defender.getWounds();
			} else if (dpa > 1) {
				int remainder = defender.getWounds() % (int)dpa;
				if (remainder > 0 ) {
					int numOfWoundsToKill = (int)(defender.getWounds()/dpa);
					dpa = dpa*defender.getWounds()/(dpa*(numOfWoundsToKill+remainder));
				}
			}
		//If damage is a dice roll
		} else {
			int sum = 0;
			List<Counter> diceRoll = new ArrayList<Counter>();
			for (int i = 0; i < numberOfDice; i++) {
				diceRoll.add(new Counter(1,numberOfSides));
			}

			for (int i = 0; i < Math.pow(numberOfSides,numberOfDice); i++) {
				boolean increment = true;
				for (Counter c : diceRoll) {
					int roll = c.getRoll();
					if (roll > defender.getWounds()) {
						sum += defender.getWounds();
					} else {
						sum += roll;
					}
					if (!c.isMax() && increment) {
						c.incrementCounter();
						increment = false;
					} 
					else if (c.isMax() && increment) {
						c.reset();
						increment = true;
					}
				}
			}
			dpa = (double)sum / Math.pow(numberOfSides,numberOfDice);
		}
				
		return dpa;
	}
	
	double getNumberOfShots(String numberOfShots) {
		if (numberOfShots.contains("d")) {
			String[] dice = numberOfShots.split("d");
			int firstNum = 1;
			if (dice[0].equals("")) {
				firstNum =1;
			} else {
				firstNum = Integer.parseInt(dice[0]);
			}
			int secondNum = Integer.parseInt(dice[1]);
			return (firstNum * (secondNum+1))/2;
		}
		else {
			return Double.parseDouble(numberOfShots);
		}
	}
	
	void computeSpecialEffect(SpecialAbilities sA) {
		if (sA.specialDoes == SpecialDoes.ap3) {
			saveSpecial = saveChance(3);
		}
		else if (sA.specialDoes == SpecialDoes.mortal1) {
			mortalWoundsSpecial = 1.0;
		}
	}
	
	void clear() {
		numberOfShots = 0;
		hit = 0;
		wound = 0;
		save = 0;
		damage = 0;
		mortalWounds = 0;
		
		numberOfShotsSpecial = 0;
		hitSpecial = 0;
		woundSpecial = 0;
		saveSpecial = 0;
		damageSpecial = 0;
		mortalWoundsSpecial = 0;
		
		results = 0;
		resultsSpecial = 0;
	}
}
