import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.style.Styler;
import sun.security.tools.keytool.CertAndKeyGen;

import java.awt.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * A class to either print out resulting data or save data to file from Analyzer
 *
 * @author Nathan Breunig
 * LAST MODIFIED 12/25/19
 */
public class Output {
    private static HashMap<String, Integer> tempHashMap = new HashMap<>(); //So the freqComparator works

    public static void saveReport(File folder, HashMap<String, HashMap<String, Integer>> countAllWordsMap, HashMap<String, HashMap<String, Integer>> mentionsOthersMao, HashMap<String, HashMap<String, Integer>> keyWordFreqMap, HashMap<String, Integer> totalMentionsMap) {
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(folder.getPath() + "\\Word Count.csv"));
            // Count All Words
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
                ArrayList<String> list = sortMap(countAllWordsMap.get(cand));
                br.write(cand + ",");
                for (int i = 0; i < 50 && i < list.size(); i++) {
                    if (i != 49) {
                        br.write(list.get(i) + "-" + countAllWordsMap.get(cand).get(list.get(i)) + ",");
                    } else {
                        br.write(list.get(i) + "-" + countAllWordsMap.get(cand).get(list.get(i)));
                    }
                }
                br.newLine();
            }
            // Generate Chart
            for (String candidate : Candidates.getCandidates()) {
                CategoryChart barChart = new CategoryChart(800, 800);
                barChart.setTitle(candidate.toUpperCase() + "'s Most Mentioned Words (of meaning)");
                barChart.getStyler().setHasAnnotations(true);
                barChart.setXAxisTitle("Word");
                barChart.setYAxisTitle("Word Frequency");
                barChart.getStyler().setXAxisMin(1.0);
                List<String> wordList = Output.sortMap(countAllWordsMap.get(candidate)).subList(0, 10);
                ArrayList<Integer> freqList = new ArrayList<>();
                for (String word : wordList) {
                    freqList.add(countAllWordsMap.get(candidate).get(word));
                }
                barChart.addSeries("Words", wordList, freqList);
                BitmapEncoder.saveBitmap(barChart, folder.getPath() + "\\" + candidate + " Word Count Chart", BitmapEncoder.BitmapFormat.PNG);
            }
            br.flush();
            br.close();

            br = new BufferedWriter(new FileWriter(folder.getPath() + "\\Mentions of Others Frequency.csv"));
            // Mention Others Frequency
            br.write("candidate,");
            for (int i = 0; i < Candidates.getCandidates().size() - 1; i++) {
                if (i == Candidates.getCandidates().size() - 1) {
                    br.write("candidate-mentions");
                } else {
                    br.write("candidate-mentions,");
                }
            }
            br.newLine();
            for (String cand : Candidates.getCandidates()) {
                br.write(cand + ",");
                ArrayList<String> list = sortMap(mentionsOthersMao.get(cand));
                for (int i = 0; i < list.size(); i++) {
                    if (i < list.size() - 1) {
                        br.write(list.get(i) + "-" + mentionsOthersMao.get(cand).get(list.get(i)) + ",");
                    } else {
                        br.write(list.get(i) + "-" + mentionsOthersMao.get(cand).get(list.get(i)));
                    }
                }
                br.newLine();
            }
            // Generate Chart
            CategoryChart chart = new CategoryChart(1400, 800);
            chart.setTitle("Candidate Mention of Others Frequency");
            chart.setXAxisTitle("Candidate");
            chart.setYAxisTitle("How many times \"Candidate (x-axis)\" has mentioned each other candidate");
            chart.getStyler().setLegendVisible(true);
            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
            chart.getStyler().setXAxisMin(1.0);
            chart.getStyler().setHasAnnotations(true);
            chart.getStyler().setPlotMargin(10);
            chart.getStyler().setSeriesColors(new Color[]{Color.RED, Color.BLACK, Color.GREEN, Color.MAGENTA, Color.cyan, Color.PINK, Color.yellow, Color.ORANGE});
            for (String candidate : Candidates.getCandidates()) {
                ArrayList<Integer> freqs = new ArrayList<>();
                for (String other : Candidates.getCandidates()) {
                    if (!candidate.equals(other)) {
                        freqs.add(mentionsOthersMao.get(other).get(candidate));
                    } else {
                        freqs.add(0);
                    }
                }
                ArrayList<String> candidateNames = new ArrayList<>(Candidates.getCandidates());
                chart.addSeries(candidate.toUpperCase(), candidateNames, freqs);
            }
            BitmapEncoder.saveBitmap(chart, folder.getPath() + "\\Mention Of Others Chart", BitmapEncoder.BitmapFormat.PNG);
            br.flush();
            br.close();

            br = new BufferedWriter(new FileWriter(folder.getPath() + "\\Total Mentions.csv"));
            // Total Mentions
            br.write("candidate,total-mentions");
            br.newLine();
            ArrayList<String> list = sortMap(totalMentionsMap);
            for (String s : list) {
                br.write(s + "," + totalMentionsMap.get(s));
                br.newLine();
            }
            // Generate Chart
            PieChart pieChart = new PieChart(800, 800);
            pieChart.setTitle("Total number of mentions of all candidates");
            for (String candidate : Candidates.getCandidates()) {
                pieChart.addSeries(candidate.toUpperCase(), totalMentionsMap.get(candidate));
            }
            pieChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
            pieChart.getStyler().setLegendVisible(true);
            pieChart.getStyler().setSeriesColors(new Color[]{Color.RED, Color.BLACK, Color.GREEN, Color.MAGENTA, Color.cyan, Color.PINK, Color.yellow, Color.GRAY});
            BitmapEncoder.saveBitmap(pieChart, folder.getPath() + "\\Total Mentions Chart", BitmapEncoder.BitmapFormat.PNG);
            br.flush();
            br.close();

            br = new BufferedWriter(new FileWriter(folder.getPath() + "\\Key Word Frequency.csv"));
            // Key Word Frequency
            //TODO - Not correctly formatting for unknown reason
            br.write("candidate,");
            for (int i = 0; i < Analyzer.getKeyWordFreqWords().size(); i++) {
                if (i != Analyzer.getKeyWordFreqWords().size() - 1) {
                    br.write(Analyzer.getKeyWordFreqWords().get(i) + ",");
                } else {
                    br.write(Analyzer.getKeyWordFreqWords().get(i));
                }
            }
            br.newLine();
            for (String cand : Candidates.getCandidates()) {
                br.write(cand + ",");
                for (int i = 0; i < Analyzer.getKeyWordFreqWords().size(); i++) {
                    String word = Analyzer.getKeyWordFreqWords().get(i);
                    if (i != Analyzer.getKeyWordFreqWords().size() - 1) {
                        br.write(keyWordFreqMap.get(word).get(cand) + ",");
                    } else {
                        br.write(keyWordFreqMap.get(word).get(cand));
                        br.newLine();
                    }
                }
            }
            // Generate Chart
            CategoryChart wordFreqChart = new CategoryChart(1400, 800);
            wordFreqChart.setTitle("Candidate Mention of Others Frequency");
            wordFreqChart.setXAxisTitle("Candidate");
            wordFreqChart.setYAxisTitle("Mention of other candidate frequency");
            wordFreqChart.getStyler().setLegendVisible(true);
            wordFreqChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
            wordFreqChart.getStyler().setXAxisMin(0.0);
            wordFreqChart.getStyler().setHasAnnotations(true);
            for (String candidate : Candidates.getCandidates()) {
                ArrayList<Integer> freqs = new ArrayList<>();
                for (String keyWord : Analyzer.getKeyWordFreqWords()) {
                    freqs.add(keyWordFreqMap.get(keyWord).get(candidate));
                }
                wordFreqChart.addSeries(candidate.toUpperCase(), Analyzer.getKeyWordFreqWords(), freqs);
            }
            BitmapEncoder.saveBitmap(wordFreqChart, folder.getPath() + "\\Key Word Frequency Chart", BitmapEncoder.BitmapFormat.PNG);
            br.flush();
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<String> sortMap(HashMap<String, Integer> hashMap) {
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
