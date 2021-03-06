import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.*;
import java.util.*;

/**
 * A tool to analyze data from presidential candidates Twitter accounts
 * @author Nathan Breunig
 * LAST MODIFIED 3/23/20
 */
public class Analyzer {
    public static Paging paging;
    private static HashSet<String> functionWords;
    private static HashMap<String, List<Status>> tweets;

    /**
     * Method that needs to be run before any other to setup
     * API and get all candidates tweets
     */
    public static void setup(){
        Twitter twitter = Setup.setup();
        paging = new Paging();
        paging.setCount(5000);
        functionWords = readFunctionWords();
        tweets = new HashMap<>();
        // Get all candidates tweets
        for (String candidate : Candidates.getCandidates()){
            try {
                List<Status> candTweets = twitter.getUserTimeline(Candidates.getUsernames().get(candidate), paging);
                tweets.put(candidate, candTweets);
            }catch (TwitterException e){
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Counts how many times a candidate has mentioned "word"
     *
     * @param word A word to check
     */
    public static int keyWordFrequency(String candidate, String word) {
        int freq = 0;
        for (Status s : tweets.get(candidate)) {
            freq += wordFreq(s.getText(), word, true);
        }
        return freq;
    }

    /**
     * Counts how many times each candidate mentions each word in "words"
     *
     * @param words List of words to search for
     * @return HashMap of each word to another hashMap of each candidate and that words frequency count
     */
    public static HashMap<String, HashMap<String, Integer>> keyWordFrequency(ArrayList<String> words){
        HashMap<String, HashMap<String, Integer>> hashMap = new HashMap<>();

        for (String cand : Candidates.getCandidates()) {
            for (String word : words) {
                HashMap<String, Integer> temp;
                if (hashMap.get(word) == null){
                    temp = new HashMap<>();
                }else {
                    temp = hashMap.get(word);
                }
                temp.put(cand, keyWordFrequency(cand, word));
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
    public static HashMap<String, Integer> mentionOthersFrequency(String candidate) {
        HashMap<String, Integer> hashMap = new HashMap<>();

        for (String otherCandidate : Candidates.getCandidates()) {
            if (!otherCandidate.equals(candidate)) {
                if (hashMap.get(otherCandidate) == null) {
                    hashMap.put(otherCandidate, 0);
                }
                hashMap.put(otherCandidate, hashMap.get(otherCandidate) + keyWordFrequency(candidate, otherCandidate)); // Called by name
                hashMap.put(otherCandidate, hashMap.get(otherCandidate) + keyWordFrequency(candidate, Candidates.getUsernames().get(otherCandidate))); // Called by username
                if (Candidates.getNicknames(otherCandidate) != null) {
                    for (String nickname : Candidates.getNicknames(otherCandidate)) {
                        hashMap.put(otherCandidate, hashMap.get(otherCandidate) + keyWordFrequency(candidate, nickname));
                    }
                }
            }
        }
        return hashMap;
    }

    /**
     * Counts the number of times each candidate mentions
     * another candidate in a tweet
     * @return HashMap
     */
    public static HashMap<String, HashMap<String, Integer>> mentionOthersFrequency() {
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
    public static HashMap<String, Integer> totalMentions() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        for (String s : Candidates.getCandidates()) {
            hashMap.put(s, 0);
        }
        for (String cand1 : Candidates.getCandidates()) {
            List<Status> candTweets = tweets.get(cand1);
            for (String cand2 : Candidates.getCandidates()) {
                if (!cand1.equals(cand2)) {
                    for (Status tweet : candTweets){
                        hashMap.put(cand2, hashMap.get(cand2) + wordFreq(tweet.getText(), cand2, true));
                        hashMap.put(cand2, hashMap.get(cand2) + wordFreq(tweet.getText(), Candidates.getUsernames().get(cand2), false));
                        if (Candidates.getNicknames(cand2) != null) {
                            for (String nickname : Candidates.getNicknames(cand2)) {
                                hashMap.put(cand2, hashMap.get(cand2) + wordFreq(tweet.getText(), nickname, true));
                            }
                        }
                    }
                }
            }
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
    public static HashMap<String, Integer> countAllWords(String candidate) {
        HashMap<String, Integer> hashMap = new HashMap<>();

        List<Status> candTweets = tweets.get(candidate);
        for (Status s : candTweets){
            String[] words = tweetSplitter(s.getText(), true, true);
            for (int i = 0; i < words.length; i++){
                if (!words[i].equals("") && !functionWords.contains(words[i]) && !words[i].equals(candidate.toLowerCase())) {
                    if (hashMap.containsKey(words[i])) {
                        hashMap.put(words[i], hashMap.get(words[i]) + 1);
                    } else {
                        hashMap.put(words[i], 1);
                    }
                }
            }
        }
        return hashMap;
    }

    /**
     * Counts all words tweeted by each candidate and
     * ranks them by most frequent
     *
     * @return HashMap from each candidate to there counted words
     */
    public static HashMap<String, HashMap<String, Integer>> countAllWords() {
        HashMap<String, HashMap<String, Integer>> hashMap = new HashMap<>();

        for (String s : Candidates.getCandidates()){
            hashMap.put(s, countAllWords(s));
        }
        return hashMap;
    }

    /**
     * Private helper method to split each tweet into n array of words
     * @param tweet text to split
     * @param removeUsername if it should remove @mentions to other users
     * @return String array
     */
    private static String[] tweetSplitter(String tweet, boolean removeUsername, boolean removeHashtags){
        tweet = tweet.toLowerCase();

        String[] words = tweet.split("\\s+");

        //Remove links and @'s
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].trim();
            if (words[i].length() > 1) {
                // Remove @ if necessary
                if (words[i].contains("@")) {
                    if (removeUsername) {
                        words[i] = "";
                    }
                }
                // Remove # if necessary
                if (words[i].contains("#")) {
                    if (removeHashtags) {
                        words[i] = "";
                    }
                }
            }
            // Remove links
            if (words[i].contains("http")){
                words[i] = "";
            }
            words[i] = words[i].replaceAll("[^A-Za-z0-9]", "");
        }
        return words;
    }

    /**
     * Private Helper Method to parse through text
     * and determine if any text matches toFind
     * @param tweet text to search through
     * @param toFind text to find
     * @param removeUsername if @blah text should be removed from the tweet
     * @return frequency that toFind was found in tweet
     */
    private static int wordFreq(String tweet, String toFind, boolean removeUsername) {
        int freq = 0;
        toFind = toFind.toLowerCase();
        // Looking for multiple words
        if (toFind.contains(" ")){
            tweet = tweet.toLowerCase();
            while (tweet.contains(toFind)){
                freq++;
                tweet = tweet.replaceFirst(toFind, "");
            }
        }else {
            String[] words = tweetSplitter(tweet, removeUsername, true);

            for (int i = 0; i < words.length; i++) {
                if (words[i].equals(toFind)) {
                    freq++;
                }
            }
        }
        return freq;
    }

    /**
     * Sets the number of tweets to retrieve from each account
     * when using the various methods
     * @param range
     */
    public static void setTweetRange(int range){
        paging.setCount(range);
    }

    /**
     * Helper method that runs on initialization
     * @return HashSet of function words
     */
    private static HashSet<String> readFunctionWords(){
        File file = new File("res/function_words.txt");
        HashSet<String> hashSet = new HashSet<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (line != null){
                if (!line.contains("!")){
                    if (line.contains(" ")) {
                        hashSet.add(line.substring(0, line.indexOf(" ")));
                    }else {
                        hashSet.add(line);
                    }
                }
                line = br.readLine();
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return hashSet;
    }

    /**
     * A list of words to use for the Key Word Frequency analysis
     * @return a list of key words
     */
    public static ArrayList<String> getKeyWordFreqWords(){
        ArrayList<String> words = new ArrayList<>();
        words.add("We");
        words.add("I");
        words.add("China");
        words.add("Climate");
        words.add("Coronavirus");
        words.add("Health care");
        words.add("Pandemic");
        words.add("Crisis");
        return words;
    }
}
