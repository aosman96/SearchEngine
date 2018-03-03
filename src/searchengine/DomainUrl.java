package searchengine;



/**

 *

 * @author ahmos

 */

public class DomainUrl {

 private String domainUrlHash;

 private String domainUrl;


    public DomainUrl(String domainUrlHash, String domainUrl){

        this.domainUrlHash = domainUrlHash;

        this.domainUrl = domainUrl;


    }



    public String getDomainUrlHash() {

        return domainUrlHash;

    }



    public String getDomainUrl() {

        return domainUrl;

    }


 

}