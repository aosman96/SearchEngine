/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ahmos
 */
public class webPage {
    float Rank ;
    LinkedList<webPage> ParentPages = new LinkedList<webPage>();
    LinkedList<webPage> childPages = new LinkedList<webPage>();
    String lastModification ;

    public String getLastModification() {
        return lastModification;
    }

    public void setLastModification(String lastModification) {
        this.lastModification = lastModification;
    }
    String Url;
    HtmlPage page ;

    public HtmlPage getPage() {
        return page;
    }

    public void setPage(HtmlPage page) {
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

