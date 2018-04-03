/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.util.LinkedList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author ahmos
 */
public class webPage {
    float Rank ; // to recawrler 
    int id ; //for database
    LinkedList<webPage> ParentPages = new LinkedList<webPage>(); // pages pointing to me 
    LinkedList<webPage> childPages = new LinkedList<webPage>(); // pages i am pointing to them
    String lastModification ;  // date of last modification
    int inLinksCount , outLinksCount ;

    public int getInLinksCount() {
        return inLinksCount;
    }

    public void setInLinksCount(int inLinksCount) {
        this.inLinksCount = inLinksCount;
    }

    public int getOutLinksCount() {
        return outLinksCount;
    }

    public void setOutLinksCount(int outLinksCount) {
        this.outLinksCount = outLinksCount;
    }
  

    public String getLastModification() {
        return lastModification;
    }

    public void setLastModification(String lastModification) {
        this.lastModification = lastModification;
    }
    String Url;
    String page ;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
    
    public float getRank() {
        return Rank;
    }

    public void setRank(float Rank) {
        this.Rank = Rank;
    }

    public List<webPage> getParentPages() {
        return ParentPages;
    }

    public void setParentPages(LinkedList<webPage> ParentPages) {
        this.ParentPages = ParentPages;
    }

    public LinkedList<webPage> getChildPages() {
        return childPages;
    }

    public void setChildPages(LinkedList<webPage> childPages) {
        this.childPages = childPages;
    }

    public String getUrl() {
        return Url;
    }
    public boolean isEqual(webPage webpage)
    {
   if( this.Url == webpage.getUrl())
    return true;
   else
    return false;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }
    public void CalculateRank()
    {
        if(page==null)
        {
            Rank=0 ;
            return ;
        }
      Document doc =  Jsoup.parse(page);
        if( ! doc.select("meta[name=description]").isEmpty() )
        {
            //to check if it has meta data and this meta data has the word news
            if(doc.select("meta[name=description]").first().attr("content").toLowerCase().contains("news") == true)
            {
                Rank+=3 ;
            }
             if(doc.select("meta[name=description]").first().attr("content").toLowerCase().contains("sports") == true)
            {
                Rank+=3;
            }
             if(lastModification!=null)
             {
             Rank +=10 ;
             }
            
            
            
        }
    
            Rank+= this.getParentPages().size()/10 * 6 ;
            Rank+= this.getChildPages().size()/10 * 3;
    }
@Override
public boolean equals(Object o) {

if ((o instanceof webPage)) {
String toCompare = ((webPage) o).Url;
        
   
if(Url.equals(toCompare))
{
  //  System.out.println("trueeeeeee");
return true;
}
    else
  //  System.out.println("falseeeeeee");
    return false;
}   
 //System.out.println("falseeeeeee");
return false;

}
}

