/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.util.ArrayList;

/**
 *
 * @author ahmos
 */
public class Producer implements Runnable {

    private final int numberOfPages = 10;
    private Carawler carawler;

    public Producer(Carawler carawler) {
        this.carawler = carawler;
    }

    public Carawler getCarawler() {
        return carawler;
    }

    public void setCarawler(Carawler carawler) {
        this.carawler = carawler;
    }


    public void search() {

        while (true) {
            webPage currentUrl = null;
            Consumer consumer = new Consumer("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0",this);
            String host = "";
            synchronized (carawler) {
      
   while (this.carawler.getPagesToVisit().isEmpty()) {

                    try {
                        carawler.wait();
                    } catch (InterruptedException ex) {
                        //Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
                         synchronized (carawler) {
                        currentUrl = this.nextUrl(host);
                              Robot robot = new Robot();
                if (currentUrl == null || !robot.isSafeUrl(currentUrl.Url, "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0", carawler)) {
                    continue;
                
                }
                this.carawler.getPagesVisited().add(currentUrl);
           System.out.println(Thread.currentThread().getName() + " add pages to pagestovisit " + carawler.getPagesToVisit().size());
            }

            System.out.println("the link now is " + currentUrl.getUrl());
            
               
            boolean add = consumer.Start(currentUrl);

            synchronized (carawler) {
                // if url return 4xx add it in disallowed urls 
                if (add == false) {
                if(carawler.getRobotTxtFiles().get(host)!=null)
                           carawler.getRobotTxtFiles().get(host).add(currentUrl);
                else
                {
                    ArrayList disallowList = new ArrayList();
                    disallowList.add(currentUrl);
                    carawler.getRobotTxtFiles().put(host, disallowList);
                }
                }
                   if(carawler.getPagesVisited().size() >= carawler.getNumberOfPages())
    {             
      System.out.println(Thread.currentThread().getName()+"   has finished it's work with page size = "+this.carawler.getPagesVisited().size() );
       return ;             
    } 
         
                    this.carawler.getPagesToVisit().addAll(consumer.getLinks());
                currentUrl.getChildPages().addAll(consumer.getLinks());
                if(consumer.getpage()!=null)
                /*    if(!carawler.getPages().contains( consumer.getpage()))
                          this.carawler.getPages().add(consumer.getpage());
                                                        */
                    currentUrl.setPage(consumer.getpage());
                System.out.println(Thread.currentThread().getName() + " add a link to pagesVisited " + currentUrl.getUrl() + "now it have size" + carawler.getPagesVisited().size());
                carawler.notifyAll();
            }

        }

    }

    private webPage nextUrl(String host) {
        webPage nextUrl;
        
            do {
                if (this.carawler.getPagesToVisit().isEmpty()) {
                    return null;
                }
                nextUrl = this.carawler.getPagesToVisit().remove(0);
                System.out.println("The link is searching in " + nextUrl.Url+ "the number of to visit list"+ carawler.getPagesToVisit().size());
            } while (this.carawler.isInVisited(nextUrl));
          

        
        return nextUrl;
    }

    @Override
    public void run() {

        search();
    }
}
