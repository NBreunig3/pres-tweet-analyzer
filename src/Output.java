import sun.security.tools.keytool.CertAndKeyGen;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A class to either print out resulting data or save data to file from Analyzer
 *
 * @author Nathan Breunig
 * LAST MODIFIED 10/8/19
 */
public class Output {
    private HashMap<String, Integer> tempHashMap = new HashMap<>(); //So the freqComparator works

    /**
     * Prints the results from a HashMap returned from the Analyzer class
     * Sorts the data and prints it to console
     *
     * @param title   Title to print
     * @param hashMap hash map
     */
    public void printResults(String title, HashMap<String, Integer> hashMap) {
        ArrayList<String> list = sortMap(hashMap);

        System.out.println(title);
        System.out.println();

        for (String s : list) {
            System.out.println(s + ": " + hashMap.get(s));
        }
    }

    /**
     * Saves a file with the results from a HashMap returned from the Analyzer class
     *
     * @param file        where to save the file
     * @param title       title of file
     * @param description description of file
     * @param hashMap     hash map
     */
    public void savePlainTextReport(File file, String title, String description, HashMap<String, Integer> hashMap) {
        ArrayList<String> list = sortMap(hashMap);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(file));
            br.write(title);
            br.newLine();
            br.write(description);
            br.newLine();
            br.write("Report Generated: " + dateFormat.format(date));
            br.newLine();
            br.write("Report based on the past " + Analyzer.paging.getCount() + " tweets.");
            br.newLine();
            br.write("");
            br.newLine();

            for (String s : list) {
                br.write(s + ": " + hashMap.get(s));
                br.newLine();
            }
            br.flush();
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    //TODO finish
    public void saveCSVReport(File file, String method, HashMap<String, HashMap<String, Integer>> hashMap, HashMap<String, Integer> totalMentionsMap) {
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(file));
            if (method.equals("CountAllWords")){
                br.write("candidate,");
                for (int i = 1; i <= 50; i++) {
                    if (i != 50) {
                        br.write("word " + i + ",");
                    } else {
                        br.write("word " + i);
                        br.newLine();
                    }
                }
                for (String cand : Candidates.getCandidates()) {
                    ArrayList<String> list = sortMap(hashMap.get(cand));
                    br.write(cand + ",");
                    for (int i = 0; i < 50 && i < list.size(); i++) {
                        if (i != 49) {
                            br.write(list.get(i) + "-" + hashMap.get(cand).get(list.get(i)) + ",");
                        } else {
                            br.write(list.get(i) + "-" + hashMap.get(cand).get(list.get(i)));
                        }
                    }
                    br.newLine();
                }
            }else if (method.equals("MentionOthersFreq")){
                br.write("candidate,candidate-mentions");
                br.newLine();
                for (String cand : Candidates.getCandidates()){
                    br.write(cand + ",");
                    ArrayList<String> list = sortMap(hashMap.get(cand));
                    for (int i = 0; i < list.size(); i++){
                        if (i < list.size() - 1){
                            br.write(list.get(i) + "-" + hashMap.get(cand).get(list.get(i)) + ",");
                        }else {
                            br.write(list.get(i) + "-" + hashMap.get(cand).get(list.get(i)));
                        }
                    }
                    br.newLine();
                }
            }else if (method.equals("TotalMentions")){
                br.write("candidate,total-mentions");
                br.newLine();
                ArrayList<String> list = sortMap(totalMentionsMap);
                for (String s : list){
                    br.write(s + "," + totalMentionsMap.get(s));
                    br.newLine();
                }
            }else  if (method.equals("WordFreq")){
                //TODO - Not correctly formatting for unknown reason
                br.write("candidate,");
                for (int i = 0; i < Analyzer.getWordFreqWords().size(); i++) {
                    if (i != Analyzer.getWordFreqWords().size() - 1) {
                        br.write(Analyzer.getWordFreqWords().get(i) + ",");
                    } else {
                        br.write(Analyzer.getWordFreqWords().get(i));
                    }
                }
                br.newLine();
                for (String cand : Candidates.getCandidates()) {
                    br.write(cand + ",");
                    for (int i = 0; i < Analyzer.getWordFreqWords().size(); i++) {
                        String word = Analyzer.getWordFreqWords().get(i);
                        if (i != Analyzer.getWordFreqWords().size() - 1) {
                            br.write(hashMap.get(word).get(cand) + ",");
                        } else {
                            br.write(hashMap.get(word).get(cand));
                            br.newLine();
                        }
                    }
                }
            }
            br.flush();
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private ArrayList<String> sortMap(HashMap<String, Integer> hashMap) {
        tempHashMap = hashMap;
        Set<String> set = hashMap.keySet();
        ArrayList<String> list = new ArrayList<>();
        for (String s : set) {
            list.add(s);
        }
        Collections.sort(list, this::freqComparator);
        return list;
    }

    /**
     * Comparator used to sort a HashMap
     *
     * @param s1 first item
     * @param s2 second item
     * @return comparison difference
     */
    private int freqComparator(String s1, String s2) {
        return tempHashMap.get(s2) - tempHashMap.get(s1);
    }

    /**
     * Gets the description of each Analyzer method
     * Used when generated report
     *
     * @param type method
     * @return description as string
     */
    public String getDescription(String type) {
        switch (type) {
            case "countAllWords":
                return "The results below are the most common words said on this candidates Twitter account.";
            case "mentionOthersFrequency":
                return "The results below are based on how many times this candidate has mentioned another candidate in their tweets.";
            case "totalMentions":
                return "The results below are based on the total number of times each candidate has been mentioned across all candidates twitter accounts.";
            case "wordFrequency":
                return "The results below are based on the number of times each candidate has mentioned a specific word in their tweets.";
        }
        return "";
    }
}
