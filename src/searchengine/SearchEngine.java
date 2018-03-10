package searchengine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ahmos
 */
class Carawler {
    //this is the links visited

    Set<String> pagesVisited = new HashSet<String>();
    //this is the links is going to be visited
    List<String> pagesToVisit = new LinkedList<String>();
    // html pages of the visited links 
    List<HtmlPage> pages = new LinkedList<HtmlPage>();
    String Kamal = new String();

    public List<HtmlPage> getPages() {
        return pages;
    }

    public void setPages(List<HtmlPage> pages) {
        this.pages = pages;
    }
    HashMap<String, ArrayList> robotTxtFiles = new HashMap<String, ArrayList>();
    public HashMap<String, Integer> domaindepth = new HashMap<String, Integer>();

    public HashMap<String, Integer> getDomaindepth() {
        return domaindepth;
    }

    public HashMap<String, ArrayList> getRobotTxtFiles() {
        return robotTxtFiles;
    }

    public Set<String> getPagesVisited() {
        return pagesVisited;
    }

    public void setPagesVisited(Set<String> pagesVisited) {
        this.pagesVisited = pagesVisited;
    }

    public List<String> getPagesToVisit() {
        return pagesToVisit;
    }

    public void setPagesToVisit(List<String> pagesToVisit) {
        this.pagesToVisit = pagesToVisit;
    }

}

public class SearchEngine {

    public static void main(String[] args) {

         
        Carawler carawler = new Carawler();
        carawler.getPagesToVisit().add("https://en.wikipedia.org");
        carawler.getPagesToVisit().add("http://baeldung.com");
        carawler.getPagesToVisit().add("https://www.youtube.com/watch?v=-VgQBc49HU8&index=27&list=RDMM8gDlG2i_U_4");
        carawler.getPagesToVisit().add("https://beginnersbook.com/2013/12/hashmap-in-java-with-example");
        Thread producer = new Thread(new Producer(carawler));
        Thread producer2 = new Thread(new Producer(carawler));
        Thread producer3 = new Thread(new Producer(carawler));
        Thread producer4 = new Thread(new Producer(carawler));
        //PagesDownloader PD = new PagesDownloader(carawler, false);
        SQLiteDBHelper sqlite = new SQLiteDBHelper();
        
        sqlite.StartConnection("test.db"); //NAME TO BE CHANGED
        
        
        producer.start();
        producer2.start();
        producer3.start();
        producer4.start();
        //PD.start();
        
        try {
            producer.join();
            producer2.join();
            producer3.join();
            producer4.join();
           // PD.setFinishedCrawling(true);
            //PD.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       /*
        String query = "CREATE TABLE crawler (\n" +
                        "    ID       INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "    URL      STRING  UNIQUE ON CONFLICT IGNORE,\n" +
                        "    Document STRING\n" +
                        ");";
        
        sqlite.CreateTable(query);
        try {
            sqlite.InsertPagesBatch(carawler.getPages());
            //sqlite.InsertPages(carawler.getPages());
            } catch (SQLException ex) {
            Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
       
        sqlite.Restore();
        
        
        /* FOR TESTING THAT DATABASE STILL HAS DATA
        ResultSet rs  = sqlite.SelectAll("crawler");
        try {
            while(rs.next())
            {
                System.out.println("tttttttttttttttttttttttttttt " + rs.getString("URL"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        
        
         System.out.println("SIZEEEEEEEEEEEEEEEEEEEE " + carawler.getPages().size());
        for (HtmlPage Page : carawler.getPages()) 
        {
            System.out.println("URL" + Page.getDomainUrlObject().getDomainUrl());
        }

        System.out.println("done");
        //sqlite.CloseConnection();
    }

}
