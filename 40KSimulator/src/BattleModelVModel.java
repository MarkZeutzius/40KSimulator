import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BattleModelVModel {
	
	List<Model> attackerModelList = new ArrayList<Model>();
	List<Model> defenderModelList = new ArrayList<Model>();
	Army attackerArmy = new Army();
	Army defenderArmy = new Army();
	double maxAttackerBackUps=0;
	double maxDefenderBackUps=0;

	BattleModelVModel(){}
	
	BattleModelVModel(Army attacker, Army defender){
		attackerArmy = attacker;
		defenderArmy = defender;
	}
	
	List<Model> GetModelList(Army a) {
		List<Model> modelList = new ArrayList<Model>();
		for (Formation f : a.getFormationList()) {
			for (Unit u : f.getUnitList()) {
				for (Model m : u.getModelList()) {
					Boolean found = false;
					for (Model mL : modelList) {
						if (mL.getName().equals(m.getName())) {
							found = true;
						}	
					}
					if (!found) {
						modelList.add(m);
					}
				}
			}
		}
		return modelList;
	}
	
	void fight() {
		attackerModelList = GetModelList(attackerArmy);
		defenderModelList = GetModelList(defenderArmy);
		double damagePerAttack=0;
		Rules rules = new Rules();
		int colTwoSize = 16;
		int colOneSize = 29;		
		//Print header
		StringBuilder headerString1 = new StringBuilder("                              ");
		StringBuilder headerString2 = new StringBuilder("Attacker                      ");
		for (Model dModel : defenderModelList) {
			headerString1.append(dModel.getName().substring(0, colTwoSize));
			headerString1.append("  ");
			if (dModel.getName().length() < 2*colTwoSize) {
				headerString2.append(dModel.getName().substring(colTwoSize, dModel.getName().length()));
				for (int i = 0 ; i < colTwoSize*2 - dModel.getName().length() ; i++) {
					headerString2.append(" ");
				}
			}
			else {
				headerString2.append(dModel.getName().substring(colTwoSize, colTwoSize*2));
			}
			headerString2.append("  ");
		}
		System.out.println(headerString1.toString());
		System.out.println(headerString2.toString());
		for (Model aModel : attackerModelList) {
			StringBuilder resultString = new StringBuilder();
			if (aModel.getName().length() < colOneSize) {
				resultString.append(aModel.getName());
				for (int i = 1 ; i < colOneSize - aModel.getName().length() ; i++) resultString.append(" ");
			}
			else {
				resultString.append(aModel.getName().substring(0, colOneSize-1));
			}
			resultString.append("  ");
			for (Model dModel : defenderModelList) {
				double sumDamage = 0;
				String bestMode = null;
				for (int i = 0 ; i < aModel.getNumWeapons() ; i++) {
					//Put logic here to throw grenade only if no other ranged weapons
					String weaponType = aModel.getWeapon(i).getType();
					if ((!weaponType.equals("grenade")||aModel.canThrowGrenade()) && (!weaponType.equals("pistol")) && (!weaponType.equals("melee"))) {
						if ( aModel.getWeapon(i).getFiringMode() == null) {
							damagePerAttack = rules.fight(aModel, i, dModel);
							sumDamage += damagePerAttack;
						} else {
							double modeDamage = 0;
							double bestDamage = rules.fight(aModel, i, dModel);
							bestMode = " (1)";
							int modeNum = 0;
							for (Weapon w : aModel.getWeapon(i).getExtraFiringModes()) {
								modeDamage = rules.fight(aModel,i,modeNum,dModel);
								if (modeDamage > bestDamage) {
									bestDamage = modeDamage;
									bestMode = " ("+(modeNum+2)+")";
								}
								modeNum = modeNum+1;
							}
							sumDamage += bestDamage;
						}	
					}
				}
				DecimalFormat df = new DecimalFormat("00.00");
				if (bestMode==null) bestMode="    ";
				resultString.append(df.format(sumDamage)+", "+df.format(dModel.getModelCost()*damagePerAttack)+bestMode+"  ");
			}
			System.out.println(resultString.toString());
		}
	}
	
	void fightWmovement() {
		List<Unit> aUnitList = new ArrayList<Unit>();
		List<Unit> dUnitList = new ArrayList<Unit>();
		for (Formation f : attackerArmy.getFormationList()) {
			for (Unit u : f.getUnitList()) {
				aUnitList.add(u);
			}	
		}
		for (Formation f : defenderArmy.getFormationList()) {
			for (Unit u : f.getUnitList()) {
				dUnitList.add(u);
			}	
		}
		for (Unit a : aUnitList) {
			double maxAttackerRange = 0;
			double minAttackerRange = 999;
			double minAttackerMove = 999;
			for (Model aModel : a.getModelList()) {
				if (aModel.getMove() < minAttackerMove) minAttackerMove = aModel.getMove();
				for (Weapon aWeapon : aModel.getWeapons()) {
					if ((!aWeapon.getType().equals("melee")) && (!aWeapon.getType().equals("grenade"))) {
						if (aWeapon.getRangeInt() > maxAttackerRange) maxAttackerRange = aWeapon.getRangeInt();
						if (aWeapon.getRangeInt() < minAttackerRange) minAttackerRange = aWeapon.getRangeInt();
						if ((aWeapon.getType().equals("rapid fire")) && (aWeapon.getRangeInt()/2.0 < minAttackerRange)) minAttackerRange = aWeapon.getRangeInt()/2.0;
					}
				}
			}
			for (Unit d : dUnitList) {
				double maxDefenderRange = 0;
				double minDefenderRange = 999;
				double minDefenderMove = 999;
				double rangeStart = 0;		
				for (Model dModel : d.getModelList()) {
					if (dModel.getMove() < minDefenderMove) minDefenderMove = dModel.getMove();
					for (Weapon dWeapon : dModel.getWeapons()) {
						if ((!dWeapon.getType().equals("melee")) && (!dWeapon.getType().equals("grenade"))) {
							if (dWeapon.getRangeInt() > maxDefenderRange) maxDefenderRange = dWeapon.getRangeInt();
							if (dWeapon.getRangeInt() < minDefenderRange) minDefenderRange = dWeapon.getRangeInt();
							if ((dWeapon.getType().equals("rapid fire")) && (dWeapon.getRangeInt()/2.0 < minDefenderRange)) minDefenderRange = dWeapon.getRangeInt()/2.0;
						}
					}
				}
				//Determine the range the fight starts at
				if (maxAttackerRange > maxDefenderRange) {
					rangeStart = maxAttackerRange;
					maxDefenderBackUps=24;
					if (maxAttackerRange<=24) {
						maxAttackerBackUps=24;
					} else if (maxAttackerRange>=48) {
						maxAttackerBackUps=0;
					} else {
						maxAttackerBackUps=maxAttackerRange-24;
					}
				} else {
					rangeStart = maxDefenderRange;
					maxAttackerBackUps=24;
					if (maxDefenderRange<=24) {
						maxDefenderBackUps=24;
					} else if (maxDefenderRange>=48) {
						maxDefenderBackUps=0;
					} else {
						maxDefenderBackUps=maxAttackerRange-24;
					}
				}
				
				double scale=1.0;
				double upperScale=50.0;
				double lowerScale=0.1;
				boolean scaleFound = false;
				while (!scaleFound) {
					double range = rangeStart;
					boolean survivors = true;
					Unit aUnit = new Unit(a);	//Make a clone of the unit so it can fight more than once.
					Unit dUnit = new Unit(d);	//Make a clone of the unit so it can fight more than once.
					while (survivors) {
						//Move phase, both the attacker and defender will move closer until all weapons they have (except grenades) are in range
						//Both attacker and defender move simultaneously
						range = range + moveUnit(aUnit,minAttackerMove,range,maxAttackerRange,minAttackerRange,maxDefenderRange,minDefenderRange) + moveUnit(dUnit,minDefenderMove,range,maxDefenderRange,minDefenderRange,maxAttackerRange,minAttackerRange);
						//Shoot phase, both the attacker and defender will shoot all weapons simultaneously.
						//Damage will be assigned to the model in the unit that causes the least amount of financial damage.
						fightUnitVUnit(aUnit,dUnit,range,scale);
						fightUnitVUnit(dUnit,aUnit,range,1.0/scale);
						removeCasaulties(aUnit);
						removeCasaulties(dUnit);
						if ((aUnit.getModelCount() < 0) && (dUnit.getModelCount() < 0)) {
							survivors = false;
							scaleFound = true;
						} else if (aUnit.getModelCount()<=0) {
							survivors = false;
							lowerScale = scale;
							scale = (scale+upperScale)/2.0;
							if (upperScale-lowerScale<0.005) scaleFound = true;
						} else if (dUnit.getModelCount()<=0) {
							survivors = false;
							upperScale = scale;
							scale = (scale+lowerScale)/2.0;
							if (upperScale-lowerScale<0.005) scaleFound = true;
						}
					}
				}
				System.out.println(a.getUnitName()+" efficiency = "+d.getUnitCost()/(a.getUnitCost()*scale));
			}
		}
	}
	
	private double moveUnit(Unit u, double move, double range, double aMax, double aMin, double dMax, double dMin) {
		//Always move forward if you are a melee unit
		if (u.getPreferredCombatMode().equals("melee")) return -1.0*move; 
		//Always move forward if you are out of range of your longest range weapon
		else if (range > aMax) return -1.0*move;
		//If you are in range with your longest gun and the opponent is our of range of his longest gun, stay put.
		else if (range > dMax) return 0.0;
		//Back up if you can get our of range of opponents longest range gun
		else if ((range < dMax) && (aMax > dMax)) return move;
		//Move forward to get in range of your short guns
		else if ((aMin > dMin) && (range > aMin)) return -1.0*move;
		//Move back to get our of range of his short guns
		else if ((aMin > dMin) && (range <= dMin)) return move;
		//His short range guns have more range than you, move forward
		else if ((aMin < dMin) && (range > aMin)) return -1.0*move;
		return 0.0;
	}
	
	private void fightUnitVUnit(Unit aUnit, Unit dUnit, double range, double scale) {
		Rules rules = new Rules();
		//One attacking unit will shoot at one defending unit
		for (Model aModel : aUnit.getModelList()) {
			for (Weapon aWeapon : aModel.getWeapons()) {
				//Each weapon that is in range will evaluate damage against every target in defending unit
				if ((!aWeapon.getType().equals("melee")) && (!aWeapon.getType().equals("grenade"))) {
					int bestTarget=0;
					double lowestEconomicDamage=99999;
					double bestDamage = 0;
					int counter = 0;
					for (Model dModel : dUnit.getModelList()) {
						if ( range <= aWeapon.getRangeInt()) {
							double damage = rules.fight(aModel, aWeapon, dModel,range) * scale;
							double economicDamage = dModel.getModelCost()*damage;
							if ((economicDamage < lowestEconomicDamage) && (dModel.getWoundsRemaining()>0)) {
								lowestEconomicDamage = economicDamage;
								bestTarget = counter;
								bestDamage = damage;
							}
						}
						counter++;
					}
					dUnit.getModel(bestTarget).takeDamage(bestDamage);
				}
			}
		}
	}
	
	private void removeCasaulties(Unit unit) {
		int numModels = unit.getModelCount();
		for (int i=numModels-1 ; i>=0 ; i--) {
			if (unit.getModel(i).getWoundsRemaining() <= 0) {
				unit.removeModel(i);
			}
		}
	}
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
}
