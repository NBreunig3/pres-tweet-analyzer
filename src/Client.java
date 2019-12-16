import twitter4j.*;
import javax.swing.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
/**
 * @author Nathan Breunig
 * @version 1.2.5
 * LAST MODIFIED 12/16/19
 */
public class Client {
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
            Analyzer.setup();
            countAllWordsMap = Analyzer.countAllWords();
            System.out.println("Word Count: Done!");
            mentionOthersMap = Analyzer.mentionOthersFrequency();
            System.out.println("Mentions of Others Frequency: Done!");
            wordFreqMap = Analyzer.keyWordFrequency(Analyzer.getWordFreqWords());
            System.out.println("Key Word Frequency: Done!");
            totalMentionsMap = Analyzer.totalMentions();
            System.out.println("Total Mentions: Done!");

            //Save Report
            File path = fileChooser.getSelectedFile();
            saveReport(path);
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
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        File folder = new File(mainDir.getPath() + "\\Report_" + dateFormat.format(new Date()) + "\\");
        folder.mkdir();

        //Count All Words
        Output.saveCSVReport(new File(folder.getPath() + "\\Word Count.csv"), "CountAllWords", countAllWordsMap, null);
        //Mentions of others
        Output.saveCSVReport(new File(folder.getPath() + "\\Mention of Others Frequency.csv"), "MentionOthersFreq", mentionOthersMap, null);
        //Word Frequency
        Output.saveCSVReport(new File(folder.getPath() + "\\Key Word Frequency.csv"), "WordFreq", wordFreqMap, null);
        //Total Mentions
        Output.saveCSVReport(new File(folder.getPath() + "\\Total Mentions.csv"), "TotalMentions", null, totalMentionsMap);
    }
}
