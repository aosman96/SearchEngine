package searchengine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kamal Aly
 */
public class PagesDownloader extends Thread
{
    Carawler c;
    boolean finishedCrawling;
    

    public PagesDownloader(Carawler c, boolean finishedCrawling) {
        this.c = c;
        this.finishedCrawling = finishedCrawling;
    }

   

    public boolean isFinishedCrawling() {
        return finishedCrawling;
    }

    public void setFinishedCrawling(boolean finishedCrawling) {
        this.finishedCrawling = finishedCrawling;
    }
    
    
    
    
    public void Download() throws IOException
    {
        
            HtmlPage Page = null;
            int size = 0;
            synchronized(c)
            {
                if(!finishedCrawling)
                    
                while (c.getPages().size() == 0)
                {
                    try {
                        c.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PagesDownloader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }    
            
            do {
                
                synchronized(c)
                {
                    Page = c.getPages().remove(0);
                    size = c.getPages().size();
                }
                BufferedWriter output = null;
                try {
                    File file = new File("HTML Documents\\" + Page.getID() + ".html");
                    output = new BufferedWriter(new FileWriter(file));
                    output.write(Page.getDomainUrlObject().getDomainUrl());
                    output.write("\n");
                    output.write(Page.getHtml());
                    c.getPages().remove(Page);
                } catch (IOException ex) {
                    Logger.getLogger(PagesDownloader.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    output.close();
                }
            } while (size > 0);
    }
    
    @Override
    public void run()
    {
        try {
            Download();
        } catch (IOException ex) {
            Logger.getLogger(PagesDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
