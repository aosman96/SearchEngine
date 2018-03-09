/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import searchengine.DomainUrl;
import searchengine.Hasher;
import searchengine.HtmlPage;
import searchengine.HtmlTools;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author ahmos
 */
public class Consumer {

    private List<String> anchors = new LinkedList<String>();
    private HtmlPage page;
    private Connection connection;
    private Response response;
    private String userAgent;

    public Consumer(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean Start(String crawlDomain) {

        DomainUrl domainUrl = new DomainUrl(Hasher.toSha256(crawlDomain), crawlDomain);

        try {
            connection = Jsoup.connect(domainUrl.getDomainUrl()).userAgent(userAgent)
                    .referrer("http://www.google.com").timeout(100000).ignoreHttpErrors(true);
            response = connection.execute();
            if (response.statusCode() % 100 == 4) {
                return false;
            }
            String contentType ;
           
            contentType = response.contentType();

            if (contentType == null )
                    return false ;
            
            if (contentType.contains("text/html")) {
                Document doc = connection.get();
                page = new HtmlPage(doc.html(), domainUrl, new Date());

                Elements hrefs = doc.select("a");
                for (Element e : hrefs) {
                    String anchor = e.attr("href").trim();
                    anchor = HtmlTools.fixUrl(anchor, domainUrl); //de 3lshan lw nafs el page teb2a et7t mara wa7da
                    if(anchor!="")
                    anchors.add(anchor);

                }
            } else {
                return false;
            }
        } catch (IOException ex) {
            // 3lshan lw 4xx eb2a mayt3mlosh save lw 5xx et3mlo save we ab2a agelo ba3den

        }
        return true;
    }


    public List<String> getLinks() {
        return this.anchors;
    }

    public HtmlPage getpage() {
        return page;
    }

}
