import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomUtils {
	DomUtils(){}

  public void process() {

    try {

	File file = new File("Factions\\Eldar.xml");

	DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                             .newDocumentBuilder();

	Document doc = dBuilder.parse(file);

	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

	if (doc.hasChildNodes()) {

		printNote(doc.getChildNodes());

	}

    } catch (Exception e) {
	System.out.println(e.getMessage());
    }

  }

  private static void printNote(NodeList nodeList) {

    for (int count = 0; count < nodeList.getLength(); count++) {

	Node tempNode = nodeList.item(count);

	// make sure it's element node.
	if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

		// get node name and value
		System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
		System.out.println("Node Value =" + tempNode.getTextContent());

		if (tempNode.hasAttributes()) {

			// get attributes names and values
			NamedNodeMap nodeMap = tempNode.getAttributes();

			for (int i = 0; i < nodeMap.getLength(); i++) {

				Node node = nodeMap.item(i);
				System.out.println("attr name : " + node.getNodeName());
				System.out.println("attr value : " + node.getNodeValue());

			}

		}

		if (tempNode.hasChildNodes()) {

			// loop again if has child nodes
			printNote(tempNode.getChildNodes());

		}

		System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");

	}

    }

  }
  
  public List<String> getFactionList()
  {
	  List<String> armyList = new ArrayList<String>();
	  try
	  {
		  File folder = new File("C:\\Users\\Mark's Games\\Documents\\JavaPrograms\\Warhammer40K\\Factions");
		  File[] listOfFiles = folder.listFiles();
		  for(int i = 0; i < listOfFiles.length; i++)
		  {
			  String filename = listOfFiles[i].getName();
			  if(filename.endsWith(".xml")||filename.endsWith(".XML"))
			  {
				  System.out.println(filename);
				  armyList.add(filename.substring(0, filename.indexOf(".")));
			  }
		  }
	  }
	  catch (Exception e)
	  {
		  System.out.println("Error finding Faction xml files.");
	  }
	  return armyList;
  }
  
  public Army getFaction(String faction, String attackOrDefend)
  {
	  Army army = new Army(attackOrDefend);
	  try {
		  File xmlFile = new File("Factions\\"+faction+".xml");
		  DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		  DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
		  Document doc = dbBuilder.parse(xmlFile);
		  doc.getDocumentElement().normalize();
		  
		  //System.out.println("Faction Name : "+doc.getDocumentElement().getNodeName());
		  army.addFormation("Everything",doc.getDocumentElement().getNodeName());
		  
		  NodeList nodeListWeapon = doc.getElementsByTagName("weapon");
		  for (int tempWeapon = 0; tempWeapon < nodeListWeapon.getLength();tempWeapon++)
		  {
			  Node nodeWeapon = nodeListWeapon.item(tempWeapon);
				
			  //System.out.println("\nCurrent Element : " + nodeWeapon.getNodeName());
						
			  if (nodeWeapon.getNodeType() == Node.ELEMENT_NODE) 
			  {
				  Element elementWeapon = (Element) nodeWeapon;
				  
				  List<SpecialAbilities> weaponSpecials = new ArrayList<SpecialAbilities>();
				  if ((elementWeapon.hasAttribute("specialOn")) && (elementWeapon.hasAttribute("specialDoes"))) {
					  List<String> specialOnList = Arrays.asList(elementWeapon.getAttribute("specialOn").split(","));
					  List<String> specialDoesList = Arrays.asList(elementWeapon.getAttribute("specialDoes").split(","));
					  for (int tempSpecial = 0; tempSpecial < specialOnList.size(); tempSpecial++) {
						  weaponSpecials.add(new SpecialAbilities(SpecialOn.valueOf(specialOnList.get(tempSpecial)),SpecialDoes.valueOf(specialDoesList.get(tempSpecial))));
					  }
				  }

				  Weapon newWeapon = new Weapon(elementWeapon.getAttribute("weaponName"),
						elementWeapon.getElementsByTagName("range").item(0).getTextContent(),
						elementWeapon.getElementsByTagName("type").item(0).getTextContent(),
						elementWeapon.getElementsByTagName("shots").item(0).getTextContent(),
						elementWeapon.getElementsByTagName("strength").item(0).getTextContent(),
						Integer.parseInt(elementWeapon.getElementsByTagName("armorPenetration").item(0).getTextContent()),
						elementWeapon.getElementsByTagName("damage").item(0).getTextContent(),
						Integer.parseInt(elementWeapon.getElementsByTagName("pointsPerWeapon").item(0).getTextContent()),
				  		weaponSpecials);	
				  
				  if (elementWeapon.hasAttribute("mode")) {
					  newWeapon.setFiringMode(elementWeapon.getAttribute("mode"));
					  newWeapon.setExtraFiringModes(getExtraFiringModes(elementWeapon));
				  }
				  army.getFormation(0).addWeapon(newWeapon);
			  }
			  
		  }
		  
		  NodeList nodeListUnit = doc.getElementsByTagName("unit");
		  for (int tempUnit = 0; tempUnit < nodeListUnit.getLength();tempUnit++)
		  {
			  Node nodeUnit = nodeListUnit.item(tempUnit);
				
			  //System.out.println("\nCurrent Element : " + nodeUnit.getNodeName());
						
			  if (nodeUnit.getNodeType() == Node.ELEMENT_NODE) 
			  {
				  Element elementUnit = (Element) nodeUnit;
				  
				  Unit unitMin = new Unit(elementUnit.getAttribute("unitName"));
				  Unit unitMax = new Unit(elementUnit.getAttribute("unitName"));
				  Unit unitMinWithOptional = new Unit(elementUnit.getAttribute("unitName"));
				  Unit unitMaxWithOptional = new Unit(elementUnit.getAttribute("unitName"));
				  boolean hasOptionalModels = false;
				  NodeList nodeListModel = elementUnit.getElementsByTagName("model");
				  for (int tempModel = 0; tempModel < nodeListModel.getLength(); tempModel++) {
					  Node nodeModel = nodeListModel.item(tempModel);
					  if (nodeModel.getNodeType() == Node.ELEMENT_NODE) {
						  Element elementModel = (Element) nodeModel;
						  List<Weapon> weaponList = new ArrayList<Weapon>();
						  
						  int minNum=0;
						  int maxNum=0;
						  double ratio=0;
						  
						  if (elementModel.hasAttribute("minNum")) {
							  minNum = Integer.parseInt(elementModel.getAttribute("minNum"));
						  }
					  	  if (elementModel.hasAttribute("maxNum")) {
							  maxNum = Integer.parseInt(elementModel.getAttribute("maxNum"));
						  }
					  	  if (elementModel.hasAttribute("ratio")) {
							  ratio = Double.parseDouble(elementModel.getAttribute("ratio"));
				  			  hasOptionalModels=true;
						  }
						  
						  NodeList nodeListModelWeapon = elementModel.getElementsByTagName("equippedWeapon");
						  for (int tempModelWeapon = 0; tempModelWeapon < nodeListModelWeapon.getLength(); tempModelWeapon++) {
							  Node nodeModelWeapon = nodeListModelWeapon.item(tempModelWeapon);
							  if (nodeModelWeapon.getNodeType() == Node.ELEMENT_NODE) {
								  Element elementModelWeapon = (Element) nodeModelWeapon;
								  String weaponName = elementModelWeapon.getAttribute("weaponName");
								  Weapon w = army.getFormation(0).getWeaponByName(weaponName);
								  //Try new constructor
								  Weapon weapon = new Weapon(w);
								  if (elementModelWeapon.hasAttribute("replacementWeapons")) {
									  List<String> replacementWeaponList = Arrays.asList(elementModelWeapon.getAttribute("replacementWeapons").split(","));
									  weapon.setReplacements(replacementWeaponList);
								  }
								  if (elementModelWeapon.hasAttribute("mode")) {
									  weapon.setFiringMode(elementModelWeapon.getAttribute("mode"));
								  }
								  weaponList.add(weapon);
							  }
						  }
						  
						  List<SpecialAbilities> modelSpecials = new ArrayList<SpecialAbilities>();
						  if ((elementModel.hasAttribute("specialOn")) && (elementModel.hasAttribute("specialDoes"))) {
							  List<String> specialOnList = Arrays.asList(elementModel.getAttribute("specialOn").split(","));
							  List<String> specialDoesList = Arrays.asList(elementModel.getAttribute("specialDoes").split(","));
							  for (int tempSpecial = 0; tempSpecial < specialOnList.size(); tempSpecial++) {
								  modelSpecials.add(new SpecialAbilities(SpecialOn.valueOf(specialOnList.get(tempSpecial)),SpecialDoes.valueOf(specialDoesList.get(tempSpecial))));
							  }
						  }
						  
						  Model newModel = new Model(elementModel.getAttribute("modelName"),
									Integer.parseInt(elementModel.getElementsByTagName("move").item(0).getTextContent()),
									Integer.parseInt(elementModel.getElementsByTagName("ws").item(0).getTextContent()),
									Integer.parseInt(elementModel.getElementsByTagName("bs").item(0).getTextContent()),
									Integer.parseInt(elementModel.getElementsByTagName("strength").item(0).getTextContent()),
									Integer.parseInt(elementModel.getElementsByTagName("toughness").item(0).getTextContent()),
									Integer.parseInt(elementModel.getElementsByTagName("wounds").item(0).getTextContent()),
									Integer.parseInt(elementModel.getElementsByTagName("attacks").item(0).getTextContent()),
									Integer.parseInt(elementModel.getElementsByTagName("leadership").item(0).getTextContent()),
									Integer.parseInt(elementModel.getElementsByTagName("armorSave").item(0).getTextContent()),
									Integer.parseInt(elementModel.getElementsByTagName("pointsPerModel").item(0).getTextContent()),
							  		weaponList,
							  		modelSpecials);	
						  //System.out.println(newModel);
						  						  
				  		  if (elementModel.hasAttribute("replaces")) {
				  			  hasOptionalModels=true;
							  unitMinWithOptional.addModelCount(maxNum, newModel);
							  unitMinWithOptional.removeModelNameCount(maxNum, elementModel.getAttribute("replaces"));
					  		  unitMaxWithOptional.addModelCount(maxNum, newModel);
					  		  unitMaxWithOptional.removeModelNameCount(maxNum, elementModel.getAttribute("replaces"));
				  		  }
				  		  else {
				  			  unitMin.addModelCount(minNum, newModel);
					  		  unitMax.addModelCount(maxNum, newModel);
					  		  if (ratio > 0) { minNum = (int)(ratio * unitMinWithOptional.getModelCount()); }
							  unitMinWithOptional.addModelCount(minNum, newModel);
					  		  if (ratio > 0) { maxNum = (int)(ratio * unitMaxWithOptional.getModelCount()); }
					  		  unitMaxWithOptional.addModelCount(maxNum, newModel);
				  		  }
					  }
				  }
				  
				  for (Unit unit : weaponVariationCombos(unitMin, army)) {
					  army.getFormation(0).addUnit(unit);
				  }
				  //army.getFormation(0).addUnit(unitMin);
				  
				  for (Unit unit : weaponVariationCombos(unitMax, army)) {
					  army.getFormation(0).addUnit(unit);
				  }
				  //army.getFormation(0).addUnit(unitMax);
				  
				  if (hasOptionalModels) {
					  for (Unit unit : weaponVariationCombos(unitMinWithOptional, army)) {
						  army.getFormation(0).addUnit(unit);
					  }
					  //army.getFormation(0).addUnit(unitMinWithOptional);
					  for (Unit unit : weaponVariationCombos(unitMaxWithOptional, army)) {
						  army.getFormation(0).addUnit(unit);
					  }
					  //army.getFormation(0).addUnit(unitMaxWithOptional);
				  }
			  }
			  
		  }
		  
	  } catch (Exception e) {
		    	System.out.println(e.getMessage());
	  }
	  
	  System.out.println(army);
	  return army;
  }
  
  public List<Unit> weaponVariationCombos(Unit unit, Army army) {
	  int unitSize = unit.getModelCount();
	  int combinations = unit.getUnitWeaponCombinations();
	  List<Unit> unitList = new ArrayList<Unit>();
	  if (combinations > 1) {
		  int stepSize = 1;
		  for (int i = 0 ; i < unitSize; i++) {
			  int increment = 0;
			  int weaponNum = 0;
			  int modelWeaponCombinations = 0;
			  modelWeaponCombinations = unit.getModel(i).getModelWeaponCombinations();
			  for (int j = 0; j < combinations; j++) {
				  if (i == 0) {
					  Unit clone = new Unit(unit.getUnitName());
					  for (Model m : unit.getModelList()) {
						  clone.addModel(m);
					  }
					  unitList.add(clone);
				  }
				  if ( modelWeaponCombinations > 1) {
					  unitList.get(j).getModel(i).setWeaponByCombinationNumber(weaponNum,army.getFormation(0));
					  increment = increment + 1;
				  }
				  if (increment >= stepSize) {
					  weaponNum = weaponNum + 1;
				  }
				  if (weaponNum >= modelWeaponCombinations) {
					  weaponNum = 0;
				  }
			  }
			  stepSize = stepSize * modelWeaponCombinations;
		  }
	  }
	  else {
		  unitList.add(unit);
	  }
	  
	  for (Unit u : unitList ) {
		  u.finalize();
	  }
	  return unitList;
  }

  public List<Weapon> getExtraFiringModes(Element element) {
	  List<Weapon> weaponList = new ArrayList<Weapon>();
	  NodeList nodeListWeapon = element.getElementsByTagName("mode");
	  for (int tempWeapon = 0; tempWeapon < nodeListWeapon.getLength();tempWeapon++)
	  {
		  Node nodeWeapon = nodeListWeapon.item(tempWeapon);
			
		  //System.out.println("\nCurrent Element : " + nodeWeapon.getNodeName());
					
		  if (nodeWeapon.getNodeType() == Node.ELEMENT_NODE) 
		  {
			  Element elementWeapon = (Element) nodeWeapon;
			  
			  List<SpecialAbilities> weaponSpecials = new ArrayList<SpecialAbilities>();
			  if ((elementWeapon.hasAttribute("specialOn")) && (elementWeapon.hasAttribute("specialDoes"))) {
				  List<String> specialOnList = Arrays.asList(elementWeapon.getAttribute("specialOn").split(","));
				  List<String> specialDoesList = Arrays.asList(elementWeapon.getAttribute("specialDoes").split(","));
				  for (int tempSpecial = 0; tempSpecial < specialOnList.size(); tempSpecial++) {
					  weaponSpecials.add(new SpecialAbilities(SpecialOn.valueOf(specialOnList.get(tempSpecial)),SpecialDoes.valueOf(specialDoesList.get(tempSpecial))));
				  }
			  }

			  Weapon newWeapon = new Weapon(elementWeapon.getAttribute("weaponName"),
					elementWeapon.getElementsByTagName("range").item(0).getTextContent(),
					elementWeapon.getElementsByTagName("type").item(0).getTextContent(),
					elementWeapon.getElementsByTagName("shots").item(0).getTextContent(),
					elementWeapon.getElementsByTagName("strength").item(0).getTextContent(),
					Integer.parseInt(elementWeapon.getElementsByTagName("armorPenetration").item(0).getTextContent()),
					elementWeapon.getElementsByTagName("damage").item(0).getTextContent(),
					Integer.parseInt(elementWeapon.getElementsByTagName("pointsPerWeapon").item(0).getTextContent()),
			  		weaponSpecials);	
			  
			  if (elementWeapon.hasAttribute("mode")) {
				  newWeapon.setFiringMode(elementWeapon.getAttribute("mode"));
			  }
			  weaponList.add(newWeapon);
		  }
	  }
	  return weaponList;
  }
}
