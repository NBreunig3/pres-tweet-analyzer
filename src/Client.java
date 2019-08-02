import twitter4j.*;

import javax.swing.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Nathan Breunig
 * @version 1.0
 * LAST MODIFIED 8/1/19
 */

public class Client {
    private static Output output = new Output();
    private static Analyzer analyzer = new Analyzer();

    /**
     * Main method
     * Meant to be run from the command line
     * @param args [number of tweets to fetch] [wordFrequency word to use]
     * @throws TwitterException
     */
    //TODO add command line arguments (number of tweets to search, wordFrequency word to use)
    public static void main(String[] args) throws TwitterException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose a directory for report");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        int response = fileChooser.showSaveDialog(null);
        if (response == JFileChooser.APPROVE_OPTION){
            File path = fileChooser.getSelectedFile();
            saveReport(path);
        }else {
            System.exit(0);
        }
    }

    /**
     * Saves multiple files in a directory for all Analyzer methods
     * (countAllWords, mentionOthersFrequency, wordFrequency, totalMentions)
     * @param path Where to save the generated report
     */
    public static void saveReport(File path) {
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        //Create Directories
        File dir = new File(path.getPath() + "\\Report_" + dateFormat.format(new Date()));
        dir.mkdir();
        new File(dir.getPath() + "\\Word Count").mkdir();
        new File(dir.getPath() + "\\Mention of Others Frequency").mkdir();
        new File(dir.getPath() + "\\Word Frequency").mkdir();
        new File(dir.getPath() + "\\Total Mentions").mkdir();

        for (String cand : Candidates.getCandidates()) {
            output.saveResults(new File(dir.getPath() + "\\Word Count\\" + cand + ".txt"), cand + " - Word Count", output.getDescription("countAllWords"), analyzer.countAllWords(cand));
            System.out.println("Done! countAll: " + cand);
        }
        for (String cand : Candidates.getCandidates()) {
            output.saveResults(new File(dir.getPath() + "\\Mention of Others Frequency\\" + cand + ".txt"), cand + " - Mentions of other candidates", output.getDescription("mentionOthersFrequency"), analyzer.mentionOthersFrequency(cand));
            System.out.println("Done! mentions: " + cand);
        }
        output.saveResults(new File(dir.getPath() + "\\Word Frequency\\We.txt"), "Frequency of \"We\" mentions", output.getDescription("wordFrequency"), analyzer.wordFrquency("We"));
        output.saveResults(new File(dir.getPath() + "\\Word Frequency\\I.txt"), "Frequency of \"I\" mentions", output.getDescription("wordFrequency"), analyzer.wordFrquency("I"));
        output.saveResults(new File(dir.getPath() + "\\Word Frequency\\Democrats.txt"), "Frequency of \"Democrats\" mentions", output.getDescription("wordFrequency"), analyzer.wordFrquency("Democrats"));
        output.saveResults(new File(dir.getPath() + "\\Word Frequency\\Republicans.txt"), "Frequency of \"Republicans\" mentions", output.getDescription("wordFrequency"), analyzer.wordFrquency("Republicans"));
        output.saveResults(new File(dir.getPath() + "\\Word Frequency\\Free.txt"), "Frequency of \"Free\" mentions", output.getDescription("wordFrequency"), analyzer.wordFrquency("Free"));
        System.out.println("Done! wordFreq");
        output.saveResults(new File(dir.getPath() + "\\Total Mentions\\totalMentions.txt"), "Total mentions of all candidates", output.getDescription("totalMentions"), analyzer.totalMentions());
        System.out.println("Done! totalMentions");
    }
}
