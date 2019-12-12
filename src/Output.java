import sun.security.tools.keytool.CertAndKeyGen;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A class to either print out resulting data or save data to file from Analyzer
 *
 * @author Nathan Breunig
 * LAST MODIFIED 12/11/19
 */
public class Output {
    private static HashMap<String, Integer> tempHashMap = new HashMap<>(); //So the freqComparator works

    public static void saveCSVReport(File file, String method, HashMap<String, HashMap<String, Integer>> hashMap, HashMap<String, Integer> totalMentionsMap) {
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
                br.write("candidate");
                for (int i = 0; i < Candidates.getCandidates().size() -1; i++){
                    br.write("candidate-mentions");
                }
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

    private static ArrayList<String> sortMap(HashMap<String, Integer> hashMap) {
        tempHashMap = hashMap;
        Set<String> set = hashMap.keySet();
        ArrayList<String> list = new ArrayList<>();
        for (String s : set) {
            list.add(s);
        }
        /**
         * Comparator used to sort a HashMap
         *
         * @param s1 first item
         * @param s2 second item
         * @return comparison difference
         */
        class Sort implements Comparator<String>{
            public int compare(String s1, String s2) {
                return tempHashMap.get(s2) - tempHashMap.get(s1);
            }
        }
        Collections.sort(list, new Sort());
        return list;
    }
}
