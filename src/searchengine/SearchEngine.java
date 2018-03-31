/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 *
 * @author ahmos
 */
class Carawler{
            //this is the links visited
            Set<webPage> pagesVisited = new HashSet<webPage>() ;
            //this is the links is going to be visited
            List<webPage> pagesToVisit = new LinkedList<webPage>();
            // html pages of the visited links 
           // List<HtmlPage> pages = new LinkedList<HtmlPage>(); //da ana haselooooo we ha5leha gowa el pages  
            //storing the rank of each page 
          //  HashMap<String,ArrayList<Integer>> linksToPage=  new HashMap<String,ArrayList<Integer>>();
               HashMap<String,ArrayList> robotTxtFiles = new HashMap<String,ArrayList>();
      //     public HashMap <String,Integer> domaindepth = new HashMap<String,Integer>();

   

            int numberOfPages = 50;

 //   public List<HtmlPage> getPages() {
   //     return pages;
    //}

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

   // public void setPages(List<HtmlPage> pages) {
    //    this.pages = pages;
    //}
         

   
            
    public HashMap<String, ArrayList> getRobotTxtFiles() {
        return robotTxtFiles;
    }

    

    public Set<webPage> getPagesVisited() {
        return pagesVisited;
    }

    public void setPagesVisited(Set<webPage> pagesVisited) {
        this.pagesVisited = pagesVisited;
    }

    public List<webPage> getPagesToVisit() {
        return pagesToVisit;
    }

    public void setPagesToVisit(List<webPage> pagesToVisit) {
        this.pagesToVisit = pagesToVisit;
    }
    public boolean isInVisited(webPage link)
    {
     for( webPage w :pagesVisited)
    {
    if (w.equals(link))
    {
        System.out.println("this link comes before : "+ link.Url);
    return true;
    }
    
    }
     return false ;
    }
    public boolean updateVistedList(webPage link ,webPage parent)
    {
    
    for( webPage w :pagesVisited)
    {
        if(w.getUrl() != null && link != null)
    if (w.equals(link))
    {
   w.getParentPages().add(parent);
    return true;
    }
    }
    return false ;
    }
    
    public boolean updateToVistedList(webPage link,webPage parent)
    {
    
    for( webPage w :pagesToVisit)
    {
    if (w.equals(link))
    {
    w.getParentPages().add(parent);
    return true;
    }
    }
    return false ;
    }            
}
public class SearchEngine {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO code application logic here
  //  while(true)
    {
            Carawler carawler =new Carawler() ;
            webPage webpage1 = new webPage();
            webpage1.setUrl("https://wikipedia.org/");
            webpage1.setRank(1/4);
            webPage webpage2 = new webPage();
            webpage2.setUrl("https://yahoo.com");
            webpage2.setRank(1/4);
            webPage webpage3 = new webPage();
            webpage3.setUrl("https://twitter.com");
            webpage3.setRank(1/4);
            webPage webpage4 = new webPage();
            webpage4.setUrl(" https://youtube.com/testtube");
            webpage4.setRank(1/4);
            
            
            
             carawler.getPagesToVisit().add(webpage1);
                carawler.getPagesToVisit().add(webpage2);
                   carawler.getPagesToVisit().add(webpage3);
                    carawler.getPagesToVisit().add(webpage4);
            Thread producer = new Thread(new Producer(carawler));
            Thread producer2 = new Thread(new Producer(carawler));
           Thread producer3 = new  Thread(new Producer(carawler));
                      Thread producer4 = new  Thread(new Producer(carawler));
       producer.start();
           Thread.sleep(100);
           producer2.start();
                       Thread.sleep(100);
            producer3.start();
                       Thread.sleep(100);
              producer4.start();
        try {
              producer.join();
              producer2.join();
            producer3.join();
            producer4.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
         System.out.println("done");
      
          System.out.println("SIZEEEEEEEEEEEEEEEEEEEE 2 " + carawler.getPagesVisited().size());
            for (webPage w :carawler.pagesVisited)
            {
            System.out.println( w.Url+ "     i have "+w.getParentPages().size()+" Pointing to me and " + w.getChildPages().size()+" pointing to them" + "the last date modified= "+w.getLastModification());
            
            }
          //  webPage e ;
            //        e = carawler.getPagesVisited().iterator().next();
                    
          //  System.out.println(e.getPage().getHtml());
    }
    }
}
