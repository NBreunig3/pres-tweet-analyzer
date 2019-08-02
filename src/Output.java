import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A class to either print out resulting data or save data to file from Analyzer
 *
 * @author Nathan Breunig
 * LAST MODIFIED 8/1/19
 */
public class Output {
    private HashMap<String, Integer> temp = new HashMap<>();

    /**
     * Prints the results from a HashMap returned from the Analyzer class
     * Sorts the data and prints it to console
     * @param title Title to print
     * @param hashMap hash map
     */
    public void printResults(String title, HashMap<String, Integer> hashMap) {
        temp = hashMap;
        //Sort
        Set<String> set = hashMap.keySet();
        ArrayList<String> list = new ArrayList<>();
        for (String s : set) {
            list.add(s);
        }
        Collections.sort(list, this::freqComparator);

        System.out.println(title);
        System.out.println();

        for (String s : list) {
            System.out.println(s + ": " + temp.get(s));
        }
    }

    /**
     * Saves a file with the results from a HashMap returned from the Analyzer class
     * @param file where to save the file
     * @param title title of file
     * @param description description of file
     * @param hashMap hash map
     */
    public void saveResults(File file, String title, String description, HashMap<String, Integer> hashMap){
        temp = hashMap;
        //Sort
        Set<String> set = hashMap.keySet();
        ArrayList<String> list = new ArrayList<>();
        for (String s : set) {
            list.add(s);
        }
        Collections.sort(list, this::freqComparator);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        try{
            BufferedWriter br = new BufferedWriter(new FileWriter(file));
            br.write(title); br.newLine();
            br.write(description); br.newLine();;
            br.write("Report Generated: " + dateFormat.format(date)); br.newLine();
            br.write("Report based on the past " + Analyzer.paging.getCount() + " tweets.");
            br.newLine();
            br.write(""); br.newLine();
            for (String s : list) {
                br.write(s + ": " + temp.get(s));
                br.newLine();
            }
            br.flush();
            br.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Comparator used to sort a HashMap
     * @param s1 first item
     * @param s2 second item
     * @return comparison difference
     */
    private int freqComparator(String s1, String s2) {
        return temp.get(s2) - temp.get(s1);
    }

    /**
     * Gets the description of each Analyzer method
     * Used when generated report
     * @param type method
     * @return description as string
     */
    public String getDescription(String type){
        switch (type){
            case "countAllWords" : return "The results below are the most common words said on this candidates Twitter account.";
            case "mentionOthersFrequency" : return "The results below are based on how many times this candidate has mentioned another candidate in their tweets.";
            case "totalMentions" : return "The results below are based on the total number of times each candidate has been mentioned across all candidates twitter accounts.";
            case "wordFrequency" : return "The results below are based on the number of times each candidate has mentioned a specific word in their tweets.";
        }
        return "";
    }
}
