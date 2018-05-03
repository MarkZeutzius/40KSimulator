import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Warhammer40KStatistics {

	public static void main(String[] args) {
		DomUtils faction = new DomUtils();
		//faction.process();
		int i=1;
		List<String> factionList = new ArrayList<String>(faction.getFactionList());
		for (String fac : factionList)
		{
			System.out.println(i+": "+fac);
			i+=1;
		}
		System.out.println("Select attacker faction by number");
		Army attacker = faction.getFaction(factionList.get(getIntFromConsole()-1),"Attacker");
		
		i=1;
		for (String fac : factionList)
		{
			System.out.println(i+": "+fac);
			i+=1;
		}
		System.out.println("Select defender faction by number");
		Army defender = faction.getFaction(factionList.get(getIntFromConsole()-1),"Defender");
		BattleModelVModel battle = new BattleModelVModel(attacker, defender);
		battle.fight();
	}
	
	public static int getIntFromConsole() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try{
            int i = Integer.parseInt(br.readLine());
            return i;
        }catch(NumberFormatException nfe){
            System.err.println("Invalid Format!");
        } catch (IOException e) {
			e.printStackTrace();
		}
        try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return 0;
	}

}
