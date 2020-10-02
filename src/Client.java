import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.Theme;
import twitter4j.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * @author Nathan Breunig
 * @version 1.3.1
 * LAST MODIFIED 12/25/19
 */
public class Client {
    private static HashMap<String, HashMap<String, Integer>> countAllWordsMap;
    private static HashMap<String, HashMap<String, Integer>> mentionOthersMap;
    private static HashMap<String, HashMap<String, Integer>> keyWordFreqMap;
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
            keyWordFreqMap = Analyzer.keyWordFrequency(Analyzer.getKeyWordFreqWords());
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

        Output.saveReport(folder, countAllWordsMap, mentionOthersMap, keyWordFreqMap, totalMentionsMap);
    }
}
