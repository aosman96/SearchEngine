/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.sql.*;

/**
 *
 * @author ahmos
 */
public class Producer implements Runnable {

    
    
    
    private final int numberOfPages = 5000;
    private Carawler carawler;

    public Producer(Carawler carawler) {
        this.carawler = carawler;
        
    }
    
    private boolean verifyUrl(String url, String host) {
// Only allow HTTP URLs.
        if (!url.toLowerCase().startsWith("http://")) {
            if (!url.toLowerCase().startsWith("https://")) {
                return false;
            }
        }
// Verify format of URL.
        URL verifiedUrl = null;
        try {

            verifiedUrl = new URL(url);
            host = verifiedUrl.getHost();
            if (!carawler.domaindepth.containsKey(host)) {
                carawler.domaindepth.put(host, 1);
                return true;
            }
            int count = carawler.domaindepth.get(host);
            //verfiy for domain tree

            if (count < 20) {
                count++;
                carawler.domaindepth.replace(host, count);
                return true;
            } else if (count >= 20) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public void search() {
        
        //NOTE: CHANGE BACK TO 5000
        while (this.carawler.getPagesVisited().size() < 5) {
            String currentUrl = null;
            Consumer consumer = new Consumer("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0");
            String host = "";
            synchronized (carawler) {
                while (this.carawler.getPagesToVisit().isEmpty()) {

                    try {
                        carawler.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                currentUrl = this.nextUrl(host);
                Robot robot = new Robot();
                if (currentUrl == null || !robot.isSafeUrl(currentUrl, "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0", carawler)) {
                    continue;
                }

            }

            System.out.println("the link now is " + currentUrl);
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
                this.carawler.getPagesVisited().add(currentUrl);
                this.carawler.getPagesToVisit().addAll(consumer.getLinks());
                
                
                if(consumer.getpage()!=null)
                    this.carawler.getPages().add(consumer.getpage());
                
                System.out.println(Thread.currentThread().getName() + " add a link to pagesVisited " + currentUrl + "now it have size" + carawler.getPagesVisited().size());
                System.out.println(Thread.currentThread().getName() + " add pages to pagestovisit " + carawler.getPagesToVisit().size());

                carawler.notifyAll();
            }

        }

    }

    private String nextUrl(String host) {
        String nextUrl;
        synchronized (carawler) {
            do {
                if (this.carawler.getPagesToVisit().isEmpty()) {
                    return null;
                }
                nextUrl = this.carawler.getPagesToVisit().remove(0);
                System.out.println("The link is searching in is" + nextUrl);
            } while (this.carawler.pagesVisited.contains(nextUrl) || !verifyUrl(nextUrl, host));
        }
        return nextUrl;
    }

    @Override
    public void run() {
            search();
    }
}
