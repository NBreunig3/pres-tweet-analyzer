import twitter4j.*;
import javax.swing.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
/**
 * @author Nathan Breunig
 * @version 1.2.3
 * LAST MODIFIED 12/11/19
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
            System.out.println("Done");
            mentionOthersMap = Analyzer.mentionOthersFrequency();
            System.out.println("Done");
            wordFreqMap = Analyzer.wordFrequency(Analyzer.getWordFreqWords());
            System.out.println("Done");
            totalMentionsMap = Analyzer.totalMentions();
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
        Output.saveCSVReport(new File(csvDir.getPath() + "\\Word Count\\WordCount.csv"), "CountAllWords", countAllWordsMap, null);
        //Mentions of others
        Output.saveCSVReport(new File(csvDir.getPath() + "\\Mention of Others Frequency\\mentionOfOthersFreq.csv"), "MentionOthersFreq", mentionOthersMap, null);
        //Word Frequency
        Output.saveCSVReport(new File(csvDir.getPath() + "\\Word Frequency\\WordFrequency.csv"), "WordFreq", wordFreqMap, null);
        //Total Mentions
        Output.saveCSVReport(new File(csvDir.getPath() + "\\Total Mentions\\totalMentions.csv"), "TotalMentions", null, totalMentionsMap);
    }

    private static File createSaveDir(File path){
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

        //Create Directories
        //Create Main Directory
        File mainDir = new File(path.getPath() + "\\Report_" + dateFormat.format(new Date()));
        mainDir.mkdir();
        //Create CSV Directory
        File csvDir = new File(mainDir.getPath() + "\\CSV");
        csvDir.mkdir();

        //Create Sub Directories
        new File(csvDir.getPath() + "\\Word Count").mkdir();
        new File(csvDir.getPath() + "\\Mention of Others Frequency").mkdir();
        new File(csvDir.getPath() + "\\Word Frequency").mkdir();
        new File(csvDir.getPath() + "\\Total Mentions").mkdir();

        return mainDir;
    }
}
