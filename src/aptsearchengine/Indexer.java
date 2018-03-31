/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aptsearchengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.*;
import org.jsoup.nodes.*;

/**
 *
 * @author 
 */


public class Indexer {
    
    //https://meta.wikimedia.org/wiki/Stop_word_list/google_stop_word_list   Google's Stop Words List
    public static String[] stopWordsList = {         
        "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't", "as", 
        "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't", "cannot", 
        "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each", 
        "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", 
        "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", 
        "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", 
        "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", 
        "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's",
        "should", "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", 
        "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", 
        "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're",
        "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", 
        "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", 
        "you've", "your", "yours", "yourself", "yourselves"
    };
    
    private static HashMap<String, HashMap<Integer,ArrayList<Float>>> wordIndexer = new HashMap<String, HashMap<Integer,ArrayList<Float>>>();  //private HashMap<String, HashMap<Integer,ArrayList<Integer>>> wordIndexer = new HashMap<String, HashMap<Integer, ArrayList<Integer>>>();
    /*
    Word1:   <URL1_ID:   [Freq, TF]>
    Word1:   <URL2_ID:   [Freq, TF]>
    */
    
    
    public Indexer(){ }
    
    public void insert(String word, int urlID, int documentSize)
    {
        if(wordIndexer.get(word) == null)   //If word doesnt exist in the indexer
            wordIndexer.put(word, new HashMap<Integer, ArrayList<Float>>());
        HashMap<Integer, ArrayList<Float>> urlID_Freq = wordIndexer.get(word);
        
        if(urlID_Freq.get(urlID) == null)   //If the URL linked to this word doesnt exist. First time occurence.
        {
            urlID_Freq.put(urlID, new ArrayList<Float>());
            ArrayList<Float> freq_TF = urlID_Freq.get(urlID);
            freq_TF.add((float)1);
            freq_TF.add((float)1/(float)documentSize);
        }
        else
        {
            ArrayList<Float> freq_TF = urlID_Freq.get(urlID);
            freq_TF.set(0, (freq_TF.get(0)+1) );
            freq_TF.set(1, (freq_TF.get(0)/documentSize) );
        }
    }
    
    public static void DisplayAll()
    {
        for( String key1 : wordIndexer.keySet())
        {
            System.out.println(key1+":");
            HashMap<Integer, ArrayList<Float>> temp1 = wordIndexer.get(key1);
            
            for( Integer key2  : temp1.keySet())
            {
                ArrayList<Float> temp2 = temp1.get(key2);
                System.out.println("        <" + key2 + ":  [" + temp2.get(0)+" ," + temp2.get(1)+"]>");
            }
        }
    }
    
    public void takeURL(String URL, int urlID) throws IOException
    {
        /*
        String html = "<html><head><title>First parse</title></head>"
                    + "<body><p>Parsed HTML into a doc.</p></body></html>";
        Document doc = Jsoup.parse(html);
        */
        
        Document doc = Jsoup.connect(URL).get();
        String contentString = doc.body().text();
        
        //1) Making All Small Letter
        contentString = contentString.toLowerCase();
        
        //2) Removing Stop Words
        for(String stopWord : stopWordsList)
            contentString = contentString.replaceAll("\\b" + stopWord + "\\b\\s*", "");     //   \\b gives you the word boundaries.  \\s* sops up any white space on either side of the word being removed 
        contentString = contentString.trim().replaceAll(" +", " ");     //Removes Extra Spaces
        
        //3) Stemming
        String []wordArray = stemmer(contentString);
        
        //4) Creating Inverted Index List  
        for(String word : wordArray)
            insert(word, urlID, wordArray.length);    //MUST BE IN A LOCK TO AVOID DIRTY READ !!!
    }
    
    private String[] stemmer(String contentString)
    {
        String []wordArray = contentString.split(" ");  //Splitting to array of words
        Stemmer stemmer = new Stemmer();
        
        int iterator = 0;
        for(String word : wordArray)        //Stems Each Word
        {
            wordArray[iterator] = stemmer.stem(word);
            iterator++;
        }
        
        return wordArray;
    }    
    
}
