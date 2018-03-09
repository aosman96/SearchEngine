/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.util.Date;

/**
 *
 * @author ahmos
 */
public class HtmlPage {
    private int id ;
    private String Html;
    private DomainUrl domainUrl ;
    private Date created ;

    public HtmlPage(String Html, DomainUrl domainUrl, Date created) {
        this.Html = Html;
        this.domainUrl = domainUrl;
        this.created = created;
    }
    

  
    public String getHtml()
    {
        return Html;
    }

    public DomainUrl getDomainUrlObject()
    {
        return domainUrl;
    }
}
