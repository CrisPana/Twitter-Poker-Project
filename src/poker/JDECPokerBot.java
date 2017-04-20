
/*
 * Software Engineering III - Twitter Poker Project
 * Team Name : JDEC
 * Team Members:
 * 		Dara Callinan 		14500717
 * 		Jazheel Luna		14486752
 * 		Eoghan O'Donnell	14464082
 * 		Crischelle Pana 	14366596
 * 
 * © 2017 
 * */

package poker;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class JDECPokerBot{

    //if something goes wrong, we might see a TwitterException
    public static void main(String... args) throws TwitterException, InterruptedException{

        //access the twitter API using your twitter4j.properties file
        Twitter twitter = TwitterFactory.getSingleton();

//        List<String> searches = new ArrayList<>();
//        searches.add("\"your welcome\"");
//        searches.add("\"your the\"");
//        searches.add("\"your a \"");
//
//        List<String> replies = new ArrayList<>();
//        replies.add( "I believe you meant \"you're\" here?");
//        replies.add(" I've detected the wrong \"you're\". Destroy!");
//        replies.add(" No, you are! Seriously. You are. \"You're\".");

        //keep tweeting forever
        while(true){

//            //create a new search, chosoe from random searches
//            Query query = new Query(searches.get((int)(searches.size()*Math.random())));
//
//            //get the results from that search
//            QueryResult result = twitter.search(query);
//
//            //get the first tweet from those results
//            Status tweetResult = result.getTweets().get(0);
//
//            //reply to that tweet, choose from random replies
//            StatusUpdate statusUpdate = new StatusUpdate(".@" + tweetResult.getUser().getScreenName() + replies.get((int)(replies.size()*Math.random())));
//            statusUpdate.inReplyToStatusId(tweetResult.getId());
//            Status status = twitter.updateStatus(statusUpdate); 
//
//            System.out.println("Sleeping.");
        	 //send a tweet
            Status status = twitter.updateStatus("Npoker Ipoker Cpoker Opoker");

            //print a message so we know when it finishes
            System.out.println("Done.");

           
        }
    }
}
