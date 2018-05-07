import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BattleModelVModel {
	
	List<Model> attackerModelList = new ArrayList<Model>();
	List<Model> defenderModelList = new ArrayList<Model>();

	BattleModelVModel(){}
	
	BattleModelVModel(Army attacker, Army defender){
		attackerModelList = GetModelList(attacker);
		defenderModelList = GetModelList(defender);
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
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
}
