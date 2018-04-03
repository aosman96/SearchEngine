/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author Ahmed
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        /*        
        Indexer indexerEngine = new Indexer();
        try {
            indexerEngine.takeURL("https://en.wikipedia.org/wiki/Computer", 0);
            indexerEngine.takeURL("https://www.vodafone.com.eg/eshop/Catalogue/Phones-Devices-/Phones", 1);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        indexerEngine.DisplayAll();
        */
        /*
        SQLiteDBHelper sqlManagerIndexer = new SQLiteDBHelper(1,"",0);
        sqlManagerIndexer.BackupIndexer();
        sqlManagerIndexer.RestoreIndexer();
        while(true)
        {
            sqlManagerIndexer.test();
            sqlManagerIndexer.BackupIndexer();
        }
        */
        
        Indexer2 indexerEngine = new Indexer2();    //Indexer
        SQLiteDBHelper sqlManagerCrawler = new SQLiteDBHelper(0,"backup.db",1); //Connection to crawler Database
        SQLiteDBHelper sqlManagerIndexer = new SQLiteDBHelper(1,"",0);  //Connection to Indexer Database
        //sqlManagerIndexer.BackupIndexer();
        sqlManagerIndexer.RestoreIndexer();
        int LastIndexCounter = 0;
        int newIndexCounter;
        
        while(true)
        {
            System.out.println("INDEXER MAIN: Reading a new batch of urls !!!");
            newIndexCounter  = indexerEngine.TakeWordInfo(sqlManagerCrawler.GetIndexerPageInfo(LastIndexCounter));  //Query
                        
            if(newIndexCounter == -1)   //Means nothing new to take
            {
                LastIndexCounter = newIndexCounter;
                try {
                    System.out.println("INDEXER MAIN: Nothing to read. Gonna go sleep");
                    TimeUnit.MINUTES.sleep(1);  //Wait for 1 minutes be4 trying again
                    System.out.println("INDEXER MAIN: I woke up! Time to work !!");
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                sqlManagerIndexer.InsertWords(indexerEngine.getOriginalWordRank(), indexerEngine.getIndexerFullInfo());
                sqlManagerIndexer.CalculateIDF();
                sqlManagerIndexer.BackupIndexer();
                indexerEngine.EmptyIndexer();
            }
            
            //indexerEngine.DisplayRank();
            //System.out.println("\n\n");
            //indexerEngine.DisplayOriginalWord();
        }
        
        
        /*
        Indexer2 indexerEngine = new Indexer2();
        try {
            indexerEngine.takeURL("https://en.wikipedia.org/wiki/Computer", "rsfes");
            //indexerEngine.takeURL("https://www.vodafone.com.eg/eshop/Catalogue/Phones-Devices-/Phones", 1);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        indexerEngine.DisplayRank();
        System.out.println("\n\n");
        indexerEngine.DisplayOriginalWord();
        */
        
        /*
        Document doc;
        try {
            doc = Jsoup.connect("http://facebook.com").get();            
            
            //System.out.println("Title: " + doc.title());            
            if(! doc.select("meta[name=description]").isEmpty())
            {
                String x = doc.select("meta[name=description]").first().attr("content");
                //System.out.println("Meta Description: " + doc.select("meta[name=description]").first().attr("content"));
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        
        
    }
    
}
