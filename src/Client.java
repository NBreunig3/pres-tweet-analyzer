import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import twitter4j.*;

import javax.swing.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * @author Nathan Breunig
 * @version 1.2
 * LAST MODIFIED 10/8/19
 */
public class Client {
    private static Output output = new Output();
    private static Analyzer analyzer = new Analyzer();

    private static HashMap<String, HashMap<String, Integer>> countAllWordsMap;
    private static HashMap<String, HashMap<String, Integer>> mentionOthersMap;
    private static HashMap<String, HashMap<String, Integer>> wordFreqMap;
    private static HashMap<String, Integer> totalMentionsMap;

    /**
     * Main method
     * Meant to be run from the command line
     * @param args [number of tweets to fetch] [wordFrequency word to use]
     * @throws TwitterException Twitter Exception
     */
    public static void main(String[] args) throws TwitterException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose a directory for report");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        int response = fileChooser.showSaveDialog(null);
        if (response == JFileChooser.APPROVE_OPTION){
            //Run Analyzer
            countAllWordsMap = analyzer.countAllWords();
            System.out.println("Done");
            mentionOthersMap = analyzer.mentionOthersFrequency();
            System.out.println("Done");
            wordFreqMap = analyzer.wordFrequency(Analyzer.getWordFreqWords());
            System.out.println("Done");
            totalMentionsMap = analyzer.totalMentions();
            System.out.println("Done");

            //Save Report
            File path = fileChooser.getSelectedFile();
            File mainDir = createSaveDir(path); //Maybe not needed? Just pass path?
            saveReport(mainDir);
        }else {
            System.exit(0);
        }
    }

    /**
     * Saves multiple files in a directory for all Analyzer methods
     * (countAllWords, mentionOthersFrequency, wordFrequency, totalMentions)
     * @param mainDir Where to save the generated report
     */
    private static void saveReport(File mainDir) {
        File plainTextDir = new File(mainDir.getPath() + "\\Plain Text\\");
        File csvDir = new File(mainDir.getPath() + "\\CSV\\");

        //Count All Words
        for (String cand : Candidates.getCandidates()) {
            output.savePlainTextReport(new File(plainTextDir.getPath() + "\\Word Count\\" + cand + ".txt"), cand + " - Word Count", output.getDescription("countAllWords"), countAllWordsMap.get(cand));
        }
        output.saveCSVReport(new File(csvDir.getPath() + "\\Word Count\\WordCount.csv"), "CountAllWords", countAllWordsMap, null);

        //Mentions of others
        for (String cand : Candidates.getCandidates()) {
            output.savePlainTextReport(new File(plainTextDir.getPath() + "\\Mention of Others Frequency\\" + cand + ".txt"), cand + " - Mentions of other candidates", output.getDescription("mentionOthersFrequency"), mentionOthersMap.get(cand));
        }
        output.saveCSVReport(new File(csvDir.getPath() + "\\Mention of Others Frequency\\mentionOfOthersFreq.csv"), "MentionOthersFreq", mentionOthersMap, null);

        //Word Frequency
        for (String s : Analyzer.getWordFreqWords()){
            output.savePlainTextReport(new File(plainTextDir.getPath() + "\\Word Frequency\\" + s + ".txt"), "Frequency of \"" + s + "\" mentions", output.getDescription("wordFrequency"), wordFreqMap.get(s));
        }
        output.saveCSVReport(new File(csvDir.getPath() + "\\Word Frequency\\WordFrequency.csv"), "WordFreq", wordFreqMap, null);

        //Total Mentions
        output.savePlainTextReport(new File(plainTextDir.getPath() + "\\Total Mentions\\totalMentions.txt"), "Total mentions of all candidates", output.getDescription("totalMentions"), totalMentionsMap);
        output.saveCSVReport(new File(csvDir.getPath() + "\\Total Mentions\\totalMentions.csv"), "TotalMentions", null, totalMentionsMap);
    }

    private static File createSaveDir(File path){
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

        //Create Directories
        //Create Main Directory
        File mainDir = new File(path.getPath() + "\\Report_" + dateFormat.format(new Date()));
        mainDir.mkdir();
        //Create Plain Text Directory
        File plainTextDir = new File(mainDir.getPath() + "\\Plain Text");
        plainTextDir.mkdir();
        //Create CSV Directory
        File csvDir = new File(mainDir.getPath() + "\\CSV");
        csvDir.mkdir();

        //Create Sub Directories
        new File(plainTextDir.getPath() + "\\Word Count").mkdir();
        new File(plainTextDir.getPath() + "\\Mention of Others Frequency").mkdir();
        new File(plainTextDir.getPath() + "\\Word Frequency").mkdir();
        new File(plainTextDir.getPath() + "\\Total Mentions").mkdir();
        new File(csvDir.getPath() + "\\Word Count").mkdir();
        new File(csvDir.getPath() + "\\Mention of Others Frequency").mkdir();
        new File(csvDir.getPath() + "\\Word Frequency").mkdir();
        new File(csvDir.getPath() + "\\Total Mentions").mkdir();

        return mainDir;
    }
}
