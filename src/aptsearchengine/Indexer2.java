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
import org.jsoup.select.Elements;

/**
 *
 * @author 
 */


public class Indexer2 {
    
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
    
    private static HashMap<String, HashMap<Integer,ArrayList<Float>>> stemmedWordIndexer = new HashMap<String, HashMap<Integer,ArrayList<Float>>>();   //For stemmed words info
    /*
    Word1:   <URL1_ID:   [Freq, TF]>
    Word1:   <URL2_ID:   [Freq, TF]>
    */
    private static HashMap<String, HashMap<Integer,ArrayList<Float>>> imagesIndexer = new HashMap<String, HashMap<Integer,ArrayList<Float>>>();
    /*
    title/word: <URL_ID: 
    */
    private static HashMap<String, HashMap<String, HashMap<Integer, wordInfo>>> originalWordIndexer = new HashMap<String, HashMap<String, HashMap<Integer, wordInfo>>>(); //For original word info
    
    public Indexer2(){ }
    
    
    public void insertOriginalWord(String stemmedWord, String originalWord, int URLID, int TotalNumberOfWords, int position, String text)
    {
        /************************************ Adding to Original HashMap ************************************/
        //First time for stemmed word
        if(originalWordIndexer.get(stemmedWord) == null)
            originalWordIndexer.put(stemmedWord, new HashMap<String,HashMap<Integer, wordInfo>>());
        //Getting the Original Word hashmap
        HashMap<String,HashMap<Integer, wordInfo>> stemmedWordValue = originalWordIndexer.get(stemmedWord);
        
        //First time for original word
        if(stemmedWordValue.get(originalWord) == null)
            stemmedWordValue.put(originalWord, new HashMap<Integer,wordInfo>());
        //Getting URLID hash map
        HashMap<Integer,wordInfo> originalWordValue = stemmedWordValue.get(originalWord);
        
        //First time for URLID
        if(originalWordValue.get(URLID) == null)
            originalWordValue.put(URLID, new wordInfo(TotalNumberOfWords));
        //Getting word info for this url
        wordInfo URLIDvalue = originalWordValue.get(URLID);
        
        //Increment the freq of this word and add its new position with the text around it
        URLIDvalue.addPosition(position, text); 
        
        
        /************************************ Adding to Original HashMap ************************************/
        //If first time for this stemmmedWord
        if(stemmedWordIndexer.get(stemmedWord) == null)
            stemmedWordIndexer.put(stemmedWord, new HashMap<Integer,ArrayList<Float>>());
        //Getting the UrlID HashMap
        HashMap<Integer,ArrayList<Float>> URLIdHashMap = stemmedWordIndexer.get(stemmedWord);
        
        //If this URL ID isnt stored for this stemmed word
        if(URLIdHashMap.get(URLID) == null)
        {
            URLIdHashMap.put(URLID, new ArrayList<Float>());
            ArrayList<Float> stemmedWordInfoArray = URLIdHashMap.get(URLID);
            stemmedWordInfoArray.add((float)1);
            stemmedWordInfoArray.add((float)1/(float)TotalNumberOfWords);
        }
        else
        {
            //IF already exists, update the info. Increment Freq and recalc TF
            ArrayList<Float> stemmedWordInfoArray = URLIdHashMap.get(URLID);
            stemmedWordInfoArray.set(0, (stemmedWordInfoArray.get(0)+1) );
            stemmedWordInfoArray.set(1, (stemmedWordInfoArray.get(0)/TotalNumberOfWords) );
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
        
        ////////extractImgs(doc);
        String contentString = doc.body().text();
        
        //1) Making All Small Letter
        contentString = contentString.toLowerCase();
        
        //2) Removing Stop Words
        for(String stopWord : stopWordsList)
            contentString = contentString.replaceAll("\\b" + stopWord + "\\b\\s*", "");     //   \\b gives you the word boundaries.  \\s* sops up any white space on either side of the word being removed 
        contentString = contentString.trim().replaceAll(" +", " ");     //Removes Extra Spaces
        
        /******** 3) Stemming ***********/        
        String []wordArray = contentString.split(" ");  //Splitting to array of words
        
        Stemmer stemmer = new Stemmer();
        int TotalNumberOfWord = wordArray.length ,iterator = 0;  //urlID  
        String stemmedWord, textAroundWord;
        for(String originalWord : wordArray)        //Traverses on each word in the document
        {
            if(iterator<2)
                textAroundWord = "";
            else if (iterator > (wordArray.length - 3) )
                textAroundWord = "";
            else
                textAroundWord = wordArray[iterator-2] + wordArray[iterator-1] + originalWord + wordArray[iterator+1] + wordArray[iterator+2];
            
            stemmedWord = stemmer.stem(originalWord);
                        
            insertOriginalWord(stemmedWord, originalWord, urlID, TotalNumberOfWord, iterator, textAroundWord);
            iterator++;
        }
    }
    
    private void extractImgs(Document doc)
    {
        String url, name, title, alt;
        for(Element image : doc.select("img"))
        {
            url = image.absUrl("src");
            
            name = url.substring(url.lastIndexOf('/') + 1).replaceAll("-", " ").replaceAll("_", " ").trim();    //Extracting name, removing - _
            name = (name.lastIndexOf(".") != -1) ? name.substring(0, name.lastIndexOf(".")) : name;             //Removing Img Extension
            
            alt = image.attr("alt");
            title = image.attr("title");
            
            System.out.println(name + " : " + alt + " : " + title + " : " + url);
            
            
            //getClosestLink(image);
            //System.out.println("\n\n\n");
            
        }
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
    
    public void DisplayStemmedWord()
    {
        System.out.println("********************************** Stemmed HashMap **********************************");
        for( String key1 : stemmedWordIndexer.keySet())
        {
            System.out.println(key1+":");
            HashMap<Integer, ArrayList<Float>> temp1 = stemmedWordIndexer.get(key1);
            
            for( Integer key2  : temp1.keySet())
            {
                ArrayList<Float> temp2 = temp1.get(key2);
                System.out.println("            <" + key2 + ":  [" + temp2.get(0)+" ," + temp2.get(1)+"]>");
            }
        }
    }
    
    public void DisplayOriginalWord()
    {
        System.out.println("********************************** Original HashMap **********************************");
        for( String stemmedWord : originalWordIndexer.keySet())
        {
            System.out.println(stemmedWord+":");
            
            HashMap<String,HashMap<Integer, wordInfo>> originalHashMap = originalWordIndexer.get(stemmedWord);
            for( String originalWord  : originalHashMap.keySet())
            {
                System.out.println("           "+originalWord+":");
                
                HashMap<Integer,wordInfo> urlIDHashMap = originalHashMap.get(originalWord);
                for(int urlID : urlIDHashMap.keySet())
                {
                    System.out.println("                      "+urlID+":");
                    
                    wordInfo wordinfo = urlIDHashMap.get(urlID);
                    System.out.println("                                 "+wordinfo.getFreq()+", "+ wordinfo.getTF()+", ");
                    
                    //Img Urls
                    //ArrayList<String> imgUrls = wordinfo.getImgURL()
                }
            }
        }
    }
    
}
