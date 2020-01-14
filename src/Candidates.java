import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * A class to store information about presidential candidates
 * @author Nathan Breunig
 * LAST MODIFIED 1/14/20
 */
public abstract class Candidates {
    /**
     * Gets the HashMap of all the candidates TWitter usernames
     * @return HashMap
     */
    public static HashMap<String, String> getUsernames(){
        HashMap<String, String> hashMap = new HashMap<>();

        //hashMap.put("Bennet", "MichaelBennet");
        //hashMap.put("Gillibrand", "SenGillibrand");
        //hashMap.put("Williamson", "marwilliamson");
        //hashMap.put("Bullock", "GovernorBullock");
        //hashMap.put("Delaney", "JohnDelaney");
        //hashMap.put("Hickenlooper", "Hickenlooper");
        //hashMap.put("Ryan", "TimRyan");
        //hashMap.put("Inslee", "JayInslee");
        //hashMap.put("de Blasio", "BilldeBlasio");
        //hashMap.put("Booker", "CoryBooker");
        //hashMap.put("Castro", "JulianCastro");
        //hashMap.put("Gabbard", "TulsiGabbard");
        //hashMap.put("Harris", "KamalaHarris");
        //hashMap.put("ORourke", "BetoORourke");

        hashMap.put("Klobuchar", "amyklobuchar");
        hashMap.put("Buttigieg", "PeteButtigieg");
        hashMap.put("Sanders", "BernieSanders");
        hashMap.put("Warren", "ewarren");
        hashMap.put("Biden", "JoeBiden");
        hashMap.put("Yang", "AndrewYang");
        hashMap.put("Steyer", "TomSteyer");
        hashMap.put("Trump", "realDonaldTrump");
        hashMap.put("Bloomberg", "MikeBloomberg");
        return hashMap;
    }

    /**
     * Gets a list of nicknames for a specific candidate
     * @param candidate candidate to get nicknames for
     * @return List of nicknames
     */
    public static ArrayList<String> getNicknames(String candidate){
        HashMap<String, ArrayList<String>> hashMap = new HashMap<>();

        // Warren
        hashMap.put("Warren", new ArrayList<>());
        hashMap.get("Warren").add("pocahontas");
        hashMap.get("Warren").add("Goofy Elizabeth Warren");
        hashMap.get("Warren").add("The Indian");
        hashMap.get("Warren").add("Uber Left Elizabeth Warren");

        // Biden
        hashMap.put("Biden", new ArrayList<>());
        hashMap.get("Biden").add("quid pro joe");
        hashMap.get("Biden").add("sleepy joe");

        // Buttigieg
        hashMap.put("Buttigieg", new ArrayList<>());
        hashMap.get("Buttigieg").add("Boot-Edge-Edge");

        // Sanders
        hashMap.put("Sanders", new ArrayList<>());
        hashMap.get("Sanders").add("Crazy Bernie");
        hashMap.get("Sanders").add("The Nutty Professor");

        // Steyer
        hashMap.put("Steyer", new ArrayList<>());
        hashMap.get("Steyer").add("Wacky Tom Steyer");
        hashMap.get("Steyer").add("Weirdo Tom Steyer");

        return hashMap.get(candidate);
    }

    /**
     * Gets a list of all the candidates names
     * @return Set
     */
    public static Set<String> getCandidates() {
        return getUsernames().keySet();
    }
}
