import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Unit {

	String unitName;
	List<Model> modelList = new ArrayList<Model>();
	int unitCost;
	float costPerModel;
	
	private List<String> modelHasReplaced = new ArrayList<String>();
	private List<String> modelHasReplacedThis = new ArrayList<String>();
	private List<String> modelHasReplacedWithThat = new ArrayList<String>();
	private List<Dbl> modelHasReplacedThisWithThat = new ArrayList<Dbl>();
	
	Unit(){}
	
	Unit(String unitName){
		this.unitName=unitName;
	}
	
	Unit(Unit u) {
		  this.unitName = u.getUnitName();
		  for (Model m : u.getModelList()) {
			  this.addModel(m);
		  }
	}
	
	Unit(String unitName, List<Model> modelList){
		this.unitName=unitName;
		this.modelList=modelList;
	}
	
	void addModel(Model m) {
		List<Weapon> weapons = new ArrayList<Weapon>(m.getWeapons());
		List<Weapon> clone = new ArrayList<Weapon>();
		for (Weapon w : weapons) {
			Weapon weapon = new Weapon(w.getWeaponName(),w.getRange(),w.getType(),w.getShots(),w.getStrength(),w.getArmorPenetration(),w.getDamage(),w.getPointsPerWeapon(),w.getWeaponSpecials(),w.getFiringMode(),w.getExtraFiringModes());
			//Weapon weapon = new Weapon(w);  
			//weapon.setFiringMode(w.getFiringMode());
			//weapon.setExtraFiringModes(w.getExtraFiringModes());
			clone.add(weapon);
		}
		Model modelToAdd = new Model(m.getName(),m.getMove(),m.getWeaponSkill(),m.getBallisticSkill(),m.getStrength(),m.getToughness(),m.getWounds(),m.getAttacks(),m.getLeadership(),m.getArmorSave(),m.getPointsPerModel(),clone,m.getModelSpecials(),m.getReplaceThis(),m.getWithThat(),m.getHowManyReplacements());
		modelList.add(modelToAdd);
	}
	
	void addModelCount(int count, Model m) {
		for (int i=0 ; i < count ; i++) {
			addModel(m);
		}
	}
	
	void removeModelNameCount(int max, String modelToRemove) {
		int count=0;
		for (int i=0 ; i < modelList.size() ; i++) {
			if (modelToRemove.equals(modelList.get(i).getName())) {
				modelList.remove(i);
				count++;
				if (count >= max) break;
			}
		}
	}
	
	public String getAllModelNames() {
		StringBuilder allNames = new StringBuilder();
		for (Model model : modelList) {
			allNames.append(model.getName()+":  "+model.getWeaponsForPrint()+"\n");
		}
		return allNames.toString();
	}
	
	public int getModelCount() {
		return this.modelList.size();
	}
	
	public Model getModel(int index) {
		return this.modelList.get(index);
	}
	
	public int getModelWeaponCombinations(int i) {
		int product = 1;
		if (isReplacementAllowed(modelList.get(i))) {
			List<String> withThat = modelList.get(i).getWithThat();	
			for (String s : withThat) {
				product = product * (1 + s.split(",").length);
			}
		}
		return product;
	}
	
	public int getUnitWeaponCombinations() {
		clearReplacementCounters();
		int product = 1;
		
		for (Model model : modelList) {
			List<String> rtL = model.getReplaceThis();
			List<String> wtL = model.getWithThat();
			List<Dbl> mrL = model.getHowManyReplacements();
			//If this model/weapon combo has not been attempted before add it to the list of all mode/weapon combo attempts
			//But use a temporary replacement counter because the unit being used here is a template for other units.
			updateUnitModelReplacementLists(model);

			boolean validReplacement=true;
			int num = 1;
			for (int i=0 ; i < rtL.size() ; i++) {			
				String[] parts = rtL.get(i).split("AND");
				for (int j=0 ; j<parts.length ; j++) {
					String r = parts[j].trim();
					if (!model.hasWeapon(r)) validReplacement=false;
				}
				if (validReplacement && isReplacementAllowed(model)) {
					String[] p = wtL.get(i).split(",");
					//Change here so that num only increases if there are replacement slots left
					//int replacementIndex = findIndexOfUnitReplacementCounts(model, i);
					//if (mrL.get(i).getValue()==0 || modelHasReplacedThisWithThat.get(replacementIndex).getValue() < mrL.get(i).getValue()) {
						num += p.length;
						product = product * num;
						//break;
					//}
				}
			}
		}
		clearReplacementCounters();
		return product;
	}
	
	private boolean isReplacementAllowed(Model model) {
		List<String> rtL = model.getReplaceThis();
		List<String> wtL = model.getWithThat();
		List<Dbl> mrL = model.getHowManyReplacements();
		boolean allowReplace = false;

		for (int i=0 ; i < modelHasReplaced.size() ; i++) {
			for (int j=0 ; j < rtL.size() ; j ++) {
				if ((model.getName().equals(modelHasReplaced.get(i))) && (modelHasReplacedThis.get(i).equals(rtL.get(j))) && (modelHasReplacedWithThat.get(i).equals(wtL.get(j)))) {
					if (mrL.get(i).getValue()==0) {
						modelHasReplacedThisWithThat.get(i).addOne();
						allowReplace = true;						
					} else if (modelHasReplacedThisWithThat.get(i).getValue() < mrL.get(j).getValue()) {
						modelHasReplacedThisWithThat.get(i).addOne();
						allowReplace = true;
					}
				}
			}
		}
		return allowReplace;
	}
	
	private void clearReplacementCounters() {
		for (Dbl d : modelHasReplacedThisWithThat) {
			d.setValue(0);
		}
	}
	
	public int setWeaponByCombinationNumber(int modelNum, int weaponNum, Formation formation) {
		Model model = modelList.get(modelNum);
		if (model.getReplaceThis() == null) return 0;
		updateUnitModelReplacementLists(model);
		if (weaponNum ==0 && model.getReplaceThis().size() > 0) return 1;
		if (weaponNum > 0) {
			int replacement = weaponNum - 1;
			//List<String> rtL = model.getReplaceThis();
			//List<String> wtL = model.getWithThat();			
			for (int i=0 ; i < modelHasReplaced.size() ; i++) {
				if (modelHasReplaced.get(i).equals(model.getName())) {
					double maxAllowed=model.getHowManyReplacements().get(i).getValue();
					int countReplaceWithThat = modelHasReplacedWithThat.get(i).split(",").length;
					if (replacement < countReplaceWithThat) {
						if (maxAllowed==0 || modelHasReplacedThisWithThat.get(i).getValue() < maxAllowed) {	
							String[] weapons = modelHasReplacedWithThat.get(i).split(",");
							model.removeWeapons(Arrays.asList(modelHasReplacedThis.get(i).split("AND")));					
							try {
								String[] weaponsToAdd = weapons[replacement].split("AND");
								for (int j=0 ; j < weaponsToAdd.length ; j++) {
									Weapon w = formation.getWeaponByName(weaponsToAdd[j].trim());
									if (w != null) {
										model.addWeapon(w);
									// If weapon can't be found, check to see if wargear by the same name exists
									} else {
										Wargear wg = formation.getWargearByName(weaponsToAdd[j].trim());
										model.addWargear(wg);
									}
									
								}
								modelHasReplacedThisWithThat.get(i).addOne();
								if (weaponNum < countReplaceWithThat ) {
									return weaponNum+1;
								} else {
									return 0;
								}
							} catch (WeaponNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
								//What to do if maxumum number of weapons replaced?	
						}
					} else {
						replacement = replacement - countReplaceWithThat;
					}
				}
			}
		}
		return 0;
	}
	
	public List<Model> getModelList() {
		return this.modelList;
	}
	
	public String getUnitName() {
		return this.unitName;
	}
	
	private void updateUnitModelReplacementLists (Model model) {
		List<String> rtL = model.getReplaceThis();
		List<String> wtL = model.getWithThat();
		if (modelHasReplaced.size() == 0 ) {
			for (int i=0 ; i < rtL.size(); i++) {
				modelHasReplaced.add(model.getName());
				modelHasReplacedThis.add(rtL.get(i));
				modelHasReplacedWithThat.add(wtL.get(i));
				modelHasReplacedThisWithThat.add(new Dbl(0));
			}
		} else {
			for (int i=0 ; i < modelHasReplaced.size() ; i++) {
				for (int j=0 ; j < rtL.size() ; j ++) {
					if ((!model.getName().equals(modelHasReplaced.get(i))) && (!modelHasReplacedThis.get(i).equals(rtL.get(j))) && (!modelHasReplacedWithThat.get(i).equals(wtL.get(j)))) {
						modelHasReplaced.add(model.getName());
						modelHasReplacedThis.add(rtL.get(j));
						modelHasReplacedWithThat.add(wtL.get(j));
						modelHasReplacedThisWithThat.add(new Dbl(0));
					}
				}
			}
		}		
	}
	
	private int findIndexOfUnitReplacementCounts(Model model, int modelReplacementIndex) {
		List<String> rtL = model.getReplaceThis();
		List<String> wtL = model.getWithThat();
		for (int i=0 ; i < modelHasReplaced.size() ; i++) {
			if ((model.getName().equals(modelHasReplaced.get(i))) && (modelHasReplacedThis.get(i).equals(rtL.get(modelReplacementIndex))) && (modelHasReplacedWithThat.get(i).equals(wtL.get(modelReplacementIndex)))) {
				return i;
			}
		}	
		return -1;
	}
	
	public void finalize() {
		Map <String,Integer> modelNames = new TreeMap<String,Integer>();
		for (Model model : modelList) {
			for (Wargear w : model.getWargear()) {
				//This model has wargear that protects the whole unit
				if (w.getSpecialOn(SpecialOn.defend) == SpecialDoes.invul5pUnit) {
					for (Model m : modelList) {
						m.setInvulSave(5);
					}
				}
			}
			model.finalize();
			unitCost = unitCost + model.getModelCost();
			if (modelNames.containsKey(model.getName())) {
				modelNames.replace(model.getName(), modelNames.get(model.getName()).intValue()+1);
			}
			else {
				modelNames.put(model.getName(),1);
			}
		}
		StringBuilder unitName = new StringBuilder();
		for (String modelName : modelNames.keySet()) {
			unitName.append(modelName+" "+modelNames.get(modelName)+"; ");
		}
		unitName.deleteCharAt(unitName.lastIndexOf(";"));
		this.unitName=unitName.toString();
		costPerModel = unitCost/modelList.size();
	}
	
	public String toString() {
		StringBuilder unitString = new StringBuilder();
		unitString.append("\nUnit Name:\t"+unitName+"\n");
		for (Model model : modelList) {
			unitString.append(model);
		}
		unitString.append("\n");
		return unitString.toString();
	}
}
