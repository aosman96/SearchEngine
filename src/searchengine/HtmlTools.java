/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

/**
 *
 * @author ahmos
 */
// handling the case www in the first 
public class HtmlTools {

    public static String fixUrl(String inUrl, DomainUrl domain) {
        String url = "";
        if (!inUrl.startsWith(domain.getDomainUrl())) {
            if (!inUrl.startsWith("http")) {
                if (inUrl.startsWith("/")) {
                    url = domain.getDomainUrl().concat(inUrl);
                } else {
                    url = domain.getDomainUrl().concat("/" + inUrl);
                }
            } 
        }
        if(url.endsWith("/")){
        url = url.substring(0,url.length()-1);
        }
        if(url.contains("#"))
        {
        url = url.substring(0, url.indexOf("#")-1);
        }
        if(url.contains("www"))
        {
        url = url.substring(0,url.indexOf("www")-1)+ url.substring(url.indexOf("www")+1);
        }
        return url;
    }
}
