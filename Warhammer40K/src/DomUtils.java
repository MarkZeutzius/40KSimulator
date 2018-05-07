import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
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
		  
		  NodeList nodeListWargear = doc.getElementsByTagName("warGear");
		  for (int tempWargear = 0; tempWargear < nodeListWargear.getLength();tempWargear++)
		  {
			  Node nodeWargear = nodeListWargear.item(tempWargear);
				
			  //System.out.println("\nCurrent Element : " + nodeWeapon.getNodeName());
						
			  if (nodeWargear.getNodeType() == Node.ELEMENT_NODE) 
			  {
				  Element elementWargear = (Element) nodeWargear;
				  
				  List<SpecialAbilities> wargearSpecials = new ArrayList<SpecialAbilities>();
				  if ((elementWargear.hasAttribute("specialOn")) && (elementWargear.hasAttribute("specialDoes"))) {
					  List<String> specialOnList = Arrays.asList(elementWargear.getAttribute("specialOn").split(","));
					  List<String> specialDoesList = Arrays.asList(elementWargear.getAttribute("specialDoes").split(","));
					  for (int tempSpecial = 0; tempSpecial < specialOnList.size(); tempSpecial++) {
						  wargearSpecials.add(new SpecialAbilities(SpecialOn.valueOf(specialOnList.get(tempSpecial)),SpecialDoes.valueOf(specialDoesList.get(tempSpecial))));
					  }
				  }

				  Wargear newWargear = new Wargear(elementWargear.getAttribute("warGearName"),
						Integer.parseInt(elementWargear.getElementsByTagName("pointsPerWargear").item(0).getTextContent()),
				  		wargearSpecials);	
				 
				  army.getFormation(0).addWargear(newWargear);
			  }
			  
		  }
		  
		  NodeList nodeListUnit = doc.getElementsByTagName("unit");
		  for (int tempUnit = 0; tempUnit < nodeListUnit.getLength();tempUnit++)
		  {
			  List<Unit> allUnitCombinations = new ArrayList<Unit>();
			  Node nodeUnit = nodeListUnit.item(tempUnit);
				
			  //System.out.println("\nCurrent Element : " + nodeUnit.getNodeName());
						
			  if (nodeUnit.getNodeType() == Node.ELEMENT_NODE) 
			  {
				  Element elementUnit = (Element) nodeUnit;
				  
				  String unitName = elementUnit.getAttribute("unitName");
				  
				  NodeList nodeListModel = elementUnit.getElementsByTagName("model");
				  for (int tempModel = 0; tempModel < nodeListModel.getLength(); tempModel++) {
					  Node nodeModel = nodeListModel.item(tempModel);
					  if (nodeModel.getNodeType() == Node.ELEMENT_NODE) {
						  Element elementModel = (Element) nodeModel;
						  List<Weapon> weaponList = new ArrayList<Weapon>();
						  List<String> replaceThisList = new ArrayList<String>();
						  List<String> replaceThatList = new ArrayList<String>();
						  List<Dbl> howManyReplacements = new ArrayList<>();
						  
						  int minNum=0;
						  int maxNum=0;
						  double ratio= 0;
						  String replaces = null;
						  
						  //Optional elements in tag header
						  if (elementModel.hasAttribute("minNum")) {
							  minNum = Integer.parseInt(elementModel.getAttribute("minNum"));
						  }
					  	  if (elementModel.hasAttribute("maxNum")) {
							  maxNum = Integer.parseInt(elementModel.getAttribute("maxNum"));
						  }
					  	  if (elementModel.hasAttribute("ratio")) {
							  ratio = Double.parseDouble(elementModel.getAttribute("ratio"));
						  }
					  	  if (elementModel.hasAttribute("replaces")) {
							  replaces = elementModel.getAttribute("replaces");
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
								  if (elementModelWeapon.hasAttribute("mode")) {
									  weapon.setFiringMode(elementModelWeapon.getAttribute("mode"));
								  }
								  weaponList.add(weapon);
							  }
						  }
						  
						  NodeList nodeListReplacementWeapons = elementModel.getElementsByTagName("replacementWeapon");
						  for (int tempReplacementWeapon = 0; tempReplacementWeapon < nodeListReplacementWeapons.getLength(); tempReplacementWeapon++) {
							  Node nodeReplacementWeapon = nodeListReplacementWeapons.item(tempReplacementWeapon);
							  if (nodeReplacementWeapon.getNodeType() == Node.ELEMENT_NODE) {
								  Element elementReplacementWeapon = (Element) nodeReplacementWeapon;
								  if (elementReplacementWeapon.hasAttribute("replaceThis")) {
									  String replaceThis = elementReplacementWeapon.getAttribute("replaceThis");
									  String replaceThat = elementReplacementWeapon.getAttribute("withThat");
									  if (elementReplacementWeapon.hasAttribute("maxReplacements")) {
										  howManyReplacements.add(new Dbl(elementReplacementWeapon.getAttribute("maxReplacements")));
									  } else {
										  howManyReplacements.add(new Dbl(0));
									  }
									  if (replaceThis != null) replaceThisList.add(replaceThis);
									  if (replaceThat != null) replaceThatList.add(replaceThat);
								  }
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
							  		modelSpecials,
							  		replaceThisList,
							  		replaceThatList,
							  		howManyReplacements);	
						  //System.out.println(newModel);
						    
						  if (allUnitCombinations.size() == 0) {
							  Unit unitMin = new Unit(unitName);
							  Unit unitMax = new Unit(unitName);
							  unitMin.addModelCount(minNum, newModel);
							  unitMax.addModelCount(maxNum, newModel);
				  			  allUnitCombinations.add(unitMin);
					  		  allUnitCombinations.add(unitMax);
						  } else {
							  int size = allUnitCombinations.size();
							  for (int i=0 ; i < size ; i++) {
								  Unit unit = allUnitCombinations.get(i);
								  if (minNum > 0) {
									  unit.addModelCount(minNum, newModel);
									  if (replaces != null) unit.removeModelNameCount(minNum, replaces);
								  }

								  if (ratio > 0) {
									  double unitSize = unit.getModelCount();
									  double maxNumDouble = unitSize * ratio;
									  maxNum = (int)maxNumDouble;
								  }
								  if (maxNum > minNum) {
									  //Unit add = new Unit(unit);
									  Unit add = new Unit(unit);
									  add.addModelCount(maxNum, newModel);
									  if (replaces != null) add.removeModelNameCount(maxNum, replaces);
									  allUnitCombinations.add(add);
								  }
							  }
						  }

					  }
				  }
				   
				  
				  for (Unit unit : allUnitCombinations) {
					  for (Unit u : weaponVariationCombos(unit, army)) {
						  army.getFormation(0).addUnit(u);
					  }
				  }
				  
			  }
			  
		  }
		  
	  } catch (Exception e) {
		    	System.out.println(e.toString() + e.getMessage());
	  }
	  
	  System.out.println(army);
	  return army;
  }
  
  public List<Unit> weaponVariationCombos(Unit unit, Army army) {
	  List<Unit> unitList = new ArrayList<Unit>();
	  int unitSize = unit.getModelCount();
	  int combinations = unit.getUnitWeaponCombinations();
	  if (combinations > 1) {
		  //Need to keep track of which models have replace which weapons
		  //so that unit totals do not get exceeded.
		  int stepSize = 1;
		  for (int i = 0 ; i < unitSize; i++) {
			  int increment = 0;
			  int weaponNum = 0;
			  int nextWeaponNum = 0;
			  int numChoices = 0;

			  for (int j = 0; j < combinations; j++) {
				  if (i == 0) {
					  Unit clone = new Unit(unit);
					  //Unit clone = new Unit(unit.getUnitName());
					  //for (Model m : unit.getModelList()) {
					//	  clone.addModel(m);
					 // }
					  unitList.add(clone);
				  }
				  nextWeaponNum = unitList.get(j).setWeaponByCombinationNumber(i,weaponNum,army.getFormation(0));
				  increment = increment + 1;
				  if (increment >= stepSize) {
					  weaponNum = weaponNum + 1;
					  increment = 0;
				  }
				  if (nextWeaponNum == 0 && increment == 0) {
					  numChoices = weaponNum;
					  weaponNum = 0;
				  }
			  }
			  stepSize = stepSize * numChoices;
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
