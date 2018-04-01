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
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

/**
 *
 * @author 
 */

/*
    1) Body might be empty
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
    
    
    private static HashMap<String, HashMap<Integer, Double>> OriginalWordRank = new HashMap<>(); //OriginalWord: <Url_ID: Rank>
    private static HashMap<String, HashMap<String, HashMap<Integer, wordInfo>>> IndexInfo = new HashMap<>(); //StemmedWord: <Original: <URL_ID: WordInfo> >
    private static HashMap<String, Integer> TagRanks = new HashMap<>();
    
    public Indexer2()
    {
        TagRanks.put("h6", 4);
        TagRanks.put("h5", 6);
        TagRanks.put("h4", 8);
        TagRanks.put("h3", 10);
        TagRanks.put("h2", 12);
        TagRanks.put("h1", 14);
        TagRanks.put("meta", 16);
        TagRanks.put("title", 20);
    }
        
    public void insertIndexInfo(String StemmedWord, String OriginalWord, int URL_ID, int Position, String AroundWordSentence, int TotalNumberOfWords)  
    {
        if(IndexInfo.get(StemmedWord) == null)
            IndexInfo.put(StemmedWord, new HashMap<>());
        HashMap <String, HashMap<Integer, wordInfo>> UrlID_WordInfo = IndexInfo.get(StemmedWord);
        
        if(UrlID_WordInfo.get(OriginalWord) == null)
            UrlID_WordInfo.put(OriginalWord, new HashMap<>());
        HashMap <Integer, wordInfo> WordsInfoMap = UrlID_WordInfo.get(OriginalWord);
        
        if(WordsInfoMap.get(URL_ID) == null)
        {
            wordInfo wordInfoDetails = new wordInfo(TotalNumberOfWords);
            wordInfoDetails.addPosition(Position, AroundWordSentence);
            
            WordsInfoMap.put(URL_ID, wordInfoDetails);
        }
        else
        {
            wordInfo wordInfoDetails = WordsInfoMap.get(URL_ID);
            wordInfoDetails.addPosition(Position, AroundWordSentence);
        }
    }
    
    public void insertRank(String OriginalWord, int URL_ID, Double Rank)
    {
        if(OriginalWordRank.get(OriginalWord) == null)
            OriginalWordRank.put(OriginalWord, new HashMap<>());
        HashMap<Integer, Double> URL_Rank_Map = OriginalWordRank.get(OriginalWord);
        
        if(URL_Rank_Map.get(URL_ID) == null)
            URL_Rank_Map.put(URL_ID, Rank);
        else    //If exists, add the Rank with the previous one
        {
            Double CurrentRank = URL_Rank_Map.get(URL_ID);
            CurrentRank += Rank;
            URL_Rank_Map.put(URL_ID, CurrentRank);
        }
    }
    
    public String cleanTheHTMLText(String HTMLContent, int RemoveStopWordsBool)
    {
        //1) Add Space After tags
        HTMLContent = HTMLContent.replaceAll("<(.*?)>","<$1> ");
        //1') Remove HTML Tags
        HTMLContent = Jsoup.parse(HTMLContent).text();
        
        //2) Remove All Special Characters
        HTMLContent = HTMLContent.replaceAll("[^a-zA-Z0-9 ]","");
        //3) Making All Small Letter
        HTMLContent = HTMLContent.toLowerCase();
        
        //4) Removing Stop Words
        if(RemoveStopWordsBool != 0)
            for(String stopWord : stopWordsList)
                HTMLContent = HTMLContent.replaceAll("\\b" + stopWord + "\\b\\s*", "");     //   \\b gives you the word boundaries.  \\s* sops up any white space on either side of the word being removed 

        //5) Removes Extra Spaces
        HTMLContent = HTMLContent.trim().replaceAll(" +", " ");   
        
        return HTMLContent;
    }
    
    public void calculateRank(Document doc, int urlID)
    {
        Elements ListH1 = doc.select("h1,h2,h3,h4,h5,h6, meta[name=description], meta[name=keywords], title");
        for(Element elementH1: ListH1)
        {
            double rankFactor = TagRanks.get(elementH1.tagName());
            String []wordArray = cleanTheHTMLText(elementH1.toString(), 1).split(" ");  //Splitting to array of words after cleaning
            for(String word : wordArray)
                insertRank(word, urlID, rankFactor);
        }
    }
    
    public void NormalizeRank(int urlID, String URL_Content)
    {
        //Removing Stop Words
        for(String stopWord : stopWordsList)
            URL_Content = URL_Content.replaceAll("\\b" + stopWord + "\\b\\s*", "");
        
        //Removes Extra Spaces
        URL_Content = URL_Content.trim().replaceAll(" +", " ");  
        
        //Taking Unique Words
        String []words = URL_Content.split(" ");
        List<String> UniqueWordArray = new ArrayList<String>();
        for (int i = 0; i < words.length; i++) {
            if (!(UniqueWordArray.contains(words[i]))) {
                UniqueWordArray.add(words[i]);
            }
        }
        
        //Normalizing Rank
        for(String UniqueWord: UniqueWordArray)
        {
            double currentRank = OriginalWordRank.get(UniqueWord).get(urlID);
            
            Stemmer stemmer = new Stemmer();
            String stemmedWord = stemmer.stem(UniqueWord);
            int wordFreq = IndexInfo.get(stemmedWord).get(UniqueWord).get(urlID).getFreq();
            
            currentRank = (double)currentRank/(double)wordFreq;
            
            OriginalWordRank.get(UniqueWord).put(urlID, currentRank);
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
        String URL_Content = doc.body().toString();  //.text();  //To get text with Tags
        
        ////////extractImgs(doc);
                
        //1) Calculating Rank
        calculateRank(doc, urlID);
        
        //2) Filter The String out of tags and stop words and special characters.
        URL_Content = cleanTheHTMLText(URL_Content, 0);
        
        /******** 3) Stemming + Fixing Others ***********/        
        String []wordArray = URL_Content.split(" ");  //Splitting to array of words
        Stemmer stemmer = new Stemmer();
        
        int TotalNumberOfWord = wordArray.length ,iterator = 0; 
        String stemmedWord, textAroundWord;
        for(String originalWord : wordArray)        //Traverses on each word in the document
        {
            //If it is a stop word, skip it
            if(Arrays.asList(stopWordsList).contains(originalWord))
            {
                iterator++;
                continue;
            }
            //Preparing the Around Word Sentence.
            textAroundWord = "";
            if( (iterator-2) >= 0 )
                textAroundWord += wordArray[iterator-2] + " ";
            if( (iterator-1) >= 0 )
                textAroundWord += wordArray[iterator-1] + " ";
            textAroundWord += wordArray[iterator];
            if( (iterator+1) < wordArray.length )
                textAroundWord += " " + wordArray[iterator+1];
            if( (iterator+2) < wordArray.length )
                textAroundWord += " " + wordArray[iterator+2];
            
            stemmedWord = stemmer.stem(originalWord);
                        
            insertIndexInfo(stemmedWord, originalWord, urlID, iterator, textAroundWord, TotalNumberOfWord);
            insertRank(originalWord, urlID, 1.0);
            iterator++;
        }
        
        //NormalizeRank(urlID, URL_Content);    //Divides each rank with the total number of that word
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
    
    public void DisplayRank()
    {
        System.out.println("********************************** Rank HashMap **********************************");
        for( String key1 : OriginalWordRank.keySet())
        {
            System.out.println(key1+":");
            HashMap<Integer, Double> temp1 = OriginalWordRank.get(key1);
            
            for( Integer key2  : temp1.keySet())
            {
                Double temp2 = temp1.get(key2);
                System.out.println("            <" + key2 + ":  [" + temp2 + "]>");
            }
        }
    }
    
    public void DisplayOriginalWord()
    {
        System.out.println("********************************** Index Info HashMap **********************************");
        for( String stemmedWord : IndexInfo.keySet())
        {
            System.out.println(stemmedWord+":");
            
            HashMap<String,HashMap<Integer, wordInfo>> originalHashMap = IndexInfo.get(stemmedWord);
            for( String originalWord  : originalHashMap.keySet())
            {
                System.out.println("           "+originalWord+":");
                
                HashMap<Integer,wordInfo> urlIDHashMap = originalHashMap.get(originalWord);
                for(int urlID : urlIDHashMap.keySet())
                {
                    System.out.println("                      "+urlID+":");
                    
                    wordInfo wordinfo = urlIDHashMap.get(urlID);
                    System.out.println("                                 "+wordinfo.getFreq()+", "+ wordinfo.getTF()+", ");
                    
                    HashMap <Integer, String> Position_AroundWordSentence = wordinfo.getPosition();
                    for(int Key : Position_AroundWordSentence.keySet())
                    {
                        System.out.println("                                                       "+Key + ": " + Position_AroundWordSentence.get(Key));
                    }
                }
            }
        }
    }
    
}
