import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.*;
import java.util.*;

/**
 * A tool to analyze data from presidential candidates Twitter accounts
 * @author Nathan Breunig
 * LAST MODIFIED 12/1/19
 */
public class Analyzer {
    private Twitter twitter;
    public static Paging paging;
    private HashSet<String> functionWords;
    private HashSet<Character> letters;

    /**
     * Constructor
     * Paging set count is the number
     * of tweets to retrieve from each account
     */
    public Analyzer() {
        twitter = Setup.setup();
        paging = new Paging();
        paging.setCount(1000);
        functionWords = readFunctionWords();
        letters = getLetters();
    }

    /**
     * Counts how many times a candidate has mentioned word
     *
     * @param word A word to check
     */
    public int wordFrequency(String candidate, String word) {
        int freq = 0;
        try {
            List<Status> tweets = twitter.getUserTimeline(Candidates.getUsernames().get(candidate), paging);

            for (Status s : tweets) {
                freq += wordFreq(s.getText(), word, true);
            }
        } catch (TwitterException e) {
            System.out.println(e.getMessage());
        }
        return freq;
    }

    /**
     * Counts how many times each candidate has a
     * tweet containing word
     *
     * @param word word to look for
     */
    public HashMap<String, Integer> wordFrequency(String word) {
        HashMap<String, Integer> hashMap = new HashMap<>();

        for (String s : Candidates.getCandidates()) {
            hashMap.put(s, wordFrequency(s, word));
        }
        return hashMap;
    }

    //todo write correct javadoc with hashmap meaning
    public HashMap<String, HashMap<String, Integer>> wordFrequency(ArrayList<String> words){
        HashMap<String, HashMap<String, Integer>> hashMap = new HashMap<>();

        for (String cand : Candidates.getCandidates()) {
            for (String word : words) {
                HashMap<String, Integer> temp;
                if (hashMap.get(word) == null){
                    temp = new HashMap<>();
                }else {
                    temp = hashMap.get(word);
                }
                temp.put(cand, wordFrequency(cand, word));
                hashMap.put(word, temp);
            }
        }
        return hashMap;
    }

    /**
     * Counts the number of times a candidate mentions another candidate
     *
     * @return HashMap
     */
    public HashMap<String, Integer> mentionOthersFrequency(String candidate) {
        HashMap<String, Integer> hashMap = new HashMap<>();

        for (String otherCandidate : Candidates.getCandidates()) {
            if (!otherCandidate.equals(candidate)) {
                if (hashMap.get(otherCandidate) == null) {
                    hashMap.put(otherCandidate, 0);
                }
                hashMap.put(otherCandidate, hashMap.get(otherCandidate) + wordFrequency(candidate, otherCandidate)); // Called by name
                hashMap.put(otherCandidate, hashMap.get(otherCandidate) + wordFrequency(candidate, Candidates.getUsernames().get(otherCandidate))); // Called by username
            }
        }
        return hashMap;
    }

    /**
     * Counts the number of times each candidate mentions
     * another candidate in a tweet
     * @return HashMap
     */
    public HashMap<String, HashMap<String, Integer>> mentionOthersFrequency() {
        HashMap<String, HashMap<String, Integer>> hashMap = new HashMap<>();

        for (String candidate : Candidates.getCandidates()) {
            hashMap.put(candidate, mentionOthersFrequency(candidate));
        }
        return hashMap;
    }

    /**
     * Counts the number of times each candidate has been
     * mentioned on the all other candidate accounts
     * @return HashMap
     */
    public HashMap<String, Integer> totalMentions() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        for (String s : Candidates.getCandidates()){
            hashMap.put(s, 0);
        }
        try {
            for (String cand1 : Candidates.getCandidates()) {
                List<Status> tweets = twitter.getUserTimeline(Candidates.getUsernames().get(cand1), paging);
                for (String cand2 : Candidates.getCandidates()) {
                    if (!cand1.equals(cand2)) {
                        for (Status tweet : tweets){
                            hashMap.put(cand2, hashMap.get(cand2) + wordFreq(tweet.getText(), cand2, false));
                        }
                    }
                }
            }
        } catch (TwitterException e) {
            System.out.println(e.getMessage());
        }
        return hashMap;
    }

    /**
     * Counts all words tweeted by a candidate and
     * ranks them by most frequent
     *
     * @param candidate Candidate to use. [Lastname] Ex. "Warren"
     * @return HashMap with each word attached to an integer frequency
     */
    public HashMap<String, Integer> countAllWords(String candidate) {
        HashMap<String, Integer> hashMap = new HashMap<>();

        try {
            List<Status> tweets = twitter.getUserTimeline(Candidates.getUsernames().get(candidate), paging);
            for (Status s : tweets){
                String[] words = tweetSplitter(s.getText(), true, true);
                for (int i = 0; i < words.length; i++){
                    if (!words[i].equals("") && !functionWords.contains(words[i])) {
                        if (hashMap.containsKey(words[i])) {
                            hashMap.put(words[i], hashMap.get(words[i]) + 1);
                        } else {
                            hashMap.put(words[i], 1);
                        }
                    }
                }
            }
        }catch (TwitterException e){
            System.out.println(e.getMessage());
        }
        return hashMap;
    }

    /**
     * Counts all words tweeted by each candidate and
     * ranks them by most frequent
     *
     * @return HashMap from each candidate to there counted words
     */
    public HashMap<String, HashMap<String, Integer>> countAllWords() {
        HashMap<String, HashMap<String, Integer>> hashMap = new HashMap<>();

        for (String s : Candidates.getCandidates()){
            hashMap.put(s, countAllWords(s));
        }
        return hashMap;
    }

    /**
     * Private helper method to split each tweet into n array of words
     * @param tweet text to split
     * @param removeAts if it should remove @mentions to other users
     * @return String array
     */
    private String[] tweetSplitter(String tweet, boolean removeAts, boolean removeHashtags){
        tweet = tweet.toLowerCase();

        String[] words = tweet.split("\\s+");

        //Remove links and @'s
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].trim();
            if (removeAts) {
                if (words[i].substring(0, 1).equals("@") || words[i].contains("http") ) {
                    words[i] = "";
                }
            }
            if (removeHashtags && !words[i].equals("")){
                if (words[i].substring(0,1).equals("#")){
                    words[i] = "";
                }
            }
            words[i] = words[i].replace(".", "");
            words[i] = words[i].replace("!", "");
            words[i] = words[i].replace(",", "");
            words[i] = words[i].replace(";", "");
            words[i] = words[i].replace("\"", "");
            words[i] = words[i].replace("'", "");
            words[i] = words[i].replace("(", "");
            words[i] = words[i].replace(")", "");
            words[i] = words[i].replace("/", "");
            words[i] = words[i].replace("\\", "");
            words[i] = words[i].replace(":", "");
            words[i] = words[i].replace("&", "");
            if (words[i].length() == 1 && words[i].contains("-")){
                words[i] = words[i].replace("-", "");
            }

            //Removes items with no letters
            boolean hasLetters = false;
            for (int j = 0; j < words[i].length(); j++){
                if (letters.contains(words[i].charAt(j))){
                    hasLetters = true;
                }
            }
            if (!hasLetters){
                words[i] = "";
            }
        }
        return words;
    }

    /**
     * Private Helper Method to parse through text
     * and determine if any text matches toFind
     * @param tweet text to search through
     * @param toFind text to find
     * @param removeAts if @blah text should be removed from the tweet
     * @return frequency that toFind was found in tweet
     */
    private int wordFreq(String tweet, String toFind, boolean removeAts) {
        int freq = 0;
        toFind = toFind.toLowerCase();
        String[] words = tweetSplitter(tweet, removeAts, true);

        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(toFind)) {
                freq++;
            }
        }
        return freq;
    }

    /**
     * Sets the number of tweets to retrieve from each account
     * when using the various methods
     * @param range
     */
    public void setTweetRange(int range){
        paging.setCount(range);
    }

    /**
     * Helper method that runs on initialization
     * @return HashSet of function words
     */
    private HashSet<String> readFunctionWords(){
        File file = new File("res\\function_words.txt");
        HashSet<String> hashSet = new HashSet<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (line != null){
                if (!line.contains("!")){
                    hashSet.add(line.substring(0, line.indexOf(" ")));
                }
                line = br.readLine();
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return hashSet;
    }

    /**
     * Gets a hash set with all letters
     * @return Hash Set
     */
    private HashSet<Character> getLetters(){
        HashSet<Character> hashSet = new HashSet<>();

        for (char c = 'a'; c <= 'z'; c++){
            hashSet.add(c);
        }
        return hashSet;
    }

    //TODO javadoc
    public static ArrayList<String> getWordFreqWords(){
        ArrayList<String> words = new ArrayList<>();
        words.add("We");
        words.add("I");
        words.add("Democrats");
        words.add("Republicans");
        words.add("Free");
        words.add("Climate");
        words.add("Impeachment");
        words.add("Ukraine");
        words.add("War");
        words.add("Health care");
        words.add("Americans");
        return words;
    }
}
