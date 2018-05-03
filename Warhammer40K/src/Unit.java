import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Unit {

	String unitName;
	List<Model> modelList = new ArrayList<Model>();
	int unitCost;
	float costPerModel;
	
	Unit(){}
	
	Unit(String unitName){
		this.unitName=unitName;
	}
	
	Unit(Unit u) {
		this(u.getUnitName(),u.getModelList());
	}
	
	Unit(String unitName, List<Model> modelList){
		this.unitName=unitName;
		this.modelList=modelList;
	}
	
	void addModel(Model m) {
		List<Weapon> weapons = new ArrayList<Weapon>(m.getWeapons());
		List<Weapon> clone = new ArrayList<Weapon>();
		for (Weapon w : weapons) {
			  Weapon weapon = new Weapon(w.getWeaponName(),w.getRange(),w.getType(),w.getShots(),w.getStrength(),w.getArmorPenetration(),w.getDamage(),w.getPointsPerWeapon(),w.getWeaponSpecials());
			  weapon.setReplacements(w.getReplacements());
			  clone.add(weapon);
		}
		Model modelToAdd = new Model(m.getName(),m.getMove(),m.getWeaponSkill(),m.getBallisticSkill(),m.getStrength(),m.getToughness(),m.getWounds(),m.getAttacks(),m.getLeadership(),m.getArmorSave(),m.getPointsPerModel(),clone,m.getModelSpecials());
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
	
	public int getUnitWeaponCombinations() {
		int product = 1;
		for (Model model : modelList) {
			for (Weapon weapon : model.getWeapons()) {
				product = product * weapon.getWeaponCombinations();
			}
		}
		return product;
	}
	
	public List<Model> getModelList() {
		return this.modelList;
	}
	
	public String getUnitName() {
		return this.unitName;
	}
	
	public void finalize() {
		Map <String,Integer> modelNames = new TreeMap<String,Integer>();
		for (Model model : modelList) {
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
