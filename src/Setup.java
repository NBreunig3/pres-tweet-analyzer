import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A class to set up the Twitter4j API
 * @author Nathan Breunig
 * LAST MODIFIED 12/4/19
 */
public abstract class Setup {

    /**
     * Sets up the Twitter4j API
     * @return Twitter object used in Analyzer
     */
    public static Twitter setup(){
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setTweetModeExtended(true); // To get full tweet
        try {
            FileInputStream fileInputStream = new FileInputStream("res\\twitter4j.properties");
            Properties properties = new Properties();

            properties.load(fileInputStream);

            String consumerKey = properties.getProperty("oauth.consumerKey");
            String consumerSecret = properties.getProperty("oauth.consumerSecret");
            String accessToken = properties.getProperty("oauth.accessToken");
            String accessTokenSecret = properties.getProperty("oauth.accessTokenSecret");

            cb.setDebugEnabled(true);
            cb.setOAuthConsumerKey(consumerKey);
            cb.setOAuthConsumerSecret(consumerSecret);
            cb.setOAuthAccessToken(accessToken);
            cb.setOAuthAccessTokenSecret(accessTokenSecret);
            cb.setTweetModeExtended(true);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return new TwitterFactory(cb.build()).getInstance();
    }
}
