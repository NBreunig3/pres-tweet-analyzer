import java.util.HashMap;
import java.util.Set;

/**
 * A class to store information about presidential candidates
 * @author Nathan Breunig
 * LAST MODIFIED 7/31/19
 */
public abstract class Candidates {
    /**
     * Gets the HashMap of all the candidates TWitter usernames
     * @return HashMap
     */
    public static HashMap<String, String> getUsernames(){
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("Warren", "ewarren");
        hashMap.put("Williamson", "marwilliamson");
        hashMap.put("Bullock", "GovernorBullock");
        hashMap.put("Buttigieg", "PeteButtigieg");
        hashMap.put("Delaney", "JohnDelaney");
        hashMap.put("Hickenlooper", "Hickenlooper");
        hashMap.put("Klobuchar", "amyklobuchar");
        hashMap.put("ORourke", "BetoORourke");
        hashMap.put("Ryan", "TimRyan");
        hashMap.put("Sanders", "BernieSanders");

        hashMap.put("Bennet", "MichaelBennet");
        hashMap.put("Gillibrand", "SenGillibrand");
        hashMap.put("Castro", "JulianCastro");
        hashMap.put("Booker", "CoryBooker");
        hashMap.put("Biden", "JoeBiden");
        hashMap.put("Harris", "KamalaHarris");
        hashMap.put("Yang", "AndrewYang");
        hashMap.put("Gabbard", "TulsiGabbard");
        hashMap.put("Inslee", "JayInslee");
        hashMap.put("de Blasio", "BilldeBlasio");

        hashMap.put("Trump", "realDonaldTrump");
        return hashMap;
    }

    /**
     * Gets a list of all the candidates names
     * @return Set
     */
    public static Set<String> getCandidates() {
        return getUsernames().keySet();
    }
}
