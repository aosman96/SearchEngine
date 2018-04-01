/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aptsearchengine;

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
        
        
        Indexer2 indexerEngine = new Indexer2();
        try {
            indexerEngine.takeURL("https://en.wikipedia.org/wiki/Computer", 0);
            //indexerEngine.takeURL("https://www.vodafone.com.eg/eshop/Catalogue/Phones-Devices-/Phones", 1);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        indexerEngine.DisplayRank();
        System.out.println("\n\n");
        indexerEngine.DisplayOriginalWord();
        
        
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
