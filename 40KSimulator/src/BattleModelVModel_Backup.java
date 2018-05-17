import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BattleModelVModel_Backup {
	
	List<Model> attackerModelList = new ArrayList<Model>();
	List<Model> defenderModelList = new ArrayList<Model>();
	Army attackerArmy = new Army();
	Army defenderArmy = new Army();
	double maxAttackerBackUps=0;
	double maxDefenderBackUps=0;
	boolean aRan = false;
	boolean dRan = false;
	boolean aMoved = false;
	boolean dMoved = false;

	BattleModelVModel_Backup(){}
	
	BattleModelVModel_Backup(Army attacker, Army defender){
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
		int colOneSize = 35;		
		//Print header
		StringBuilder headerString1 = new StringBuilder("                                    ");
		StringBuilder headerString2 = new StringBuilder("Attacker                            ");
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
				resultString.append(df.format(sumDamage)+", "+df.format(dModel.getModelCost()*sumDamage)+bestMode+"  ");
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
		
		int colTwoSize = 16;
		int colOneSize = 80;		
		//Print header
		StringBuilder headerString1 = new StringBuilder("                                                                                 ");
		StringBuilder headerString2 = new StringBuilder("Attacker                                                                         ");
		for (Unit dUnit : dUnitList) {
			headerString1.append(dUnit.getUnitName().substring(0, colTwoSize));
			headerString1.append("  ");
			if (dUnit.getUnitName().length() < 2*colTwoSize) {
				headerString2.append(dUnit.getUnitName().substring(colTwoSize, dUnit.getUnitName().length()));
				for (int i = 0 ; i < colTwoSize*2 - dUnit.getUnitName().length() ; i++) {
					headerString2.append(" ");
				}
			}
			else {
				headerString2.append(dUnit.getUnitName().substring(colTwoSize, colTwoSize*2));
			}
			headerString2.append("  ");
		}
		System.out.println("");
		System.out.println(headerString1.toString());
		System.out.println(headerString2.toString());
		
		for (Unit a : aUnitList) {
			StringBuilder resultString = new StringBuilder();
			if (a.getUnitName().length() < colOneSize) {
				resultString.append(a.getUnitName());
				for (int i = 1 ; i < colOneSize - a.getUnitName().length() ; i++) resultString.append(" ");
			}
			else {
				resultString.append(a.getUnitName().substring(0, colOneSize-1));
			}
			resultString.append("  ");
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
					if (rangeStart < 24) rangeStart=24;
					maxDefenderBackUps=24;
					if (maxAttackerRange<=24) {
						maxAttackerBackUps=24;
					} else if (maxAttackerRange>=48) {
						maxAttackerBackUps=0;
					} else {
						maxAttackerBackUps=48-maxAttackerRange;
					}
				} else if (maxAttackerRange < maxDefenderRange) {
					rangeStart = maxDefenderRange;
					if (rangeStart < 24) rangeStart=24;
					maxAttackerBackUps=24;
					if (maxDefenderRange<=24) {
						maxDefenderBackUps=24;
					} else if (maxDefenderRange>=48) {
						maxDefenderBackUps=0;
					} else {
						maxDefenderBackUps=48-maxDefenderRange;
					}
				} else {
					rangeStart = maxDefenderRange;
					if (rangeStart < 24) {
						rangeStart=24;
						maxDefenderBackUps=24;
						maxAttackerBackUps=24;
					} else if (maxDefenderRange>=72) {
						maxDefenderBackUps=0;
						maxAttackerBackUps=0;
					} else {
						double maxBackup = (72.0 - maxDefenderRange)/2.0;
						maxDefenderBackUps=maxBackup;
						maxAttackerBackUps=0;
					}
				}
				
				double scale=1.0;
				double upperScale=20.0;
				double lowerScale=0.05;
				double attackerResid=0;
				double defenderResid=0;
				boolean scaleFound = false;
				while (!scaleFound) {
					double range = rangeStart;
					boolean survivors = true;
					Unit aUnit = new Unit(a);	//Make a clone of the unit so it can fight more than once.
					Unit dUnit = new Unit(d);	//Make a clone of the unit so it can fight more than once.
					while (survivors) {
						//Move phase, both the attacker and defender will move closer until all weapons they have (except grenades) are in range
						//Both attacker and defender move simultaneously
						range = moveUnit(range,aUnit,minAttackerMove,maxAttackerRange,minAttackerRange,dUnit,minDefenderMove,maxDefenderRange,minDefenderRange);
						//Shoot phase, both the attacker and defender will shoot all weapons simultaneously.
						//Damage will be assigned to the model in the unit that causes the least amount of financial damage.
						fightUnitVUnit(aUnit,dUnit,range,scale,aRan,aMoved);
						fightUnitVUnit(dUnit,aUnit,range,1.0/scale,dRan,aMoved);
						attackerResid = -aUnit.getModelList().get(0).getWoundsRemaining();
						defenderResid = -dUnit.getModelList().get(0).getWoundsRemaining();						
						removeCasaulties(aUnit);
						removeCasaulties(dUnit);
						if ((aUnit.getModelCount() <= 0) && (dUnit.getModelCount() <= 0)) {
							survivors = false;
							if ((upperScale-lowerScale)/(0.5*(upperScale+lowerScale))<0.00001) {
								scaleFound = true;
								//System.out.println("Scale: "+scale);
							} else {
								// The battle officially ended as a 'tie' but the solution is not 
								// converged yet.  Will use residual damage to determine if the 
								// attacker or defender won and try to drive both of those
								// figures down by choosing better scale.
								if (attackerResid > defenderResid) {
									// The attacker has lost
									lowerScale = scale;							
								} else {
									//The attacker has won
									upperScale = scale;
								}
								scale = (lowerScale + upperScale)/2.0;
							}
						} else if (aUnit.getModelCount()<=0) {
							survivors = false;
							if ((upperScale-lowerScale)/(0.5*(upperScale+lowerScale))<0.00001) {
								scaleFound = true;
								//System.out.println("Scale: "+scale);
							}
							else {
								lowerScale = scale;
								scale = (lowerScale+upperScale)/2.0;
							}
						} else if (dUnit.getModelCount()<=0) {
							survivors = false;
							if ((upperScale-lowerScale)/(0.5*(upperScale+lowerScale))<0.00001) {
								scaleFound = true;
								//System.out.println("Scale: "+scale);
							}
							else {
								upperScale = scale;
								scale = (lowerScale+upperScale)/2.0;
							}
						}
					}
				}
				double combatEfficiency = d.getUnitCost()/(a.getUnitCost()*scale);
				DecimalFormat df = new DecimalFormat("00.00");
				resultString.append(df.format(combatEfficiency)+", "+a.getPreferredCombatMode()+",  ");
			}
			System.out.println(resultString.toString());
		}
	}
	
	private double moveUnit(double r, Unit aU, double aMove, double aMax, double aMin, Unit dU, double dMove, double dMax, double dMin) {
		double range = r;
		double aAdjustment = 0;
		double dAdjustment = 0;
		aRan=false;
		dRan=false;
		aMoved=false;
		dMoved=false;
		//Attackers move
		//Always move forward if you are a melee unit
		if (aU.getPreferredCombatMode().equals("melee")) {
			aAdjustment = -aMove - 3.5; 
			aRan=true;
		}
		//Always run if you need to get in shooting range.
		else if (range > (aMax+aMove)) {
			aAdjustment = -aMove - 3.5;
			aRan=true;
		}
		else if (range > aMax) aAdjustment = -aMove;
		//If you are in range with your longest gun and the opponent is our of range of his longest gun, stay put.
		else if (range > dMax) aAdjustment = 0;
		//Back up if you can get out of range of opponents longest range gun
		else if ((range < dMax) && (aMax > dMax)) aAdjustment = aMove;
		//Move forward to get in range of your short guns
		else if ((aMin > dMin) && (range > aMin)) aAdjustment = -aMove;
		//Move back to get our of range of his short guns
		else if ((aMin > dMin) && (range <= dMin)) aAdjustment = aMove;
		//His short range guns have more range than you, move forward
		else if ((aMin < dMin) && (range > aMin)) aAdjustment = -aMove;
		if ((aAdjustment < 0) || (maxAttackerBackUps > aAdjustment)) {
			maxAttackerBackUps = maxAttackerBackUps - aAdjustment;
		} else {
			aAdjustment = maxAttackerBackUps;
			maxAttackerBackUps = 0; 
		}
		if (aAdjustment != 0) aMoved=true;

		//Defenders move
		//Always move forward if you are a melee unit
		if (dU.getPreferredCombatMode().equals("melee")) {
			dAdjustment = -dMove - 3.5;
			dRan=true;
		}
		//Always run if you need to get in shooting range.
		else if (range > (dMax+dMove)) {
			dAdjustment = -dMove - 3.5;
			dRan=true;
		}
		else if (range > dMax) dAdjustment = -dMove;
		//If you are in range with your longest gun and the opponent is our of range of his longest gun, stay put.
		else if (range > aMax) dAdjustment = 0;
		//Back up if you can get out of range of opponents longest range gun
		else if ((range < aMax) && (dMax > aMax)) dAdjustment = dMove;
		//Move forward to get in range of your short guns
		else if ((dMin > aMin) && (range > dMin)) dAdjustment = -dMove;
		//Move back to get our of range of his short guns
		else if ((dMin > aMin) && (range <= aMin)) dAdjustment = dMove;
		//His short range guns have more range than you, move forward
		else if ((dMin < aMin) && (range > dMin)) dAdjustment = -dMove;
		if ((dAdjustment < 0) || (maxDefenderBackUps > dAdjustment)) {
			maxDefenderBackUps = maxDefenderBackUps - dAdjustment;
		} else {
			dAdjustment = maxDefenderBackUps;
			maxDefenderBackUps = 0; 
		}
		if (dAdjustment != 0) dMoved=true;
		
		return range + aAdjustment + dAdjustment;
	}
	

	private void fightUnitVUnit(Unit aUnit, Unit dUnit, double range, double scale, boolean ran, boolean moved) {
		Rules rules = new Rules();
		//double residualDamage=0;

		int aSkip=aUnit.countModelsByName("Heavy Weapon Platform");

		//One attacking unit will shoot at one defending unit
		for (Model aModel : aUnit.getModelList()) {
			for (Weapon aWeapon : aModel.getWeapons()) {
				//Each weapon that is in range will evaluate damage against every target in defending unit
				if ((!aWeapon.getType().equals("melee")) && (!aWeapon.getType().equals("grenade"))) {
					int bestTarget=0;
					double lowestEconomicDamage=99999;
					double bestDamage = 0;
					int counter = 0;
					if (aSkip > 0) {
						aSkip --;
					} else {
						
						for (Model dModel : dUnit.getModelList()) {
							if (dModel.getWoundsRemaining() > 0) {
								double damage = 0;
								if (range <= aWeapon.getRangeInt()) {
									damage = rules.fight(aModel, aWeapon, dModel,range,ran,moved) * scale;
								}
								for (Weapon w : aWeapon.getExtraFiringModes()) {
									if ( range <= w.getRangeInt()) {
										double modeDamage = rules.fight(aModel, w, dModel,range,ran,moved) * scale;
										if (modeDamage > damage) damage = modeDamage;
									}
								}
								double economicDamage = dModel.getModelCost()*damage;
								if (economicDamage < lowestEconomicDamage) {
									lowestEconomicDamage = economicDamage;
									bestTarget = counter;
									bestDamage = damage;
								}
							}
							counter++;
						}
						//Residual damage was tried to see if not taking it into account was causing small units to differ from big units.
						//bestDamage=bestDamage+residualDamage;
						//if (bestDamage>dUnit.getModel(bestTarget).getWoundsRemaining()) {
						//	residualDamage=bestDamage-dUnit.getModel(bestTarget).getWoundsRemaining();
						//} else {
						//	residualDamage=0;
						//}
						dUnit.getModel(bestTarget).takeDamage(bestDamage);
					}
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
